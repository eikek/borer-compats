import sbt._

object Dependencies {
  object V {
    val scala3 = "3.3.5"
    val borer = "1.15.0"
    val fs2 = "3.11.0"
    val http4s = "0.23.30"
    val munit = "1.1.0"
    val munitCatsEffect = "2.0.0"
    val scribe = "3.15.0"
  }

  val fs2 = Seq(
    "co.fs2" %% "fs2-core" % V.fs2
  )

  val fs2Io = Seq(
    "co.fs2" %% "fs2-io" % V.fs2
  )

  val borer = Seq(
    "io.bullet" %% "borer-core" % V.borer
  )

  val borerDerive = Seq(
    "io.bullet" %% "borer-derivation" % V.borer
  )
  val borerScodec = Seq(
    "io.bullet" %% "borer-compat-scodec" % V.borer
  )

  val scribe = Seq(
    "com.outr" %% "scribe" % V.scribe,
    "com.outr" %% "scribe-slf4j2" % V.scribe,
    "com.outr" %% "scribe-cats" % V.scribe
  )

  val http4s = Seq(
    "org.http4s" %% "http4s-core" % V.http4s
  )

  val http4sEmber = Seq(
    "org.http4s" %% "http4s-ember-server" % V.http4s
  )

  val munit = Seq(
    "org.scalameta" %% "munit" % V.munit,
    "org.scalameta" %% "munit-scalacheck" % V.munit,
    "org.typelevel" %% "munit-cats-effect" % V.munitCatsEffect
  )
}
