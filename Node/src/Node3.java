/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Tu Huynh
 */
public class Node3{
    
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
        
        String sentence = "D:\\GitHub\\JavaSocketDownloadFile\\Node\\Node\\Data\\Node3";
        String port = "3333";
        String modifiedSentence;

        Socket clientSocket = new Socket("localhost", 6789);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        outToServer.writeBytes(port + '\n');
        Node3 a = new Node3();
        File dir = new File(sentence);
        String[] Name = a.BuiltDirectoryChildName(dir);
        for(String name : Name){
            outToServer.writeBytes(name + '\n');
        }
        
//        modifiedSentence = inFromServer.readLine();
//        System.out.println("SERVER: " + modifiedSentence);
//        modifiedSentence = inFromServer.readLine();
//        System.out.println("        " + modifiedSentence);
        
        clientSocket.close();
    }
}
