package com.github.eikek.borer.compats.http4s

import cats.effect.Async

import io.bullet.borer.Decoder
import org.http4s.EntityDecoder

trait BorerEntityJsonDecoder:
  given [F[_]: Async, A: Decoder]: EntityDecoder[F, A] =
    BorerEntities.decodeEntityJson[F, A]

object BorerEntityJsonDecoder extends BorerEntityJsonDecoder
