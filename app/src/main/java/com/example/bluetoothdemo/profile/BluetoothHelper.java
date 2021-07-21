package com.example.bluetoothdemo.profile;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by 刘伦 on 2016/2/24.
 */
public class BluetoothHelper extends BluetoothHeadsetUtils {
    private static final String TAG = BluetoothHelper.class.getSimpleName();
    private Context mContext;

    int mCallvol;
    AudioManager mAudioManager;

    public BluetoothHelper(Context context) {
        super(context);
        mContext = context;
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mCallvol = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
    }

    @Override
    public void onHeadsetDisconnected() {
        mAudioManager.setBluetoothScoOn(false);
    }

    @Override
    public void onHeadsetConnected() {
        mAudioManager.setBluetoothScoOn(true);
    }

    @Override
    public void onScoAudioDisconnected() {
        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, mCallvol, 0);
    }

    @Override
    public void onScoAudioConnected() {
        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
    }
}
