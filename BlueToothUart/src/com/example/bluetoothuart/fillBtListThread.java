package freemouse.itsxld.com.lightcontroll;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Duan on 2017/2/17.
 */

public class fillBtListThread extends Thread {
    public Context baseCtx;
    public List<btListBean> beanArr = new ArrayList<btListBean>();
    public ListView btList;
    public Handler mHandler;
    public fillBtListThread(Context ctx, ListView list, Handler mHandler){
        this.baseCtx = ctx;
        this.btList = list;
        this.mHandler = mHandler;
    }
    public void run(){
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        if(adapter != null){
            System.out.println("本机拥有蓝牙设备");
            if(!adapter.isEnabled()){
                adapter.enable();
            }
            //得到所有已经配对的蓝牙适配器对象
            Set<BluetoothDevice> devices = adapter.getBondedDevices();
            if(devices.size()>0){
                //用迭代　　　　　　　
                for(Iterator iterator = devices.iterator(); iterator.hasNext();){
                    btListBean item = new btListBean();
                    BluetoothDevice device = (BluetoothDevice)iterator.next();
                    item.deviceAddr = device.getAddress();
                    item.deviceName = device.getName();
                    this.beanArr.add(item);
                    System.out.println("device: "+item.deviceName+","+item.deviceAddr);
                    if(device.getName().matches(".Itsl.")){
                        System.out.println(device.getName());
                    }
                }
            }
            final btListAdapter mAdapter = new btListAdapter(beanArr, this.baseCtx);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Here");
                    btList.setAdapter(mAdapter);
                    btList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //Toast.makeText(baseCtx, "You choosed device:"+beanArr.get(position).deviceName, Toast.LENGTH_SHORT).show();
                            Intent toControl = new Intent();
                            Message msg = new Message();
                            Bundle turnData = new Bundle();
                            turnData.putString("deviceName", beanArr.get(position).deviceName);
                            turnData.putString("deviceAddr", beanArr.get(position).deviceAddr);
                            msg.setData(turnData);
                            mHandler.sendMessage(msg);
                        }

                    });
                }
            });
        }else{
            System.out.println("没有蓝牙设备");
        }
    }
}
