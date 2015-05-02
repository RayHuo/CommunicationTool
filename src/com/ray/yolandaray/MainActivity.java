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
		// ����handler��������UI���̰߳󶨵�handler��
		m_handler = new Handler() {
			@Override
			// ��д�����������������ִ��������ʣ�����ȡ���ݣ��Ը���UI��
			public void handleMessage(Message msg) { 
				super.handleMessage(msg);
				// �������ȡmsg�е���Ϣ���������޸�UI�������msg���Ǻ����m_thread���߳���sendMessage�������ġ�
				switch (msg.what) {
				case 0:
					m_textView.setText("From Server : " + msg.obj.toString());
					break;
				case 1:
					// �ѽ����ֵ��exchangeAccount
					m_textView.setText("Server Wrong!");
					break;
				default:
					break;
				}
				
			}
		};
		
		
		// �½�һ��thread�����￪�����̣߳�ִ�з����������ݵ���Ϊ��Ȼ������ݸ���һ��message���󣬲�ͨ��handler��sendMessage��
		// ��Ϣ���ݸ�handler�󶨵�UI���߳��е�messageQueue��Ȼ��ʹ�������Ϣ�����޸�UI��
		// ��������Ҫ��Thread��߽���Toast��Ϊ����Ϊ����Ҳ��һ������UI���̵߳���Ϊ�����Ǳ���ֹ�ġ�
		m_thread = new Thread(new Runnable() {
			
			public void run() {
		
				Socket socket = null;
				String message = m_editText.getText().toString();
//				Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
				try {
					socket = new Socket("192.168.1.224", 10001);	// 121.33.190.166  192.168.1.224
					// �������������Ϣ
					PrintWriter out = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(socket.getOutputStream())), true);
					out.println(message);
					
					// �������Է���������Ϣ
					BufferedReader br = new BufferedReader(
							new InputStreamReader(socket.getInputStream()));
					String info = br.readLine();
					
					if(info != null) {
	//					m_textView.setText(msg);
						Message msg = new Message();
						msg.obj = info;
						msg.what = 0;
						// m_handler��Ĭ�ϸ����̰߳󶨵ģ�������msg�ᴫ�����̵߳�messageQueue�У�
						// ��ͨ��Looper��ȡ����msg����handlerMessage�д���
						m_handler.sendMessage(msg);	
					}
					else {
	//					m_textView.setText("Server Wrong!");
						Message msg = new Message();
						msg.obj = "Server Wrong!";
						msg.what = 1;
						// m_handler��Ĭ�ϸ����̰߳󶨵ģ�������msg�ᴫ�����̵߳�messageQueue�У�
						// ��ͨ��Looper��ȡ����msg����handlerMessage�д���
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
		m_thread.start();	// �����߳�
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
