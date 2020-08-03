package org.simple.server

import java.sql.Timestamp

import cats.effect.IO
import io.circe.generic.auto._
import io.circe.{Encoder, Json}
import org.http4s.{EntityDecoder, HttpRoutes}
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.simple.db.{Models, Queries, Tables}
import io.circe.generic.auto._
import io.circe.syntax._

class ManagementRoutes(queries: Queries) {

  implicit val timestampEncoder: Encoder[Timestamp] = Encoder[String].contramap(_.toString())

  def getTransportInformation: HttpRoutes[IO] = {
    val dsl = new Http4sDsl[IO]{}
    import dsl._
    implicit val decoder: EntityDecoder[IO, Seq[Models.Vehicle]] = jsonOf[IO, Seq[Models.Vehicle]]
    HttpRoutes.of[IO] {
      case GET -> Root / "vehicles" =>
        try {
          val answer = queries.getVehicles
          Ok(Json.obj(("vehicles", answer.map(_.asJson).asJson)))
        }
        catch {
          case e: Throwable =>
            println(e.getMessage)
            InternalServerError(s"Something went wrong:\n${e.getMessage}")
        }
      case post @ POST -> Root / "vehicles" =>
        for {
          newVehics <- post.as[Seq[Models.Vehicle]]
          _ <- IO { queries.addVehicles(newVehics) }
          resp <- Ok(Json.fromString("OK"))
        } yield {
          resp
        }
    }
  }

}