package com.example.bluetoothdemo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 刘伦 on 2016/2/21.
 */
public class BluetoothController {
    private BluetoothAdapter mAdapter;
    public BluetoothController(){
        mAdapter=BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * 是否支蓝牙
     * @return
     */
    public boolean isSupportBluetooth(){
        return mAdapter!=null?true:false;
    }

    /**
     * 是否开启蓝牙
     * @return
     */
    public boolean isEnableBluetooth(){
        assert(mAdapter!=null);
        return mAdapter.isEnabled();
    }

    /**
     * 打开蓝牙
     * @param activity
     * @param requestCode
     */
    public void turnOnBluetooth(Activity activity,int requestCode){
        assert(mAdapter!=null);
        Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent,requestCode);
    }

    /**
     * 关闭蓝牙
     */
    public void turnOffBluetooth(){
        assert(mAdapter!=null);
        mAdapter.disable();
    }

    /**
     * 打开蓝牙可见性
     * @param context
     */
    public void enableVisible(Context context){
        Intent intent =new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
        context.startActivity(intent);
    }

    /**
     * 使设备不被发现
     */
    public void disableVisible(){
        mAdapter.cancelDiscovery();
    }
    /**
     * 查找设备
     */
    public void findDevices(){
        assert(mAdapter!=null);
        mAdapter.startDiscovery();
    }
    public List<BluetoothDevice> getBoundDevices(){
        return new ArrayList<BluetoothDevice>(mAdapter.getBondedDevices());
    }

    public BluetoothAdapter getAdapter() {
        return mAdapter;
    }
}
