import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import javax.crypto.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

//TODO: Maybe split up the send and receiving ips and file lists
/**
 * Created with IntelliJ IDEA.
 * User: Stig
 * Date: 10/31/12
 * Time: 6:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class Client implements ActionListener{

    //Sockets for data transfer
    private Socket socket, clientSock;
    private ServerSocket serverSocket;

    //Input and output streams for data transfer
    private InputStream in;
    private OutputStream out;
    private static FileInputStream fileIn = null;

    //File handler
    private OutputStream fos;

    //Encryption
    //byte[] keyBytes, ivBytes;
    //SecretKeySpec key = new SecretKeySpec(keyBytes, "DES");
    //IvParameterSpec ivSpec = new IvParameterSpec(ivBytes)
    private static Cipher cipher;
    private static KeyGenerator keyGen;
    private static SecretKey secKey;
    private CipherInputStream cis;
    private CipherOutputStream cos;

    //Byte arrays to hold data being transferred
    private byte[] sendData = new byte[4096];
    private byte[] receiveData = new byte[4096];
    private int bytesRead;
	private boolean complete = false;

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


   // public Client(){}
JTextField IPaddress;
	JTextField num2222;                                        
	JTextField outPut;
	String[] stuff = {"one", "two"};
	JList list2 = new JList(stuff);
	String[] stuff2 = {" "," "," "," "," "," "};
	JList displayListIPs = new JList(stuff2);
	String[] stuff4 = {" "," "," "," "," "," "};
	JList displayListFile = new JList(stuff4);
	JScrollPane scrollPane = new JScrollPane(displayListFile);
	
	
	Client(){
	    //encryptor.setPassword("password");
	   IPaddress = new JTextField(20);
		num2222 = new JTextField(10);
		JFrame frame = new JFrame("Creating a JList Component");
		JPanel containerTop = new JPanel();
		containerTop.setLayout(new BoxLayout(containerTop, BoxLayout.Y_AXIS));
		
		//top part enter fields
		JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayout(4,3));
		panel1.add(new JLabel("Enter IP Address"));
		panel1.add(IPaddress);
		IPaddress.addActionListener(this);
		
		panel1.add(new JLabel("Enter this: 2222"));
		panel1.add(num2222);
		num2222.addActionListener(this);
		
		//submit button 1
		JButton submitBut = new JButton("Submit");
		submitBut.addActionListener(this);
		panel1.add(submitBut);
		
		//submit button 2
		JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayout(2,2));
		panel2.add(new JLabel("Entered IP Address"));
		panel2.add(list2);
		panel2.add(new JLabel("Press button to get IPs"));
		JButton submitButton = new JButton("GO");
		panel2.add(submitButton);
		
		//add to container
		
		
		submitButton.addActionListener(new ActionListener() { 
		  public void actionPerformed(ActionEvent e) { 
			//retrieveMode(IPaddress.getText(),num2222.getText());
			getIPs(IPaddress.getText(),num2222.getText());
			displayIPs();
			
			displayListIPs.setListData(listOfIPs.toArray());
		  } 
		});
		
		//output list
		JPanel panel = new JPanel();
			panel.setLayout(new GridLayout(1,3));
			
			
			panel.add(new JLabel("Gathered IP Address"));
			panel.add(displayListIPs);
			
			//pick items in IP
			JButton connectButton = new JButton("Connect");
			panel.add(connectButton);
			
			//add to container
			connectButton.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				
				Object sellectionIP = displayListIPs.getSelectedValue();
				getFileList(sellectionIP.toString());
				
				displayListFile.setListData(listOfRemoteFiles.toArray());
			  } 
			});
			
		// output file list
		JPanel panel3 = new JPanel();
			panel3.setLayout(new GridLayout(1,3));
			
			
			panel3.add(new JLabel("Gathered Files List"));
			
			panel3.add(scrollPane);
			
			//pick items in Files
			JButton downloadButton = new JButton("download");
			panel3.add(downloadButton);
			
			//add to container
			
			
			downloadButton.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
				//retrieveMode();
				//getIPs();
				//displayIPs();
				//returnIPs();
				//displayListIPs.setListData(listOfIPs);
				Object sellectionFile = displayListFile.getSelectedValue();
				writeFile(sellectionFile.toString());
				
				if(complete == true)
				{
					//String alert = JOptionPane.showInputDialog("Download Successful");
					String alert = "Download Successful";
					JOptionPane.showMessageDialog(null, alert);
				}
			  } 
			  
			});
		
		//********************************
	  
  
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		frame.getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
		
		containerTop.add(panel1);
		containerTop.add(panel2);
		containerTop.add(panel);
		containerTop.add(panel3);
		containerTop.revalidate();
		containerTop.repaint();
		
		frame.add(containerTop);
		frame.setSize(600,600);
		frame.setVisible(true);
	}
	//***************************
	
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("Submit"))
		{
			String fullString[] = {num2222.getText(), IPaddress.getText()};
			list2.setListData(fullString);
		}
		
		
		
	}
	
	//*****************************************
    //Program takes IP and port of server
    public static void main(String args[]){
       Client client = new Client();
		
       SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    new Client();
                }
            });

        try {
            cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            keyGen = KeyGenerator.getInstance("DES/CBC/PKCS5Padding");
            secKey = keyGen.generateKey();
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchPaddingException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

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
	
	//my code ******************
	
	
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
                //System.out.println(i + ": " + listOfIPs.get(i));
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

        try {
            cipher.init(Cipher.DECRYPT_MODE, secKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try{
            in = new DataInputStream(clientSock.getInputStream());
            out = new DataOutputStream(clientSock.getOutputStream());
            cis = new CipherInputStream(in, cipher);

            out.write("sendFile".getBytes());
            out.write(fileName.getBytes());
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
                bytesRead = cis.read(receiveData, 0, receiveData.length);
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
		complete = true;
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
            cipher.init(Cipher.ENCRYPT_MODE, secKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

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
                cos = new CipherOutputStream(out, cipher);
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
                    cos.write(sendData, 0, bytesRead);
                    System.out.println(bytesRead);
                    sendData = new byte[4096];
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
     }
}
