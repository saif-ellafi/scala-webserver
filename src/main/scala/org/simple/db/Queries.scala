package org.simple.db

import slick.jdbc.JdbcProfile
import slick.jdbc.meta.MTable

import scala.collection.immutable
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class Queries(tables: Tables)(implicit profile: JdbcProfile) {

  import profile.api._

  def getVehicles: Seq[Models.Vehicle] = {
    val vehicleList = tables.vehicles

    val task = tables.db.run(vehicleList.result)

    Await.result(task, Duration.Inf)
  }

  def addVehicles(newVehicles: Seq[Models.Vehicle]): Unit = {
    println(s"adding: ${newVehicles.head}")
    tables.db.run(tables.vehicles ++= newVehicles)
  }

  def getTables: immutable.Seq[MTable] = {
    val task = tables.db.run(MTable.getTables)
    Await.result(task, Duration.Inf)
  }

}
