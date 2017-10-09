package com.github.gvolpe.smartbackpacker.parser

import cats.effect.Effect
import com.github.gvolpe.smartbackpacker.config.SBConfiguration
import com.github.gvolpe.smartbackpacker.model._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.{Document, Element}
import net.ruippeixotog.scalascraper.scraper.HtmlExtractor

import scala.util.{Failure, Success, Try}

object WikiPageParser {
  def apply[F[_]: Effect]: WikiPageParser[F] = new WikiPageParser[F]
}

class WikiPageParser[F[_] : Effect] extends AbstractWikiPageParser[F] {

  override def htmlDocument(from: CountryCode): F[Document] = Effect[F].delay {
    val browser = new JsoupBrowser()
    val wikiPage = SBConfiguration.wikiPage(from).getOrElse("http://google.com")
    browser.get(wikiPage)
  }

}

abstract class AbstractWikiPageParser[F[_] : Effect] {

  def htmlDocument(from: CountryCode): F[Document]

  def visaRequirementsFor(from: CountryCode, to: CountryName): F[VisaRequirementsFor] =
    Effect[F].map(parseVisaRequirements(from)) { requirements =>
      requirements.find(_.country == to)
        .getOrElse(VisaRequirementsFor(to, UnknownVisaCategory, "No information available"))
    }

  // To handle special cases like the Irish wiki page containing a table of both 4 and 5 columns
  private def wikiTableExtractor: HtmlExtractor[Element, Iterable[String]] = _.flatMap { e =>
    val text = e.text.split('!').head.trim // for cases like Ivory Coast
    Try(e.attr("colspan")) match {
      case Success(cs) if cs == "2" => Seq(text, "")
      case Success(_)               => Seq(text)
      case Failure(_)               => Seq(text)
    }
  }

  private def colspanExtractor: HtmlExtractor[Element, Option[Element]] = _.find { e =>
    Try(e.attr("colspan")) match {
      case Success(cs) if cs == "2" => true
      case Success(_)               => true
      case Failure(_)               => false
    }
  }

  private val normalTableMapper: List[String] => VisaRequirementsFor = seq => {
    VisaRequirementsFor(seq.head.asCountry, seq(1).asVisaCategory, seq(2).asDescription)
  }

  private val colspanTableMapper: List[String] => VisaRequirementsFor = seq => {
    VisaRequirementsFor(seq.head.asCountry, seq(1).asVisaCategory, seq(2).asDescription + " " + seq(3))
  }

  // TODO: Aggregate ".sortable" table with ".wikitable" table that for some countries have partially recognized countries like Kosovo
  // TODO: This will require add more visa categories (See Polish page)
  private def parseVisaRequirements(from: CountryCode): F[List[VisaRequirementsFor]] =
    Effect[F].map(htmlDocument(from)) { doc =>
      // Get first of all sortable tables
      val wikiTables = (doc >> elementList(".sortable")).headOption
      // Find out whether it's an irregular (colspan=2) or regular table
      val colspan = wikiTables.flatMap(_ >> extractor(".sortable td", colspanExtractor))
      // Extract all the information from the first wikitable found
      val table = wikiTables.toList.flatMap(_ >> extractor(".sortable td", wikiTableExtractor))
      // Find out the number of columns
      val tableSize = wikiTables.map { e => (e >> extractor(".sortable th", texts)).size }

      // Group it per country using the corresponding mapper
      val mapper = colspan.fold(normalTableMapper)(_ => colspanTableMapper)
      table.grouped(tableSize.getOrElse(3)).map(mapper).toList
    }

}