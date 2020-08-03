package org.simple.db

import java.time.LocalDateTime

import slick.jdbc.JdbcBackend.Database
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class Tables(val db: Database)(implicit val profile: JdbcProfile) extends AutoCloseable {

  import profile.api._
  import org.simple.db.Models._

  val platforms: TableQuery[Platforms] = TableQuery[Platforms]
  val vehicles: TableQuery[Vehicles] = TableQuery[Vehicles]

  val allTables = Seq(platforms, vehicles)

  /** VEHICLES */
  class Vehicles(tag: Tag)(implicit val profile: JdbcProfile)
    extends Table[Vehicle](tag, "VEHICLES") with GenericTable[Vehicle] {

    val platformId = column[Int]("PLATFORM_ID")

    def platformFK = foreignKey("VEH_PLAT_FK", platformId, platforms)(
      _.id,
      onUpdate=ForeignKeyAction.Restrict,
      onDelete=ForeignKeyAction.Restrict
    )

    def * : ProvenShape[Vehicle] = (id, created, modified, name, platformId, notes).mapTo[Vehicle]
  }

  /** PLATFORMS */
  class Platforms(tag: Tag)(implicit val profile: JdbcProfile)
    extends Table[Platform](tag, "PLATFORMS") with GenericTable[Platform] {

    def * : ProvenShape[Platform] = (id, created, modified, name, notes).mapTo[Platform]
  }

  def createSchema(): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    val existing = db.run(sql"""show tables""".as[String])
    val createWhenNotExisting = existing.flatMap( names => {
      val createIfNotExist = allTables.filter( table =>
        !names.contains(table.baseTableRow.tableName.toLowerCase)
      ).map(_.schema.create)
      db.run(DBIO.sequence(createIfNotExist))
    })
    Await.result(
      createWhenNotExisting
        .flatMap(_ => db.run(basicPlatforms)), Duration.Inf)
  }

  private def basicPlatforms = {
    val now = LocalDateTime.now()
    platforms.exists.result.flatMap(p => {
      if (!p) platforms += Platform(0, now, now, "Team Unassigned", "")
      else DBIO.successful(p)
    })
  }

  override def close(): Unit = db.close()

}
