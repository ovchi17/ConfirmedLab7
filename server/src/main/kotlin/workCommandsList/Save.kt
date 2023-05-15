package workCommandsList

import dataSet.Route
import dataSet.RouteComporator
import java.util.PriorityQueue

/**
 * Class Save. Save to file in JSON format.
 *
 * @author jutsoNNN
 * @since 1.0.0
 */
class Save: Command() {

    /**
     * execute method. Save collection to file
     *
     * @param getArgs arguments
     */
    override fun execute(getArgs: MutableList<Any>, login:String, uniqueToken:String){
        val pathToFile: String = System.getenv("DATAOFCOLLECTION")
        val collection = PriorityQueue<Route>(RouteComporator())
        collection.addAll(workWithCollection.getCollection())
        val list = workWithCollection.collectionToList()
        val jsonString = serializer.serialize(list)
        workWithFile.writeToFile(collection, pathToFile, jsonString)

        workWithResultModule.setMessages("saved")
        workWithResultModule.setUniqueKey(uniqueToken)

        serverModule.queueExeSen.put(workWithResultModule.getResultModule())
        workWithResultModule.clear()
     }
}