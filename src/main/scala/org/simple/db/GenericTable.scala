package org.simple.db

import java.time.LocalDateTime

import slick.jdbc.JdbcProfile

trait GenericTable[T] { this: JdbcProfile#Table[T] =>

  val profile: JdbcProfile
  import profile.api._

  def id: Rep[Int] = column[Int]("ID", O.PrimaryKey, O.AutoInc)

  def name: Rep[String] = column[String]("NAME")

  def notes: Rep[String] = column[String]("NOTES")

  def created: Rep[LocalDateTime] = column[LocalDateTime]("CREATED")

  def modified: Rep[LocalDateTime] = column[LocalDateTime]("MODIFIED")

}