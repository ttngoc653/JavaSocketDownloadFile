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
public class Node1{
    
    //Liá»‡t kÃª Ä‘Æ°á»�ng dáº«n cá»§a táº¥t cáº£ cÃ¡c file trong thÆ° má»¥c
    private void PrintDirectoryChildPath(File dir) {
        File[] children = dir.listFiles();
        for (File child : children) {
            System.out.println(child.getAbsolutePath()); //getAbsolutePath: Láº¥y Ä‘Æ°á»�ng dáº«n ra
        }
    }

    //Liá»‡t kÃª tÃªn cá»§a táº¥t cáº£ cÃ¡c file trong thÆ° má»¥c 
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
    	File dir = new File(link);
        if(dir.exists()) {
        	System.out.println("not found foldel");
        	return;
        }
        
        String link = "Node\\Data\\Node1";
        String port = "1742";
        String modifiedSentence;

        Socket clientSocket = new Socket("localhost", Integer.valueOf(port));
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());        
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));

        outToServer.writeUTF(port + '\n');
        Node1 a = new Node1();
        String[] Name = a.BuiltDirectoryChildName(dir);
        for(String name : Name){
            outToServer.writeUTF(name + '\n');
        }        
        
        clientSocket.close();
    }
}
