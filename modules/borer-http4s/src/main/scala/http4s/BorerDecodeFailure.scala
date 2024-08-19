package com.github.eikek.borer.compats.http4s

import io.bullet.borer.*
import org.http4s.*

final case class BorerDecodeFailure(respString: String, error: Borer.Error[?])
    extends DecodeFailure {

  override val message: String = s"${error.getMessage}: $respString"

  override val cause: Option[Throwable] = Option(error.getCause)

  def makeHttpResponse[F[_]](httpVersion: HttpVersion)(using
      EntityEncoder[F, BorerDecodeFailure]
  ): Response[F] =
    Response(status = Status.BadRequest).withEntity(this)

  def toHttpResponse[F[_]](httpVersion: HttpVersion): Response[F] =
    import BorerEntityJsonEncoder.given
    makeHttpResponse(httpVersion)
}

object BorerDecodeFailure:
  given Encoder[Borer.Error[?]] =
    Encoder.forString.contramap(_.getMessage)

  given Encoder[BorerDecodeFailure] = new Encoder[BorerDecodeFailure] {
    def write(w: Writer, v: BorerDecodeFailure) =
      w.writeMapOpen(2)
      w.writeMapMember("response", v.respString)
      w.writeMapMember("decodeError", v.error)
      w.writeMapClose()
  }
