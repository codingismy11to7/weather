name := "weather"
version := "0.1.0"
scalaVersion := "2.13.4"

scalacOptions ++= Seq(
  "-feature",
  "-Xfatal-warnings",
  "-Xlint:unused",
  "-deprecation",
)

scalafmtOnCompile := true

val http4sVersion     = "0.21.22"
val pureConfigVersion = "0.16.0"

evictionErrorLevel := Level.Warn

libraryDependencies ++= Seq(
  "com.github.pureconfig" %% "pureconfig"             % pureConfigVersion,
  "com.github.pureconfig" %% "pureconfig-cats-effect" % pureConfigVersion,
  "com.github.valskalla"  %% "odin-core"              % "0.12.0",
  "org.http4s"            %% "http4s-dsl"             % http4sVersion,
  "org.http4s"            %% "http4s-blaze-server"    % http4sVersion,
  "org.http4s"            %% "http4s-blaze-client"    % http4sVersion,
  "org.typelevel"         %% "cats-effect"            % "3.1.1",
)
