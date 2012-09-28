   import java.io.*; 
   import java.net.*; 
	  
   public class TCPServerRouter
   {
      ServerSocket incomingSocket = null;
      ServerSocket outgoingServerSocket = null;
      Socket clientSocket = null;
      int portInt;
      String ipArray [][] = new String[5][3];
   
      byte[] receiveData = new byte[1024]; 
      byte[] sendData  = new byte[1024]; 
   	
      public TCPServerRouter()
      {
         try
         {
            incomingSocket = new ServerSocket(portInt);
         }
            catch(IOException e)
            {
               System.out.println("Could not listen on port: " + portInt);
            }
      }
   	
      public void getAndBuildData() throws Exception {
         while(true)
         {
            clientSocket = incomingSocket.accept();
            InputStream inFromClient = new DataInputStream(clientSocket.getInputStream());
            inFromClient.read(receiveData);
            String message = new String (receiveData);
            message = message.trim();
            inFromClient.close();
         
            for( int i = 0 ; i < ipArray.length; i++ ) {
            
               if( ipArray[i][0].equals(  incomingSocket.getInetAddress())) 
               {
                  this.Router( i, sendData, incomingSocket.getInetAddress(), ipArray[i][2] );
               }                
            }
            
         }
      }
      
      public void Router( int link, byte[] message, InetAddress ipAddress,
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
   	/* Not Done yet
      public void sendMessage( byte[] message,  InetAddress ipAddress, int port ) {
        
         try {
            outgoingServerSocket = new ServerSocket(port);
         
         }
            catch( SocketException e ) {
               System.out.println( e.getMessage() );
            }
        
         try {
            
   			
            DatagramPacket sendPacket = new DatagramPacket( message, message.length, 
                ipAddress, port );
         
            outgoingServerSocket.send(sendPacket);
         
         }
            catch( IOException e ) {
               System.out.println( e.getMessage() );
            }
   			
      }
   	*/
   }