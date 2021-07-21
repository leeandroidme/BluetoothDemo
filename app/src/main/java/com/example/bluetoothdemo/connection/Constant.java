package com.example.bluetoothdemo.connection;

/**
 * Created by 刘伦 on 2016/2/23.
 */
public class Constant {
    /**
     * 链接
     */
    public static final  String  CONNECTTION_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    /**
     * 开始监听
     */
    public static final int MSG_START_LISTENING=1;
    /**
     * 结束监听
     */
    public static final int MSG_FINISH_LISTENING=2;
    /**
     * 有客户端进行链接
     */
    public static final int MSG_HAS_CLIENT_CONNECTED=3;
    /**
     * 连接到服务器
     */
    public static final int MSG_CONNECTED_TO_SERVER=4;
    /**
     * 获取到数据
     */
    public static final int MSG_OBTAINED_DATA=5;
    /**
     * 异常错误
     */
    public static final int MSG_ERROR=-1;

}
