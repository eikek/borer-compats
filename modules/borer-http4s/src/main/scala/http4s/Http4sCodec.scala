package com.github.eikek.borer.compats.http4s

import io.bullet.borer.*
import org.http4s.*

trait Http4sCodec:
  given Encoder[Uri] = Encoder.forString.contramap(_.renderString)
  given Decoder[Uri] =
    Decoder.forString.mapEither(s => Uri.fromString(s).left.map(_.message))

object Http4sCodec extends Http4sCodec
