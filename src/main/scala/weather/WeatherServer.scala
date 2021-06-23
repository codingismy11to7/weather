package weather

import cats.effect._
import cats.implicits._
import org.http4s.blaze.server._
import org.http4s.implicits._
import org.http4s.server.Router
import weather.config.{ServiceConfig, WeatherServiceConfig}
import weather.services.{HelloService, WeatherService}
import weather.util.OWMClient.ClientMethods
import weather.util.{createLogger, OWMClient}

object WeatherServer extends IOApp {

  private val logger = createLogger()

  private def httpserver(cfg: WeatherServiceConfig, client: ClientMethods) =
    BlazeServerBuilder[IO](scala.concurrent.ExecutionContext.global)
      .bindHttp(cfg.port.p, cfg.host)
      .withHttpApp(Router("/" -> HelloService(), "/api" -> (HelloService() <+> WeatherService(client))).orNotFound)
      .serve
      .compile
      .drain

  override def run(args: List[String]): IO[ExitCode] = for {
    _         <- logger.info(s"$BuildInfo")
    svcConfig <- ServiceConfig.load
    config = svcConfig.weather
    _ <- OWMClient(svcConfig).use { client =>
      for {
        _ <- logger.info(s"Binding to ${config.host}:${config.port.p}")
        _ <- httpserver(config, client)
      } yield {}
    }
  } yield ExitCode.Success

}
