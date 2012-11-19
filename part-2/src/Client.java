import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


//TODO: Maybe split up the send and receiving ips and file lists
/**
 * Created with IntelliJ IDEA.
 * User: Stig
 * Date: 10/31/12
 * Time: 6:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class Client {

    //Sockets for data transfer
    private Socket socket, clientSock;
    private ServerSocket serverSocket;

    //Input and output streams for data transfer
    private InputStream in;
    private OutputStream out;
    private static FileInputStream fileIn = null;

    //File handler
    private OutputStream fos;

    //Byte arrays to hold data being transferred
    private byte[] sendData = new byte[4096];
    private byte[] receiveData = new byte[4096];
    private int bytesRead;

    //Ancillary numbers
    private int fileSize;
    Integer numberOfIPs;
    Integer numberOfFiles;

    //Message handler
    private static String message;
    private static String command;
    private static String fileList;
    private String fileSizeStr;
    private String currentSize = "";

    //File array handler
    private static File file = null;
    File folder = new File(".");
    File[] listOfLocalFiles = folder.listFiles();
    private static String files = "";
    ArrayList<String> listOfRemoteFiles = new ArrayList<String>();
    ArrayList<String> listOfIPs = new ArrayList<String>();

    private static Scanner scan = new Scanner(System.in);


    public Client(){}

    //Program takes IP and port of server
    public static void main(String args[]){
        Client client = new Client();

        int choice = client.menu();
        //int choice = scan.nextInt();
        if (choice == 0)  {
           System.out.println("Try again");
           client.menu();
        }
        else if (choice == 1)
            client.retrieveMode(args[0], args[1]);
        else
            client.listenMode();
    }

    private int menu(){
        System.out.println("Cake or death?");
        System.out.print("1. Retrieve Mode \n2. Listen Mode\n");
        switch (scan.nextInt()){
            case 1:
                return 1;
            case 2:
                return 2;
            default:
                return 0;
        }
    }

    private void retrieveMode(String routerIP, String routerPort){
        try{
            socket = new Socket(routerIP, Integer.parseInt(routerPort));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        //Get all IP addresses of clients on the router
        //getIPs();
        displayIPs();
        System.out.println("Please choose an ip to view files");

        String clientAddress = listOfIPs.get(scan.nextInt());
        System.out.println("Connecting to <" + clientAddress + "> on port <" + 2222 + ">");
        
        //move this to getFileList()


        //Get all files of remote client
        getFileList(clientAddress);
        System.out.println("Please choose a file to retrieve");

        int selection = scan.nextInt();

        if (selection!=numberOfFiles+1) {
            message = listOfRemoteFiles.get(selection);

            System.out.println("Request for <" + message + "> is being sent" +
                    "\nWaiting to receive....");
            //this.sendRequest(message);

            this.writeFile(message);
            System.out.println("Data transfer complete");

            try{
                in.close();
                out.close();
                fos.close();
                socket.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        else {
            displayIPs();
        }
    }

    public void getIPs(String routerIP, String routerPort){
         try{
            socket = new Socket(routerIP, Integer.parseInt(routerPort));
        }
        catch (Exception e){
            e.printStackTrace();
        }

        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            out.write("getIPs".getBytes());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        //Receive amount of IPs
        try {
            in.read(receiveData);
            numberOfIPs = new Integer(new String(receiveData).trim());
            System.out.println("This is the length of the ipList: " + numberOfIPs);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void displayIPs(){
        try {
            for(int i=0; i < numberOfIPs; i++){
                in.read(receiveData);
                listOfIPs.add(new String(receiveData).trim());
                System.out.println(i + ": " + listOfIPs.get(i));
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    public ArrayList<String> returnIPs(){
        return listOfIPs;
    }

    public void getFileList(String clientAddress){
        try {
            clientSock = new Socket(clientAddress, 2222);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            in = new DataInputStream(clientSock.getInputStream());
            out = new DataOutputStream(clientSock.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            out.write("sendNumberOfFiles".getBytes());
            System.out.println("Requested number of files");
        } catch (java.lang.Exception exception) {
            exception.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        //Receive amount of files
        try {
            receiveData = new byte[4096];
            in.read(receiveData);
            numberOfFiles = new Integer(new String(receiveData).trim());
            System.out.println("These are the number of files: " + numberOfFiles);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            out.write("sendFileList".getBytes());
            System.out.println("Sent request for list of files");
        } catch (java.lang.Exception exception) {
            exception.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try{
            out.flush();
            receiveData = new byte[4096];
            in.read(receiveData);
            fileList = new String(receiveData).trim();
            System.out.println(fileList);
            listOfRemoteFiles = new ArrayList<String>(Arrays.asList(fileList.split("[ ]+")));
        } catch (Exception e) {
            e.printStackTrace();
        }

         for(int i=0; i<listOfRemoteFiles.size(); i++){
             System.out.println(i + ": " + listOfRemoteFiles.get(i));
         }
    }

    public ArrayList<String> returnFileList(){
        return listOfRemoteFiles;
    }

    public void writeFile(String fileName){
        //receive file size
        try{
            in = new DataInputStream(clientSock.getInputStream());
            out = new DataOutputStream(clientSock.getOutputStream());

            out.write("sendFile".getBytes());
            out.write(message.getBytes());
            out.flush();

            fos = new FileOutputStream(new File("./" + fileName));
            receiveData = new byte[4096];
            in.read(receiveData);
            fileSizeStr = new String(receiveData);
            fileSizeStr = fileSizeStr.trim();
            System.out.println("File size is: " + fileSizeStr);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        //receive file
        try{
            while(!currentSize.equals(fileSizeStr)){
                receiveData = new byte[4096];
                bytesRead = in.read(receiveData, 0, receiveData.length);
                fos.write(receiveData, 0, bytesRead);
                fileSize += bytesRead;
                currentSize = String.valueOf(fileSize);
                currentSize = currentSize.trim();
                System.out.println(fileSizeStr + ":" + currentSize);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void listenMode(){
        try{
            serverSocket = new ServerSocket(2222);
            System.out.println("Created servSocket");
        }
        catch(Exception e){
            e.printStackTrace();
        }

            //Accept connection and create data streams
        try{
            clientSock = serverSocket.accept();
            System.out.println("Accepted connection, waiting to receive message");
            in = new DataInputStream(clientSock.getInputStream());
            out = new DataOutputStream(clientSock.getOutputStream());
        }
        catch(Exception e){
            e.printStackTrace();
        }

        while(true){
            System.out.println("Waiting to receive message...");
            listenMessage();
        }
    }

    private void listenMessage(){
        try {
            in = new DataInputStream(clientSock.getInputStream());
            receiveData = new byte[4096];
            in.read(receiveData);
            command = new String(receiveData).trim();
            System.out.println("Received command is: " + command);
        } catch (java.lang.Exception exception) {
            exception.printStackTrace();
            System.exit(0);  //To change body of catch statement use File | Settings | File Templates.
        }

        if (command.equals("sendNumberOfFiles")){
            System.out.println("Sending number of files...");
            sendNumberOfFiles();
            receiveData = new byte[4096];
        }
        else if (command.equals("sendFileList")){
            System.out.println("Sending file list...");
            sendFileList();
            receiveData = new byte[4096];
        }
        else if (command.equals("sendFile")){
            System.out.println("Sending file...");
            sendFile();
            receiveData = new byte[4096];
        }
    }

    private void sendNumberOfFiles(){
        try {
                numberOfFiles = listOfLocalFiles.length;
                String numOfFilesStr = String.valueOf(numberOfFiles);
                sendData = numOfFilesStr.getBytes();
                out.write(sendData);
            } catch (java.lang.Exception exception) {
                exception.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
    }

    private void sendFileList(){
            //Send file list

        try {
            for(int i=0; i<listOfLocalFiles.length; i++){
                files += listOfLocalFiles[i].getName() + " ";
            }
            System.out.println(files);
            sendData = new byte[4096];
            out.write(files.getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
    private void sendFile(){

            try {
                receiveData = new byte[4096];
                in.read(receiveData);
                message = new String(receiveData);
                message = message.trim();
                System.out.println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Fetching Data...");
            //Fetch file of message
            try {
                file = new File(message);
                fileIn = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }           //Send file size
            try {
                String fileSize = String.valueOf(file.length());
                System.out.println(fileSize);
                sendData = new byte[4096];
                sendData = fileSize.getBytes();
                out.write(sendData);
            }
            catch (Exception e){
                e.printStackTrace();
            }

            //Send file
            try {
                sendData = new byte[4096];
                while( (bytesRead=fileIn.read(sendData)) != -1){
                    System.out.println(fileIn.available() + " bytes remaining");
                    out.write(sendData, 0, bytesRead);
                    System.out.println(bytesRead);
                    sendData = new byte[4096];
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
     }
}
