import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.glass.ui.CommonDialogs.Type;



public class Server {

	public static ArrayList<Node>dsNode=new ArrayList<>();
	public static  int sstClient=1;
	public void server()
	{
		ServerSocket server = null;
		Socket socket=null;
		System.out.println("SERVER IS READY CONNECT!!!!!!");
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
					System.out.println(node.getTen()+" Đã truy cập:");
					System.out.println("nhận danh sách File từ "+node.getTen());
					for(String i:node.getDanhsachFile())
					{
						System.out.println(i);
					}
					System.out.println("=======================================================");
				}
				else//do client truy cap
				{
					System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
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
