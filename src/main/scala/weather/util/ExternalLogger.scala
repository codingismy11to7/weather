package weather.util

import cats.effect.{IO, Sync}
import cats.effect.std.Dispatcher
import cats.effect.unsafe.implicits.global
import io.odin.{Level, Logger}
import io.odin.slf4j.OdinLoggerBinder

class ExternalLogger extends OdinLoggerBinder[IO] {
  implicit val F: Sync[IO]                = IO.asyncForIO
  implicit val dispatcher: Dispatcher[IO] = Dispatcher[IO].allocated.unsafeRunSync()._1

  val loggers: PartialFunction[String, Logger[IO]] = {
    case c if c startsWith "org.http4s.blaze" => createLogger(Level.Info)
    case _                                    => createLogger()
  }

}
