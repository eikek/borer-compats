package com.github.eikek.borer.compats.http4s

import io.bullet.borer.derivation.MapBasedCodecs
import io.bullet.borer.{Decoder, Encoder}

final case class Person(name: String, year: Int)
object Person:
  given Encoder[Person] = MapBasedCodecs.deriveEncoder
  given Decoder[Person] = MapBasedCodecs.deriveDecoder
