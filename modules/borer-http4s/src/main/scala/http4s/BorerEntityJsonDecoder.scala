package com.github.eikek.borer.compats.http4s

import cats.effect.Sync

import io.bullet.borer.Decoder
import org.http4s.EntityDecoder

trait BorerEntityJsonDecoder:
  given [F[_]: Sync, A: Decoder]: EntityDecoder[F, A] =
    BorerEntities.decodeEntityJson[F, A]

object BorerEntityJsonDecoder extends BorerEntityJsonDecoder
