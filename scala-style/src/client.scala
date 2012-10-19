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

    //TODO: Try throwing an exception on this Socket
    val socket = new Socket(args(1), 2222)

    val messageAndIp = args(0) + ":" + args(1) + ":" + args(2)

    sendRequest(socket, messageAndIp.getBytes)

    println("A request for <" + args(0) + "> has been sent to <" + args(1) + ">")
    println("Waiting to receive file...")

    receiveAndWrite(socket, args(0))

    println("File transfer complete"); socket.close();
  }

  def receiveAndWrite(socket: Socket, fileName: String): Unit = {
    val fileSize = receiveFileSize(socket)

    val startTimer = System.currentTimeMillis()
    receiveFile(socket, fileName, fileSize)
    val endTimer = System.currentTimeMillis()

    println("File size: " + fileSize)
    println("Time taken: " + startTimer / endTimer + " ms")
    println("Transfer rate: " + fileSize / (startTimer / endTimer) + " bytes/ms")
  }

  def sendRequest(socket: Socket, sendData: Array[Byte]): Unit = {
    val out = new DataOutputStream(socket.getOutputStream)
    out.write(sendData)
  }

  def receiveFileSize(socket: Socket): Long = {
    val in = new DataInputStream(socket.getInputStream)
    val fileSize: Array[Byte] = null
    in.read(fileSize)
    val fileSizeStr = new String(fileSize)
    fileSizeStr.trim
    fileSizeStr.toLong
  }

  def receiveFile(socket: Socket, fileName: String, fileSize: Long): Unit = {
    val in = new DataInputStream(socket.getInputStream)
    val receiveData: Array[Byte] = null
    val fos = new FileOutputStream(new File(fileName))

    def receiveAndWrite(in: DataInputStream, fos: FileOutputStream, fileSize: Long, currentSize: Long) {
      if (fileSize == currentSize) 1
      else if (fileSize != currentSize) {
        var bytesRead = in.read(receiveData)
        fos.write(receiveData)
        println(fileSize + " " + currentSize)
        receiveAndWrite(in, fos, fileSize, currentSize + bytesRead.toLong)
      }
      else throw sys.error("File transfer did not complete")
    }

    receiveAndWrite(in, fos, fileSize, 0)
  }
}
