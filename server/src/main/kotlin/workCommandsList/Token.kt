package workCommandsList

import ShaBuilder
import moduleWithResults.ResultModule
import moduleWithResults.Status
import java.security.MessageDigest

class Token: Command() {

    val hashSHA = ShaBuilder()

    override fun execute(getArgs: MutableList<Any>, login:String){

        val lognpass = (getArgs[0].toString()).split(":")
        val resultPas =  hashSHA.toSha(lognpass[1])
        val resultLog = hashSHA.toSha(lognpass[0])
        val middleResult = resultPas + resultLog.take(10)
        val middle = hashSHA.toSha(middleResult)
        val chunks = middle.chunked(4)
        val token:String = chunks[0] + "-" + chunks[1] + "-" + chunks[2] + "-" + chunks[3]
        println(token)

        workWithResultModule.setStatus(Status.TOKEN)
        workWithResultModule.setToken(token)
        serverModule.availableTokens[hashSHA.toSha(token)] = resultLog
        serverModule.serverSender(workWithResultModule.getResultModule())
        workWithResultModule.clear()
    }
}