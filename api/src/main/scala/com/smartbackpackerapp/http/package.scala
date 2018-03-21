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

package com.smartbackpackerapp

import com.smartbackpackerapp.model._
import io.circe.Encoder
import io.circe.generic.extras.decoding.UnwrappedDecoder
import io.circe.generic.extras.encoding.UnwrappedEncoder

package object http {

  val ApiVersion = "v1"

  case class ApiErrorCode(value: Int) extends AnyVal

  object ApiErrorCode {
    val ENTITY_NOT_FOUND = ApiErrorCode(100)
    val SAME_COUNTRIES_SEARCH = ApiErrorCode(101)
  }

  case class ApiError(code: ApiErrorCode, error: String)

  implicit def valueClassEncoder[A: UnwrappedEncoder]: Encoder[A] = implicitly

  //implicit def jsonDecoder[A <: Product : Decoder](implicit F: Sync[F]): EntityDecoder[F, A] = jsonOf[F, A]
  //implicit def jsonEncoder[A <: Product : Encoder](implicit F: Sync[F]): EntityEncoder[F, A] = jsonEncoderOf[F, A]

}
