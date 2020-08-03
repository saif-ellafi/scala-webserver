package org.simple.runtime

import java.time.LocalDateTime

import cats.effect.IO
import io.circe.Json
import org.http4s._
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.implicits._
import org.simple.db.{Queries, Tables}
import org.simple.server.ManagementRoutes
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import slick.jdbc.H2Profile
import slick.jdbc.JdbcBackend.Database

class ManagementServerTestSpec extends AnyFlatSpec with BeforeAndAfterAll {

  val db = Database.forConfig("h2mem")
  implicit val profile: H2Profile.type = H2Profile

  val tables = new Tables(db)
  val queries = new Queries(tables)

  import org.simple.db.Models._

  implicit val decoder: EntityDecoder[IO, Json] = jsonOf[IO, Json]

  override def beforeAll() {
    /** Do not remove, this ensures unique local context */
    tables.createSchema()
    def insertVehicles(): Unit =
      queries.addVehicles(Seq(
        Vehicle(1, LocalDateTime.parse("2020-07-23T17:27:16.268"), LocalDateTime.parse("2020-07-23T17:27:16.268"), "Numion-RX1", 1, "")
      ))
    insertVehicles()
  }

  // Return true if match succeeds; otherwise false
  def check[A](actual:        IO[Response[IO]],
               expectedStatus: Status,
               expectedBody:   Option[A])(
                implicit ev: EntityDecoder[IO, A]
              ): Boolean =  {
    val actualResp         = actual.unsafeRunSync
    val statusCheck        = actualResp.status == expectedStatus
    val bodyCheck          = expectedBody.fold[Boolean](
      actualResp.body.compile.toVector.unsafeRunSync.isEmpty)( // Verify Response's body is empty.
      expected => {
        val jsonResponse = actualResp.as[A].unsafeRunSync
        println(s"Expected is:\n${expected}")
        println(s"Got response:\n${jsonResponse}")
        jsonResponse == expected
      }
    )
    statusCheck && bodyCheck
  }

  it should "Pull the list of vehicles" in {

    import io.circe.generic.auto._
    import io.circe.syntax._

    val response: IO[Response[IO]] = new ManagementRoutes(queries).getTransportInformation.orNotFound.run(
      Request(method = Method.GET, uri = uri"/vehicles" )
    )

    val vehicleResp = Json.obj(
      (
        "vehicles",
        Seq(Vehicle(1, LocalDateTime.parse("2020-07-23T17:27:16.268"), LocalDateTime.parse("2020-07-23T17:27:16.268"), "Numion-RX1", 1, "")).asJson
      )
    )

    assert(check[Json](response, Status.Ok, Some(vehicleResp)))
  }

  it should "Push a new vehicle" in {
    import io.circe.generic.auto._
    import io.circe.syntax._

    implicit val encoder = jsonEncoderOf[IO, Json]

    val response: IO[Response[IO]] = new ManagementRoutes(queries).getTransportInformation.orNotFound.run(
      Request(
        method = Method.POST,
        uri = uri"/vehicles",
        body = encoder.toEntity(Seq(Vehicle(0, LocalDateTime.parse("2020-07-23T17:27:16.999"), LocalDateTime.parse("2020-07-23T17:27:16.999"), "Feansa-ZL1", 1, "")).asJson).body
      )
    )

    assert(check[Json](response, Status.Ok, Some(Json.fromString("OK"))))

    val vehicles = queries.getVehicles
    assert(vehicles.length == 2)
  }

}