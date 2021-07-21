package com.example.bluetoothdemo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
    private BluetoothController mController;
    private BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                int status=intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,-1);
                switch(status){
                    case BluetoothAdapter.STATE_OFF:
                        Toast.makeText(getApplicationContext(),"蓝牙已经关闭",Toast.LENGTH_LONG).show();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Toast.makeText(getApplicationContext(),"蓝牙已经打开",Toast.LENGTH_LONG).show();

                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Toast.makeText(getApplicationContext(),"蓝牙正在关闭",Toast.LENGTH_LONG).show();

                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Toast.makeText(getApplicationContext(),"蓝牙正在打开",Toast.LENGTH_LONG).show();
                        break;
                }
            }else if(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)){
                int mode=intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE,0);
                //如果是可发现的
                if(mode==BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){

                }else{

                }
            }else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                //开始查找设备
            }else if(BluetoothDevice.ACTION_FOUND.equals(action)){
                //找到设备
                BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){

            }




        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        mController=new BluetoothController();
        IntentFilter filter=new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }
    public void isSupportBluetooth(View view){
        if(mController.isSupportBluetooth()){
            Toast.makeText(this,"支持蓝牙",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,"不支持蓝牙",Toast.LENGTH_LONG).show();
        }
    }
    public void checkBluetoothStatus(View view){
        if(mController.isSupportBluetooth()){
            Toast.makeText(this,"蓝牙已经打开",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,"蓝牙还未打开",Toast.LENGTH_LONG).show();
        }
    }
    public void turnOnBluetooth(View view){
        mController.turnOnBluetooth(this, 1);
    }
    public void turnOffBluetooth(View view){
        mController.turnOffBluetooth();
    }
    public void showBluetooth(View view){
        mController.enableVisible(this);
    }
    public void scanBluetoothDevice(View view){
        Intent intent=new Intent(this,DiscoverableActivity.class
        );
        startActivity(intent);
    }
}
