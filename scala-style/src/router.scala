/**
 * Created with IntelliJ IDEA.
 * User: Stig
 * Date: 10/17/12
 * Time: 7:00 PM
 * To change this template use File | Settings | File Templates.
 */

import java.io._
import java.net._

object router {

  def main(args: List[String]) = {
    require(args < 6, "Must have 5 or less arguments")

    val servSock = new ServerSocket(2222)
    val clientSock = servSock.accept()
    var start = Int
    val ipList = buildRoutingTable(args, List[(String, String)])

    while(true){
      println("Waiting to receive message...")
      buildData(clientSock, ipList)
    }
  }

  def buildRoutingTable(args: List[String], list: List[(String, String)]): List[(String, String)] = {
    if (args.isEmpty) list
    else {list ::: (args.head, "2222"); buildRoutingTable(args.tail, list)}
  }

  def buildData(clientSock: Socket, ipList: List[(String, String)]) {
    def receiveMessage(): List[String] = {
      val receiveData = Array[Byte]
      val inFromClient = DataInputStream(clientSock.getInputStream)
      inFromClient.read(receiveData)
      var message = new String(receiveData)
      message = message.trim
      val messages: List[String] = message.split(":");
    }

    val start = System.currentTimeMillis()
    //TODO: Fix this, probably need to be a recursive function
    for(i <- receiveMessage()){
      if (ipList(i, 0) == )
    }
  }
}
