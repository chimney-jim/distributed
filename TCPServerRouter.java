   import java.io.*; 
   import java.net.*; 
	  
public class TCPServerRouter
   {
      static ServerSocket incomingSocket = null;
      static Socket outgoingServerSocket = null;
      static Socket clientSocket = null;
      int portInt;
      static int bytesRead = 0;
	  static int sizeOfArgs = 0;
	  static int timesRun = 0;
	  static long start = 0;
	  static long elapsedTime = 0;
	  static long avgLookup = 0;
      static String ipAddressOfClient = null;
	  static String ipAddressOfServer = null;
	  static String[] messages = null;
      static String ipArray [][] = new String[5][3];
   
      static byte[] receiveData = new byte[10240]; 
      static byte[] sendData  = new byte[10240]; 
	  static byte[] receiveFile = new byte[10240];
   	
      public TCPServerRouter()
      {
         try
         {
            incomingSocket = new ServerSocket(2222);
         }
            catch(IOException e)
            {
               System.out.println("Could not listen on port: " + portInt);
            }
        
        

      }
   	
      public void getAndBuildDataToServer() throws Exception {
            InputStream inFromClient = new DataInputStream(clientSocket.getInputStream());
            inFromClient.read(receiveData);
            String message = new String (receiveData);
            message = message.trim();
			System.out.println(message);
			messages = message.split(":");
			message = messages[0];
			ipAddressOfClient = messages[1];
			ipAddressOfServer = messages[2];
			sendData = messages[0].getBytes();
			
			start = System.currentTimeMillis();
            for( int i = 0 ; i < sizeOfArgs; i++ ) {
               if( ipArray[i][0].equals(ipAddressOfServer)) 
               {
					elapsedTime = System.currentTimeMillis()-start;
					this.Router( i, sendData, ipAddressOfServer, ipArray[i][2] );
               }		   
            }    
         }                     
      

        public static void main(String args[]){
			sizeOfArgs = args.length;
		
            for( int i = 0 ; i < sizeOfArgs; i++ ) {
                ipArray[i][0] = args[i];
                ipArray[i][2] = "2222";
            }
            
            TCPServerRouter myServerRouter = null;
            while(true){
			try{
				myServerRouter = new TCPServerRouter();
				System.out.println("Waiting to receive message");
                clientSocket = incomingSocket.accept();
				System.out.println("Message received...getting data.");
				
                myServerRouter.getAndBuildDataToServer();
				
				System.out.println("Data built and sent");
                
				InputStream inFromServer = new DataInputStream(outgoingServerSocket.getInputStream());
                DataOutputStream fileStreamOut = new DataOutputStream(clientSocket.getOutputStream());
				
				System.out.println("Receiving data");

				do{
                    bytesRead = inFromServer.read(receiveFile);
					System.out.println("There are " + inFromServer.available() + " remaining bytes");
					fileStreamOut.write(receiveFile, 0, bytesRead);    
                }while(inFromServer.available() != 0);
				
				outgoingServerSocket.close();
				clientSocket.close();
				
				timesRun++;
				avgLookup = elapsedTime/timesRun;
				System.out.println("Current routing table lookup time(sec) = " + elapsedTime/1000);
				System.out.println("Average routing table lookup time(sec) = " + avgLookup/1000);
            }
            catch(Exception e){
                System.out.println(e);
            }
			}
        }

      public void Router( int link, byte[] message, String ipAddress,
        String ports ) throws Exception {
        
         int port = Integer.parseInt( ports );
      
         switch(link) {
            case 0:
               this.sendMessage( message, ipAddress, port );
               break;
                          
            case 1:
               this.sendMessage( message, ipAddress, port );
               break;
            
            case 2:
               this.sendMessage( message, ipAddress, port );
               break;
            
            case 3:
               this.sendMessage( message, ipAddress, port );
               break;
            
            case 4:
               this.sendMessage( message, ipAddress, port );
               break;
            
            case 5:
               this.sendMessage( message, ipAddress, port );
               break;
                
            case 6:
               this.sendMessage( message, ipAddress, port );
               break;
                
            case 7:
               this.sendMessage( message, ipAddress, port );
               break;
            
            
            default : 
               System.out.println("No interface to bind to");
               break;
         }
        
      }
   	// Not Done yet
      public void sendMessage( byte[] message,  String ipAddress, int port ) {
          try{
          outgoingServerSocket = new Socket(ipAddress, port);
          OutputStream out = new DataOutputStream(outgoingServerSocket.getOutputStream());
          out.write(sendData);
		  	System.out.println("check");
          }
          catch(Exception e){
              System.out.println(e);
          }
      }
   }
