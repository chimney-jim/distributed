import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.ArrayList;
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
    private Socket socket;
    private ServerSocket serverSocket;

    //Input and output streams for data transfer
    private InputStream in;
    private OutputStream out;

    //File handler
    private OutputStream fos;

    //Byte arrays to hold data being transferred
    private byte[] sendData = new byte[64000];
    private byte[] receiveData = new byte[64000];
    private int bytesRead;

    //Ancillary numbers
    private int fileSize;
    Integer numberOfIPs;
    Integer numberOfFiles;

    //Message handler
    private String message;
    private String fileSizeStr;
    private String currentSize;

    //File array handler
    File folder = new File(".");
    File[] listOfLocalFiles = folder.listFiles();
    ArrayList<String> listOfRemoteFiles;
    ArrayList<String> listOfIPs;

    private static Scanner scan = new Scanner(System.in);


    public Client(){}

    //Program takes IP and port of server
    public static void main(String args[]){
        Client client = new Client();

        client.menu();
        int choice = scan.nextInt();
        if (choice == 0)  {
           System.out.println("Try again");
           client.menu();
        }
        else if (choice == 1)
            client.retrieveMode(args[0], args[2]);
        else
            client.listenMode();
    }

    private int menu(){
        System.out.println("Cake or death?");
        System.out.print("1. Retrieve Mode \n2. Listen Mode");
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
        getIPs();
        displayIPs();
        System.out.println("Please choose an ip to view files");

        try {
            socket = new Socket(listOfIPs.get(scan.nextInt()), 2222);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        //Get all files of remote client
        //TODO: Figure out how to make this work with the new if else
        getFileList();
        System.out.println("Please choose a file to retrieve");

        int selection = scan.nextInt();

        if (selection!=0) {
            message = listOfRemoteFiles.get(scan.nextInt());

            System.out.println("Request for <" + message + "> is being sent" +
                    "\nWaiting to receive....");
            this.sendRequest(message);

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

    private void getIPs(){
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
            numberOfIPs = new Integer(new String(receiveData));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void displayIPs(){
        try {
            for(int i=0; i < numberOfIPs; i++){
                in.read(receiveData);
                listOfIPs.add(new String(receiveData));
                System.out.println(i + ": " + listOfIPs.get(i));
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    private void getFileList(){
        try {
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        //out.write();
        //Receive amount of files
        try {
            in.read(receiveData);
            numberOfFiles = new Integer(new String(receiveData));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            for(int i=0; i < numberOfFiles; i++){
                in.read(receiveData);
                listOfRemoteFiles.add(new String(receiveData));
                System.out.println(i + ": " + listOfRemoteFiles.get(i));
            }
            System.out.println(numberOfFiles + ": Go back to IP list");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void sendRequest(String message){
        try{
            out = new DataOutputStream(socket.getOutputStream());
            out.write(message.getBytes());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void writeFile(String fileName){
        //receive file size
        try{
            in = new DataInputStream(socket.getInputStream());
            fos = new FileOutputStream(new File(fileName));
            in.read(receiveData);
            fileSizeStr = new String(receiveData);
            fileSizeStr = fileSizeStr.trim();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        //receive file
        try{
            while(!currentSize.equals(fileSizeStr)){
                bytesRead = in.read(receiveData, 0, receiveData.length);
                fos.write(receiveData, 0, bytesRead);
                fileSize += bytesRead;
                currentSize = String.valueOf(fileSize);
                currentSize = currentSize.trim();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void listenMode(){
        //TODO: Need method to give file names

        try{
            serverSocket = new ServerSocket(2222);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        //TODO: Find a way to exit listen mode
        while(true){
            System.out.println("Waiting to receive message...");
            //Accept connection and create data streams
            try{
                socket = serverSocket.accept();
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
            }
            catch(Exception e){
                e.printStackTrace();
            }
            //Read in message and trim
            try {
                in.read(receiveData);
                String message = new String(receiveData);
                message = message.trim();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Fetching Data...");
            //Fetch file of message
            try {
                File file = new File(message);
                in = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //Send file
            try {
                while( (bytesRead = in.read(sendData)) != -1){
                    System.out.println(in.available() + " bytes remaining");
                    out.write(sendData, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
