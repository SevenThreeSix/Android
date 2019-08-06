package freemouse.itsxld.com.lightcontroll;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Duan on 2017/2/17.
 */

public class btListAdapter extends BaseAdapter {
    List<btListBean> beanArr;
    Context baseCtx;
    LayoutInflater mInflater;
    public btListAdapter(List<btListBean> beanList, Context ctx){
        this.beanArr = beanList;
        this.baseCtx = ctx;
        this.mInflater = LayoutInflater.from(baseCtx);
    }

    @Override
    public int getCount() {
        return beanArr.size();
    }

    @Override
    public Object getItem(int position) {
        return beanArr.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        btListViewHolder mHolder = new btListViewHolder();
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.list_bt_item, null);
            TextView deviceName = (TextView) convertView.findViewById(R.id.deviceName);
            TextView deviceAddr = (TextView) convertView.findViewById(R.id.deviceAddr);
            mHolder.deviceAddr = deviceAddr;
            mHolder.deviceName = deviceName;
            mHolder.item = convertView;
            convertView.setTag(mHolder);
        }else{
            mHolder = (btListViewHolder) convertView.getTag();
        }
        mHolder.deviceAddr.setText(this.beanArr.get(position).deviceAddr);
        mHolder.deviceName.setText(this.beanArr.get(position).deviceName);
        return convertView;
    }
}
