package com.example.bluetoothdemo.connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by 刘伦 on 2016/2/23.
 */
public class ConnectThread extends Thread {
    private static final UUID MY_UUID = UUID.fromString(Constant.CONNECTTION_UUID);
    private final BluetoothSocket mBluetoothSocket;
    private final BluetoothDevice mBluetoothDevice;
    private final BluetoothAdapter mBluetoothAdapter;

    private final Handler mHandler;

    public ConnectThread(BluetoothAdapter bluetoothAdapter, BluetoothDevice bluetoothDevice, Handler handler) {
        this.mBluetoothAdapter = bluetoothAdapter;
        this.mBluetoothDevice = bluetoothDevice;
        this.mHandler = handler;

        BluetoothSocket temp = null;
        try {
            temp = this.mBluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
            mHandler
                    .obtainMessage(Constant.MSG_ERROR, e);
        }
        mBluetoothSocket = temp;
    }

    @Override
    public void run() {
        mBluetoothAdapter.cancelDiscovery();
        try {
            mBluetoothSocket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            mHandler.obtainMessage(Constant.MSG_ERROR,e).sendToTarget();
            try {
                mBluetoothSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        managerConnection(mBluetoothSocket);
    }

    public void cancel(){
        try{
            mBluetoothSocket.close();
        }catch(IOException e){
        }
    }
    private void managerConnection(BluetoothSocket socket){
        mHandler
                .obtainMessage(Constant.MSG_HAS_CLIENT_CONNECTED);
        mConnectedThread=new ConnectedThread(socket,mHandler);
        mConnectedThread.start();
    }
    private ConnectedThread mConnectedThread;
    public void setData(byte[] data){
        if(mConnectedThread!=null){
            mConnectedThread.write(data);
        }
    }
}
