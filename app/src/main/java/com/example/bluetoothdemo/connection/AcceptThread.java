package com.example.bluetoothdemo.connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by 刘伦 on 2016/2/23.
 */
public class AcceptThread extends Thread {
    private static final String NAME = "BlueThoothDemo";
    private static final UUID MY_UUID = UUID.fromString(Constant.CONNECTTION_UUID);

    private final BluetoothAdapter mBluetoothAdapter;
    private final BluetoothServerSocket mServerSocket;
    private final Handler mHandler;

    private ConnectedThread mManagerThread;

    public AcceptThread(BluetoothAdapter bluetoothAdapter, Handler handler) {
        this.mBluetoothAdapter = bluetoothAdapter;
        this.mHandler = handler;
        BluetoothServerSocket temp = null;
        try {
            temp = this.mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, MY_UUID);
        } catch (Exception e) {
            e.printStackTrace();
            this.mHandler.obtainMessage(Constant.MSG_ERROR, e).sendToTarget();
        }
        mServerSocket = temp;
    }

    @Override
    public void run() {
        BluetoothSocket bluetoothSocket = null;
        while (true) {
            try {
                mHandler.obtainMessage(Constant.MSG_START_LISTENING).sendToTarget();
                bluetoothSocket = mServerSocket.accept();
                manageConnectedSocket(bluetoothSocket);
            } catch (IOException e) {
                mHandler.obtainMessage(Constant.MSG_ERROR, e);
                break;
            }
            if (bluetoothSocket != null) {
                try {
                    mServerSocket.close();
                    mHandler.obtainMessage(Constant.MSG_FINISH_LISTENING).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                    mHandler
                            .obtainMessage(Constant.MSG_ERROR, e);
                }
                break;
            }
        }
    }

    private void manageConnectedSocket(BluetoothSocket socket) {
        if (mManagerThread != null) {
            mManagerThread.cancel();
        }
        mHandler
                .obtainMessage(Constant.MSG_HAS_CLIENT_CONNECTED);
        mManagerThread = new ConnectedThread(socket, mHandler);
        mManagerThread.start();
    }

    public void cancel() {
        if (mServerSocket != null) {
            try {
                mServerSocket.close();
                mHandler.obtainMessage(Constant.MSG_FINISH_LISTENING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setData(byte[] data) {
        if (mManagerThread != null) {
            mManagerThread.write(data);
        }
    }
}
