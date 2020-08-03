package org.simple.runtime

import cats.effect.{ExitCode, IO, IOApp}
import org.simple.db.{Queries, Tables}
import org.simple.server.{ManagementRoutes, ManagementServer}
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.{JdbcProfile, MySQLProfile}

object Main extends IOApp {

  implicit val profile = MySQLProfile
  val db = Database.forConfig("mysql")

  val tables = new Tables(db)
  val queries = new Queries(tables)
  tables.createSchema()

  def run(args: List[String]): IO[ExitCode] =
    ManagementServer.stream(new ManagementRoutes(queries)).compile.drain.as(ExitCode.Success)
}