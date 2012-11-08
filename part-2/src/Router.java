import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
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
    private static LinkedList<String> ipList = new LinkedList<String>();
    private static String ip;
    private static String otherRouterIP;

    //Sockets
    private static ServerSocket serverSocket;
    private static Socket client, otherRouter;

    //Streams
    private static InputStream inFromClient, inFromOtherRouter;
    private static OutputStream outToClient, outToOtherRouter;

    //Data buffers
    static byte[] sendData = new byte[64000];
    static byte[] receiveData = new byte[64000];

    String command;
    static int bytesRead;
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

        try {
            while(!(ip = scan.next()).equals("e")){
                ipList.add(ip);
            }
        } catch (Exception e) {
            System.out.println("Error adding IP");  //To change body of catch statement use File | Settings | File Templates.
        }

        System.out.println("Please enter the IP of the other router.");

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
                getIPs();
            }
            else if (command.equals("sendIPs")){
                sendIPs();
            }
    }

    private static void getIPs(){
        System.out.println("Grabbing IPs...");
        try {
            otherRouter = new Socket(otherRouterIP, 2222);
            inFromOtherRouter = new DataInputStream(otherRouter.getInputStream());
            outToOtherRouter = new DataOutputStream(otherRouter.getOutputStream());
            outToClient = new DataOutputStream(client.getOutputStream());

            outToOtherRouter.write("sendIPs".getBytes());

            receiveData = new byte[64000];
            inFromOtherRouter.read(receiveData);
            outToClient.write(receiveData);

            while( (bytesRead = inFromOtherRouter.read(receiveData)) != -1){
                System.out.println(new String(receiveData).trim());
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

    private static void sendIPs(){
        System.out.println("Sending IPs...");
        try {
            inFromOtherRouter = new DataInputStream(client.getInputStream());
            outToOtherRouter = new DataOutputStream(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        String ipListSize = String.valueOf(ipList.size());
        try {
            outToOtherRouter.write(ipListSize.getBytes());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        System.out.println("passed streams");
        try {
            for(int i=0; i<ipList.size(); i++){
                System.out.println(ipList.get(i));
                sendData = ipList.get(i).getBytes();
                outToOtherRouter.write(sendData);
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NullPointerException e){
            System.out.println("Cannot send IPs...");
        }
    }
}
