package com.github.eikek.borer.compats.http4s

import cats.effect.*
import fs2.{Chunk, Stream}

import com.github.eikek.borer.compats.http4s.BorerEntityCborCodec.given
import io.bullet.borer.Cbor
import munit.*
import org.http4s.*
import org.http4s.headers.`Content-Type`
import scodec.bits.ByteVector

class BorerEntityCborCodecTest extends CatsEffectSuite with BaseSuite:

  val john = Person("John", 1988)
  val johnCbor = ByteVector.view(Cbor.encode(john).toByteArray)

  test("write cbor"):
    for
      entity <- EntityEncoder[IO, Person].toEntity(john).body.compile.to(ByteVector)
      _ = assertEquals(entity, johnCbor)
    yield ()

  test("cbor response should have appropriate content-type"):
    val encoder = summon[EntityEncoder[IO, Person]]
    assertEquals(encoder.contentType, Some(`Content-Type`(MediaType.application.cbor)))

  test("decode cbor payload"):
    val decoder = summon[EntityDecoder[IO, Person]]
    val media = Media(
      Stream.chunk(Chunk.byteVector(johnCbor)).covary[IO],
      Headers("content-type" -> "application/cbor")
    )
    for
      dec <- decoder.decode(media, strict = true).value
      _ = assertEquals(dec, Right(john))
    yield ()

  test("decode cbor fail when no content-type is present"):
    val decoder = summon[EntityDecoder[IO, Person]]
    val media = Media(
      Stream.chunk(Chunk.byteVector(johnCbor)).covary[IO],
      Headers.empty
    )
    val dec = decoder.decode(media, strict = true).value
    dec.assertEquals(Left(MediaTypeMissing(Set(MediaType.application.cbor))))

  test("decode cbor fail when cbor is bad"):
    val decoder = summon[EntityDecoder[IO, Person]]
    val media = Media(
      Stream.chunk(Chunk.byteVector(johnCbor.drop(2))).covary[IO],
      Headers("content-type" -> "application/cbor")
    )
    val dec = decoder.decode(media, strict = true).value
    dec.assert {
      case Right(_) => fail("unexpected success")
      case Left(err) =>
        assert(err.isInstanceOf[BorerDecodeFailure])
        assertEquals(
          err.asInstanceOf[BorerDecodeFailure].respString,
          "6e616d65644a6f686e64796561721907c4"
        )
        true
    }
