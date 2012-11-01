import java.net.Socket;
import java.io.*;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: Stig
 * Date: 10/31/12
 * Time: 6:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class Client {

    //Sockets for data transfer
    private Socket routerPipe;
    private Socket clientPipe;

    //Input and output streams for data transfer
    private InputStream in;
    private OutputStream out;

    //File handler
    private OutputStream fos;

    //Byte arrays to hold data being transferred
    private byte[] sendData = new byte[64000];
    private byte[] receiveData = new byte[6400];
    private int bytesRead;

    //Ancillary numbers
    private int available;
    private int fileSize;

    //Strings to handle messages
    private String message;
    private String fileSizeStr;
    private String currentSize;

    private Scanner scan = new Scanner(System.in);


    public Client(String ip, String port){
        try{
            routerPipe = new Socket(ip, Integer.parseInt(port));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    //Program takes IP and port of server
    public void main(String args[]){
        Client client = new Client(args[0], args[1]);

        client.menu();
    }

    private void menu(){
        System.out.println("Cake or death?");
        System.out.print("1. Retrieve Mode \n2. Listen Mode");
        switch (scan.nextInt()){
            case 1:
                this.retrieveMode();
                break;
            case 2:
                this.listenMode();
                break;
            default:
                System.out.println("Try again");
        }
    }

    private void retrieveMode(){
        //TODO: Get IPs from other router
        System.out.println("Please choose an ip to view files");
        //TODO: Display all IPs on other side.
        //TODO: Get file list from that IP they choose
        System.out.println("Type the name of the file to retrieve");
        //TODO: Display all files from chosen IP

        message = scan.next();

        //TODO: if(message!=any message in the list)
        System.out.println("Request for <" + message + "> is being sent" +
                "\nWaiting to receive....");
        this.sendRequest(message);

        this.writeFile(message);
    }

    private void sendRequest(String message){
        try{
            out = new DataOutputStream(routerPipe.getOutputStream());
            out.write(message.getBytes());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void writeFile(String fileName){
        //receive file size
        try{
            in = new DataInputStream(clientPipe.getInputStream());
            fos = new FileOutputStream(new File(fileName));
            in.read(receiveData);
            fileSizeStr = new String(receiveData);
            fileSizeStr = fileSizeStr.trim();
        }
        catch(Exception e){
            e.printStackTrace();
        }

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

    }
}
