package weather.config

import cats.effect.IO
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import pureconfig.module.catseffect.syntax._

case class Port(p: Int) extends AnyVal

case class WeatherServiceConfig(host: String, port: Port)

case class OpenWeatherMapConfig(apiKey: String)

case class ServiceConfig(weather: WeatherServiceConfig, openweathermap: OpenWeatherMapConfig)

object ServiceConfig {
  def load: IO[ServiceConfig] = ConfigSource.default.loadF[IO, ServiceConfig]()
}
