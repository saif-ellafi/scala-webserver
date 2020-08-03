package org.simple.db

import java.time.LocalDateTime

import org.scalatest._
import org.scalatest.flatspec.AnyFlatSpec
import slick.jdbc.H2Profile
import slick.jdbc.JdbcBackend.Database


class ManagementDBSuite extends AnyFlatSpec with BeforeAndAfterAll {

  val db = Database.forConfig("h2mem")
  implicit val profile: H2Profile.type = H2Profile

  val tables = new Tables(db)
  val queries = new Queries(tables)

  import org.simple.db.Models._

  override def beforeAll() {
    /** Do not remove, this ensures unique local context */
    tables.createSchema()
    def insertVehicles(): Unit =
      queries.addVehicles(Seq(
        Vehicle(1, LocalDateTime.parse("2020-07-23T17:27:16.268"), LocalDateTime.parse("2020-07-23T17:27:16.268"), "Numion-RX1", 1, "")
      ))
    insertVehicles()
  }

  it should "Create the Schema" in {
    val tables = queries.getTables

    assert(tables.size == 2)
    assert(tables.count(_.name.name.equalsIgnoreCase("vehicles")) == 1)
    assert(tables.count(_.name.name.equalsIgnoreCase("platforms")) == 1)
  }

  "Vehicle names and IDs" should "be inserted successfully" in {
    assert(queries.getVehicles.length == 1)
    val result = queries.getVehicles
    println(result)
  }

  override def afterAll() { tables.close() }
}
