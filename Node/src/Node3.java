/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author Tu Huynh
 */
public class Node3{
    
    //Liệt kê đường dẫn của tất cả các file trong thư mục
    private void PrintDirectoryChildPath(File dir) {
        File[] children = dir.listFiles();
        for (File child : children) {
            System.out.println(child.getAbsolutePath()); //getAbsolutePath: Lấy đường dẫn ra
        }
    }

    //Liệt kê tên của tất cả các file trong thư mục 
    private void PrintDirectoryChildName(File dir) {
        String[] paths = dir.list();
        for (String path : paths) {
            System.out.println(path);
        }
    }
    
    private String[] BuiltDirectoryChildName(File dir) {
        String[] paths = dir.list();
        return  paths;
    }
    
    
    public static void main(String argv[]) throws Exception {    
        String link = "Data\\Node3";
        File dir = new File(link);
        if(!dir.exists()) {
        	System.out.println("Not found foldel");
        	System.out.println("Created folder save file new: "+(dir.mkdirs()==true?"success":"failse"));
        	//return;
        }
        Socket clientSocket=null;
        try {
            clientSocket = new Socket(InetAddress.getLocalHost().getHostName(),1742);
            ObjectOutputStream oos=new ObjectOutputStream(clientSocket.getOutputStream());
            
            oos.writeObject("0");
            
            ObjectInputStream ois=new ObjectInputStream(clientSocket.getInputStream());
            String serverSend=ois.readObject().toString();
            System.out.println(serverSend);
            Node3 a = new Node3();
            String[] nameFile = a.BuiltDirectoryChildName(dir);
            for(String name : nameFile){
            	oos.writeObject(name);
            	
            	if(ois.readObject().toString()=="was")
            	System.out.println("ĐÃ GỬI FILE "+name);
            }
            oos.writeObject("end");
            
            ois.close();
            oos.close();
            while(true) {
            	
            }
		} catch (Exception e) {
			e.printStackTrace();
	        clientSocket.close();
		}

        
    }
}
/*/
public class Node1 {

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException{
        //get the localhost IP address, if server is running on some other IP, you need to use that
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        for(int i=0; i<5;i++){
            //establish socket connection to server
            socket = new Socket(host.getHostName(), 9876);
            //write to socket using ObjectOutputStream
            oos = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Sending request to Socket Server");
            if(i==4)oos.writeObject("exit");
            else oos.writeObject(""+i);
            //read the server response message
            ois = new ObjectInputStream(socket.getInputStream());
            String message = (String) ois.readObject();
            System.out.println("Message: " + message);
            //close resources
            ois.close();
            oos.close();
            //Thread.sleep(10000);
        }
    }
}*/