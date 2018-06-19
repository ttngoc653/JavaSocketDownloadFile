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
public class Node1 {

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
        return paths;
    }

    public static void main(String argv[]) throws Exception {
        String link = "Data\\Node1";
        File dir = new File(link);
        if (!dir.exists()) {
            System.out.println("Not found foldel");
            System.out.println("Created folder save file new: " + (dir.mkdirs() == true ? "success" : "failse"));
            //return;
        }
        Socket clientSocket = null;
        try {
            clientSocket = new Socket(InetAddress.getLocalHost().getHostName(), 1742);
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());

            oos.writeObject("0");

            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            String serverSend = ois.readObject().toString();
            System.out.println(serverSend);
            Node1 a = new Node1();
            String[] nameFile = a.BuiltDirectoryChildName(dir);

            for (String name : nameFile) {
                oos.writeObject(name);

                if (ois.readObject().toString().equalsIgnoreCase("was")) {
                    System.out.println("ĐÃ GỬI FILE " + name);
                }
            }
            oos.writeObject("end");

            ois.close();
            oos.close();
            
            System.out.println("waiting client connect to...");
            
            while (true) {
            	// code of udp
            	
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {

            clientSocket.close();
		}

    }
}