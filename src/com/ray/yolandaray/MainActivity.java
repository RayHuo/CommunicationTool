package com.ray.yolandaray;

import java.net.*;
import java.io.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.app.Activity;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

public class MainActivity extends Activity {
	private final String DEBUG_TAG = "YRMainActivity";
	
	private TextView m_textView = null;
	private EditText m_editText = null;
	private Button m_submition = null;	
	
	private Handler m_handler = null;
	private Thread m_thread = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		m_textView = (TextView)findViewById(R.id.serverFeedback);
		m_editText = (EditText)findViewById(R.id.clientInput);
		m_submition = (Button)findViewById(R.id.submition);
		
		m_submition.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				sendMessageToServer();
			}
		});
		
	}
	
	
	private void sendMessageToServer() {
		// 创建handler，这是与UI主线程绑定的handler。
		m_handler = new Handler() {
			@Override
			// 重写这个方法，即在这里执行网络访问，并获取数据，以更新UI。
			public void handleMessage(Message msg) { 
				super.handleMessage(msg);
				// 在这里获取msg中的信息，并依此修改UI，这里的msg就是后面的m_thread子线程所sendMessage发过来的。
				switch (msg.what) {
				case 0:
					m_textView.setText("From Server : " + msg.obj.toString());
					break;
				case 1:
					// 把结果赋值给exchangeAccount
					m_textView.setText("Server Wrong!");
					break;
				default:
					break;
				}
				
			}
		};
		
		
		// 新建一个thread，这里开个子线程，执行访问网络数据的行为，然后把数据给到一个message对象，并通过handler的sendMessage把
		// 信息传递给handler绑定的UI主线程中的messageQueue，然后使用这个信息，并修改UI。
		// ！！！不要在Thread里边进行Toast行为，因为这样也是一个更新UI主线程的行为，这是被禁止的。
		m_thread = new Thread(new Runnable() {
			
			public void run() {
		
				Socket socket = null;
				String message = m_editText.getText().toString();
//				Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
				try {
					socket = new Socket("192.168.1.224", 10001);	// 121.33.190.166  192.168.1.224
					// 向服务器发送信息
					PrintWriter out = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(socket.getOutputStream())), true);
					out.println(message);
					
					// 接收来自服务器的信息
					BufferedReader br = new BufferedReader(
							new InputStreamReader(socket.getInputStream()));
					String info = br.readLine();
					
					if(info != null) {
	//					m_textView.setText(msg);
						Message msg = new Message();
						msg.obj = info;
						msg.what = 0;
						// m_handler是默认跟主线程绑定的，所以其msg会传到主线程的messageQueue中，
						// 并通过Looper获取到该msg并在handlerMessage中处理。
						m_handler.sendMessage(msg);	
					}
					else {
	//					m_textView.setText("Server Wrong!");
						Message msg = new Message();
						msg.obj = "Server Wrong!";
						msg.what = 1;
						// m_handler是默认跟主线程绑定的，所以其msg会传到主线程的messageQueue中，
						// 并通过Looper获取到该msg并在handlerMessage中处理。
						m_handler.sendMessage(msg);	
					}
					
					out.close();
					br.close();
					socket.close();
				}
				catch(Exception e) {
					Log.e(DEBUG_TAG, e.toString());
				}
			}
		});
		m_thread.start();	// 启动线程
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
