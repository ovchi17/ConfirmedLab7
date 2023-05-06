import com.google.gson.Gson
import controllers.CollectionMainCommands
import moduleWithResults.ResultModule
import moduleWithResults.WorkWithResultModule
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import workCommandsList.*
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.nio.channels.Selector
import java.util.concurrent.Executors
import java.util.concurrent.ForkJoinPool

/**
 * Class ServerModule.
 *
 * @author OvchinnikovI17
 * @since 1.0.0
 */
class ServerModule {
    var socket = DatagramSocket(2043)
    val commandStarter = CommandStarter()
    val gson = Gson()
    val buffer = ByteArray(65535)
    val packet = DatagramPacket(buffer, buffer.size)
    val selector = Selector.open()
    val logger: Logger = LogManager.getLogger(ServerModule::class.java)
    val availableTokens = mutableMapOf<String, String>()
    val hashSHA = ShaBuilder()
    val workWithResultModule = WorkWithResultModule()
    val threadPool = Executors.newFixedThreadPool(10)
    val executor = Executors.newFixedThreadPool(5)
    var ct = 0


    /**
     * serverReceiver method. Receives args and command from client
     *
     */
    fun serverReceiver(){
        ct++
        socket.receive(packet)
        executor.execute {
            val worker: Runnable = WorkerThread(packet, ct)
            threadPool.execute(worker)
        }
    }

    /**
     * serverSender method. Send to client ResultModule
     *
     * @param result arguments
     */
    fun serverSender(result: ResultModule){
        ForkJoinPool.commonPool().execute{
            val json = gson.toJson(result)
            val changedToBytes = json.toByteArray()
            val packetToSend = DatagramPacket(changedToBytes, changedToBytes.size, packet.address, packet.port)
            println(result.msgToPrint + "alert!!")
            logger.info("Отправлен результат")
            socket.send(packetToSend)
        }
    }

}

class CommandStarter(): KoinComponent{

    val workWithCollection: CollectionMainCommands by inject()

    val info: Info = Info()
    val show: Show = Show()
    val add: Add = Add()
    val removeById: RemoveById = RemoveById()
    val clear: Clear = Clear()
    val save: Save = Save()
    val load: Load = Load()
    val updateCommand: UpdateCommand = UpdateCommand()
    val updateId: UpdateId = UpdateId()
    val exitServer: ExitServer = ExitServer()
    val removeFirst: RemoveFirst = RemoveFirst()
    val addIfMax: AddIfMax = AddIfMax()
    val history: History = History()
    val removeAllByDistance: RemoveAllByDistance = RemoveAllByDistance()
    val averageOfDistance: AverageOfDistance = AverageOfDistance()
    val filterLessThanDistance: FilterLessThanDistance = FilterLessThanDistance()
    val switch: Switch = Switch()
    val token: Token = Token()

    fun mp(command: String): Command? {

        val COMMANDS = mapOf(
            "info" to info,
            "show" to show,
            "add" to add,
            "remove_by_id" to removeById,
            "clear" to clear,
            "save" to save,
            "load" to load,
            "update_id" to updateId,
            "update_command" to updateCommand,
            "exit_server" to exitServer,
            "remove_first" to removeFirst,
            "add_if_max" to addIfMax,
            "history" to history,
            "remove_all_by_distance" to removeAllByDistance,
            "average_of_distance" to averageOfDistance,
            "filter_less_than_distance" to filterLessThanDistance,
            "switch" to switch,
            "token" to token)

        if (command in COMMANDS) {
            workWithCollection.historyUpdate(command)
            return COMMANDS[command]
        }else{
            return null
        }
    }
}