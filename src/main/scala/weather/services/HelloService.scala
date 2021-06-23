package weather.services

import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import weather.BuildInfo

object HelloService {

  def apply(): HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root => Ok(BuildInfo.toString)

    case GET -> Root / "hello" => Ok("hello")

    case GET -> Root / "hello" / name => Ok(s"hello $name")
  }

}
