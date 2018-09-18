package com.sumian.hw.command;

/**
 * Created by jzz
 * on 2017/8/18.
 * desc:
 */

public interface Cmd {

    byte CMD_OK = (byte) 0x88;//success
    byte CMD_LOSE = (byte) 0xff;//failed

    byte CMD_APP_HEADER = (byte) 0xaa;//app header cmd
    byte CMD_RING_HEADER = 0x55;//ring header cmd
    byte CMD_SET_TIMER = 0x40;//set time yyyy-mm-dd HH:mm:ss 24
    byte CMD_GET_RING_BATTERY = 0x44;//get 监测仪(监测仪?)  battery
    byte CMD_GET_SLEEPY_BATTERY = 0x45;//get 速眠仪 battery
    byte CMD_GET_SLEEPY_POWER = 0x47;//get 速眠仪的档位功率  0xaa47010f
    byte CMD_SET_POWER = 0x48;  //set 速眠仪的档位功率  强/弱
    byte CMD_SET_USER_INFO = 0x4b;//set 用户信息
    byte CMD_GET_SLEEPY_CONNECT_STATE = 0x4e;//get 速眠仪的蓝牙连接状态,判断是否已经连接上了监测仪设备// UID一致且数据存在：心率、体动、肌电、睡眠深度值
    byte CMD_GET_SLEEP_DATA = 0x4f;//get 主动获取睡眠特征数据  有数据,监测仪收到该指令开始上报睡眠数据,无数据返回  0x554f0100
    byte CMD_GET_RING_FIRMWARE_VERSION = 0x50;//get  获取监测仪的固件版本信息
    byte CMD_SET_RING_DFU_MODE = 0x51;//使能监测仪进入dfu空中升级模式
    byte CMD_SET_SLEEPY_SN = 0x52;// set连接的速眠仪的sn序列号 aa 52 12 xx xx...
    byte CMD_GET_RING_SN = 0x53;// get读取监测仪的sn序列号
    byte CMD_GET_SLEEPY_FIRMWARE_VERSION = 0x54;//get 获取速眠仪的固件版本信息
    byte CMD_GET_SLEEPY_SN = 0x55;//get 读取监测仪绑定并连接的速眠仪的SN序列号
    byte CMD_GET_SLEEPY_MAC = 0x56;//get 读取监测仪绑定的速眠仪的MAC地址
    byte CMD_SET_RING_MONITOR_MODE = 0x57;//set 使监测仪进入独立（监测）工作模式 即监测仪只监测，不启动速眠仪PA工作模式  00 -- 关闭 01 -- 开启
    byte CMD_SET_SLEEPY_PA_MODE = 0x58;//set 开启速眠仪的PA工作模式  note:速眠仪连接监测仪，同时满足下列条件可以开启PA功能：①佩戴状态 ②非睡状态
    byte CMD_SET_SLEEPY_DFU_MODE = 0x59;//set  APP获取速眠仪软件版本后，发送此指令给监测仪，使监测仪让速眠仪进入 dfu升级模式。
    // 监测仪发送 AA 36指令到速眠仪，通知速眠仪进入升级模式。然后，APP主动连接速眠仪进行升级。
    //①监测仪收到该指令后，断开与速眠仪的连接，并在10分钟内，不与速眠仪连接。
    //②10分钟内，如果被手机APP连接，则退出速眠仪升级模式，可以连接速眠仪
    byte CMD_GET_MONITOR_SLEEPY_STATE = 0x61;//get  获取监测仪和连接上的速眠仪的所有状态信息
    byte CMD_SET_PATTERN = 0x4a;
    byte CMD_GET_PATTERN = 0x4c;
}
