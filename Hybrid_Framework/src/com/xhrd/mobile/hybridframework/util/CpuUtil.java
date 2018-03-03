package com.xhrd.mobile.hybridframework.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * Cpu操作类。
 * Created by wangqianyu on 15/6/4.
 */
public class CpuUtil {

    /**
     * 获得设备CPU的频率
     *
     * @return
     */
    public static int getCPUFrequency() {
        int mhz = 0;
        LineNumberReader isr = null;
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq");
            isr = new LineNumberReader(new InputStreamReader(pp.getInputStream()));
            String line = isr.readLine();
            if (line != null && line.length() > 0) {
                try {
                    mhz = Integer.parseInt(line.trim()) / 1000;
                } catch (Exception e) {
//					Log.e("SystemInfo","EUExDeviceInfo---getCPUFrequency()---NumberFormatException ");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (isr != null) {
                    isr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mhz;
    }

    /**
     processor	: 0
     model name	: ARMv7 Processor rev 0 (v7l)
     BogoMIPS	: 38.40
     Features	: swp half thumb fastmult vfp edsp neon vfpv3 tls vfpv4 idiva idivt
     CPU implementer	: 0x41
     CPU architecture: 7
     CPU variant	: 0x0
     CPU part	: 0xd03
     CPU revision	: 0

     processor	: 1
     model name	: ARMv7 Processor rev 0 (v7l)
     BogoMIPS	: 38.40
     Features	: swp half thumb fastmult vfp edsp neon vfpv3 tls vfpv4 idiva idivt
     CPU implementer	: 0x41
     CPU architecture: 7
     CPU variant	: 0x0
     CPU part	: 0xd03
     CPU revision	: 0

     processor	: 2
     model name	: ARMv7 Processor rev 0 (v7l)
     BogoMIPS	: 38.40
     Features	: swp half thumb fastmult vfp edsp neon vfpv3 tls vfpv4 idiva idivt
     CPU implementer	: 0x41
     CPU architecture: 7
     CPU variant	: 0x0
     CPU part	: 0xd03
     CPU revision	: 0

     processor	: 3
     model name	: ARMv7 Processor rev 0 (v7l)
     BogoMIPS	: 38.40
     Features	: swp half thumb fastmult vfp edsp neon vfpv3 tls vfpv4 idiva idivt
     CPU implementer	: 0x41
     CPU architecture: 7
     CPU variant	: 0x0
     CPU part	: 0xd03
     CPU revision	: 0

     Hardware	: Qualcomm Technologies, Inc MSM8916
     Revision	: 0000
     Serial		: 0000000000000000
     Processor	: ARMv7 Processor rev 0 (v7l)
     * @return
     */
    // 获取手机CPU类型信息
    public static CpuType getCpuInfo() {
        String str1 = "/proc/cpuinfo";
        StringBuilder str2 = new StringBuilder();
        String[] cpuInfo = { "", "" }; // 1-cpu型号 //2-cpu频率
        String[] arrayOfString;
        FileReader fr = null;
        BufferedReader localBufferedReader = null;
        try {
            fr = new FileReader(str1);
            localBufferedReader = new BufferedReader(fr, 8192);
            String line = null;
            while ((line = localBufferedReader.readLine()) != null) {
                str2.append(line).append('\n');
            }
//            arrayOfString = str2.split("\\s+");
//            for (int i = 2; i < arrayOfString.length; i++) {
//                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
//            }
//            str2 = localBufferedReader.readLine();
//            arrayOfString = str2.split("\\s+");
//            cpuInfo[1] += arrayOfString[2]; // cpu频率。

            cpuInfo[0] = str2.toString().toLowerCase();
            localBufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fr != null)
                    fr.close();

                if (localBufferedReader != null)
                    localBufferedReader.close();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // Log.i(TAG, "cpuinfo:" + cpuInfo[0] + " " + cpuInfo[1]);
        if (cpuInfo[0].contains("armv7")) {
            return CpuType.armv7;
        } else if (cpuInfo[0].contains("armv8")) {
            return CpuType.armv8;
        } else if (cpuInfo[0].contains("arm64")) {
            return CpuType.arm64;
        } else if (cpuInfo[0].contains("arm")) {
            return CpuType.armeabi;
        } else if (cpuInfo[0].contains("mips64")) {
            return CpuType.mips64;
        } else if (cpuInfo[0].contains("mips")) {
            return CpuType.mips;
        } else if (cpuInfo[0].contains("aarch64")){
            return CpuType.arm64v8a;
        } else if (cpuInfo[0].contains("x86_64")){
            return CpuType.x86_64;
        } else if (cpuInfo[0].contains("x86")) {
            return CpuType.x86;
        } else {
            return CpuType.x86;
        }

    }

    public static enum CpuType {
        armv7, armv8, arm64, arm64v8a, armeabi, mips, mips64, x86, x86_64;
    }
}
