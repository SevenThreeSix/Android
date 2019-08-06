package com.example.user.helpeachother.Service;


import android.app.Service;  
import android.content.Intent;  
import android.os.Binder;  
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
//import android.util.Log;

public class Service_Map extends Service{

	//private boolean t_flag=false;
	public String Map_latitude = "31.48935";
	public String Map_longitude = "114.38696";
	/*
	public myHTTPClient map_httpclient = new myHTTPClient();
	//public Handler handle;
	//public String map_url = "gcxw.sinaapp.com/text.html";//http://192.168.31.144/AppServer.php?opt=1&p_1=1
	//public Thread t_m= new Thread()
	//public class myTread extends Thread
	public Thread StarMapThread(final String map_url,final Handler mhandler)
	{
		//String map_url = "http://192.168.31.144/AppServer.php?opt=2";
		return new Thread(){
		@Override
		public void run(){
		//conenection HTTP server,get loction data
			map_httpclient.Get(map_url);
			if(!map_httpclient.StatusCode.substring(0,1).equals("2"))
				return;
			int[] pos= new int[5];
			int k=0;
			//����������@֮��//
			for( int i=0 ; i < map_httpclient.rev.length() ; ++i)
				if( '@' == map_httpclient.rev.charAt(i))
				{
					pos[k]=i;
					++k;
				}
			//���������,��������@
			if( k>1 )
			{
				Map_longitude = map_httpclient.rev.substring(pos[0]+1, pos[1]);
				//Toast.makeText(MapActivity.this, "�����ʾ", 0).show(); 
				Map_latitude = map_httpclient.rev.substring(pos[1]+1, pos[2]);
				Message msg = new Message();
				msg.obj = "latitude:"+Map_latitude+" \nlongitude:"+Map_longitude;
				mhandler.sendMessage(msg);
				//tv.setText("latitude:"+Map_latitude+" longitude:"+Map_longitude);
//				Map_latitude = "32";
//				Map_longitude = "115";
			}
			
			//BroadCast
			Intent intent = new Intent();
			//intent.putExtra("Map_latitude",Map_latitude);
			//intent.putExtra("Map_longitude",Map_longitude);
			intent.putExtra("Map_latitude",Map_latitude);
			intent.putExtra("Map_longitude",Map_longitude);
			intent.setAction("com.example.m11.service_map");
			sendBroadcast(intent);
		}
	  };
	}*/
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
//		this.t_m.start();
		return new MBinder();
	}
/*	@Override
    public void onStart(Intent intent, int startId) {  
        // TODO Auto-generated method stub  
        super.onStart(intent, startId);  
        this.t_m.start();
    } 
    */ 
    @Override  
    public boolean onUnbind(Intent intent) {  
        // TODO Auto-generated method stub 
 //   	try{
//    		this.t_m.wait();
//    	}catch( InterruptedException e){e.printStackTrace();}
//    	this.t_m.stop();
        return super.onUnbind(intent);  
    }  
	@Override
    public void onDestroy(){
//    	this.t_m.stop();
//    	this.t_flag=false;
    	super.onDestroy();
    }
    @Override
    public void onCreate(){
//    	this.t_flag=true;
    	super.onCreate();
    }
	public class MBinder extends Binder{
		public Service_Map GetService(){
			return Service_Map.this;
		}
	}

}
