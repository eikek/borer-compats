package com.github.eikek.borer.compats.http4s

import io.bullet.borer.Encoder
import org.http4s.EntityEncoder

trait BorerEntityJsonEncoder:
  given [F[_], A: Encoder]: EntityEncoder[F, A] =
    BorerEntities.encodeEntityJson[F, A]

object BorerEntityJsonEncoder extends BorerEntityJsonEncoder
