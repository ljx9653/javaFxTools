package com.ljx.javaFx.utils;

import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.MultimediaInfo;

import java.io.File;

/**
 * @author lijx
 * @date 2019/4/15 - 18:07
 */
public class VideoUtil {
    private static Encoder encoder = new Encoder();

    public static String getVideoTimeString(File source) {
        Encoder encoder = new Encoder();
        String length = "";
        try {
            MultimediaInfo m = encoder.getInfo(source);
            long ls = m.getDuration() / 1000;
            int hour = (int) (ls / 3600);
            int minute = (int) (ls % 3600) / 60;
            int second = (int) (ls - hour * 3600 - minute * 60);
            length = hour + ":" + minute + ":" + second + ":'";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return length;
    }

    public static long getVideoTimeSeconds(File source) {
        long result = 0;
        try {
            MultimediaInfo m = encoder.getInfo(source);
            result = m.getDuration() / 1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
