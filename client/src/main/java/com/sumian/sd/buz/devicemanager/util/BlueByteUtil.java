package com.sumian.sd.buz.devicemanager.util;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

/**
 * Created by jzz
 * on 2017/9/4
 * desc: byte 解析工具类
 */
public class BlueByteUtil {

    private static final String TAG = BlueByteUtil.class.getSimpleName();

    private static final char[] DIGITS = {'0', '1', '2', '3',
        '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
        'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
        'u', 'v', 'w', 'x', 'y', 'z'};
    private static final char[] UPPER_CASE_DIGITS = {'0', '1', '2', '3',
        '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
        'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
        'U', 'V', 'W', 'X', 'Y', 'Z'};

    /**
     * 根据bit数组创建byte 自动在末位补0至8位
     *
     * @param b 输入的 bit数组
     * @return 创建出的byte
     */
    public static byte getBit(int[] b) {
        byte result = 0x00;
        for (int i = 7; i >= 0; i--) {
            result = (byte) (result << 1);
            if (i < b.length && b[i] == 1)
                result = (byte) (result | 0x01);
        }
        return result;
    }

    /**
     * 获取单个 byte 中的每一个bit值 (boolean表示)
     *
     * @param b 输入的 byte
     * @return boolean数组
     */
    public static boolean[] byte2bitBool(byte b) {
        boolean[] result = new boolean[8];
        for (int i = 0; i < result.length; i++)
            result[i] = ((b >> i & 0x01) == 1);
        return result;
    }

    /**
     * 获取单个 byte 中的每一个bit值 (int表示)
     *
     * @param b 输入的 byte
     * @return int数组
     */
    public static int[] byte2bitInt(byte b) {
        int[] result = new int[8];
        for (int i = 0; i < result.length; i++)
            result[i] = ((b >> i & 0x01));
        return result;
    }

    /**
     * 获取定长的byte[]（长截断，短补0）
     *
     * @param old    原始byte[]
     * @param length 长度
     * @return 定长byte
     */
    public static byte[] getByteWithLength(byte[] old, int length) {
        if (old == null)
            return getEmptyByte(length);
        byte[] result = new byte[length];
        for (int i = 0; i < old.length; i++)
            if (i < result.length)
                result[i] = old[i];
        return result;
    }

    /**
     * 获取全部置0的byte[]
     *
     * @param num byte[]长度
     * @return 全部置0的byte[]
     */
    public static byte[] getEmptyByte(int num) {
        byte[] result = new byte[num];
        for (byte b : result) {
            b = 0x00;
        }
        return result;
    }

    /**
     * 获取定长String字串的ASCII码的十六进制数组 like ABCD to 0x41 0x42 0x43 0x44 注意是16进制表示
     *
     * @param s 输入字串
     * @return 长截断, 短补零的byte[]
     */
    public static byte[] getAsciiByte(String s, int length) {
        return getByteWithLength(getAsciiByte(s), length);
    }

    /**
     * 获取String字串的ASCII码的十六进制数组 like ABCD to 0x41 0x42 0x43 0x44 注意是16进制表示
     *
     * @param s 输入字串
     * @return byte[]
     */
    public static byte[] getAsciiByte(String s) {
        byte[] result = getEmptyByte(s.length());
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++)
            result[i] = (byte) chars[i];
        return result;
    }

    /**
     * 十六进制的ASCII码翻译回String like 0x41 0x42 0x43 0x44 to ABCD
     *
     * @param b 输入的byte数组
     * @return result 翻译后的String
     */
    public static String getStringFromByte(byte[] b) {
        char[] chars = new char[b.length];
        for (int i = 0; i < b.length; i++)
            chars[i] = (char) b[i];
        return new String(chars);
    }

    /**
     * byte高低位反制
     *
     * @param s 输入的byte数组
     * @return b 返回byte数组
     */
    public static byte[] swapByte(byte[] s) {
        return new byte[]{s[1], s[0]};
    }

    /**
     * CRC运算函数
     *
     * @param buf 需要进行CRC计算的字串
     * @return result 返回计算后的结果
     */
    public static byte[] getCRC(byte[] buf) {
        byte[] result = new byte[2];
        int i, j;
        int c, crc = 0xFFFF;
        for (i = 0; i < buf.length; i++) {
            c = buf[i] & 0x00FF;
            crc ^= c;
            for (j = 0; j < 8; j++) {
                if ((crc & 0x0001) != 0) {
                    crc >>= 1;
                    crc ^= 0xA001;
                } else {
                    crc >>= 1;
                }
            }
        }

        result[0] = (byte) (crc & 0xFF);
        result[1] = (byte) ((crc >> 8) & 0xFF);
        return result;
    }

    /**
     * byte串拼接函数
     *
     * @param buf1 第一个串
     * @param buf2 第二个串
     * @return result 拼接结果
     */
    public static byte[] arraycat(byte[] buf1, byte[] buf2) {
        int len1 = buf1.length;
        int len2 = buf2.length;
        byte[] bufret = new byte[len1 + len2];
        System.arraycopy(buf1, 0, bufret, 0, len1);
        System.arraycopy(buf2, 0, bufret, len1, len2);
        return bufret;
    }

    /**
     * 取byte串字串
     *
     * @param original 原串
     * @param from     开始下标（包含）
     * @param to       结束下标（包含）
     * @return result 取出的字串
     */
    public static byte[] copyOfRange(byte[] original, int from, int to) {
        return copyOfLength(original, from, to + 1 - from);
    }

    /**
     * 取byte串字串
     *
     * @param original  原串
     * @param from      开始下标（包含）
     * @param newLength 要取的长度
     * @return result 取出的字串
     */
    public static byte[] copyOfLength(byte[] original, int from, int newLength) {
        int to = from + newLength;
        if (newLength < 0 || to > original.length || from > original.length)
            return new byte[]{};
        byte[] result = new byte[newLength];
        System.arraycopy(original, from, result, 0, newLength);
        return result;
    }

    /**
     * 查找byte串子串
     *
     * @param original 原串
     * @param from     开始位置
     * @param target   要寻找的子串
     * @return 找到的子串首位
     */
    public static int findByte(byte[] original, int from, byte[] target) {
        if (original == null || from <= 0 || target == null
            || original.length - target.length < from)
            return -1;

        for (int i = from; i <= original.length - target.length; i++)
            for (int j = 0; j < target.length; j++)
                if (original[i + j] != target[j])
                    break;
                else if (j == target.length - 1)
                    return i;
        return -1;
    }

    /**
     * byte串中查找数值
     *
     * @param original 原串
     * @param from     开始位置
     * @param target   要寻找的值
     * @return 找到的值首位
     */
    public static int findByte(byte[] original, int from, byte target) {
        if (original == null || from <= 0)
            return -1;

        for (int i = from; i <= original.length; i++)
            if (original[i] == target)
                return i;

        return -1;
    }

    /**
     * 将byte转为Hex String形式
     *
     * @param b 原始byte数据
     * @return hex String形式的数据
     */
    public static String byte2hex(byte b) {
        return byte2hex(new byte[]{b});
    }

    /**
     * 将byte[]数据转为Hex String形式
     *
     * @param buffer 原始byte[]数据
     * @return hex String形式的数据
     */
    public static String byte2hex(byte[] buffer) {
        return byte2hex(buffer, false);
    }

    /**
     * 将byte[]数据转为Hex String形式
     *
     * @param buffer 原始byte[]数据
     * @param space  中间是否有空格
     * @return hex String形式的数据
     */
    public static String byte2hex(byte[] buffer, boolean space) {

        return bytesToHexString(buffer, space, true);
    }

    /**
     * 将byte[]数据转为Hex String形式
     *
     * @param bytes     原始byte[]数据
     * @param space     中间是否有空格
     * @param upperCase 大小写
     * @return hex String形式的数据
     */
    public static String bytesToHexString(byte[] bytes, boolean space,
                                          boolean upperCase) {
        char[] digits = upperCase ? UPPER_CASE_DIGITS : DIGITS;
        char[] buf;
        if (space)
            buf = new char[bytes.length * 3];
        else
            buf = new char[bytes.length * 2];
        int c = 0;
        for (byte b : bytes) {
            buf[c++] = digits[(b >> 4) & 0xf];
            buf[c++] = digits[b & 0xf];
            if (space)
                buf[c++] = ' ';
        }

        //  Log.e(TAG, "转换为hex的string的bytes[]数据=" + str);
        return new String(buf);
    }

    /**
     * 将HexString数据转为byte形式
     *
     * @param hexString 原始HexString数据
     * @return byte[]形式的数据
     */
    @SuppressLint("DefaultLocale")
    public static byte[] hex2byte(String hexString) {
        if (hexString == null || hexString.equals(""))
            return new byte[]{};

        hexString = hexString.replaceAll(" ", "").toUpperCase();
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[hexString.length() / 2];

        for (int i = 0; i < d.length; i++)
            d[i] = (byte) (charToByte(hexChars[i * 2]) << 4 | charToByte(hexChars[i * 2 + 1]));
        return d;
    }

    /**
     * 获得带CRC的byte字串
     *
     * @param buffer 原始byte数据
     * @return byte[]形式的数据
     */
    public static byte[] getByteWithCRC(byte[] buffer) {
        return arraycat(buffer, getCRC(buffer));
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }


    /**
     * 剥离 肌电，脉率，加速度等临床特征值的数据报文
     *
     * @param command command
     * @return formatData
     */
    @NonNull
    public static String formatData(byte[] command) {
        //0x 55 d1
        //      08 0c3226 0001 0101 0101 0101 0101 0101 0101 0101
        String cmd = BlueByteUtil.byte2hex(command);
        cmd = cmd.substring(2);
        //String cmd = "d1 08 0c3226 00010101010101010101010101010101";
        //            55d1 1a 000b00 0b0010000c000a000b000c000800
        char[] tempChars = cmd.toCharArray();
        char[] finalChars = new char[cmd.length() * 2];

        int index = 0;
        for (int i = 0, len = tempChars.length; i < len; i++) {
            switch (i) {
                case 6:
                case 8:
                    finalChars[index++] = ' ';
                default:
                    finalChars[index++] = tempChars[i];
                    if (i > 7 && ((i + 1) % 4 == 0) && !(i == len - 1)) {
                        finalChars[index++] = ' ';
                    }
                    break;
            }
        }

        cmd = new String(finalChars, 0, index);
        return cmd;
    }

    /**
     * 截断字符串,并以16进制的数字展示
     *
     * @param text       text
     * @param beginIndex beginIndex
     * @param endIndex   endIndex
     * @return 16 number
     */
    public static int subText2hex(String text, int beginIndex, int endIndex) {
        return Integer.parseInt(text.substring(beginIndex, endIndex), 16);
    }

    /**
     * 截断字符串,并以16进制的数字展示
     *
     * @param text       text
     * @param beginIndex beginIndex
     * @return 16 number
     */
    public static int subText2hex(String text, int beginIndex) {
        return subText2hex(text, beginIndex, text.length());
    }

}