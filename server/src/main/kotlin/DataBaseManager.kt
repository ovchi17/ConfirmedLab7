import dataSet.Coordinates
import org.koin.core.component.KoinComponent
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class DataBaseManager(){

    val user = "postgres"
    val pas = "admin"
    val url = "jdbc:postgresql://localhost:5433/studs"
    val connectionDB = connect()
    val adderRoute =
        connectionDB.prepareStatement(
            "insert into public.\"Route\" " +
                    "(id, name, \"creationDate\", location11, location12, location13, location21, location22, location23, distance, coordinates1, coordinates2, owner) " +
                    "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
        )
    val clearStatement = connectionDB.prepareStatement("delete from public.\"Route\";")
    val deleteRouteStatment = connectionDB.prepareStatement("delete from public.\"Route\" where(public.\"Route\".id = ?);")

    fun connect(): Connection {
        try {
            val connection = DriverManager.getConnection(url, user, pas)
            println("коннект настройка")
            return connection
        } catch (e: SQLException) {
            throw e
        }
    }

    fun addRoute(id: Long, name: String, creationDate: LocalDate, location11: Long, location12: Long, location13: Int, location21: Long, location22: Long, location23: Int, distance: Long, coordinates1: Long, coordinates2: Long, owner: String) {
        try{
            val date = Date.from(creationDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
            val sqlDate = java.sql.Date(date.time)
            adderRoute.setLong(1, id)
            adderRoute.setString(2, name)
            adderRoute.setDate(3, sqlDate)
            adderRoute.setLong(4, location11)
            adderRoute.setLong(5, location12)
            adderRoute.setInt(6, location13)
            adderRoute.setLong(7, location21)
            adderRoute.setLong(8, location22)
            adderRoute.setInt(9, location23)
            adderRoute.setLong(10, distance)
            adderRoute.setLong(11, coordinates1)
            adderRoute.setLong(12, coordinates2)
            adderRoute.setString(13, owner)
            val resultBuild = adderRoute.executeUpdate()
            println(resultBuild)
            if (resultBuild == 0){
                throw SQLException()
            }
        }catch (e: SQLException) {
            println(e.message)
            println("Smth wrong in addRoute")
        }
    }

    fun clearRoute(){
        try{
            clearStatement.executeUpdate()
        }catch (e: SQLException) {
            println(e.message)
            println("Smth wrong in clearRoute")
        }
    }

    fun deleteRoute(id: Long){
        try{
            deleteRouteStatment.setLong(1, id)
            deleteRouteStatment.executeUpdate()
        }catch (e: SQLException) {
            println(e.message)
            println("Smth wrong in deleteRoute")
        }
    }

    fun updateRoute(id: Long, name: String, creationDate: LocalDate, location11: Long, location12: Long, location13: Int, location21: Long, location22: Long, location23: Int, distance: Long, coordinates1: Long, coordinates2: Long, owner: String){
        deleteRoute(id)
        addRoute(id, name, creationDate, location11, location12, location13, location21, location22, location23, distance, coordinates1, coordinates2, owner)
    }



}