import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Server {

	//static List<Socket> list_socket = new ArrayList<Socket>(); //public static ArrayList<Node> dsNode=new ArrayList<>();
	static int sstClient = 1;
	static int port = 1742;
	static List<TenFile> list_file=new ArrayList<>();
	
	
	static void convertNodeToListFile(Node node,Socket sk) {
		for (int i = 0; i < node.getDanhsachFile().size(); i++) {
			list_file.add(new TenFile(sk.getPort(), node.getTen(), node.getDanhsachFile().get(i)));
		}
	}
	
	@SuppressWarnings("resource")
	static void softAndDeleteFileNodeDisConnected() {
		for (int i = 0; i < list_file.size(); i++)
			try {
				new Socket(InetAddress.getByName(list_file.get(i).getTennode().split(",")[1]), list_file.get(i).getPort());
			} catch (Exception e) {
				//e.printStackTrace();
				list_file.remove(i);
				i--;
			}
		
		TenFile temp;
		for (int i = 0; i < list_file.size()-1; i++) 
			for (int j = i+1; j < list_file.size(); j++) 
				if(list_file.get(i).getTenfile().compareToIgnoreCase(list_file.get(j).getTenfile())>0) {
					temp = new TenFile(list_file.get(i).getPort(), list_file.get(i).getTennode(), list_file.get(i).getTenfile());
					list_file.set(i, list_file.get(j));
					list_file.set(j, temp);
				}
		
	}
	private static Gson gson;
	private static Node node ;
	private static java.lang.reflect.Type type;
	
	public static void server()
	{
		System.out.println("===================================================================================");
		System.out.println("                          SERVER RUNNING");
		//System.out.println("===================================================================================");
		ServerSocket server = null;
		Socket socket=null;
		try {
			System.out.println("SERVER ID: "+InetAddress.getLocalHost().getHostAddress());
			System.out.println("SERVER port: "+port);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("SERVER waiting connect!!!");
		try {
			server=new ServerSocket(port);
			//server.setSoTimeout(20000);
			while(true)
			{
				socket= server.accept();

				//nhận json node (matruycap=0,port node,ten node,ds file)
				BufferedReader brNode=new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String requestNode=brNode.readLine();
				//System.out.println(requestNode);
				
				
				//chuyen chuoi json thành dữ liệu node
				gson = new Gson();
				node = new Node();

				type=new TypeToken<Node>(){}.getType();
				node = gson.fromJson(requestNode,type);
				
				if(node.getMa()==0)//do node truy cap
				{
					convertNodeToListFile(node, socket);
					//them node vao danh sach node cua sever
					//dsNode.add(node);
					//hiển thi node da nhan dc
					System.out.println("============================= NOTIFY =======================================");

					System.out.println("=["+node.getTen().toUpperCase()+" ] CONNECTED SERVER.");
					System.out.println("= Received list file from "+node.getTen());
					for(String i:node.getDanhsachFile())
					{
						System.out.println("File: "+i);
					}
					System.out.println("=======================================================");
				}
				else//do client truy cap
				{
					System.out.println("================================= NOTIFY =====================================");
					System.out.println(node.getTen().toUpperCase()+"["+sstClient+"]"+" connected.");
					sstClient ++;
					// server gui xac nhan ve client
					PrintStream pstentruycap =new PrintStream(socket.getOutputStream());
				    gson=new Gson();
				    softAndDeleteFileNodeDisConnected();
				    String jsondanhsachnode=gson.toJson(list_file);
					pstentruycap.println(jsondanhsachnode);
				}
				
				
				//đóng các ket noi
				brNode.close();
				socket.close();
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        server();
		
	}
}
