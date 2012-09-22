import java.io.*; 
import java.net.*;
  
class UDPClient { 
    public static void main(String args[]) throws Exception 
    { 
  
      // Read message/data from keyboard or file //
		
  
      DatagramSocket clientSocket = new DatagramSocket(); 
  
      InetAddress IPAddress = InetAddress.getByName("localhost"); 
  
	  // Prepare data structure for payloads - sending and receiving //
      byte[] sendData = new byte[1024]; 
      byte[] receiveData = new byte[1024]; 
  
      // Construct the message itself - and 'pack' it //
      sendData = args[0].getBytes();
      InetAddress IPAddressOut = InetAddress.getByName(args[1]);
      DatagramPacket sendDatagram = new DatagramPacket(sendData, sendData.length, IPAddressOut, 2222);

      // Echo message to ensure it is correct //
      System.out.println("A request for " + args[0] + " is being sent");

      // Prepare a packet of information  and send it via the 'clientSocket' //
      clientSocket.send(sendDatagram);
  
     // Prepare a packet of 'empty' payload and receive message into it via the 
     // 'clientSocket' //
     DatagramPacket receiveDatagram = new DatagramPacket(receiveData, receiveData.length);
      System.out.println("Waiting to receive package...");
     clientSocket.receive(receiveDatagram);
     System.out.println("Datagram received!!");

     InputStream is = null;
     //is = clientSocket.getInputStream();

	 // 'Unpack' the message into character string and print it out to verify 
	 // for correctness and close the socket //
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileOutputStream fos = new FileOutputStream("./" + args[0]);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        int bytesRead = is.read(receiveData, 0, receiveData.length);

        do{
            baos.write(receiveData);
            bytesRead = is.read(receiveData);
        }while (bytesRead != -1);

        bos.write(baos.toByteArray());
        bos.flush();
        bos.close();
  
      clientSocket.close(); 
      } 
} 
      

