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

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		svr = new ServerSocket(1742);
		Socket sk=null;
		list_node = new ArrayList<>();
		list_file = new ArrayList<>();
		while (true) {
			System.out.println("CHỜ KẾT NỐI TỚI...");
			sk=svr.accept();
			
			ObjectInputStream ois=new ObjectInputStream(sk.getInputStream());
			ObjectOutputStream oos=new ObjectOutputStream(sk.getOutputStream());
			
			String type=ois.readUTF().toString();
			// if sk is node
			if(type.equals("0")) {
				oos.writeObject("accepted");
				sk.getInetAddress().toString();
				sk.getPort();
				
				String file_name=ois.readUTF().toString();
				while (!file_name.equals("end")) {
					list_file.add(sk.getInetAddress().toString()+"_"+sk.getPort()+"_"+file_name);
					oos.writeUTF("was");
					file_name=ois.readUTF().toString();
				}
				list_node.add(sk);
			}else if(type.equals("1")) {
				oos.writeUTF("accepted");
				for (String i : list_file) {
				}
			}
		}
	}
}
