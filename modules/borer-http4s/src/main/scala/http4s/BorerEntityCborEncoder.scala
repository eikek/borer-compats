package com.github.eikek.borer.compats.http4s

import io.bullet.borer.Encoder
import org.http4s.EntityEncoder

trait BorerEntityCborEncoder:
  given [F[_], A: Encoder]: EntityEncoder[F, A] =
    BorerEntities.encodeEntityCbor[F, A]

object BorerEntityCborEncoder extends BorerEntityCborEncoder
