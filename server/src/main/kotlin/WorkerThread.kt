import com.google.gson.Gson
import moduleWithResults.ResultModule
import moduleWithResults.Status
import moduleWithResults.WorkWithResultModule
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.net.DatagramPacket

class WorkerThread(packetGet: DatagramPacket): Runnable, KoinComponent {

    val packet = packetGet
    val gson = Gson()
    val commandStarter = CommandStarter()
    val hashSHA = ShaBuilder()
    val serverModule: ServerModule by inject()
    val workWithResultModule: WorkWithResultModule by inject()
    var ct = 0


    override fun run() {
        println("START | thread $ct")
        processingCommand()
        println("END | thread $ct")
    }

    private fun processingCommand(){
        try {
            //Thread.sleep(500)
            ct++
            val json = String(packet.data, 0, packet.length)
            val getInfo = gson.fromJson(json, ResultModule::class.java)
            if (getInfo.token == "Update"){
                commandStarter.mp(getInfo.commandName)?.execute(getInfo.args, "noNeed")
            } else if (hashSHA.toSha(getInfo.token) in serverModule.availableTokens.keys){
                if (getInfo.commandName != "sessionIsOver"){
                    println(getInfo)
                    serverModule.availableTokens.get(hashSHA.toSha(getInfo.token))
                        ?.let { commandStarter.mp(getInfo.commandName)?.execute(getInfo.args, it) }
                }else{
                    serverModule.availableTokens.remove(hashSHA.toSha(getInfo.token))
                    workWithResultModule.setStatus(Status.SUCCESS)
                    serverModule.serverSender(workWithResultModule.getResultModule())
                    workWithResultModule.clear()
                }
            }else{
                workWithResultModule.setStatus(Status.ERROR)
                workWithResultModule.setError("noToken")
                serverModule.serverSender(workWithResultModule.getResultModule())
                workWithResultModule.clear()
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}