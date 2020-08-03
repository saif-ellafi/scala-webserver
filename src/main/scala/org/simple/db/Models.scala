package org.simple.db

import java.time.LocalDateTime

object Models {
  case class Platform(id: Int, created: LocalDateTime, modified: LocalDateTime, name: String, notes: String)
  case class Vehicle(id: Int, created: LocalDateTime, modified: LocalDateTime, name: String, platformId: Int, notes: String)
}
