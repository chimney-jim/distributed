import java.io.*; 
import java.net.*;
  
class TCPClient { 
    public static void main(String args[]) throws Exception 
    { 
  
      // Read message/data from keyboard or file //
		
  
  
      InetAddress IPAddress = InetAddress.getByName("localhost"); 
      
      Socket sendSocket = new Socket(args[1], 2222);
      Socket receiveSocket = new Socket();

	  // Prepare data structure for payloads - sending and receiving //
      byte[] sendData = new byte[1024]; 
      byte[] receiveData = new byte[10240]; 
      int bytesRead;
      // Construct the message itself - and 'pack' it //
      sendData = args[0].getBytes();
      System.out.println(sendData);
      //InetAddress IPAddressOut = InetAddress.getByName(args[1]);
      //InputStream is = new StringBufferInputStream(args[0]);
      InputStream in = new DataInputStream(sendSocket.getInputStream());
      OutputStream out = new DataOutputStream(sendSocket.getOutputStream());
      out.write(sendData);
      //is.read(sendData);
      //BufferedReader socketInput = new BufferedReader(new InputStreamReader(is));

      // Echo message to ensure it is correct //
      System.out.println("A request for " + args[0] + " is being sent to " + args[1]);

      // Prepare a packet of information  and send it via the 'clientSocket' //
      //socket.connect(sendSocket);

     // Prepare a packet of 'empty' payload and receive message into it via the 
     // 'clientSocket' //
      File fileFromServer = new File("./" + args[0]);
      OutputStream fos = new FileOutputStream(fileFromServer);
      DataOutputStream dos = new DataOutputStream(fos);
      bytesRead = in.read(receiveData, 0, receiveData.length);
      //int current = bytesRead;
      System.out.println("Waiting to receive package...");
      String stuff = "";
      String stuffTrim = "";

      do{
        //bytesRead = in.read(receiveData, 0, receiveData.length);
      //bytesRead = in.read(receiveData, 0, receiveData.length);
        //if (bytesRead >= 0)
            //current += bytesRead;
      fos.write(receiveData, 0, bytesRead);
        stuff = new String(receiveData);
        stuffTrim = stuff.trim();
        System.out.println(stuffTrim);
     //fos = sendSocket.getOutputStream();
      }while(stuffTrim != "###");

      //fos.flush();
      //fos.close();
      //sendSocket.close();

     //System.out.println("Data is " + receiveDatagram.getData() + " with a length of " + receiveDatagram.getLength());

     /*is = socketocket.getInputStream();

	 // 'Unpack' the message into character string and print it out to verify 
	 // for correctness and close the socket //
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        if (is != null){

            File outputFile = new File("./" + args[0]);
            FileOutputStream fos = new FileOutputStream(outputFile);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bytesRead = is.read(receiveData, 0, receiveData.length);

            do{
                boas.write(recieveData);
                bytesRead = is.read(recieveData);
            }while(bytesRead != 1);
        }

            bos.write(baos.toByteArray());
            bos.flush();
            bos.cloase();
            socket.close();
        //fos.write(receiveData, 0, receiveData.length);
        */
        //fos.write(receiveDatagram.getData());
        //fos.close();
      //sendSocket.close();
      //receiveSocket.close();
      } 
} 
      

