/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.*;
import java.net.*;

/**
 *
 * @author Tu Huynh
 */
public class Server{    
        
    public static void main(String argv[]) throws Exception {
        
        String clientSentence;
        String capitalizedSentence;
        ServerSocket welcomeSocket = new ServerSocket(6789);
        System.out.println("Server mở kết nối port 6789");

        while (true) {
            Socket connectionSocket = welcomeSocket.accept();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream(),"UTF-8"));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            
            System.out.println("Node: ");
            String line;

            while ((line = inFromClient.readLine()) != null) {
                System.out.println("     " + line);
                try {
                    File file = new File("node.txt");
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileWriter fw = new FileWriter(file.getName(), true);
                        
                    try {
                        Integer.parseInt(line);
                        fw.write('\n' + line + ',');
                        fw.close();
                    } catch (NumberFormatException e) {
                        fw.write(line + ' ');
                        fw.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            
        }

    }
}
