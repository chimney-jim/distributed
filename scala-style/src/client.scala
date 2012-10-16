/**
 * Created with IntelliJ IDEA.
 * User: Stig
 * Date: 10/8/12
 * Time: 7:48 PM
 * To change this template use File | Settings | File Templates.
 */

import java.io._
import java.net._

object client {

  def main(args: Array[String]) = {
    require(args.length == 3, "args must be <fileName> <ipOfRouter> <ipOfServer>")

    val socket = new Socket(args(1), 2222)

    val messageAndIp = args(0) + ":" + args(1) + ":" + args(2)

    sendRequest(socket, messageAndIp.getBytes)

    println("A request for <" + args(0) + "> has been sent to <" + args(1) + ">")
    println("Waiting to receive file...")

    receiveAndWrite(socket, args(0))

    println("File transfer complete")
  }

  def receiveAndWrite(socket: Socket, fileName: String): Unit = {
    val fileSize = receiveFileSize(socket)

    val startTimer = System.currentTimeMillis()
    receiveFile(socket, fileName, fileSize, 0)
    val endTimer = System.currentTimeMillis()

    println("File size: " + fileSize)
    println("Time taken: " + startTimer / endTimer + " ms")
    println("Transfer rate: " + fileSize / (startTimer / endTimer) + " bytes/ms")
  }

  def sendRequest(socket: Socket, sendData: Array[Byte]) = {
    val out = new DataOutputStream(socket.getOutputStream)
    out.write(sendData)
  }

  def receiveFileSize(socket: Socket) = {
    val in = new DataInputStream(socket.getInputStream)
    val fileSize: Array[Byte] = null
    in.read(fileSize)
    val fileSizeStr = new String(fileSize)
    fileSizeStr.trim
    fileSizeStr.toLong
  }

  def receiveFile(socket: Socket, fileName: String, fileSize: Long, currentSize: Long): Unit = {
    val in = new DataInputStream(socket.getInputStream)
    var receiveData = null
    val fos = new FileOutputStream(new File(fileName))
    while (fileSize != currentSize) {
      var bytesRead = in.read(receiveData)
      fos.write(receiveData)
      println(fileSize + " " + currentSize)
      //receiveFile(socket, fileName, fileSize, currentSize + bytesRead.toLong)
    }
  }
}
