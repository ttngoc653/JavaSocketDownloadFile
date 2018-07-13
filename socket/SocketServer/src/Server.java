import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.glass.ui.CommonDialogs.Type;



public class Server {

	public static ArrayList<Node>dsNode=new ArrayList<>();
	public static  int sstClient=1;
	public void server()
	{
		System.out.println("===================================================================================");
		System.out.println("                          SERVER ĐANG CHẠY");
		//System.out.println("===================================================================================");
		ServerSocket server = null;
		Socket socket=null;
		try {
			System.out.println("SERVER ID: "+InetAddress.getLocalHost());
			System.out.println("SERVER port: "+"1992");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.println("SERVER Đang đợi kết nối!!!");
		try {
			server=new ServerSocket(1992);
			while(true)
			{
				socket=server.accept();

				//nhận json node (matruycap=0,port node,ten node,ds file)
				BufferedReader brNode=new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String requestNode=brNode.readLine();
				//System.out.println(requestNode);
				
				
				//chuyen chuoi json thành dữ liệu node
				Gson gson=new Gson();
				Node node=new Node();

				java.lang.reflect.Type type=new TypeToken<Node>(){}.getType();
				node=gson.fromJson(requestNode,type);
				
				if(node.getMa()==0)//do node truy cap
				{
					//them node vao danh sach node cua sever
					dsNode.add(node);
					//hiển thi node da nhan dc
					System.out.println("============================= THÔNG BÁO ==================================");

					System.out.println("=["+node.getTen().toUpperCase()+" ] ĐÃ TRUY CẬP HỆ THỐNG:");
					System.out.println("=Đã nhận danh sách File từ "+node.getTen());
					for(String i:node.getDanhsachFile())
					{
						System.out.println("File: "+i);
					}
					System.out.println("=======================================================");
				}
				else//do client truy cap
				{
					System.out.println("========================= Thông Báo ===========================");
					System.out.println(node.getTen().toUpperCase()+"["+sstClient+"]"+" Đã truy cập:");
					sstClient ++;
					// server gui xac nhan ve client
					PrintStream pstentruycap =new PrintStream(socket.getOutputStream());
				    gson=new Gson();
				    String jsondanhsachnode=gson.toJson(dsNode);
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
        Server server=new Server();
        server.server();
		
	}

}
