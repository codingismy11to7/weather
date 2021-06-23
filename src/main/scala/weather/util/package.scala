package weather

import cats.effect.IO
import io.odin.{consoleLogger, Level, Logger}
import io.odin.formatter.Formatter

package object util {
  @inline def createLogger(minLevel: Level = Level.Trace): Logger[IO] = consoleLogger[IO](Formatter.colorful, minLevel)
}
