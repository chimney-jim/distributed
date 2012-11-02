import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

//TODO: make sure this thing is finished

/**
 * Created with IntelliJ IDEA.
 * User: Stig
 * Date: 10/31/12
 * Time: 7:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class Router {

    //For ip list
    private ArrayList<String> ipList;
    private String ip;

    //Sockets
    private ServerSocket serverSocket;
    private Socket client, otherRouter;

    //Streams
    private InputStream inFromClient, inFromOtherRouter;
    private OutputStream outToClient, outToOtherRouter;

    //Data buffers
    byte[] sendData = new byte[64000];
    byte[] receiveData = new byte[64000];

    String command;
    int bytesRead;
    Scanner scan = new Scanner(System.in);

    public Router(){
        try {
            serverSocket = new ServerSocket(2222);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void main(String[] args){
        System.out.println("Please enter all IPs of all clients on this side" +
        "\nType the letter <e> when finished");

        while(!(ip = scan.next()).equals("e")){
            ipList.add(ip);
        }

        Router router = new Router();

        System.out.println("Waiting to receive command...");
        try {
            client = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        receiveMessage(receiveCommand());
    }

    private String receiveCommand(){
        try {
            inFromClient = new DataInputStream(client.getInputStream());
            inFromClient.read(receiveData);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        command = new String(receiveData);
        return command.trim();
    }

    private void receiveMessage(String command){
            if(command.equals("getIPs")){
                getAndSendIPs();
            }
    }

    private void getAndSendIPs(){
        try {
            inFromOtherRouter = new DataInputStream(otherRouter.getInputStream());
            outToOtherRouter = new DataOutputStream(otherRouter.getOutputStream());
            outToClient = new DataOutputStream(client.getOutputStream());

            while( (bytesRead = inFromOtherRouter.read(receiveData)) != -1){
                outToClient.write(receiveData, 0, bytesRead);
            }

            inFromOtherRouter.close();
            outToClient.close();
            otherRouter.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
