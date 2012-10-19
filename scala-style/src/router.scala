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

  def main(args: Array[String]) = {
    require(args.length < 6, "Must have 5 or less arguments")
    //TODO: Test where things are failing and why
    //TODO: Maybe try adding in some exceptions here
    println("check1")
    val servSock = new ServerSocket(2222)
    val clientSock = servSock.accept()
    val ipList = buildRoutingTable(args, List(), 0)
    var timesRun = 0

    while(true){
      println("This program has been consecuatively run " + timesRun)
      println("Waiting to receive message...")
      //Get (message, (ipOfServer, port), elapsed time of routing table lookup)
      val messageAndIp = buildData(clientSock, ipList)
      //break apart messageAndIp into it's parts
      val routingTableLookupTime = messageAndIp.last.toLong
      val toServer = new Socket(messageAndIp.tail.head.toString, messageAndIp.init.last.toInt)

      sendMessage(messageAndIp.head.getBytes, toServer)
      println("Message sent...\nTransporting data...")

      transportData(clientSock, toServer)

      println("Data has been transported")

      servSock.close; clientSock.close;

      timesRun+=1
      val avgLookup = routingTableLookupTime/timesRun.toLong
      println("Current routing table lookup time " + routingTableLookupTime + "ms")
      println("Average routing table lookup time " + avgLookup + "ms")
    }
  }

  def buildRoutingTable(args: Array[String], list: List[(String, String)], count: Int): List[(String, String)] = {
    if (args.isEmpty) list
    else buildRoutingTable(args, list ++ List((args(count), "2222")), count + 1)
  }

  def buildData(clientSock: Socket, ipList: List[(String, String)]): List[String] = {
    def receiveMessage(): List[String] = {
      val receiveData: Array[Byte] = null
      val inFromClient = new DataInputStream(clientSock.getInputStream)
      inFromClient.read(receiveData)
      var message = new String(receiveData)
      message = message.trim
      val messages = message.split(":")
      messages.toList
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
    List(messages(0), ipToSendTo._1, ipToSendTo._2, elapsedTime.toString)
  }

  def sendMessage(sendData: Array[Byte], toServer: Socket) = {
    val out = new DataOutputStream(toServer.getOutputStream)
    out.write(sendData)
  }

  def transportData(clientSock: Socket, fromServer: Socket) = {
    val in = fromServer.getInputStream
    val out = clientSock.getOutputStream
    val receiveFile: Array[Byte] = null
    var bytesRead = in.read(receiveFile)
    while(bytesRead != -1) {
      out.write(receiveFile, 0, bytesRead)
      bytesRead = in.read(receiveFile)
    }
  }
}
