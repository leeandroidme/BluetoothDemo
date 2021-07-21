package com.example.bluetoothdemo.connection;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by 刘伦 on 2016/2/23.
 */
public class ConnectedThread extends Thread {
    private final BluetoothSocket mBluetoothSocket;
    private final Handler mHandler;

    private final InputStream mInputStream;
    private final OutputStream mOutputStream;

    public ConnectedThread(BluetoothSocket bluetoothSocket, Handler handler) {
        this.mBluetoothSocket = bluetoothSocket;
        this.mHandler = handler;

        InputStream inTemp = null;
        OutputStream outTemp = null;
        try {
            inTemp = mBluetoothSocket.getInputStream();
            outTemp = mBluetoothSocket.getOutputStream() ;
        } catch (IOException e) {
        }
        mInputStream = inTemp;
        mOutputStream = outTemp;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        int length;
        while (true) {
            try {
                length = mInputStream.read(buffer);
                if (length > 0) {
                    mHandler.obtainMessage(Constant.MSG_OBTAINED_DATA, new String(buffer, 0, length)).sendToTarget();
                }
            } catch (Exception e) {
                mHandler.obtainMessage(Constant.MSG_ERROR, e).sendToTarget();
            }
        }
    }

    public void write(byte[] data) {
        try {
            mOutputStream.write(data);
        } catch (IOException e) {
        }
    }

    public void cancel() {
        try {
            mBluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
