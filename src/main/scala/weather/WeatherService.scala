package weather

import cats.effect._
import weather.config.ServiceConfig
import weather.util.createLogger

object WeatherService extends IOApp.Simple {
  private val logger = createLogger

  override def run: IO[Unit] = for {
    svcConfig <- ServiceConfig.load
    config = svcConfig.weather
    _ <- logger.info(s"Binding to ${config.host}:${config.port.p}")
    _ <- logger.debug(s"Using API key ${svcConfig.openweathermap.apiKey} for OWM requests")
  } yield {}

}
