import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Client {
    private static ArrayList<Object> list_file_info;
	private static Socket client;
	private static int PIECES_OF_FILE_SIZE=1024 * 32;
	private static DatagramSocket clientSocket;
	private static BufferedOutputStream bos;
	private static Scanner input;

	private static void ConnectNode(String fileChonDownload){
		try {
			// TODO Auto-generated method stub
			 byte[] sendData = new byte[1024];
			 byte[] receiveData = new byte[1024];
			 byte[] receiveData2 = new byte[PIECES_OF_FILE_SIZE];
			 //DatagramPacket receivePacket;
			 sendData=fileChonDownload.split("\t")[2].getBytes();
			 clientSocket = new DatagramSocket();
	        // Tao goi tin goi di thang qua IP address va  port bat ky > 1023
	        InetAddress IPAddress = InetAddress.getLocalHost();
	        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, Integer.parseInt(fileChonDownload.split("\t")[1]));
	        clientSocket.send(sendPacket);
	        
	        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	        clientSocket.receive(receivePacket);
	        String modifiedSentence = new String(receivePacket.getData());
	        System.out.println("From server: " + modifiedSentence);
	        
	        Gson gson=new Gson();
	        FileInfo fileInfo=new FileInfo();
	        java.lang.reflect.Type type=new TypeToken<FileInfo>(){}.getType();
	        fileInfo=gson.fromJson(modifiedSentence.trim(),type);
	        if (fileInfo != null) {
	            System.out.println("File name: " + fileInfo.getFilename());
	            System.out.println("File size: " + fileInfo.getFileSize());
	            System.out.println("Pieces of file: " + fileInfo.getPiecesOfFile());
	            System.out.println("Last bytes length: "+ fileInfo.getLastByteLength());
	        }
	        else
	        {
	        	  System.out.println("File name: null ");
	        }
	        // get file content
	       
	        File fileReceive = new File(fileInfo.getDestinationDirectory());
	        bos = new BufferedOutputStream(
	                new FileOutputStream(fileReceive));
	        // write pieces of file
	        for (int i = 0; i < (fileInfo.getPiecesOfFile() - 1); i++) {
	            receivePacket = new DatagramPacket(receiveData2, receiveData2.length, 
	            		IPAddress, Integer.parseInt(fileChonDownload.split("\t")[1]));
	            clientSocket.receive(receivePacket);
	            System.out.println("Receiving file..."+(i+1));
	            bos.write(receiveData2, 0, PIECES_OF_FILE_SIZE);
	        }
	        // write last bytes of file
	        receivePacket = new DatagramPacket(receiveData2, receiveData2.length, 
	        		IPAddress, Integer.parseInt(fileChonDownload.split("\t")[1]));
	        clientSocket.receive(receivePacket);
	        System.out.println("Receiving file Done...");
	        bos.write(receiveData2, 0, fileInfo.getLastByteLength());
	        bos.flush();
	        System.out.println("Done!");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}


	
	public static void main(String[] args) {
    	try {
			client = new Socket(InetAddress.getLocalHost().getHostName(), 1742);
			
            ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
            oos.writeObject("1");

            ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
            
            Object server_send="";
            list_file_info = new ArrayList<>();
        	
            do {
            	server_send = ois.readObject().toString();
            	
            	list_file_info.add(server_send);
            	
            	if(server_send.equals("end"))
            		break;
            	
            	oos.writeObject(server_send);
            }while(!server_send.equals("end"));
			
            
            for (int i = 0; i < list_file_info.size() - 1; i++) {
				System.out.println(i+". "+list_file_info.get(i).toString().split("\t")[2]);
			}
            
            System.out.println("HAY CHON SO TUONG UNG DE DOWNLOAD FILE: ");
            input = new Scanner(System.in);
            int number=input.nextInt();
            
            while(true) {
            	
        		//TenFile fileChonDowloard=new TenFile();
            	String file_choose = (String) list_file_info.get(number);
        		System.out.println("Downloading file "+file_choose.split("\t")[2]);
        		try {
        			ConnectNode(file_choose);
        		} catch (Exception e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}

        		number=Integer.parseInt(input.nextLine());

            }
            
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} 
    }
    
}
