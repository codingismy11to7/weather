package weather.services

import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.IO
import io.circe.Encoder
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.io._
import weather.services.WeatherService.WeatherType.WeatherType
import weather.util.OWMClient.{ClientMethods, WeatherResponse}
import weather.util.createLogger

object WeatherService {
  private val logger = createLogger()

  private object LatParamMatcher extends ValidatingQueryParamDecoderMatcher[Double]("lat")
  private object LngParamMatcher extends ValidatingQueryParamDecoderMatcher[Double]("lng")

  object WeatherType extends Enumeration {
    type WeatherType = Value
    val hot, cold, moderate              = Value
    implicit val encoder: Encoder[Value] = Encoder.encodeString.contramap(_.toString)
  }

  final private case class WeatherAlert(alertName: String, alertString: String)

  final private case class WeatherResult(
      weatherType: WeatherType,
      condition: String = "",
      conditionStr: String = "",
      alerts: Seq[WeatherAlert] = Nil,
  )

  private def tempToType(temp: Double) =
    if (temp < 60) WeatherType.cold
    else if (temp <= 75) WeatherType.moderate
    else WeatherType.hot

  private def responseToResult(response: WeatherResponse) = {
    val base = WeatherResult(tempToType(response.main.temp))
    val withCondition =
      response.weather.headOption.map(w => base.copy(condition = w.main, conditionStr = w.description)).getOrElse(base)
    withCondition.copy(alerts =
      response.weather.filterNot(_.main == "Clear").map(we => WeatherAlert(we.main, we.description))
    )
  }

  private def lookupWeather(
      client: ClientMethods
  )(lat: ValidatedNel[ParseFailure, Double], lng: ValidatedNel[ParseFailure, Double]) =
    (lat, lng) match {
      case (Valid(lat), Valid(lng)) =>
        for {
          _       <- logger.debug(s"looking up weather for $lat/$lng")
          owmresp <- client.weather(lat, lng)
          _       <- logger.debug(s"got response $owmresp")
          resp    <- Ok(responseToResult(owmresp).asJson)
        } yield resp

      case _ => BadRequest("Invalid latitude or longitude")
    }

  def apply(client: ClientMethods): HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "weather" :? LatParamMatcher(lat) +& LngParamMatcher(lng) =>
      for {
        resp <- lookupWeather(client)(lat, lng)
      } yield resp
  }

}
