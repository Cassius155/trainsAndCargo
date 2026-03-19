import java.io.File
import java.io.FileNotFoundException
import java.util.LinkedList
import java.util.Queue

fun main() {
    var stations = getInput()
    var initStation = getStartStation(stations)
    while((stations as? Boolean) != null || initStation == -1){
        stations = getInput()
        initStation = getStartStation(stations)
    }
    stations = stations as Array<Station>

    val reachableStations = findReachableFromStart(stations, initStation)

    for(i in reachableStations){
        setTransportArea(stations, i)
    }

    for(station in stations){
        println(station.toString())
    }
}

fun findReachableFromStart(stations: Array<Station>, start: Int): MutableList<Int>{
    //Finds the stations that are reachable from the starting station using BFS and returns their indexes
    val reachable = mutableListOf<Int>(start)
    val queue: Queue<Int> = LinkedList<Int>()
    queue.add(start)
    while(queue.isNotEmpty()){
        val station = queue.poll()
        if(station != null){
            val neighbors = stations[station].outgoing
            for (neighbor in neighbors){
                if (!reachable.contains(neighbor)){
                    reachable.add(neighbor)
                    queue.add(neighbor)
                }
            }
        }
    }
    return reachable
}

fun setTransportArea(stations: Array<Station>, start: Int): Unit{
    //Runs BFS from the specified start station and updates the possible cargo types of each station with starting stations cargo type
    //Ends branch early if it encounters a station that unloads starting stations cargo type
    val reachable = mutableListOf<Int>()
    val queue: Queue<Int> = LinkedList<Int>()
    queue.addAll(stations[start].outgoing)
    while(queue.isNotEmpty()){
        val station = queue.poll()
        if(station != null ){
            stations[station].addPossibleCargoType(stations[start].loaded)
            if(stations[station].unloaded != stations[start].loaded){
                val neighbors = stations[station].outgoing
                for (neighbor in neighbors){
                    if (!reachable.contains(neighbor)){
                        reachable.add(neighbor)
                        queue.add(neighbor)
                    }
                }
            }
        }
    }
}

fun getStartStation(stations: Any): Int{
    //Finds the starting station and returns its index, or -1 if it does not exist
    val stationArray = stations as? Array<Station>
    if(stationArray != null){
        for(i in stationArray){
            if(i.startStation){
                return i.index-1
            }
        }
    } else {
        return -1
    }
    print("Starting station not specified")
    return -1
}

fun getInput(): Any {
    //Reads the input either from the console or from a specified text file
    println("Enter input (or text file name): ")
    val input: String = readln()
    val lines: MutableList<String> = mutableListOf()
    var stationNum = 0
    var trackNum = 0
    if(input.endsWith(".txt")){
        try {
            val inputFile = File(input).readLines()
            stationNum = inputFile[0].split(", ")[0].toInt()
            trackNum = inputFile[1].split(", ")[0].toInt()
            lines.addAll(inputFile)
            lines.removeAt(0)
        } catch (e: FileNotFoundException) {
            println("Specified file could not be found")
            return false
        } catch (e: Exception) {
            print("Input not in expected format")
            return false
        }
    } else {
        try {
            val firstLine = input.split(", ")
            check(firstLine.size == 2)
            stationNum = firstLine[0].toInt()
            trackNum = firstLine[1].toInt()
            for (i in 0 until stationNum+trackNum+1) {
                lines.add(readln())
            }
        } catch (e: Exception) {
            println("Input not in expected format")
            return false
        }
    }
    return processInput(stationNum, trackNum, lines)
}

fun processInput(stationNum: Int, trackNum: Int, input: MutableList<String>): Any{
    //Converts input into station objects
    val stationArray: Array<Station?> = arrayOfNulls(stationNum)
    try {
        for(i in 0 until stationNum){
            val nextLine = input[i].split(", ")
            val station: Station = Station(nextLine[0].toInt(), nextLine[1].toInt(), nextLine[2].toInt(), stationNum)
            stationArray[i] = station
        }
        for(i in stationNum until trackNum+stationNum){
            val nextLine = input[i].split(", ")
            val from = nextLine[0].toInt()-1
            val to = nextLine[1].toInt()-1
            stationArray[from]!!.addOutgoingConnection(to)
        }
        stationArray[input[trackNum+stationNum].toInt()-1]!!.startStation = true
    } catch (e: Exception) {
        println("Input not in expected format")
        return false
    }
    return stationArray
}

class Station(val index: Int, val unloaded: Int, val loaded: Int, val totalStations: Int) {
    //Class representing stations
    val outgoing  = mutableSetOf<Int>()

    val possibleCargoTypes = mutableSetOf<Int>()
    var startStation = false

    fun addOutgoingConnection(connection: Int): Boolean {
        if(connection >= totalStations || (connection < 0)) return false
        return outgoing.add(connection)
    }

    fun addPossibleCargoType(type: Int): Boolean {
        return possibleCargoTypes.add(type)
    }

    override fun toString(): String{
        var output = index.toString() + ": "
        for (i in possibleCargoTypes){
            output = output + i.toString() + ", "
        }
        return output
    }
}