package com.github.eikek.borer.compats.http4s

import cats.effect.IO
import fs2.*
import fs2.text.utf8

import org.http4s.EntityEncoder

trait BaseSuite:

  def writeToString[A](a: A)(implicit W: EntityEncoder[IO, A]): IO[String] =
    Stream
      .emit(W.toEntity(a))
      .flatMap(_.body)
      .through(utf8.decode)
      .foldMonoid
      .compile
      .last
      .map(_.getOrElse(""))
