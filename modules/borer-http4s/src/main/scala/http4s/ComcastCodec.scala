package com.github.eikek.borer.compats.http4s

import com.comcast.ip4s.*
import io.bullet.borer.*

trait ComcastCodec:
  given Encoder[Port] = Encoder.forInt.contramap(_.value)
  given Decoder[Port] =
    Decoder.forInt.mapEither(n => Port.fromInt(n).toRight(s"Invalid port: $n"))

  given Encoder[Host] = Encoder.forString.contramap(_.toString)
  given Decoder[Host] =
    Decoder.forString.mapEither(s => Host.fromString(s).toRight(s"Invalid host: $s"))

  given Encoder[Ipv6Address] = Encoder.forString.contramap(_.toMixedString)
  given Decoder[Ipv6Address] = Decoder.forString.mapEither(s =>
    Ipv6Address.fromString(s).toRight(s"Invalid ipv6 address: $s")
  )

  given Encoder[Ipv4Address] = Encoder.forString.contramap(_.toUriString)
  given Decoder[Ipv4Address] = Decoder.forString.mapEither(s =>
    Ipv4Address.fromString(s).toRight(s"Invalid ipv4 address: $s")
  )

  given Encoder[IDN] = Encoder.forString.contramap(_.toString)
  given Decoder[IDN] =
    Decoder.forString.mapEither(s => IDN.fromString(s).toRight(s"Invalid IDN: $s"))

object ComcastCodec extends ComcastCodec
