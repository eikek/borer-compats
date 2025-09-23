import sbt._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Dependencies {
  object V {
    val scala3 = "3.3.6"
    val borer = "1.16.1"
    val fs2 = "3.12.2"
    val http4s = "0.23.31"
    val munit = "1.1.0"
    val munitCatsEffect = "2.1.0"
    val scribe = "3.15.0"
  }

  val fs2 = Def.setting(
    Seq(
      "co.fs2" %%% "fs2-core" % V.fs2
    )
  )

  val fs2Io = Seq("co.fs2" %% "fs2-io" % V.fs2)

  val borer = Def.setting(
    Seq(
      "io.bullet" %%% "borer-core" % V.borer
    )
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

  val http4s = Def.setting(
    Seq(
      "org.http4s" %%% "http4s-core" % V.http4s
    )
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
