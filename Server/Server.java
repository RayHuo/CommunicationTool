import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;

public class Server implements Runnable {

	@SuppressWarnings("resource")
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			ServerSocket serverSocket = new ServerSocket(10001);
			System.out.println("set port");
			while(true) {
				System.out.println("Try to accept...");
				Socket client = serverSocket.accept();
				System.out.println("accept");
				try {
					// 接受客户端信息
					BufferedReader in = new BufferedReader(
							new InputStreamReader(client.getInputStream()));
					String str = in.readLine();
					System.out.println("read : " + str);
					
					// 向客户端发送信息
					PrintWriter out = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(client.getOutputStream())), true);
					out.println("server message");
					
					in.close();
					out.close();
				}
				catch(Exception e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
				finally {
					client.close();
					System.out.println("close");
				}
			}
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	public static void main(String a[]) {
		Thread desktopServerThread = new Thread(new Server());
		desktopServerThread.start();
		System.out.println("Server Start");
	}

}
