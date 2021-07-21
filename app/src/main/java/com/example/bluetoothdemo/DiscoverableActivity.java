package com.example.bluetoothdemo;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bluetoothdemo.connection.AcceptThread;
import com.example.bluetoothdemo.connection.ConnectThread;
import com.example.bluetoothdemo.connection.ConnectedThread;
import com.example.bluetoothdemo.connection.Constant;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class DiscoverableActivity extends Activity {

    private List<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();
    private List<BluetoothDevice> mBoundedDeviceList = new ArrayList<BluetoothDevice>();

    private BluetoothController mController = new BluetoothController();

    private ListView mListView;

    private DeviceAdapter mAdapter;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {//开始查找设备
                setProgressBarIndeterminateVisibility(true);
                mDeviceList.clear();
                mAdapter.notifyDataSetChanged();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {//找到设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDeviceList.add(device);
                mAdapter.notifyDataSetChanged();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {//发现停止
                setProgressBarIndeterminateVisibility(false);
            } else if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {//扫描模式改变广播
                int scanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, -1);
                if (scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    setProgressBarIndeterminateVisibility(true);
                } else {
                    setProgressBarIndeterminateVisibility(false);
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (remoteDevice == null) {
                    showToast("no device");
                    return;
                }
                int status = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
                if (status == BluetoothDevice.BOND_BONDED) {
                    showToast("bounded " + remoteDevice.getName());
                } else if (status == BluetoothDevice.BOND_BONDING) {
                    showToast("bonding " + remoteDevice.getName());
                } else if (status == BluetoothDevice.BOND_NONE) {
                    showToast("not bond " + remoteDevice.getName());
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                ex.printStackTrace();
            }
        });
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discoverable);
        initActionBar();
        initView();
        initBluetoothReceiver();
        mController.turnOnBluetooth(this, 1);

    }

    private AdapterView.OnItemClickListener bindDeviceClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                BluetoothDevice device = mDeviceList.get(position);
                if(device.getBondState()==BluetoothDevice.BOND_NONE){
                    device.createBond();
                }else if(device.getBondState()==BluetoothDevice.BOND_BONDED){
                    if(mConnectThread!=null){
                        mConnectThread.cancel();
                    }
                    mConnectThread=new ConnectThread(mController.getAdapter(),device,mHandler);
                    mConnectThread.start();
                }

            }
        }
    };

    private void initView() {
        mListView = (ListView) findViewById(R.id.device_list);
        mAdapter = new DeviceAdapter(mDeviceList, this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(bindDeviceClick);
    }

    private void initBluetoothReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void initActionBar() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        boolean ischild = isChild();
        Window window = getWindow();
        boolean b = window.hasFeature(Window.FEATURE_ACTION_BAR);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
        setProgressBarIndeterminate(true);
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {

        }

    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private AcceptThread mAcceptThread;
    private MyHandler mHandler = new MyHandler();
    private ConnectThread mConnectThread;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.enable_visiblity:
                mController.enableVisible(this);
                break;
            case R.id.find_device:
                mAdapter
                        .refresh(mDeviceList);
                mController.findDevices();
                mListView.setOnItemClickListener(bindDeviceClick);
                break;
            case R.id.bonded_device:
                mBoundedDeviceList = mController.getBoundDevices();
                mAdapter
                        .refresh(mBoundedDeviceList);
                mListView.setOnItemClickListener(null);
                break;
            case R.id.listening:
                if (mAcceptThread != null) {
                    mAcceptThread.cancel();
                }
                mAcceptThread = new AcceptThread(mController.getAdapter(), mHandler);
                mAcceptThread.start();
                break;
            case R.id.stop_listening:
                if (mAcceptThread != null) {
                    mAcceptThread.cancel();
                }
                break;
            case R.id.disconnect:
                if (mConnectThread != null) {
                    mConnectThread.cancel();
                }
                break;
            case R.id.say_hello:
                say("hello");
                break;
            case R.id.say_hi:
                say("hi");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void say(String str) {
        if (mAcceptThread != null) {
            mAcceptThread.setData(str.getBytes(Charset.defaultCharset()));
        } else if (mConnectThread != null) {
            mConnectThread.setData(str.getBytes(Charset.defaultCharset()));
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constant.MSG_START_LISTENING:
                    setProgressBarIndeterminateVisibility(true);
                    break;
                case Constant.MSG_FINISH_LISTENING:
                    setProgressBarIndeterminateVisibility(false);
                    break;
                case Constant.MSG_OBTAINED_DATA:
                    showToast("data: " + String.valueOf(msg.obj));
                    break;
                case Constant.MSG_ERROR:
                    showToast("error: " + String.valueOf(msg.obj));
                    break;
                case Constant.MSG_CONNECTED_TO_SERVER:
                    showToast("Connected to Server");
                    break;
                case Constant.MSG_HAS_CLIENT_CONNECTED:
                    showToast("Got a Client");
                    break;
            }
        }
    }
}
