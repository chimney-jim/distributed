/**
 * Created with IntelliJ IDEA.
 * User: Stig
 * Date: 10/16/12
 * Time: 2:12 PM
 * To change this template use File | Settings | File Templates.
 */

import java.net._
import java.io._

object server {

  def main(args: Array[String]) = {
    require(args.length == 0, "This program takes no arguments")

    while(true){
      println("Waiting to receive package...")
      val serverSock = new ServerSocket(2222)
      val clientSock = serverSock.accept
      val message = receiveMessage(clientSock)

      println(message + " is being retrieved...")
      println("Data in transit...")
      println(fetch(clientSock, message))

      clientSock.close(); serverSock.close()
    }
  }

  def receiveMessage(clientSock: Socket): String = {
    val inFromClient = new DataInputStream(clientSock.getInputStream)
    val receiveData: Array[Byte] = null

    inFromClient.read(receiveData)
    val message = new String(receiveData)
    message.trim
  }

  def fetch(clientSock: Socket, message: String): Unit = {
    val fileStreamOut = new DataOutputStream(clientSock.getOutputStream)
    val fileIn = new FileInputStream(new File(message))
    val sendData: Array[Byte] = null

    def writeOut(bytesRead: Int): String = {
      if (bytesRead == 1) "Data transfer complete"
      else {
        println("There are " + fileIn.available + " remaining bytes")
        fileStreamOut.write(sendData, 0, bytesRead)
        writeOut(fileIn.read(sendData))
      }
    }
    writeOut(fileIn.read(sendData))
  }
}
