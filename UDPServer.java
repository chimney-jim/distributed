import java.io.*; 
import java.net.*; 

class UDPServer { 
  public static void main(String args[]) throws Exception 
    { 
  
	  // The chosen port of the server is: 21212 //
      DatagramSocket serverSocket = new DatagramSocket(21212);
  
      byte[] receiveData = new byte[1024]; 
      byte[] sendData  = new byte[1024]; 
  
      while(true) 
        { 
  
          DatagramPacket receivePacket = 
             new DatagramPacket(receiveData, receiveData.length);
          System.out.println("Waiting to recieve a packet...");
           serverSocket.receive(receivePacket); 
		  String sentence = new String(receivePacket.getData()); 
  
          InetAddress IPAddress = receivePacket.getAddress(); 
  
          int port = receivePacket.getPort(); 

          System.out.println("A packet of: <" + sentence + "> has been received from <" + IPAddress + "> through port <" + port);

		  // 'Pack' the sentence into 'sendData', echo to verify, make a packet of it, and send it back via the 'port'//
          File sendFile = new File(sentence);
          FileInputStream fileStream = new FileInputStream(sendFile);
          int bytesRead = fileStream.read(sendData, 0, sendData.length);

          do{
              System.out.println("There are " + fileStream.available() + "remaining bytes");
            fileStream.read(sendData);
            bytesRead = fileStream.read(sendData);
          }while(bytesRead!=-1);

          //sendData = IOUtils.toByteArray(filestream);
          System.out.println(sendData);
          DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 2121);
          System.out.println("Datagram packed...Sending...");
          serverSocket.send(sendPacket); 
          System.out.println("Datagram sent");
        } 
    } 
}  

