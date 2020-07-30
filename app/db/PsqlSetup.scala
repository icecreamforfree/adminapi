package db
import play.api.db._

class PsqlSetup {
    val dbUrl = "jdbc:postgresql://localhost:5432/robotcone?user=icebear&password=1234"
    val db = Databases("org.postgresql.Driver",dbUrl,"Psql Robotcone database")
}