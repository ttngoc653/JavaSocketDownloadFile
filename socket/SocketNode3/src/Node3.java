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

public class Node3 {
	public static final int PIECES_OF_FILE_SIZE = 1024 * 32;
	public DatagramSocket serverSocket;
	public static int serverPort =333;
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
        	System.out.println("==NODE 3 đang lưu trữ các file:");
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
	    
	    return json;
	}
	public void MoKetNoiToiClient() throws Exception
	{
		   
			serverSocket = new DatagramSocket(serverPort);
		    while(true)
		    {
				 InetAddress inetAddress;
				 DatagramPacket sendPacket;
				System.out.println("============Node 3 đang lắng nghe kết nối từ client...");
	            // nhận gói tin từ Client
	             byte[] receiveData = new byte[1024];
	            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	            serverSocket.receive(receivePacket);
			    String sentence = new String(receivePacket.getData(),"UTF-8");
				System.out.println("================================= THONG BAO=====================================");
			    System.out.println(" ===Client kết nối thành công: " );
	            System.out.println(" ===Client yêu cầu dowloard file: " + sentence);
	            
	            InetAddress IPAddress = receivePacket.getAddress();
	            int clientPort = receivePacket.getPort();
	 
	            //tạo gói tin và gửi Client
	              byte[] sendData = new byte[1024];
	              sourcePath="data\\node3\\"+sentence.trim();
	              destinationDir="Socketdowloard\\"+sentence.trim();
	              String fileInfor=GetFileInfor();
	              sendData = fileInfor.getBytes();
	            DatagramPacket sendPacketdata = new DatagramPacket(sendData, sendData.length, IPAddress, clientPort);
	            serverSocket.send(sendPacketdata);
	            
//		     
	
		        // send file content
	            
		        System.out.println("=== Đang gửi File cho Client...");
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
		    	System.out.println("==== Đã gửi thành công.");
			
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
		try {
			String ip=InetAddress.getLocalHost().getHostAddress();
			ArrayList<String>ds=new ArrayList<>();
			ds=LayDanhSachFile("data\\node3");
			Node node1=new Node(0, 333, "Node3,"+ip, ds);
			Gson gson=new Gson();
		     json=gson.toJson(node1);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	   // System.out.println(json);
	    
	    Socket socket=null;
	    try {
			socket=new Socket(InetAddress.getByName(severname), 1992);
			PrintStream pstentruycap =new PrintStream(socket.getOutputStream());
			pstentruycap.println(json);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    
	    
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("================================================================================================");
		System.out.println("                                       [NODE3 ĐANG CHẠY]");
		System.out.println("Port node :"+Node3.serverPort);

		System.out.println("================================================================================================");
		System.out.print("nhập địa chỉ ip:");
        Scanner rc=new Scanner(System.in);
        severname=rc.nextLine();
		Node3 node1=new Node3();
        node1.Connect();
      
		try {
			node1.MoKetNoiToiClient();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
