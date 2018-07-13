import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Client {
	private static final int PIECES_OF_FILE_SIZE = 1024 * 32;
	private DatagramSocket clientSocket;
	public static ArrayList<Node>dsNode=new ArrayList<>();
	public static ArrayList<TenFile>dsFile=new ArrayList<>();
	public static int portConnect=0;
	public void ConnectServer()
	{
		Node node1=new Node(1,0, "Client", null);
		Gson gson=new Gson();
	    String json=gson.toJson(node1);
	  //  System.out.println(json);
	    
	    Socket socket=null;
	    try {
			socket=new Socket(InetAddress.getLocalHost().getHostName(), 1992);
			PrintStream pstentruycap =new PrintStream(socket.getOutputStream());
			pstentruycap.println(json);
			
			BufferedReader brNode=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String requestNode=brNode.readLine();
		//	System.out.println(requestNode);
			
		    gson=new Gson();

			java.lang.reflect.Type type=new TypeToken<ArrayList<Node>>(){}.getType();
			dsNode=gson.fromJson(requestNode,type);
			if(dsNode.size()>0)
			{
				hienThiVaLuaChonDowloardFile();
				
			}
			else
			{
				System.out.println("danh sach file rỗng!!");
			}
			
			} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void hienThiVaLuaChonDowloardFile() {
		// TODO Auto-generated method stub
		getAllFileName();
		System.out.println();
		boolean kt=true;

		System.out.println("================Hay lua chon file ban muon dowloard !!====================");
		{
			for(TenFile t:dsFile)
			{
				System.out.println(t.getStt()+"-"+t.getTenfile());
			}
			
		}

		int chon;
		System.out.print("Moi ban chon :");
		Scanner sc=new Scanner(System.in);
		chon=Integer.parseInt(sc.nextLine());


		TenFile fileChonDowloard=new TenFile();
		for(TenFile t:dsFile)
		{
			if(t.getStt()==chon)
			{
				fileChonDowloard=t;
				break;
			}
		}
		System.out.println("============================ THONG BAO =====================");

		System.out.println("====> Dang Dowloard file "+fileChonDowloard.getTenfile());
		try {
			ConnectNode(fileChonDowloard);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	private void ConnectNode(TenFile fileChonDowloard) throws Exception {
		// TODO Auto-generated method stub
		 byte[] sendData = new byte[1024];
		 byte[] receiveData = new byte[1024];
		 byte[] receiveData2 = new byte[PIECES_OF_FILE_SIZE];
//		    DatagramPacket receivePacket;
		 sendData=fileChonDowloard.getTenfile().getBytes();
		 clientSocket = new DatagramSocket();
        // Tạo gói tin gửi đi thông qua IP address và port bất kì > 1023
        InetAddress IPAddress = InetAddress.getLocalHost();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, fileChonDowloard.getPort());
        clientSocket.send(sendPacket);
        
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String modifiedSentence = new String(receivePacket.getData());
        //System.out.println("From server: " + modifiedSentence);
        
        Gson gson=new Gson();
        FileInfo fileInfo=new FileInfo();
        java.lang.reflect.Type type=new TypeToken<FileInfo>(){}.getType();
        fileInfo=gson.fromJson(modifiedSentence.trim(),type);
        if (fileInfo != null) {
            System.out.println("= Ban can dowloard file: " + fileInfo.getFilename());
            System.out.println("= Tong dung luong la: " + fileInfo.getFileSize()+"Byte");
           // System.out.println("Pieces of file: " + fileInfo.getPiecesOfFile());
           // System.out.println("Last bytes length: "+ fileInfo.getLastByteLength());
        }
        else
        {
        	  System.out.println("File name:null ");
        }
        // get file content
       
        File fileReceive = new File(fileInfo.getDestinationDirectory());
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(fileReceive));
        // write pieces of file
        int m=fileInfo.getPiecesOfFile() - 1;
        for (int i = 0; i < m; i++) {
            receivePacket = new DatagramPacket(receiveData2, receiveData2.length, 
            		IPAddress, fileChonDowloard.getPort());
            clientSocket.receive(receivePacket);
            System.out.println("---Dowloard [ "+(i+1)+"/"+m+" ]");
            bos.write(receiveData2, 0, PIECES_OF_FILE_SIZE);
        }
        // write last bytes of file
        receivePacket = new DatagramPacket(receiveData2, receiveData2.length, 
        		IPAddress, fileChonDowloard.getPort());
        clientSocket.receive(receivePacket);
        System.out.println("Dowloard file Done...");
      //  System.out.println("Dowloard f...");
        bos.write(receiveData2, 0, fileInfo.getLastByteLength());
        bos.flush();
        System.out.println("Done!");
			}

	private void getAllFileName() {
		int i=1;
		// TODO Auto-generated method stub
		for(Node n:dsNode)
		{
			for(String s:n.getDanhsachFile())
			{
				TenFile t=new TenFile(i, n.getPort(), n.getTen(), s);
				dsFile.add(t);
				i++;
			}
		}
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("================================================================================================");
		System.out.println("                                       [CLIENT DANG CHAY]");

		System.out.println("================================================================================================");
		Client client=new Client();

		String chon="Y";
		boolean kt=true;
		do
		{
			client.ConnectServer();

			System.out.println("Ban co muon tiep tuc [Y]:Yes or [N]:No ");
			System.out.println("moi ban chon :");
			Scanner sc=new Scanner(System.in);
			chon=sc.nextLine();
			if(chon.equals("N"))
			{
				kt=false;
				System.out.println("====== KẾT THÚC ============");
			}
			else
			{
				kt=true;
				dsFile=new ArrayList<>();
			}

		}while(kt);
		
	}

}
