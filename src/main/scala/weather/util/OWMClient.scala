package weather.util

import cats.effect._
import io.circe.generic.auto._
import org.http4s.EntityDecoder
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.circe._
import org.http4s.client._
import org.http4s.implicits._
import weather.config.ServiceConfig
import weather.util.OWMClient.WeatherResponse.{WeatherCoords, WeatherEntry, WeatherMain}

object OWMClient {
  private val logger = createLogger()

  object WeatherResponse {
    final case class WeatherEntry(main: String, description: String)
    final case class WeatherCoords(lon: Double, lat: Double)
    final case class WeatherMain(temp: Double, feels_like: Double)
    implicit val decoder: EntityDecoder[IO, WeatherResponse] = jsonOf[IO, WeatherResponse]
  }

  final case class WeatherResponse(coord: WeatherCoords, weather: Seq[WeatherEntry], main: WeatherMain)

  final case class ClientMethods(client: Client[IO], apiKey: String) {
    private val baseUrl    = uri"https://api.openweathermap.org"
    private val weatherUrl = baseUrl / "data" / "2.5" / "weather"

    def weather(lat: Double, lng: Double): IO[WeatherResponse] = client.expect[WeatherResponse](
      weatherUrl
        withQueryParam ("appid", apiKey)
        withQueryParam ("lat", lat)
        withQueryParam ("lon", lng)
        withQueryParam ("units", "imperial")
    )

  }

  def apply(cfg: ServiceConfig): Resource[IO, ClientMethods] = for {
    client <- BlazeClientBuilder[IO](
      scala.concurrent.ExecutionContext.global
    ).resource.map(ClientMethods(_, cfg.openweathermap.apiKey))
    _ <- Resource eval logger.debug(s"Using API key ${cfg.openweathermap.apiKey} for OWM requests")
  } yield client

}
