package weather.util

import cats.effect._
import io.circe.generic.auto._
import org.http4s.{EntityDecoder, Uri}
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.circe._
import org.http4s.client._
import org.http4s.implicits._
import weather.config.ServiceConfig
import weather.util.OWMClient.OneCallResponse.{Alert, CurrentWeather}
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

  object OneCallResponse {
    final case class Alert(event: String, description: String)
    final case class CurrentWeather(temp: Double, feels_like: Double, weather: Seq[WeatherEntry])
    implicit val decoder: EntityDecoder[IO, OneCallResponse] = jsonOf[IO, OneCallResponse]
  }

  final case class OneCallResponse(lat: Double, lon: Double, current: CurrentWeather, alerts: Option[Seq[Alert]])

  final case class ClientMethods(client: Client[IO], apiKey: String) {
    private val baseUrl    = uri"https://api.openweathermap.org" / "data" / "2.5"
    private val weatherUrl = baseUrl / "weather"
    private val oneCallUrl = baseUrl / "onecall"

    private def withParams(uri: Uri, lat: Double, lng: Double) =
      uri withQueryParam ("appid", apiKey) withQueryParam ("lat", lat) withQueryParam ("lon", lng) withQueryParam ("units", "imperial")

    def weather(lat: Double, lng: Double): IO[WeatherResponse] = client.expect[WeatherResponse](
      withParams(weatherUrl, lat, lng)
    )

    def onecall(lat: Double, lng: Double): IO[OneCallResponse] = client.expect[OneCallResponse](
      withParams(oneCallUrl, lat, lng) withQueryParam ("exclude", "minutely,hourly,daily")
    )

  }

  def apply(cfg: ServiceConfig): Resource[IO, ClientMethods] = for {
    client <- BlazeClientBuilder[IO](
      scala.concurrent.ExecutionContext.global
    ).resource.map(ClientMethods(_, cfg.openweathermap.apiKey))
    _ <- Resource eval logger.debug(s"Using API key ${cfg.openweathermap.apiKey} for OWM requests")
  } yield client

}
