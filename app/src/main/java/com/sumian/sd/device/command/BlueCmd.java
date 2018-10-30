package com.sumian.sd.device.command;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.sumian.sd.account.bean.Answers;
import com.sumian.sd.app.AppManager;
import com.sumian.sd.device.util.BlueByteUtil;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by jzz
 * on 2017/8/17.
 * desc:  指令编码
 */

@SuppressWarnings({"unused", "WeakerAccess"})
public final class BlueCmd {

    private static final String TAG = BlueCmd.class.getSimpleName();
    public static final String CMD_PATTERN_SEND = "aa4a";
    public static final String CMD_PATTERN_RECEIVE = "554a";

    public static String bytes2HexString(byte[] bytes) {
        StringBuilder ret = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret.append(hex.toLowerCase(Locale.getDefault()));
        }
        return ret.toString();
    }

    public static byte[] hexStringToByteArray(String s) {
        return new BigInteger(s, 16).toByteArray();
    }

    public static String formatCmdIndex(String command) {
        // 55 40 01 88
        return command.substring(2, 4);
    }

    public static byte[] cResponseOk(byte cmdIndex) {
        return cMakeCommand(cmdIndex, Cmd.CMD_OK, true);
    }

    public static byte[] cResponseFailed(byte cmdIndex) {
        return cMakeCommand(cmdIndex, Cmd.CMD_LOSE, true);
    }

    public static byte[] cResponseSleepyOk(int indexH, int indexL) {
        return cSleepyResponse(indexH, indexL, true);
    }

    public static byte[] cResponseSleepyFailed(int indexH, int indexL) {
        return cSleepyResponse(indexH, indexL, false);
    }

    public static byte[] cRTC() {
        //2017-08-18 13:02:05/24   yyyy-mm-dd HH:mm:ss 24
        Calendar calendar = Calendar.getInstance();

        //0xaa4008 14 11 08 12 0f 13 01 18    //2017-08-18 15:22
        //0xAA4008 14 11 08 12 0F 16 2F 18
        byte[] command = new byte[11];
        command[0] = Cmd.CMD_APP_HEADER;
        command[1] = Cmd.CMD_SET_TIMER;
        command[2] = 0x08;//数据长度
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        command[3] = (byte) (Integer.parseInt(year.substring(0, 2), 10));//20
        command[4] = (byte) (Integer.parseInt(year.substring(2), 10));//17
        command[5] = (byte) (calendar.get(Calendar.MONTH) + 1);//08
        command[6] = (byte) calendar.get(Calendar.DATE);//18
        command[7] = (byte) calendar.get(Calendar.HOUR_OF_DAY);//13
        command[8] = (byte) calendar.get(Calendar.MINUTE);//02
        command[9] = (byte) calendar.get(Calendar.SECOND);//05
        command[10] = 0x18;//24小时制

        return command;
    }

    public static byte[] cSleepData() {
        return cMakeCommand(Cmd.CMD_GET_SLEEP_DATA, (byte) 0x01);
    }

    public static byte[] cMonitorBattery() {
        return cMakeCommand(Cmd.CMD_GET_RING_BATTERY);
    }

    public static byte[] cDoMonitorDfuMode() {
        return cMakeCommand(Cmd.CMD_SET_RING_DFU_MODE);
    }

    public static byte[] cDoMonitor2BindSleepySnNumber(String sn) {
        byte[] command = new byte[15];
        command[0] = Cmd.CMD_APP_HEADER;
        command[1] = Cmd.CMD_SET_SLEEPY_SN;
        command[2] = 0x0c;
        char[] chars = sn.toCharArray();
        command[3] = (byte) chars[0];
        command[4] = (byte) chars[1];
        command[5] = (byte) chars[2];
        command[6] = (byte) chars[3];
        command[7] = (byte) chars[4];
        command[8] = (byte) chars[5];
        command[9] = (byte) chars[6];
        command[10] = (byte) chars[7];
        command[11] = (byte) chars[8];
        command[12] = (byte) chars[9];
        command[13] = (byte) chars[10];
        command[14] = (byte) chars[11];

        return command;
    }

    public static byte[] cSleepyPower() {
        byte[] command = new byte[4];
        command[0] = Cmd.CMD_APP_HEADER;
        command[1] = Cmd.CMD_GET_SLEEPY_POWER;
        command[2] = 0x01;
        command[3] = 0x0f;
        return command;
    }

    public static byte[] cDoMonitorMonitoringMode(int monitorMonitoringMode) {
        return cMakeCommand(Cmd.CMD_SET_RING_MONITOR_MODE, (byte) monitorMonitoringMode);
    }

    public static byte[] cDoSleepyPaMode() {
        return cMakeCommand(Cmd.CMD_SET_SLEEPY_PA_MODE, (byte) 0x01);
    }

    public static byte[] cDoSleepyDfuMode() {
        return cMakeCommand(Cmd.CMD_SET_SLEEPY_DFU_MODE);
    }

    public static byte[] cSleepyConnectedState() {
        return cMakeCommand(Cmd.CMD_GET_SLEEPY_CONNECT_STATE);
    }

    public static byte[] cSleepyBattery() {
        return cMakeCommand(Cmd.CMD_GET_SLEEPY_BATTERY);
    }

    public static byte[] cMonitorFirmwareVersion() {
        return cMakeCommand(Cmd.CMD_GET_RING_FIRMWARE_VERSION);
    }

    public static byte[] cSleepyFirmwareVersion() {
        return cMakeCommand(Cmd.CMD_GET_SLEEPY_FIRMWARE_VERSION);
    }

    public static byte[] cSleepySnNumber() {
        return cMakeCommand(Cmd.CMD_GET_SLEEPY_SN);
    }

    public static byte[] cMonitorSnNumber() {
        return cMakeCommand(Cmd.CMD_GET_RING_SN);
    }

    public static byte[] cSleepyMac() {
        return cMakeCommand(Cmd.CMD_GET_SLEEPY_MAC);
    }

    public static byte[] cMakeCommand(byte cmdIndex, byte data) {
        return cMakeCommand(cmdIndex, data, true);
    }

    public static byte[] cMakeCommand(byte cmdIndex) {
        return cMakeCommand(cmdIndex, (byte) 0x00, false);
    }

    private static byte[] cSleepyResponse(int indexH, int indexL, boolean isOK) {
        byte[] command = new byte[6];
        command[0] = Cmd.CMD_APP_HEADER;
        // command[1] = Cmd.CMD_GET_SLEEP_DATA_CHA;
        command[2] = 0x03;
        command[3] = (byte) indexH;
        command[4] = (byte) indexL;
        command[5] = isOK ? Cmd.CMD_OK : Cmd.CMD_LOSE;
        return command;
    }

    private static byte[] cMakeCommand(byte cmdIndex, byte data, boolean hasData) {
        final int len = hasData ? 4 : 2;
        byte[] command = new byte[len];  //0xaa 44 01 01
        command[0] = Cmd.CMD_APP_HEADER;
        command[1] = cmdIndex;
        if (hasData) {
            command[2] = 0x01;//数据长度
            command[3] = data;
        }
        return command;
    }

    public static byte[] cMonitorAndSleepyState() {
        return cMakeCommand(Cmd.CMD_GET_MONITOR_SLEEPY_STATE);
    }

    public static String formatSn(byte[] data) {
        byte[] snBytes = Arrays.copyOfRange(data, 3, data.length);
        byte[] emptyByte = BlueByteUtil.getEmptyByte(snBytes.length);//字符为 null
        if (Arrays.equals(snBytes, emptyByte)) {//说明全部为0,即 null  没有设置 sn 码
            Log.e(TAG, "-----sn is  null---->");
            return null;
        }
        return BlueByteUtil.getStringFromByte(snBytes);
    }

    public static String formatMac(byte[] data) {
        byte[] snBytes = Arrays.copyOfRange(data, 3, data.length);
        byte[] emptyByte = BlueByteUtil.getEmptyByte(snBytes.length);//字符为 null
        if (Arrays.equals(snBytes, emptyByte)) {//说明全部为0,即 null  没有设置 sn 码
            Log.e(TAG, "-----mac is  null---->");
            return null;
        }
        return BlueByteUtil.getStringFromByte(snBytes);
    }

    public static byte[] cUserInfo() {
        byte[] bytes = new byte[7];
        bytes[0] = Cmd.CMD_APP_HEADER;
        bytes[1] = Cmd.CMD_SET_USER_INFO;
        bytes[2] = 0x04;
        bytes[3] = (byte) (AppManager.getAccountViewModel().getUserInfo() != null ? getGenderType(AppManager.getAccountViewModel().getUserInfo().getGender()) : 0xff);//性别特征
        bytes[4] = (byte) (AppManager.getAccountViewModel().getUserInfo() != null ? getFormatAge(AppManager.getAccountViewModel().getUserInfo().getAge()) : 0xff);//年龄
        bytes[5] = (byte) (AppManager.getAccountViewModel().getUserInfo() != null ? getFormatBmi(AppManager.getAccountViewModel().getUserInfo().getBmi()) : 0xff);//身高体重比
        bytes[6] = (byte) (AppManager.getAccountViewModel().getUserInfo() != null ? getInsomnia(AppManager.getAccountViewModel().getUserInfo().getAnswers()) : 0xff);//失眠程度
        return bytes;
    }

    public static int getGenderType(String gender) {
        if (TextUtils.isEmpty(gender)) {
            gender = "";
        }
        int genderType;
        if (TextUtils.isEmpty(gender)) {
            genderType = 0xff;
        } else {
            switch (gender) {
                case "male":
                    genderType = 0x00;
                    break;
                case "female":
                    genderType = 0x01;
                    break;
                case "secrecy":
                default:
                    genderType = 0xff;
                    break;
            }
        }
        return genderType;
    }

    public static int getFormatAge(Integer age) {
        if (age == null) {
            return 0xff;
        } else {
            return age;
        }
    }

    public static int getFormatBmi(String bmi) {
        if (TextUtils.isEmpty(bmi)) {
            return 0xff;
        } else {
            return (int) (Double.parseDouble(bmi) * 5.0f);
        }
    }

    public static int getInsomnia(Answers answers) {
        if (answers == null) {
            return 0xff;
        } else {
            return answers.level;
        }
    }

    public static byte[] makePatternCmd(@NonNull String data) {
        return makeCmd(Cmd.CMD_SET_PATTERN, data);
    }

    /**
     * @param cmd cmd, see {@link com.sumian.sd.device.command.Cmd}
     */
    public static byte[] makeCmd(byte cmd, @NonNull String data) {
        return makeCmd(cmd, hexStringToByteArray(data));
    }

    /**
     * @param cmd cmd, see {@link com.sumian.sd.device.command.Cmd}
     */
    public static byte[] makeCmd(byte cmd, byte[] data) {
        // aa 4a 05 10 01 9f 20 50
        // APP pattern 数据长度 模式信息（长度不定）
        int dataLen = data != null ? data.length : 0;
        int totalLen = 3 + dataLen;
        byte[] bytes = new byte[totalLen];
        bytes[0] = Cmd.CMD_APP_HEADER;  // header
        bytes[1] = cmd; // cmd
        bytes[2] = BigInteger.valueOf(dataLen).toByteArray()[0];
        if (dataLen > 0) {
            System.arraycopy(data, 0, bytes, 3, dataLen);
        }
        return bytes;
    }
}
