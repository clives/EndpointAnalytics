package service

import com.zaxxer.hikari.{ HikariConfig, HikariDataSource }
import model.db.DAO
import slick.jdbc.{ JdbcProfile }
import slick.jdbc.JdbcBackend._
import com.typesafe.config.Config

class DatabaseService(dbConfig: Config, val driver: JdbcProfile) {

  private val hikariConfig = new HikariConfig()
  hikariConfig.setJdbcUrl(dbConfig.getString("url"))
  hikariConfig.setUsername(dbConfig.getString("user"))
  hikariConfig.setPassword(dbConfig.getString("password"))
  hikariConfig.setMaximumPoolSize(dbConfig.getInt("maxPoolSize"))

  val dataSource = new HikariDataSource(hikariConfig)

  private val flywayService = new FlywayService(dataSource)
  println("Migrating database..")
  flywayService.migrateDatabaseSchema()

  dataSource.getDataSource
  val db = Database.forDataSource(dataSource)

  val daoType = DAO(driver)
  db.createSession()
}

class DatabaseServicePostgress(dbConfig: Config) extends DatabaseService(dbConfig, slick.jdbc.PostgresProfile)

class DatabaseServiceMysql(dbConfig: Config) extends DatabaseService(dbConfig, slick.jdbc.MySQLProfile)