import controllers.CollectionMainCommands
import controllers.WorkWithCollection
import dataSet.Coordinates
import dataSet.Location
import dataSet.Route
import di.serverModule
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent.inject
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class DataBaseManager(): KoinComponent{

    val user = "postgres"
    val pas = "admin"
    val url = "jdbc:postgresql://localhost:5433/studs"
    val workWithCollection: CollectionMainCommands by inject()
    val serverModule: ServerModule by inject()
    val connectionDB = connect()
    val adderRoute =
        connectionDB.prepareStatement(
            "insert into public.\"Route\" " +
                    "(id, name, \"creationDate\", location11, location12, location13, location21, location22, location23, distance, coordinates1, coordinates2, owner) " +
                    "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
        )
    val clearStatement = connectionDB.prepareStatement("delete from public.\"Route\";")
    val deleteRouteStatment = connectionDB.prepareStatement("delete from public.\"Route\" where(public.\"Route\".id = ?);")
    val allRoute = connectionDB.prepareStatement("SELECT * FROM public.\"Route\";")
    val allLogin = connectionDB.prepareStatement("SELECT * FROM public.\"Login\"")
    val deleteLogin = connectionDB.prepareStatement("delete from public.\"Login\"")
    val insertLogin =
        connectionDB.prepareStatement(
            "insert into public.\"Login\" " +
                    "(login, token, valid, status) " +
                    "values(?, ?, ?, ?);"
        )

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

    fun uploadAllRoutes(){
        try{
            val allRoutes= allRoute.executeQuery()
            while (allRoutes.next()){
                val coordinates = Coordinates(allRoutes.getLong("coordinates1"), allRoutes.getLong("coordinates2"))
                val to = Location(allRoutes.getLong("location11"), allRoutes.getLong("location12"), allRoutes.getInt("location13"))
                val from = Location(allRoutes.getLong("location21"), allRoutes.getLong("location22"), allRoutes.getInt("location23"))

                val routeToAdd: Route = Route(
                    allRoutes.getLong("id"),
                    name = allRoutes.getString("name"),
                    coordinates = coordinates,
                    creationDate = allRoutes.getDate("creationDate").toLocalDate(),
                    from = from,
                    to = to,
                    distance = allRoutes.getLong("distance"),
                    owner = allRoutes.getString("owner")
                )

                workWithCollection.addElementToCollection(routeToAdd)

            }
        }catch (e: SQLException) {
            println(e.message)
            println("Smth wrong in uploadAllRoutes")
        }
    }

    fun uploadAllLogins(){
        try{
            val allLogins= allLogin.executeQuery()
            while (allLogins.next()){
                val login = allLogins.getString("login")
                val token = allLogins.getString("token")
                val valid = allLogins.getBoolean("valid")
                val status = allLogins.getString("status")
                serverModule.availableTokens[token] = login
                serverModule.tokenToValid[token] = valid
                serverModule.tokenToStatus[token] = status
            }
        }catch (e: SQLException) {
            println(e.message)
            println("Smth wrong in uploadAllLogins")
        }
    }

    fun clearLogin(){
        try{
            deleteLogin.executeUpdate()
        }catch (e: SQLException) {
            println(e.message)
            println("Smth wrong in clearLogin")
        }
    }

    fun loadAllLogins(tokens: Set<String>){
        clearLogin()
        for (tkn in tokens){
            insertLogin.setString(1, serverModule.availableTokens[tkn])
            insertLogin.setString(2, tkn)
            serverModule.tokenToValid[tkn]?.let { insertLogin.setBoolean(3, it) }
            insertLogin.setString(4, serverModule.tokenToStatus[tkn])
            insertLogin.execute()
        }
    }

}