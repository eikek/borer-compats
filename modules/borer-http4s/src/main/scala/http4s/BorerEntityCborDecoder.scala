package com.github.eikek.borer.compats.http4s

import cats.effect.Async

import io.bullet.borer.Decoder
import org.http4s.EntityDecoder

trait BorerEntityCborDecoder:
  given [F[_]: Async, A: Decoder]: EntityDecoder[F, A] =
    BorerEntities.decodeEntityCbor[F, A]

object BorerEntityCborDecoder extends BorerEntityCborDecoder
