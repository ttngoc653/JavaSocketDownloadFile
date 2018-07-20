import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Client {
	private static final int PIECES_OF_FILE_SIZE = 1024 * 32;
	private static DatagramSocket clientSocket;
	public static ArrayList<TenFile>dsFile=new ArrayList<>();
	public static int portConnect=1742;
	public static String severname="";
	public static String severnode;
	private static Scanner sc = new Scanner(System.in);;
	public static void ConnectServer()
	{
		Node node1=new Node(1,0, "Client", null);
		Gson gson=new Gson();
	    String json=gson.toJson(node1);
	  //  System.out.println(json);
	    
	    Socket socket=null;
	    try {
			socket=new Socket(InetAddress.getByName(severname), portConnect);
			PrintStream pstentruycap =new PrintStream(socket.getOutputStream());
			pstentruycap.println(json);
			
			BufferedReader brNode=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String requestNode=brNode.readLine();
		//	System.out.println(requestNode);
			
		    gson=new Gson();

			java.lang.reflect.Type type=new TypeToken<ArrayList<TenFile>>(){}.getType();
			dsFile=gson.fromJson(requestNode,type);
			if(dsFile.size()>0)
			{
				hienThiVaLuaChonDowloardFile();
				
			}
			else
			{
				System.out.println("File list is empty!!");
			}
			
			} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void hienThiVaLuaChonDowloardFile() {
		// TODO Auto-generated method stub
		System.out.println();
		boolean kt=false;;

		do{
			System.out.println("================ Please enter the following code to download: ====================");
			{
				for (int i = 0; i < dsFile.size(); i++) {
					System.out.format("( "+String.valueOf(i+1)+"\t )---- FILE: "+dsFile.get(i).getTenfile());
				}
			}

			int chon;
			System.out.print("Enter the code you want to download: ");
			
			chon=Integer.parseInt(sc.nextLine());
			int size=dsFile.size();
			if(chon>=1 && chon<=size)
			{
				kt=false;
				TenFile fileChonDowloard=dsFile.get(chon-1);
				
				System.out.println("============================ NOTIFY =====================");

				System.out.println("====> Downloading file "+fileChonDowloard.getTenfile());
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
				System.out.println("The file you selected is not valid. \nPlease enter the file number from 1 to "+size+" to download the file.");
			}
		}while(kt);
			
	}
	private static void ConnectNode(TenFile fileChonDowloard) throws Exception {
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
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,IPAddress, Integer.parseInt(words[2]));
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
            System.out.println("= Selected file: " + fileInfo.getFilename());
            System.out.println("= Total file size: " + fileInfo.getFileSize()+"Byte");
            //System.out.println("Tong so file nho: " + (fileInfo.getPiecesOfFile()-1));
           
        }
        else
        {
        	  System.out.println("File name: file not fount in node");
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
            System.out.println("*************************Dowloard [ "+i*100/m+"% ]****************************");
            bos.write(receiveData2, 0, PIECES_OF_FILE_SIZE);
        }
        // write last bytes of file
        receivePacket = new DatagramPacket(receiveData2, receiveData2.length, 
        		IPAddress, fileChonDowloard.getPort());
        clientSocket.receive(receivePacket);
        System.out.println("Finished downloading the file...");
      //  System.out.println("Dowloard f...");
        bos.write(receiveData2, 0, fileInfo.getLastByteLength());
        bos.flush();
        bos.close();
        System.out.println("===========================DONE================================");
			}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("================================================================================================");
		System.out.println("                                       [CLIENT RUNNING]");

		
		System.out.println("================================================================================================");
		System.out.print("Enter the IP address of the server: ");
        severname=sc.nextLine();
		
		String chon="Y";
		boolean kt=true;
		do
		{
			

			System.out.println("Do you want to continue (Y - Yes or N - No): ");
			chon=sc.nextLine();
			if(!chon.equals("N")&& !chon.equals("Y"))
			{
				kt=true;
				System.out.println("========== NOTIFY ============");
				System.out.println("Only enter Y or N!");
			}else
			{
				if(chon.equals("N"))
				{
					kt=false;
					System.out.println("====== END ============");
				}
				else
				{
					ConnectServer();
					kt=true;
					dsFile=new ArrayList<>();
				}
			}
			

		}while(kt);
		
	}

}
