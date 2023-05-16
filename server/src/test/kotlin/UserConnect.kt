import dataSet.Coordinates
import dataSet.Location
import org.junit.*
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import java.sql.DriverManager
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import java.sql.SQLException
import java.time.LocalDate
import kotlin.test.assertEquals as assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class UserConnect {

    private var userRepository: DataBaseManager = DataBaseManager()

    val url = "jdbc:postgresql://localhost:5433/testdb"
    val user = "postgres"
    val pas = "admin"

//    companion object {
//        val url = "jdbc:postgresql://localhost:5433/testdb"
//        val user = "postgres"
//        val pas = "admin"
//        @BeforeAll
//        @JvmStatic
//        fun setUp() {
//            DriverManager.getConnection("jdbc:postgresql://localhost:5433/testdb", user, pas).use { conn ->
//                conn.createStatement().use { stmt ->
//                    stmt.execute("SELECT * FROM public.\"Route\"")
//                }
//            }
//            try {
//                val userRepository = DriverManager.getConnection(url, user, pas)
//            } catch (e: SQLException) {
//                throw e
//            }
//        }
//    }
    @BeforeAll
    fun setUp() {
        DriverManager.getConnection("jdbc:postgresql://localhost:5433/testdb", user, pas).use { conn ->
            conn.createStatement().use { stmt ->
                stmt.execute("SELECT * FROM public.\"Route\"")
            }
        }
        try {
            val userRepository = DriverManager.getConnection(url, user, pas)
        } catch (e: SQLException) {
            throw e
        }
    }

    @AfterAll
    fun tearDown() {
        DriverManager.getConnection("jdbc:postgresql://localhost:5433/testdb", user, pas).use { conn ->
            conn.createStatement().use { stmt ->
                stmt.execute("DELETE FROM testdb")
            }
        }
    }

    @Test
    fun testCreateRoute() {
        var id:Long = 1
        var name: String = "3"
        var creationDate: LocalDate = LocalDate.now()
        var from: Location = Location(3, 3,3)
        var to: Location = Location(3, 3,3)
        var distance: Long = 3
        var coordinates: Coordinates = Coordinates(3,3)
        var owner: String = "testuser"
        var saved: Boolean = true
        val route = dataSet.Route(id,name,creationDate,from, to,distance, coordinates,owner,saved)
        userRepository.addRoute(id,name,creationDate,3, 3,3, 3, 3,3,distance, 3,3,owner,saved)

//        assertEquals(user, userRepository)
//        val retrievedUser = userRepository.getUserById(1L)
//        Assert.assertEquals(user, retrievedUser)

    }

    @Test
    fun testSaveRoute() {
//
    }

    @Test
    fun testConnectionToServer() {
//
    }

    @Test
    fun testSavingToUser() {
//
    }

    @Test
    fun testTokenSystem() {

    }
}