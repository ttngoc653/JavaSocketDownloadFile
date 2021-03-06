import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import com.google.gson.Gson;

public class Node2 {
	public static final int PIECES_OF_FILE_SIZE = 1024 * 32;
	public DatagramSocket serverSocket;
	public static int serverPort =222;
	public static String severname="172.20.10.5";
	String severhost="localsost";
	public static String sourcePath="";
	public static String destinationDir="";
	public static FileInfo fileInfo;
	public int count;
	public  byte[][] fileBytess;
	String json;
	public ArrayList<String> LayDanhSachFile(String dir)
	{
		ArrayList<String> ds=new ArrayList<>();
		File folder = new File(dir);
		File[] listOfFiles = folder.listFiles();
        if(listOfFiles.length>0)
        {
        	System.out.println("================================= THONG BAO=====================================");
        	System.out.println("==NODE 2 ðang lýu tr? các file:");
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
	public String GetFileInfor() throws Exception
	{
		File fileSend = new File(sourcePath);
        InputStream inputStream = new FileInputStream(fileSend);
        BufferedInputStream bis = new BufferedInputStream(inputStream);
       // inetAddress = InetAddress.getByName(serverHost);
        byte[] bytePart = new byte[PIECES_OF_FILE_SIZE];
        
        // get file size
        long fileLength = fileSend.length();
        System.out.println("=== dung lý?ng file:"+fileLength+" Byte");
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
	    
	    return json;
	}
	public void MoKetNoiToiClient() throws Exception
	{
		   
			serverSocket = new DatagramSocket(serverPort);
		    while(true)
		    {
				 InetAddress inetAddress;
				 DatagramPacket sendPacket;
				System.out.println("============Node 2 ðang l?ng nghe k?t n?i t? client...");
	            // nh?n gói tin t? Client
	             byte[] receiveData = new byte[1024];
	            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	            serverSocket.receive(receivePacket);
			    String sentence = new String(receivePacket.getData(),"UTF-8");
			    System.out.println("================================= THONG BAO=====================================");
			    System.out.println(" ===Client k?t n?i thành công: " + sentence);
	            System.out.println(" ===Client yêu c?u dowloard file: " + sentence);
	            
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
	            
//		        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		        ObjectOutputStream oos = new ObjectOutputStream(baos);
//		        oos.writeObject(fileInfo);
//		        sendPacket = new DatagramPacket(baos.toByteArray(), 
//		                baos.toByteArray().length,IPAddress, clientPort);
//		        serverSocket.send(sendPacket);
	
		        // send file content
		        System.out.println("=== Ðang g?i File cho Client...");
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
		    	System.out.println("==== Ð? g?i thành công!!.");
			
		    }
		
       
	}
	public void waitMillisecond(long millisecond) {
	    try {
	        Thread.sleep(millisecond);
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }
	}
	public void Connect()
	{
		String ip;
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
			ArrayList<String>ds=new ArrayList<>();
			ds=LayDanhSachFile("data\\node2");
			Node node1=new Node(0, 222, "Node2,"+ip, ds);
			Gson gson=new Gson();
		     json=gson.toJson(node1);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	   // System.out.println(json);
	    try {
			Socket nodeSocket = new Socket(InetAddress.getByName(severname), 1992);
			PrintStream pstentruycap =new PrintStream(nodeSocket.getOutputStream());
			pstentruycap.println(json);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//	    Socket socket=null;
//	    try {
//			socket=new Socket("localhost", 1992);
//			PrintStream pstentruycap =new PrintStream(socket.getOutputStream());
//			pstentruycap.println(json);
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	    
	    
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("================================================================================================");
		System.out.println("                                       [NODE2 ÐANG CH?Y]");
		System.out.println("Port node :"+Node2.serverPort);
		try {
			System.out.println("IP node :"+InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("================================================================================================");
		System.out.print("nh?p ð?a ch? ip:");
        Scanner rc=new Scanner(System.in);
        severname=rc.nextLine();
		Node2 node1=new Node2();
        node1.Connect();
      
		try {
			node1.MoKetNoiToiClient();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
