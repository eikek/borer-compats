package com.github.eikek.borer.compats.http4s

import cats.effect.*
import fs2.{Chunk, Stream}

import com.github.eikek.borer.compats.http4s.BorerEntityJsonCodec.given
import munit.*
import org.http4s.*
import org.http4s.headers.`Content-Type`

class BorerEntityJsonCodecTest extends CatsEffectSuite with BaseSuite:
  test("write json"):
    val data = Person("John", 1988)
    for
      s <- writeToString(data)
      _ = assertEquals("""{"name":"John","year":1988}""", s)
    yield ()

  test("json response should have appropriate content-type"):
    val encoder = summon[EntityEncoder[IO, Person]]
    assertEquals(encoder.contentType, Some(`Content-Type`(MediaType.application.json)))

  test("decode json payload"):
    val decoder = summon[EntityDecoder[IO, Person]]
    val media = Media(
      Stream.chunk(Chunk.array("""{"name":"John","year":1988}""".getBytes)).covary[IO],
      Headers("content-type" -> "application/json")
    )
    for
      dec <- decoder.decode(media, strict = true).value
      _ = assertEquals(dec, Right(Person("John", 1988)))
    yield ()

  test("decode json fail when no content-type is present"):
    val decoder = summon[EntityDecoder[IO, Person]]
    val media = Media(
      Stream.chunk(Chunk.array("""{"name":"John","year":1988}""".getBytes)).covary[IO],
      Headers.empty
    )
    val dec = decoder.decode(media, strict = true).value
    dec.assertEquals(Left(MediaTypeMissing(Set(MediaType.application.json))))

  test("decode json fail when json is bad"):
    val decoder = summon[EntityDecoder[IO, Person]]
    val media = Media(
      Stream.chunk(Chunk.array("""name:"John88""".getBytes)).covary[IO],
      Headers("content-type" -> "application/json")
    )
    val dec = decoder.decode(media, strict = true).value
    dec.assert {
      case Right(_) => fail("unexpected success")
      case Left(err) =>
        assert(err.isInstanceOf[BorerDecodeFailure])
        assertEquals(err.asInstanceOf[BorerDecodeFailure].respString, """name:"John88""")
        true
    }
