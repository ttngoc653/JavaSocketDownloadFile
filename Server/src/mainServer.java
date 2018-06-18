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
	private static String file_name=null;
	private static String port_udp_of_note=null;
	
	private static boolean softListFileName(){
		try {
			String temp="";
			for (int i = 0; i < list_file.size()-1; i++)
				for (int j = i+1; j < list_file.size(); j++) 
					if(list_file.get(i).split("\t")[2].compareToIgnoreCase(list_file.get(j).split("\t")[2])<0) {
						temp=list_file.get(i);
						list_file.set(i, list_file.get(j));
						list_file.set(j, temp);
					}	
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		svr = new ServerSocket(1742);
		Socket sk=null;
		int no_file_from_none=0;
		
		list_node = new ArrayList<>();
		list_file = new ArrayList<>();
		System.out.println("IP server: "+svr.getInetAddress().getHostAddress());
		System.out.println("Port server: "+svr.getLocalPort());
		while (true) {
			System.out.println("Waiting for connection...");
			sk=svr.accept();
			
			//System.out.println("DA NHAN 1 MAY!");
			ois=new ObjectInputStream(sk.getInputStream());
			String type=ois.readObject().toString();
			//System.out.println(type);

			oos=new ObjectOutputStream(sk.getOutputStream());
			//oos.writeObject("accepted");
			// if sk is node
			try {
				if(type.equals("0")) {
					oos.writeObject("accepted");
					System.out.println("Node connected!!!");
					sk.getInetAddress().toString();
					sk.getPort();
					
					// nhận tên file đầu tiên
					file_name=ois.readObject().toString();
					
					while (!file_name.equals("end")) {
						no_file_from_none++;
						System.out.println("Received file name: "+file_name);
						
						list_file.add(sk.getInetAddress().toString()+"\t"+sk.getPort()+"\t"+file_name);
						//System.out.println(sk.getInetAddress().toString()+"_"+sk.getPort()+"_"+file_name);
						
						oos.writeObject("was");
						file_name=ois.readObject().toString();
					}
					
					softListFileName();
					
					if(no_file_from_none>0)
						list_node.add(sk);
					
					no_file_from_none=0;
				}else if(type.equals("1")) {
					System.out.print("Cliend connected!!!\t");

					for (String str : list_file)
						for (Socket sk1 : list_node)
							if(!sk1.isClosed() && str.split("\t")[1].equals(String.valueOf(sk1.getPort()))) {
								oos.writeObject(str);
										
								ois.readObject();
							}
					
					System.out.println("Sent all file name and IP + port (UDP of node) to client");
				}

				oos.close();
				ois.close();
			} catch (Exception e) {
				e.printStackTrace();
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