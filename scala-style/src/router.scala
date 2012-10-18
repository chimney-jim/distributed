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

    val incomingSocket = new ServerSocket(2222)
    val ipList = buildRoutingTable(args, List[(String, String)])

    while(true){

    }
  }

  def buildRoutingTable(args: List[String], list: List[(String, String)]): List[(String, String)] = {
    if (args.isEmpty) list
    else {list ::: (args.head, "2222"); buildRoutingTable(args.tail, list)}
  }
}
