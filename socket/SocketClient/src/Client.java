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
import java.util.Collections;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Client {
	private static final int PIECES_OF_FILE_SIZE = 1024 * 32;
	private DatagramSocket clientSocket;
	public static ArrayList<Node>dsNode=new ArrayList<>();
	public static ArrayList<TenFile>dsFile=new ArrayList<>();
	public static int portConnect=0;
	public static String severname="172.20.10.5";
	public static String severnode;
	public void ConnectServer()
	{
		Node node1=new Node(1,0, "Client", null);
		Gson gson=new Gson();
	    String json=gson.toJson(node1);
	  //  System.out.println(json);
	    
	    Socket socket=null;
	    try {
			socket=new Socket(InetAddress.getByName(severname), 1992);
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
				System.out.println("danh sach file rong!!");
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
		boolean kt=false;;

		do{
			System.out.println("================Hay lua chon file ban muon dowloard !!====================");
			{
				for(TenFile t:dsFile)
				{
					System.out.println("( "+t.getStt()+" )---- FILE: "+t.getTenfile());
				}
				
			}

			int chon;
			System.out.print("Moi ban chon :");
			Scanner sc=new Scanner(System.in);
			chon=Integer.parseInt(sc.nextLine());
			int size=dsFile.size();
			if(chon>=1 && chon<=size)
			{
				kt=false;
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
			else
			{
				kt=true;
				System.out.println("gia tri ban duoc chon chi tu 1 den "+size);
				System.out.println("xin hay chon lai !!!");
			}
		}while(kt);
		


		
	
	}
	private void ConnectNode(TenFile fileChonDowloard) throws Exception {
		// TODO Auto-generated method stub
		 byte[] sendData = new byte[1024];
		 byte[] receiveData = new byte[1024];
		 byte[] receiveData2 = new byte[PIECES_OF_FILE_SIZE];
		 String[] words=new String[10];
		 words=fileChonDowloard.getTennode().split(",");
		 
			 severnode=words[1];
		 
		 
//		    DatagramPacket receivePacket;
		 sendData=fileChonDowloard.getTenfile().getBytes();
		 clientSocket = new DatagramSocket();
        // T?o gói tin g?i ði thông qua IP address và port b?t k? > 1023
       InetAddress IPAddress = InetAddress.getByName(severnode);
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,IPAddress, fileChonDowloard.getPort());
        clientSocket.send(sendPacket);
        
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String modifiedSentence = new String(receivePacket.getData(),"UTF-8");
        System.out.println("From server: " + modifiedSentence);
        
        Gson gson=new Gson();
        FileInfo fileInfo=new FileInfo();
        java.lang.reflect.Type type=new TypeToken<FileInfo>(){}.getType();
        fileInfo=gson.fromJson(modifiedSentence.trim(),type);
        if (fileInfo != null) {
            System.out.println("= Ban can dowloard file: " + fileInfo.getFilename());
            System.out.println("= Tong dung luong la: " + fileInfo.getFileSize()+"Byte");
            System.out.println("Tong so file nho: " + (fileInfo.getPiecesOfFile()-1));
           
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
        for (int i = 1; i <= m; i++) {
            receivePacket = new DatagramPacket(receiveData2, receiveData2.length, 
            		IPAddress, fileChonDowloard.getPort());
            clientSocket.receive(receivePacket);
            System.out.println("*************************Dowloard [ "+(i)+"/"+m+" ]****************************");
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
        bos.close();
        System.out.println("===========================DONE================================");
			}

	private void getAllFileName() {
		int i=1;
		// TODO Auto-generated method stub
		for(Node n:dsNode)
		{
			for(String s:n.getDanhsachFile())
			{
				TenFile t=new TenFile(0, n.getPort(), n.getTen(), s);
				dsFile.add(t);
				
				
			}
		}
		Collections.sort(dsFile);
		for(TenFile n:dsFile)
		{
			n.setStt(i);
			i++;
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("================================================================================================");
		System.out.println("                                       [CLIENT DANG CHAY]");

		
		System.out.println("================================================================================================");
		System.out.print("nhap dia chi ip sever:");
        Scanner rc=new Scanner(System.in);
        severname=rc.nextLine();
		

//		System.out.print("nh?p ð?a ch? ip Nodesever:");
//       
//        severnode=rc.nextLine();
		Client client=new Client();
		String chon="Y";
		boolean kt=true;
		do
		{
			

			System.out.println("Ban co muon tiep tuc [Y]:Yes or [N]:No ");
			System.out.print("moi ban chon :");
			Scanner sc=new Scanner(System.in);
			chon=sc.nextLine();
			if(!chon.equals("N")&& !chon.equals("Y"))
			{
				kt=true;
				System.out.println("==========Thong bao============");
				System.out.println("chi duoc nhap Y or N ");
			}else
			{
				if(chon.equals("N"))
				{
					kt=false;
					System.out.println("====== KET THÚC ============");
				}
				else
				{
					client.ConnectServer();
					kt=true;
					dsFile=new ArrayList<>();
				}
			}
			

		}while(kt);
		
	}

}
