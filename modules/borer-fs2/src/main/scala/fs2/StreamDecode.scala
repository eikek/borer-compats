package com.github.eikek.borer.compats.fs2

import fs2.Pull
import fs2.RaiseThrowable
import fs2.{Chunk, Pipe, Stream}

import io.bullet.borer.*

/** Caveat: the buffersize must be large enough to decode at least one A */
object StreamDecode {

  def decodeJson[F[_]: RaiseThrowable, A: Decoder](
      in: Stream[F, Byte],
      bufferSize: Int = 16384
  ): Stream[F, A] =
    decode0[F, A](true, ba => Json.decode(ba).withPrefixOnly.to[A])(in, bufferSize)

  def decodeCbor[F[_]: RaiseThrowable, A: Decoder](
      in: Stream[F, Byte],
      bufferSize: Int = 16384
  ): Stream[F, A] =
    decode0[F, A](false, ba => Cbor.decode(ba).withPrefixOnly.to[A])(in, bufferSize)

  def cbor[F[_]: RaiseThrowable, A: Decoder](bufferSize: Int = 16384): Pipe[F, Byte, A] =
    in => decodeCbor(in, bufferSize)

  def json[F[_]: RaiseThrowable, A: Decoder](bufferSize: Int = 16384): Pipe[F, Byte, A] =
    in => decodeJson(in, bufferSize)

  private def decode0[F[_]: RaiseThrowable, A](
      fixPosition: Boolean,
      decode: Input[Array[Byte]] => DecodingSetup.Sealed[A]
  )(
      in: Stream[F, Byte],
      bufferSize: Int
  ): Stream[F, A] =
    in.repeatPull(_.unconsN(bufferSize, allowFewer = true).flatMap {
      case Some((hd, tl)) =>
        decodeCont[A](fixPosition, decode)(hd) match {
          case Right((v, remain)) =>
            Pull.output(Chunk.from(v)).as(Some(Stream.chunk(remain) ++ tl))
          case Left(ex) =>
            Pull.raiseError(ex)
        }
      case None =>
        Pull.pure(None)
    })

  private val curlyClose = Chunk('}'.toByte)
  private def decodeCont[A](
      fixPosition: Boolean,
      decode: Input[Array[Byte]] => DecodingSetup.Sealed[A]
  )(input: Chunk[Byte]): Either[Throwable, (Vector[A], Chunk[Byte])] = {
    @annotation.tailrec
    def go(
        in: Input[Array[Byte]],
        pos: Int,
        result: Vector[A]
    ): Either[Throwable, (Vector[A], Chunk[Byte])] =
      decode(in).valueAndInputEither match
        case Right((v, rest)) =>
          val rb = rest.asInstanceOf[Input[Array[Byte]]]
          val nextPos = rb.cursor.toInt
          if (fixPosition) go(rb.unread(1), nextPos - 1, result :+ v)
          else go(rb, nextPos, result :+ v)

        case Left(ex) =>
          Left(ex)
          if (result.isEmpty) Left(ex)
          else {
            // the position seems sometimes off by 1, standing on the last char closing the json object
            val hack = {
              val next = input.drop(pos)
              if (next == curlyClose) Chunk.empty
              else next
            }
            Right(result -> hack)
          }

    go(Input.fromByteArray(input.toArray), 0, Vector.empty)
  }
}
