package commandsHelpers

import ClientModule
import moduleWithResults.Status
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent.inject
import usersView.AnswerToUser
import usersView.ConsoleWriter

class GetToken: KoinComponent {

    val clientModule: ClientModule by inject()
    val answerToUser: AnswerToUser = AnswerToUser()

    fun loginAndGetToken(login: String, pass: String){
        val logPass = "$login:$pass"
        val sendList = mutableListOf<Any>()
        sendList.add(logPass)
        clientModule.sender("token", sendList, "Update")
        val resultAnswer = clientModule.receiver()
        if (resultAnswer.status == Status.TOKEN){
            answerToUser.writeToConsoleLn("Успешно! Ваш токен: ${resultAnswer.token}")
        }
    }
}