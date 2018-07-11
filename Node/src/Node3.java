/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import com.google.gson.Gson;

public class Node3 {

    private static Integer getPort() {
        return 1742;
    }

    private static String dir() {
        return "Data\\Node3";
    }
    // udp
    private static final int PIECES_OF_FILE_SIZE = 1024 * 32;
    private DatagramSocket serverSocket;
    private byte[][] fileBytess;
    private int count;
    private FileInfo fileInfo;
    private BufferedInputStream bis;

    public String GetFileInfor(String file_name) throws Exception {
        File fileSend = new File(dir() + "\\" + file_name);
        InputStream inputStream = new FileInputStream(fileSend);
        bis = new BufferedInputStream(inputStream);
        // inetAddress = InetAddress.getByName(serverHost);
        byte[] bytePart = new byte[PIECES_OF_FILE_SIZE];

        // get file size
        long fileLength = fileSend.length();
        System.out.println(fileLength);
        int piecesOfFile = (int) (fileLength / PIECES_OF_FILE_SIZE);
        int lastByteLength = (int) (fileLength % PIECES_OF_FILE_SIZE);

        // check last bytes of file
        if (lastByteLength > 0) {
            piecesOfFile++;
        }

        // split file into pieces and assign to fileBytess
        fileBytess = new byte[piecesOfFile][PIECES_OF_FILE_SIZE];
        count = 0;
        while (bis.read(bytePart, 0, PIECES_OF_FILE_SIZE) > 0) {
            fileBytess[count++] = bytePart;
            bytePart = new byte[PIECES_OF_FILE_SIZE];
        }

        // read file info
        fileInfo = new FileInfo();
        fileInfo.setFilename(fileSend.getName());
        fileInfo.setFileSize(fileSend.length());
        fileInfo.setPiecesOfFile(piecesOfFile);
        fileInfo.setLastByteLength(lastByteLength);
        fileInfo.setDestinationDirectory(file_name);

        Gson gson = new Gson();
        String json = gson.toJson(fileInfo);

        return json;
    }

    public void MoKetNoiToiClient() throws Exception {
        //int count;
        serverSocket = new DatagramSocket(getPort());
        while (true) {
            //InetAddress inetAddress;
            DatagramPacket sendPacket;
            System.out.println("Node 1 is listening...");
            // nháº­n gÃ³i tin tá»« Client
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String sentence = new String(receivePacket.getData());
            System.out.println("From client: " + sentence);

            InetAddress IPAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();

            //táº¡o gÃ³i tin vÃ  gá»­i Client
            byte[] sendData = new byte[1024];
            //sourcePath="data\\node1\\"+sentence.trim();
            //destinationDir="dowloard\\"+sentence.trim();
            String fileInfor = GetFileInfor(sentence);
            sendData = fileInfor.getBytes();
            DatagramPacket sendPacketdata = new DatagramPacket(sendData, sendData.length, IPAddress, clientPort);
            serverSocket.send(sendPacketdata);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(fileInfo);
            sendPacket = new DatagramPacket(baos.toByteArray(),
                    baos.toByteArray().length, IPAddress, clientPort);
            serverSocket.send(sendPacket);

            // send file content
            System.out.println("Sending file...");
            // send pieces of file
            for (int i = 0; i < (count - 1); i++) {
                sendPacket = new DatagramPacket(fileBytess[i], PIECES_OF_FILE_SIZE,
                        IPAddress, clientPort);
                serverSocket.send(sendPacket);
                waitMillisecond(40);
            }
            // send last bytes of file
            sendPacket = new DatagramPacket(fileBytess[count - 1], PIECES_OF_FILE_SIZE,
                    IPAddress, clientPort);
            serverSocket.send(sendPacket);
            waitMillisecond(40);
            System.out.println("Sent.");

        }

    }

    public void waitMillisecond(long millisecond) {
        try {
            Thread.sleep(millisecond);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    // end udp

    /*
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
     */
    private String[] BuiltDirectoryChildName(File dir) {
        String[] paths = dir.list();
        return paths;
    }

    public static void main(String argv[]) throws Exception {
        //String link = "Data\\Node1";
        File dir = new File(dir());
        if (!dir.exists()) {
            System.out.println("Not found foldel");
            System.out.println("Created folder save file new: " + (dir.mkdirs() == true ? "success" : "failse"));
            //return;
        }
        Socket clientSocket = null;
        try {
            clientSocket = new Socket(InetAddress.getLocalHost().getHostName(), getPort());
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());

            oos.writeObject("0");

            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            String serverSend = ois.readObject().toString();
            System.out.println(serverSend);
            Node3 a = new Node3();
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
        } finally {

            clientSocket.close();
        }

    }
}
