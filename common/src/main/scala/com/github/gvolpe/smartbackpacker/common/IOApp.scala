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

package com.github.gvolpe.smartbackpacker.common

import cats.effect.IO
import org.joda.time.Instant

trait IOApp {
  def start(args: List[String]): IO[Unit]
  def main(args: Array[String]): Unit = start(args.toList).unsafeRunSync()

  def putStrLn(value: String): IO[Unit] = IO(println(value))
  def getLine: IO[String] = IO(scala.io.StdIn.readLine())
  def getTime: IO[Instant] = IO(Instant.now())
}