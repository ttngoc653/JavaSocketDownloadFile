
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class mainServer {

    private static ServerSocket svr;
    private static ArrayList<Socket> list_node;
    private static ArrayList<String> list_file;

    private static ObjectInputStream ois = null;
    private static ObjectOutputStream oos = null;
    private static String file_name = null;
    
    private static boolean softListFileName() {
        try {
            String temp = "";
            for (int i = 0; i < list_file.size() - 1; i++) {
                for (int j = i + 1; j < list_file.size(); j++) {
                    if (list_file.get(i).split("\t")[2].compareToIgnoreCase(list_file.get(j).split("\t")[2]) > 0) {
                        temp = list_file.get(i);
                        list_file.set(i, list_file.get(j));
                        list_file.set(j, temp);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
    
    private static boolean connectedNode(Socket sk){
    	try {
	        int no_file_from_none = 0;
			oos.writeObject("accepted");
			// TODO Auto-generated catch block
			System.out.println("Node connected!!!");
	        sk.getInetAddress().toString();
	        sk.getPort();
	
	        // nhận tên file đầu tiên
				file_name = ois.readObject().toString();
	
	        while (!file_name.equals("end")) {
	            no_file_from_none++;
	            System.out.println("Received file name: " + file_name);
	
	            list_file.add(sk.getInetAddress().toString() + "\t" + sk.getPort() + "\t" + file_name);
	            //System.out.println(sk.getInetAddress().toString()+"_"+sk.getPort()+"_"+file_name);
	
	            oos.writeObject("was");
	            file_name = ois.readObject().toString();
	        }
	
	        softListFileName();
	
	        if (no_file_from_none > 0) {
	            list_node.add(sk);
	        }
	
	        no_file_from_none = 0;

		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;	
		}
		return true;
}
    
    private static boolean connectedClient() {
        System.out.print("Cliend connected!!!\t");
        int dem=0;
        try {
        for (String str : list_file) {
            for (Socket sk1 : list_node) {
                if (sk1.isConnected() && str.split("\t")[1].equals(String.valueOf(sk1.getPort()))) {
                    oos.writeObject(str);
                    System.out.println("sent file name: "+str);
                    ois.readObject();
                    dem++;
                }
            }
        }
        System.out.println(dem);
        oos.writeObject("end");
        }catch (Exception e) {
			e.printStackTrace();
			return false;
		}
        System.out.println("Receive all file name and IP + port to client");
		return true;
	}
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        svr = new ServerSocket(1742);
        Socket sk = null;

        list_node = new ArrayList<>();
        list_file = new ArrayList<>();
        System.out.println("IP server: " + svr.getInetAddress().getHostAddress());
        System.out.println("Port server: " + svr.getLocalPort());
        while (true) {
            System.out.println("Waiting for connection...");
            sk = svr.accept();

            //System.out.println("DA NHAN 1 MAY!");
            ois = new ObjectInputStream(sk.getInputStream());
            String type = ois.readObject().toString();
            //System.out.println(type);

            oos = new ObjectOutputStream(sk.getOutputStream());
            //oos.writeObject("accepted");
            // if sk is node
            try {
                if (type.equals("0")) {
                    connectedNode(sk);
                } else if (type.equals("1")) {
                	connectedClient();
                }

                oos.close();
                ois.close();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
				sk.close();
			}
        }
    }
}
