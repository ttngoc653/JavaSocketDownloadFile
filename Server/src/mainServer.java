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
	
	private static ObjectInputStream ois=null;
	private static ObjectOutputStream oos=null;
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		svr = new ServerSocket(1742);
		Socket sk=null;
		list_node = new ArrayList<>();
		list_file = new ArrayList<>();
		System.out.println("IP server: "+svr.getInetAddress().getHostAddress());
		System.out.println("Port server: "+svr.getLocalPort());
		while (true) {
			System.out.println("CHỜ KẾT NỐI TỚI...");
			sk=svr.accept();
			
			System.out.println("DA NHAN 1 MAY!");
			ois=new ObjectInputStream(sk.getInputStream());
			String type=ois.readObject().toString();
			System.out.println(type);

			oos=new ObjectOutputStream(sk.getOutputStream());
			oos.writeObject("accepted");
			// if sk is node
			if(type.equals("0")) {
				System.out.println("Node connected!!!");
				sk.getInetAddress().toString();
				sk.getPort();
				
				String file_name=ois.readObject().toString();

				System.out.println(file_name);
				while (!file_name.equals("end")) {
					System.out.println(file_name);
					list_file.add(sk.getInetAddress().toString()+"_"+sk.getPort()+"_"+file_name);
					oos.writeObject("was");
					System.out.println(sk.getInetAddress().toString()+"_"+sk.getPort()+"_"+file_name);
					file_name=ois.readObject().toString();
				}
				list_node.add(sk);
				oos.close();
				ois.close();
			}else if(type.equals("1")) {
				for (String i : list_file) {
					// making
				}
			}
		}
	}
}
/*/
public class mainServer {
    
    //static ServerSocket variable
    private static ServerSocket server;
    //socket server port on which it will listen
    private static int port = 9876;
    private static ObjectInputStream ois=null;
    private static ObjectOutputStream oos=null;
    public static void main(String args[]) throws IOException, ClassNotFoundException{
        //create the socket server object
        server = new ServerSocket(port);
        //keep listens indefinitely until receives 'exit' call or program terminates
        while(true){
            System.out.println("Waiting for client request");
            //creating socket and waiting for client connection
            Socket socket = server.accept();
            //read from socket to ObjectInputStream object
            ois = new ObjectInputStream(socket.getInputStream());
            //convert ObjectInputStream object to String
            String message = (String) ois.readObject();
            System.out.println("Message Received: " + message);
            //create ObjectOutputStream object
            oos = new ObjectOutputStream(socket.getOutputStream());
            //write object to Socket
            oos.writeObject("Hi Client "+message);
            //close resources
            ois.close();
            oos.close();
            socket.close();
            //terminate the server if client sends exit request
            if(message.equalsIgnoreCase("exit")) break;
        }
        System.out.println("Shutting down Socket server!!");
        //close the ServerSocket object
        server.close();
    }
    
}*/