import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import com.google.gson.Gson;

class SocketTestConnectThread extends Thread{
	private int port;
	
	public SocketTestConnectThread(int port) {
		this.port = port;
	}
	
	public void run() {
		ServerSocket server;
		System.out.println("gone to thread");
		try {
			server = new ServerSocket(port);
			while(true)
				server.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

public class Node3 {
	public static final int PIECES_OF_FILE_SIZE = 1024 * 32;
	public static DatagramSocket serverSocket;
	public static int serverPort =333;
	String severhost="localhost";
	public static String severname="";
	public static String sourcePath="";
	public static String destinationDir="";
	public static FileInfo fileInfo;
	public static int count;
	static String chuoiNode;
	public static  byte[][] fileBytess;
	private static Socket nodeSocket;
	private static Scanner rc;
	private static PrintStream pstentruycap;
	private static Gson gson;
	
	public static ArrayList<String> LayDanhSachFile(String dir)
	{
		ArrayList<String> ds=new ArrayList<>();
		File folder = new File(dir);
		File[] listOfFiles = folder.listFiles();
        if(listOfFiles.length>0)
        {
        	System.out.println("================================= NOTIFY =====================================");
        	System.out.println("==NODE 3 saving file: ");
			for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				ds.add(listOfFiles[i].getName());
				
				System.out.println("File " + listOfFiles[i].getName());
			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + listOfFiles[i].getName());
			}
		}
        }
		return ds;
	}
	public static String GetFileInfor() throws Exception
	{
		File fileSend = new File(sourcePath);
        InputStream inputStream = new FileInputStream(fileSend);
        BufferedInputStream bis = new BufferedInputStream(inputStream);
       // inetAddress = InetAddress.getByName("localhost");
        byte[] bytePart = new byte[PIECES_OF_FILE_SIZE];
        
        // get file size
        long fileLength = fileSend.length();
        System.out.println("=== dung lượng file:"+fileLength+" Byte");
        int piecesOfFile = (int) (fileLength / PIECES_OF_FILE_SIZE);
        int lastByteLength = (int) (fileLength % PIECES_OF_FILE_SIZE);

        // check last bytes of file
        if (lastByteLength > 0) {
            piecesOfFile++;
        }

        // split file into pieces and assign to fileBytess
         fileBytess = new byte[piecesOfFile][PIECES_OF_FILE_SIZE];
        count = 0;
        while (bis.read(bytePart, 0, PIECES_OF_FILE_SIZE) > 0) {
            fileBytess[count++] = bytePart;
            bytePart = new byte[PIECES_OF_FILE_SIZE];
        }

        // read file info
        fileInfo = new FileInfo();
        fileInfo.setFilename(fileSend.getName());
        fileInfo.setFileSize(fileSend.length());
        fileInfo.setPiecesOfFile(piecesOfFile);
        fileInfo.setLastByteLength(lastByteLength);
        fileInfo.setDestinationDirectory(destinationDir);
        
        Gson gson=new Gson();
	    String json=gson.toJson(fileInfo);
	    inputStream.close();
	    bis.close();
	    return json;
	}
	public static void MoKetNoiToiClient() throws Exception
	{
			serverSocket = new DatagramSocket(serverPort);
		    while(true)
		    {    	
				DatagramPacket sendPacket;
				System.out.println("============The node 3 is listening for connections from the client...");
			    // nh?n gói tin t? Client
	             byte[] receiveData = new byte[1024];
	            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	            serverSocket.receive(receivePacket);
			    String sentence = new String(receivePacket.getData(),"UTF-8");
			    System.out.println("================================= NOTIFY =====================================");
			    System.out.println(" ===Client successfully connected: " + sentence);
	            System.out.println(" ===Client requests file download: " + sentence);
	    
	            InetAddress IPAddress = receivePacket.getAddress();
	            int clientPort = receivePacket.getPort();
	 
	            //t?o gói tin và g?i Client
	              byte[] sendData = new byte[1024];
	              sourcePath="data\\node2\\"+sentence.trim();
	              destinationDir="Socketdowloard\\"+sentence.trim();
	              String fileInfor=GetFileInfor();
	              sendData = fileInfor.getBytes();
	            DatagramPacket sendPacketdata = new DatagramPacket(sendData, sendData.length, IPAddress, clientPort);
	            serverSocket.send(sendPacketdata);

		        // send file content
	            System.out.println("=== Sending File to Client...");
	    		        // send pieces of file
		        for (int i = 0; i < (count - 1); i++) {
		            sendPacket = new DatagramPacket(fileBytess[i], PIECES_OF_FILE_SIZE,
		            		IPAddress,clientPort);
		            serverSocket.send(sendPacket);
		            waitMillisecond(40);
		        }
		        // send last bytes of file
		        sendPacket = new DatagramPacket(fileBytess[count - 1], PIECES_OF_FILE_SIZE,
		        		IPAddress, clientPort);
		        serverSocket.send(sendPacket);
		        waitMillisecond(40);
		    	System.out.println("==== Sent successfully!!.");
			}
	}
	public static void waitMillisecond(long millisecond) {
	    try {
	        Thread.sleep(millisecond);
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }
	}
	public static void Connect()
	{
		try {
			String ip=InetAddress.getLocalHost().getHostAddress();
			ArrayList<String> ds=new ArrayList<>();
			ds=LayDanhSachFile("data\\node3");
			Node node1=new Node(0, serverPort, "Node3,"+ip, ds);
			//System.out.println("Node1,"+ip);
			gson=new Gson();
			
	    	nodeSocket = new Socket(InetAddress.getByName(severname), 1742);
	    	
	    	node1.setTen(node1.getTen()+","+serverPort);
	    	chuoiNode=gson.toJson(node1);
			
	    	pstentruycap = new PrintStream(nodeSocket.getOutputStream());
			pstentruycap.println(chuoiNode);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	}

	private static void checkPortToClient() {
		try {
			serverSocket = new DatagramSocket(serverPort);
			serverSocket.close();
		} catch (Exception e) {
			serverPort++;
			checkPortToClient();
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		checkPortToClient();
		
		System.out.println("================================================================================================");
		System.out.println("                                       [NODE 2 RUNNING]");
		System.out.println("Port node: "+serverPort);
		try {
			System.out.println("IP of node: "+InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("================================================================================================");
       
		System.out.print("Enter the IP address: ");
        rc = new Scanner(System.in);
        severname=rc.nextLine();
		Connect();
		
		SocketTestConnectThread sk=new SocketTestConnectThread(nodeSocket.getLocalPort());
		try {
			nodeSocket.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		sk.start();
		
		try {
			MoKetNoiToiClient();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
