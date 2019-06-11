package com.xsjqzt.module_main.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CPUUtil {
    public static void show() {
        Log.e("pan", "cpu最大频率：" + CPUUtil.getMaxCPU());
        Log.e("pan", "cpu最小频率：" + CPUUtil.getMinCPU());
        Log.e("pan", "cpu核数：" + CPUUtil.getCpuNum());
        Log.e("pan", "cpu可支持策略：" + CPUUtil.getsScalinAvailableGovernors());
        Log.e("pan", "cpu当前策略：" + CPUUtil.getsScalingGovernor());
        Log.e("pan", "cpu可支持频率：" + CPUUtil.getCpuScalingAvailableFrequencies());
        Log.e("pan", "cpu当前频率：" + CPUUtil.getCurCPU());
        Log.e("pan", "cpu温度：" + CPUUtil.getCPUTemperature());
        Log.e("pan", "cpu频率上限：" + CPUUtil.getScalingMaxFreq());
        Log.e("pan", "cpu频率下限：" + CPUUtil.getScalingMinFreq());
    }

    /**
     * //获取当前CPU频率
     */
    public static int getCurCPU() {
        String CurPath = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq";
        int result = 0;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(CurPath);
            br = new BufferedReader(fr);
            String text = br.readLine();
            result = Integer.parseInt(text.trim());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null) try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (br != null) try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 所有支持的主频率列表
     */
    public static String getCpuScalingAvailableFrequencies() {
        String mCurPath = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_frequencies";
        String result = "";
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(mCurPath);
            br = new BufferedReader(fr);
            String text = br.readLine();
            result = text;//Integer.parseInt(text.trim());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null) try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (br != null) try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static int getMaxCPU() {
        String MaxPath = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq";//保存CPU可运行最大频率 //获取CPU可运行最大频率
        int result = 0;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(MaxPath);
            br = new BufferedReader(fr);
            String text = br.readLine();
            result = Integer.parseInt(text.trim());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null) try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (br != null) try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    public static int getMinCPU() {
        String MinPath = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq";//保存CPU可运行最小频率 //获取CPU可运行最小频率
        int result = 0;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(MinPath);
            br = new BufferedReader(fr);
            String text = br.readLine();
            result = Integer.parseInt(text.trim());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null) try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (br != null) try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 查看 CPU 的支持策略
     */
    public static String getsScalinAvailableGovernors() {
        String mCurPath = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_available_governors";
        String result = "";
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(mCurPath);
            br = new BufferedReader(fr);
            String text = br.readLine();
            result = text;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null) try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (br != null) try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 查看 CPU 的当前策略
     */
    public static String getsScalingGovernor() {
        String mCurPath = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";
        String result = "";
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(mCurPath);
            br = new BufferedReader(fr);
            String text = br.readLine();
            result = text;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null) try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (br != null) try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 查看cpu核数
     */
    public static String getCpuNum() {
        // String mCurPath = "/sys/devices/system/cpu/present";
        String mCurPath = "/sys/devices/system/cpu/online";
        String result = "";
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(mCurPath);
            br = new BufferedReader(fr);
            String text = br.readLine();
            result = text;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null) try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (br != null) try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * cpu温度
     *
     * @return
     */
    public static int getCPUTemperature() {
        String temperaturePath = "/sys/devices/ff280000.tsadc/temp1_input";
        int result = 0;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(temperaturePath);
            br = new BufferedReader(fr);
            String text = br.readLine();
            result = Integer.parseInt(text.trim());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null) try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (br != null) try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;

    }

    /**
     * 获取频率上/下限
     *
     * @return
     */
    public static int getScalingMaxFreq() {
        String path = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq";
        int result = 0;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(path);
            br = new BufferedReader(fr);
            String text = br.readLine();
            result = Integer.parseInt(text.trim());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null) try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (br != null) try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static int getScalingMinFreq() {
        String path = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq";
        int result = 0;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(path);
            br = new BufferedReader(fr);
            String text = br.readLine();
            result = Integer.parseInt(text.trim());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null) try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (br != null) try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    //3288板子
    public static boolean setScalingMaxFreq(String governor) {

        DataOutputStream os = null;
        byte[] buffer = new byte[256];
        String command = "echo " + governor + " > " + "/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq";
        try {
            Process process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (IOException e) {
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

}