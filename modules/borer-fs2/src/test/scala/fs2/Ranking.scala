package com.github.eikek.borer.compats.fs2

import java.net.URL

import cats.effect.*
import fs2.Stream

import io.bullet.borer.derivation.MapBasedCodecs
import io.bullet.borer.{Decoder, Encoder}

final case class Ranking(
    ranking: Int,
    name: String,
    team: String
)

object Ranking:
  given Encoder[Ranking] = MapBasedCodecs.deriveEncoder
  given Decoder[Ranking] = MapBasedCodecs.deriveDecoder

  private def urlOf(name: String) =
    IO.blocking(
      Option(getClass.getResource(name))
        .getOrElse(sys.error("rankings.json not found"))
    )
  private def readUrl(url: URL): Stream[IO, Byte] =
    fs2.io
      .readInputStream(
        IO.blocking(url.openStream()),
        256,
        closeAfterUse = true
      )

  val jsonUrl = urlOf("/rankings.json")
  val cborUrl = urlOf("/rankings.cbor")
  val cborSingleUrl = urlOf("/ranking1.cbor")

  val jsonBytes: Stream[IO, Byte] =
    Stream.eval(jsonUrl).flatMap(readUrl).dropLastIf(_ == 10)
  val cborBytes: Stream[IO, Byte] = Stream.eval(cborUrl).flatMap(readUrl)
  val cborSingleBytes: Stream[IO, Byte] = Stream.eval(cborSingleUrl).flatMap(readUrl)
