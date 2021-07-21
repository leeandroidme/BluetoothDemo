package com.example.bluetoothdemo.profile;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

import java.util.List;

/**
 * Created by 刘伦 on 2016/2/24.
 */
public abstract class BluetoothHeadsetUtils {
    private Context mContext;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothHeadset mBluetoothHeadset;
    private BluetoothDevice mConnectedHeadset;

    private AudioManager mAudioManager;

    private boolean mIsOnHeadsetSco;
    private boolean mIsStarted;

    public BluetoothHeadsetUtils(Context context) {
        this.mContext = context;
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mAudioManager = (AudioManager) this.mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * 开启蓝牙
     *
     * @return
     */
    public boolean start() {
        if (!mBluetoothAdapter.isEnabled()) {
            mIsStarted = false;
            return mIsStarted;
        }
        if (!mIsStarted) {
            mIsStarted = true;
            mIsStarted = startBluetooth();
        }
        return mIsStarted;
    }

    public void stop() {
        if (mIsStarted) {
            mIsStarted = false;
            stopBluetooth();
        }
    }

    public boolean isOnHeadsetSco() {
        return mIsOnHeadsetSco;
    }

    public abstract void onHeadsetDisconnected();

    public abstract void onHeadsetConnected();

    public abstract void onScoAudioDisconnected();

    public abstract void onScoAudioConnected();

    private boolean startBluetooth() {
        if (mBluetoothAdapter != null) {
            if (mAudioManager.isBluetoothScoAvailableOffCall()) {
                if (mBluetoothAdapter.getProfileProxy(mContext, mHeadsetProfileListener, BluetoothProfile.HEADSET)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void stopBluetooth() {
        if (mBluetoothHeadset != null) {
            mBluetoothHeadset.stopVoiceRecognition(mConnectedHeadset);
            mContext.unregisterReceiver(mHeadsetBroadcastReceiver);
            mBluetoothAdapter.closeProfileProxy(BluetoothHeadset.HEADSET, mBluetoothHeadset);
            mBluetoothHeadset = null;
        }
    }

    private BluetoothProfile.ServiceListener mHeadsetProfileListener = new BluetoothProfile.ServiceListener() {

        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            mBluetoothHeadset = (BluetoothHeadset) proxy;
            List<BluetoothDevice> devices = mBluetoothHeadset.getConnectedDevices();
            if (devices.size() > 0) {
                mConnectedHeadset = devices.get(0);
                onHeadsetConnected();
            }
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED);
            filter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
            mContext.registerReceiver(mHeadsetBroadcastReceiver, filter);
        }

        @Override
        public void onServiceDisconnected(int profile) {
            stopBluetooth();
        }
    };
    private BroadcastReceiver mHeadsetBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int state;
            if (BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED.equals(action)) {
                state = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, -1);
                if (state == BluetoothHeadset.STATE_AUDIO_CONNECTED) {
                    onScoAudioConnected();
                } else if (BluetoothHeadset.STATE_AUDIO_DISCONNECTED == state) {
                    mIsOnHeadsetSco = false;
                    onScoAudioDisconnected();
                }
            } else if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                state = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, -1);
                if (state == BluetoothHeadset.STATE_CONNECTED) {
                    mConnectedHeadset = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    onHeadsetConnected();
                } else if (state == BluetoothHeadset.STATE_DISCONNECTED) {
                    mConnectedHeadset = null;
                    onHeadsetDisconnected();
                }
            }
        }
    };
}
