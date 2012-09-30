   import java.io.*; 
   import java.net.*; 
	  
public class TCPServerRouter
   {
      static ServerSocket incomingSocket = null;
      static Socket outgoingServerSocket = null;
      static Socket clientSocket = null;
      int portInt;
      static int bytesRead = 0;
      String ipAddress = null;
      static String ipArray [][] = new String[5][3];
   
      static byte[] receiveData = new byte[10240]; 
      static byte[] sendData  = new byte[10240]; 
   	
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
         while(true)
         {
            InputStream inFromClient = new DataInputStream(clientSocket.getInputStream());
            inFromClient.read(receiveData);
            sendData = receiveData;
            String message = new String (receiveData);
            message = message.trim();
            inFromClient.read(receiveData);
            ipAddress = new String(receiveData);
            ipAddress = ipAddress.trim();
         
            for( int i = 0 ; i < ipArray.length; i++ ) {
            
               if( ipArray[i][0].equals(ipAddress)) 
               {
                  this.Router( i, sendData, ipAddress, ipArray[i][2] );
               }                
            }
            
         }                            
      }
      

        public static void main(String args[]){
            for( int i = 0 ; i < ipArray.length; i++ ) {
                ipArray[i][0] = args[i];
                ipArray[i][2] = "2222";
            }
            
            TCPServerRouter myServerRouter = new TCPServerRouter();
            try{
                clientSocket = incomingSocket.accept();
                myServerRouter.getAndBuildDataToServer();

                InputStream inFromServer = new DataInputStream(outgoingServerSocket.getInputStream());
                DataOutputStream fileStreamOut = new DataOutputStream(clientSocket.getOutputStream());
            

                while( (bytesRead = inFromServer.read(receiveData)) != -1){
                    fileStreamOut.write(receiveData, 0, bytesRead);    
                }
            }
            catch(Exception e){
                System.out.println(e);
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
          }
          catch(Exception e){
              System.out.println(e);
          }
      }
   }
