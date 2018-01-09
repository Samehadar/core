/*
 * Copyright 2017 Smart Backpacker App
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.smartbackpackerapp.repository

import cats.effect.IO
import com.smartbackpackerapp.common.IOAssertion
import com.smartbackpackerapp.common.sql.RepositorySpec
import com.smartbackpackerapp.model.CountryCode

class VisaRestrictionsIndexRepositorySpec extends RepositorySpec {

  override def testDbName: String = getClass.getSimpleName

  private lazy val repo = new PostgresVisaRestrictionsIndexRepository[IO](transactor)

  test("NOT find the visa restriction index") {
    IOAssertion {
      for {
        idx <- repo.findRestrictionsIndex(new CountryCode("AR"))
      } yield {
        assert(idx.isEmpty)
      }
    }
  }

  test("find visa restrictions index query") {
    check(VisaRestrictionsIndexStatement.findIndex(new CountryCode("AR")))
  }

}