package com.github.eikek.borer.compats.http4s

import cats.data.EitherT
import cats.effect.*
import cats.syntax.all.*
import fs2.{Chunk, Stream}

import io.bullet.borer.*
import org.http4s.*
import org.http4s.headers.*
import scodec.bits.ByteVector

object BorerEntities:

  def decodeEntityJson[F[_]: Sync, A: Decoder]: EntityDecoder[F, A] =
    EntityDecoder.decodeBy(MediaType.application.json)(decodeJson)

  def decodeEntityCbor[F[_]: Sync, A: Decoder]: EntityDecoder[F, A] =
    EntityDecoder.decodeBy(MediaType.application.cbor)(decodeCbor)

  def decodeJson[F[_]: Sync, A: Decoder](media: Media[F]): DecodeResult[F, A] =
    EitherT(readStream(media.body).flatMap { input =>
      for {
        res <- Sync[F].delay(Json.decode(input).to[A].valueEither)
        txt <- if (res.isLeft) media.bodyText.compile.string else Sync[F].pure("")
      } yield res.left.map(BorerDecodeFailure(txt, _))
    })

  def decodeCbor[F[_]: Sync, A: Decoder](media: Media[F]): DecodeResult[F, A] =
    EitherT(readStream(media.body).flatMap { input =>
      for {
        res <- Sync[F].delay(Cbor.decode(input).to[A].valueEither)
        txt <-
          if (res.isLeft) media.body.compile.to(ByteVector).map(_.toHex)
          else Sync[F].pure("")
      } yield res.left.map(BorerDecodeFailure(txt, _))
    })

  def encodeEntityJson[F[_], A: Encoder]: EntityEncoder[F, A] =
    EntityEncoder.simple(`Content-Type`(MediaType.application.json))(a =>
      Chunk.array(Json.encode(a).toByteArray)
    )

  def encodeEntityCbor[F[_], A: Encoder]: EntityEncoder[F, A] =
    EntityEncoder.simple(`Content-Type`(MediaType.application.cbor))(a =>
      Chunk.array(Cbor.encode(a).toByteArray)
    )

  private def readStream[F[_]: Sync](
      in: Stream[F, Byte]
  ): F[Input[Array[Byte]]] =
    in.compile.to(Array).map(bv => Input.fromByteArray(bv))
