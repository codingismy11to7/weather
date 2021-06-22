package weather

import cats.effect.IO
import io.odin.{consoleLogger, Logger}
import io.odin.formatter.Formatter

package object util {
  @inline def createLogger: Logger[IO] = consoleLogger[IO](Formatter.colorful)
}
