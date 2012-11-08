import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: Stig
 * Date: 10/31/12
 * Time: 7:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class Router {

    //For ip list
    private static ArrayList<String> ipList;
    private static String ip;
    private static String otherRouterIP;

    //Sockets
    private static ServerSocket serverSocket;
    private static Socket client, otherRouter;

    //Streams
    private InputStream inFromClient, inFromOtherRouter;
    private OutputStream outToClient, outToOtherRouter;

    //Data buffers
    byte[] sendData = new byte[64000];
    byte[] receiveData = new byte[64000];

    String command;
    int bytesRead;
    private static Scanner scan = new Scanner(System.in);

    public Router(){
        try {
            serverSocket = new ServerSocket(2222);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void main(String[] args){
        System.out.println("Please enter all IPs of all clients on this side" +
        "\nType the letter <e> when finished");

        while(!(ip = scan.next()).equals("e")){
            ipList.add(ip);
        }

        System.out.println("Please enther the IP of the other router.");

        otherRouterIP = scan.next();

        Router router = new Router();

        System.out.println("Waiting to receive command...");
        try {
            client = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        router.receiveMessage(router.receiveCommand());
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
                System.out.println("Grabbing IPs...");
                getIPs();
            }
            else if (command.equals("sendIPs")){
                System.out.println("Sending IPs...");
                sendIPs();
            }
    }

    private void getIPs(){
        try {
            otherRouter = new Socket(otherRouterIP, 2222);
            inFromOtherRouter = new DataInputStream(otherRouter.getInputStream());
            outToOtherRouter = new DataOutputStream(otherRouter.getOutputStream());
            outToClient = new DataOutputStream(client.getOutputStream());

            outToOtherRouter.write("sendIPs".getBytes());

            while( (bytesRead = inFromOtherRouter.read(receiveData)) != -1){
                outToClient.write(receiveData, 0, bytesRead);
            }

            inFromOtherRouter.close();
            outToOtherRouter.close();
            outToClient.close();
            otherRouter.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void sendIPs(){
        try {
            inFromOtherRouter = new DataInputStream(otherRouter.getInputStream());
            outToOtherRouter = new DataOutputStream(otherRouter.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            for(String send: ipList){
                sendData = send.getBytes();
                outToOtherRouter.write(sendData);
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
