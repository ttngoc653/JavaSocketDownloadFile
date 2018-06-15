/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Node;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Tu Huynh
 */
public class Node2{
    
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
        
        String link = "D:\\GitHub\\JavaSocketDownloadFile\\Node\\Node\\Data\\Node2";
        String port = "2222";
        String modifiedSentence;

        Socket clientSocket = new Socket("localhost", 6789);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());        
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));

        outToServer.writeUTF(port + '\n');
        Node2 a = new Node2();
        File dir = new File(link);
        String[] Name = a.BuiltDirectoryChildName(dir);
        for(String name : Name){
            outToServer.writeUTF(name + '\n');
        }        
        
        clientSocket.close();
    }
}
