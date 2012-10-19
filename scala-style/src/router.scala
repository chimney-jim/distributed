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
    val ipList = buildRoutingTable(args, List[(String, String)])
    var timesRun = 0

    while(true){
      println("This program has been consecuatively run " + timesRun)
      println("Waiting to receive message...")
      val messageAndIp = buildData(clientSock, ipList)
      val routingTableLookupTime = messageAndIp(2)
      val toServer = new Socket(messageAndIp(1)._1, messageAndIp(1)._2)
      sendMessage(messages(0).getBytes, toServer)
      println("Message sent...\nTransporting data...")

      transportData(clientSocket, toServer)

      println("Data has been transported")

      servSock.close; clientSock.close;

      timesRun++
      var avgLookup = routingTableLookupTime/timesRun
      println("Current routing table lookup time " + routingTableLookupTime + "ms")
      println("Average routing table lookup time " + avgLookup + "ms")
    }
  }

  def buildRoutingTable(args: List[String], list: List[(String, String)]): List[(String, String)] = {
    if (args.isEmpty) list
    else buildRoutingTable(args.tail, list ::: (args.head, "2222"))
  }

  def buildData(clientSock: Socket, ipList: List[(String, String)]) {
    def receiveMessage(): List[String] = {
      val receiveData = Array[Byte]
      val inFromClient = DataInputStream(clientSock.getInputStream)
      inFromClient.read(receiveData)
      var message = new String(receiveData)
      message = message.trim
      val messages: List[String] = message.split(":")
    }

    def searchIpList(serverIP: String, ipList: List[(String, String)]): (String, String) = {
      if (serverIP == ipList.head._1) ipList.head
      else if (ipList.isEmpty)
        throw sys.error("There is no server on the network that matches your request")
      else searchIpList(serverIP, ipList.tail)
    }

    val messages = receiveMessage()
    val start = System.currentTimeMillis()
    val ipToSendTo = searchIpList(messages(2), ipList)
    val elapsedTime = System.currentTimeMillis()-start
    List(messages(0), ipToSendTo, elapsedTime)
  }

  def sendMessage(sendData: Array[Byte], toServer: Socket) = {
    val out = new DataOutputStream(toServer.getOutputStream)
    out.write(sendData)
  }

  def transportData(clientSock: Socket, fromServer: Socket) = {
    val in = fromServer.getInputStream
    val out = clientSock.getOutputStream
    val receiveFile = Array[Byte]
    var bytesRead = in.read(receiveFile)
    while(bytesRead != -1) {
      out.write(receiveFile, 0, bytesRead)
      bytesRead = in.read(receiveFile)
    }
  }
}
