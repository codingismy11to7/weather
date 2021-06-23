name := "weather"
version := "0.1.0"
scalaVersion := "2.13.6"

scalacOptions ++= Seq(
  "-feature",
  "-Xfatal-warnings",
  "-Xlint:unused",
  "-deprecation",
)

enablePlugins(BuildInfoPlugin)
buildInfoPackage := "weather"

scalafmtOnCompile := true

val circeVersion      = "0.14.1"
val http4sVersion     = "1.0.0-M23"
val odinVersion       = "0.12.0"
val pureConfigVersion = "0.16.0"

evictionErrorLevel := Level.Warn

libraryDependencies ++= Seq(
  "com.github.pureconfig" %% "pureconfig"             % pureConfigVersion,
  "com.github.pureconfig" %% "pureconfig-cats-effect" % pureConfigVersion,
  "com.github.valskalla"  %% "odin-core"              % odinVersion,
  "com.github.valskalla"  %% "odin-slf4j"             % odinVersion,
  "io.circe"              %% "circe-generic"          % circeVersion,
  "org.http4s"            %% "http4s-circe"           % http4sVersion,
  "org.http4s"            %% "http4s-dsl"             % http4sVersion,
  "org.http4s"            %% "http4s-blaze-server"    % http4sVersion,
  "org.http4s"            %% "http4s-blaze-client"    % http4sVersion,
  "org.typelevel"         %% "cats-effect"            % "3.1.1",
)
