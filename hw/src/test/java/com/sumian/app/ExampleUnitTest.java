package com.sumian.app;

import com.sumian.app.common.util.StreamUtil;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {

        //CD:9D:C4:08:D8:9D
        String mac = "CD:9D:C4:08:D8:FF";

        String[] split = mac.split(":");

        StringBuilder macSb = new StringBuilder();
        for (String s : split) {
            macSb.append(s);
        }

        //uint64 x old mac;, y new mac;
        // y = (( x & 0xFF ) + 1) + ((x >> 8) << 8);
        //由于 dfu 升级需要设备 mac+1
        long oldMac = Long.parseLong(macSb.toString(), 16);
        long newMac = ((oldMac & 0xff) + 1) + ((oldMac >> 8) << 8);


        macSb.delete(0, macSb.length());

        String hexString = Long.toHexString(newMac);

        for (int i = 0, len = hexString.length(); i < len; i++) {
            if (i % 2 == 0) {
                macSb.append(hexString.substring(i, i + 2));
                if (i != len - 2) {
                    macSb.append(":");
                }
            }
        }

        System.out.println("hexString=" + hexString + "   " + macSb.toString());
        //assertEquals(4, 2 + 2);
    }


    @Test
    public void loadSleepData() {
        File file = new File("../app/sampledata/sleepData.txt");
        System.out.println("file.path=" + file.getAbsolutePath());

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                Thread.sleep(200);
                if (line.equals("558e11a10f5ac2c2b4")) {
                    break;
                }
                System.out.println("data=" + line);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close(br);
        }
    }
}