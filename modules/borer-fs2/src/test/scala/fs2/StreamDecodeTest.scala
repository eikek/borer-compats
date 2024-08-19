package com.github.eikek.borer.compats.fs2

import cats.effect.*
import cats.syntax.all.*

import io.bullet.borer.Cbor
import io.bullet.borer.compat.scodec.*
import munit.*
import scodec.bits.ByteVector

class StreamDecodeTest extends CatsEffectSuite:

  test("read json records"):
    val listIO = StreamDecode
      .decodeJson[IO, Ranking](Ranking.jsonBytes, 128)
      .evalTap { r =>
        IO(assert(r.ranking > 0))
      }
      .compile
      .toList
    assertIO(listIO.map(_.size), 100)

  test("read cbor records"):
    val listIO = StreamDecode
      .decodeCbor[IO, Ranking](Ranking.cborBytes, 128)
      .evalTap { r =>
        IO(assert(r.ranking > 0))
      }
      .compile
      .toList
    assertIO(listIO.map(_.size), 100)

  test("read json and cbor"):
    val listJson =
      Ranking.jsonBytes.through(StreamDecode.json[IO, Ranking](128)).compile.toList
    val listCbor =
      Ranking.cborBytes.through(StreamDecode.cbor[IO, Ranking](128)).compile.toList
    (listJson, listCbor).mapN { (js, cb) =>
      assertEquals(js, cb)
    }

  test("read single cbor record"):
    for
      bytes <- Ranking.cborSingleBytes.compile.to(ByteVector)
      r = Cbor.decode(bytes).to[Ranking].value
      _ = assertEquals(r.ranking, 1)
    yield ()
