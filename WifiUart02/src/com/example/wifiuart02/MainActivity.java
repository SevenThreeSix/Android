package com.example.wifiuart02;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wifiuart02.R;

public class MainActivity extends Activity
{
	private static final int RECEIVE_NEW_MESSAGE = 1;  
    private static final int SOCKET_CONNECT_SUCCESS = 2;  
    private static final int SOCKET_CONNECT_FAIL = 3;  
   
    EditText edAlias,edIP,edPort,edUser,edPW;
    //TextView t;
    TextView t1,t2,t3;
    private Button ForWard;
	private Button BackWard;
	private Button TurnLeft;
	private Button TurnRight,Send1,Send2,Send21;
	EditText eTTime;
	Button Send;
    private boolean isConnecting = false;
    private Thread mThreadClient = null;
    private Thread mThreadSend = null;
	private Socket mSocketClient = null;
	static PrintWriter mPrintWriterClient = null;
	static BufferedReader mBufferedReaderClient	= null;
	
	String time;
	//mIsConnectServer = true;  
	
    //public static String CameraIp;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//隐去标题（应用的名字必须要写在setContentView之前，否则会有异常）
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        ForWard= (Button)findViewById(R.id.ForWard);
        TurnLeft= (Button)findViewById(R.id.TurnLeft);
        TurnRight= (Button)findViewById(R.id.TurnRight);
        BackWard= (Button)findViewById(R.id.BackWard);
        Send1= (Button)findViewById(R.id.button1);
        Send2= (Button)findViewById(R.id.button2);
        Send21= (Button)findViewById(R.id.buttonS);
        t1 = (TextView)findViewById(R.id.textView1);
        t2 = (TextView)findViewById(R.id.textView2);
        t3 = (TextView)findViewById(R.id.textView3);
        eTTime = (EditText)findViewById(R.id.eTTime);
        Send = (Button)findViewById(R.id.btSend);
        
        	 	mThreadClient = new Thread(mRunnable);
				mThreadClient.start();
				
				ForWard.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View arg0) {
						Toast.makeText(MainActivity.this,"↑",0).show();
						
						new Thread(new Runnable() {  
		                    @Override  
		                    public void run() {  
		                    	if(mSocketClient != null)
								{
									if(mPrintWriterClient != null){
										mPrintWriterClient.print('0');  
										mPrintWriterClient.flush();
									}
									else
										Toast.makeText(MainActivity.this,"mPrintWriterClient NULL",0).show();
								}
								else
									Toast.makeText(MainActivity.this,"mSocketClient NULL",0).show();
		                    }  
		                }).start(); 
					}
						
		        });
				
				Send.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View arg0) {
						time = eTTime.getText().toString();
						if(time.equals(""))
							Toast.makeText(MainActivity.this,"不能为空",0).show();
						else{
							
						new Thread(new Runnable() {  
		                    @Override  
		                    public void run() {
		                    	time = eTTime.getText().toString();
		                    	//t3.setText(time);
		                    	
		                    	if(mSocketClient != null)
								{
									if(mPrintWriterClient != null){
										
											//Toast.makeText(MainActivity.this,"Send",0).show();
											mPrintWriterClient.println(eTTime.getText().toString()); 
											//t3.setText(eTTime.getText().toString());
											mPrintWriterClient.flush();
										
									}
									else
										Toast.makeText(MainActivity.this,"mPrintWriterClient NULL",0).show();
								}
								else
									Toast.makeText(MainActivity.this,"mSocketClient NULL",0).show();
		                    } 
		                }).start();
						Toast.makeText(MainActivity.this,"Send",0).show();
						}
					}
						
		        });
				

				/*ForWard.setOnTouchListener(new View.OnTouchListener() 
		        {
					public boolean onTouch(View v, MotionEvent event) {
						int action = event.getAction();
						switch(action)
						{
						case MotionEvent.ACTION_DOWN:
						    	mPrintWriterClient.write(0x31);
						    	t1.setText(1);
						    	mPrintWriterClient.flush();	
						    	t2.setText(2);
						break;
						case MotionEvent.ACTION_UP:
						    	mPrintWriterClient.write('0'); 
						    	t3.setText(3);
						    	mPrintWriterClient.flush();	
								t3.setText(3);
						}
						return false;
					}
		        }
		        );*/
				BackWard.setEnabled(false);
				BackWard.setOnTouchListener(new View.OnTouchListener() 
		        {
					public boolean onTouch(View v, MotionEvent event) {
						int action = event.getAction();
						switch(action)
						{
						case MotionEvent.ACTION_DOWN:
						    	mPrintWriterClient.print("S");           
						    	mPrintWriterClient.flush();				    
						break;
						case MotionEvent.ACTION_UP:
						    	mPrintWriterClient.print("0");         
						    	mPrintWriterClient.flush();				    
						}
						return false;
					}
		        	
		        }
		        );
				TurnRight.setEnabled(false);
				TurnRight.setOnTouchListener(new View.OnTouchListener() 
		        {
					public boolean onTouch(View v, MotionEvent event) {
						int action = event.getAction();
						switch(action)
						{
						case MotionEvent.ACTION_DOWN:
						    	mPrintWriterClient.print("D");       
						    	mPrintWriterClient.flush();				    
						break;
						case MotionEvent.ACTION_UP:
						    	mPrintWriterClient.print("0");     
						    	mPrintWriterClient.flush();				    
						}
						return false;
					}
		        }
		        );
				TurnLeft.setEnabled(false);
				TurnLeft.setOnTouchListener(new View.OnTouchListener() 
		        {
					public boolean onTouch(View v, MotionEvent event) {
						int action = event.getAction();
						switch(action)
						{
						case MotionEvent.ACTION_DOWN:
						    	mPrintWriterClient.print("A");    
						    	mPrintWriterClient.flush();			
						break;
						case MotionEvent.ACTION_UP: 	
						    	mPrintWriterClient.print("0");   
						    	mPrintWriterClient.flush();		
						}
						return false;
					}
		        }
		        );
		}

	
	private Runnable	mRunnable	= new Runnable() 
	{
		public void run()
		{
			String msgText ="192.168.4.1:2001";

			int start = msgText.indexOf(":");

			String sIP = msgText.substring(0, start);
			String sPort = msgText.substring(start+1);
			int port = Integer.parseInt(sPort);				
			
			Log.d("gjz", "IP:"+ sIP + ":" + port);		
			((TextView)findViewById(R.id.textView1)).setText(sIP);
			((TextView)findViewById(R.id.textView2)).setText(sPort);
			//((TextView)findViewById(R.id.textView2)).setText(time);

			while (mSocketClient == null) {
			try 
			{				
				//连接服务器
				mSocketClient = new Socket(sIP, port);	//portnum
				//取得输入、输出流
				//mBufferedReaderClient = new BufferedReader(new InputStreamReader(mSocketClient.getInputStream()));
				//建立一个向clientSocket发送信息的通道
				mPrintWriterClient = new PrintWriter(mSocketClient.getOutputStream(), true);
				
				isConnecting = true;
				//Message msg = new Message();
                //msg.what = 1;
				//mHandler.sendMessage(msg);		
				//break;
				mPrintWriterClient.print(0x30);  
				mPrintWriterClient.flush();
				//mPrintWriterClient.print('0');  
				//mPrintWriterClient.flush();
				//mPrintWriterClient.print('0');
				//mPrintWriterClient.flush();	
				 mHandler.obtainMessage(SOCKET_CONNECT_SUCCESS).sendToTarget();
			}
			catch (Exception e) 
			{
				//Message msg = new Message();
                //msg.what = 2;
				//mHandler.sendMessage(msg);
				mHandler.obtainMessage(SOCKET_CONNECT_FAIL).sendToTarget(); 
				return;
			}
			}

			/*char[] buffer = new char[256];
			while (isConnecting)
			{
				try
				{
					//if ( (recvMessageClient = mBufferedReaderClient.readLine()) != null )
					if((mBufferedReaderClient.read(buffer))>0)
					{						
						Message msg = new Message();
		                msg.what = 3;
						mHandler.sendMessage(msg);
					}
				}
				catch (Exception e)
				{
					Message msg = new Message();
	                msg.what = 4;
					mHandler.sendMessage(msg);
				}
			}*/
		}
	};
	
	Handler mHandler = new Handler()
	{										
		  public void handleMessage(Message msg)										
		  {	
			  //t3.setText(3333);
			  //super.handleMessage(msg);			
			  //t3.setText(333);
			  switch (msg.what) {  
			  
              /*case RECEIVE_NEW_MESSAGE:  
                  TextView textView = new TextView(MainActivity.this);  
                  textView.setText((String)msg.obj);  
                  mShowLinear.addView(textView);  
                  break;  */

              case SOCKET_CONNECT_SUCCESS:  
                  Toast.makeText(MainActivity.this,"连接服务端成功",Toast.LENGTH_SHORT).show();  
                  break;  

              case SOCKET_CONNECT_FAIL:  
                  Toast.makeText(MainActivity.this,"连接服务端失败，请重新尝试",Toast.LENGTH_SHORT).show();  
                  break;  
              default:  
                  break;  

          }
		  }									
	 };
		
		public void onDestroy() {
			super.onDestroy();
			if (isConnecting) 
			{				
				isConnecting = false;
				try {
					if(mSocketClient!=null)
					{
						mSocketClient.close();
						mSocketClient = null;
						
						mPrintWriterClient.close();
						mPrintWriterClient = null;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				mThreadClient.interrupt();
			}

		}
}


