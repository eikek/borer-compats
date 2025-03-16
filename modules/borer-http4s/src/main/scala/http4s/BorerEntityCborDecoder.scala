package com.github.eikek.borer.compats.http4s

import cats.effect.Sync

import io.bullet.borer.Decoder
import org.http4s.EntityDecoder

trait BorerEntityCborDecoder:
  given [F[_]: Sync, A: Decoder]: EntityDecoder[F, A] =
    BorerEntities.decodeEntityCbor[F, A]

object BorerEntityCborDecoder extends BorerEntityCborDecoder
