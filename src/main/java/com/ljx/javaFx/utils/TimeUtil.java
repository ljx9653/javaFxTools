package com.ljx.javaFx.utils;

/**
 * @author lijx
 * @date 2019/4/15 - 10:30
 */
public class TimeUtil {

    public static String timeRegex="([0-9]{2,4}:){0,1}([0-9]{2}:[0-9]{2})";

    public static String timeSum(String resultTime, String tmpTime) {
        resultTime = formatTime(resultTime);
        tmpTime = formatTime(tmpTime);
        String[] resultTimeSplit = resultTime.split(":");
        String[] tmpTimeSplit = tmpTime.split(":");
        int[] time = {Integer.parseInt(resultTimeSplit[0]), Integer.parseInt(resultTimeSplit[1]), Integer.parseInt(resultTimeSplit[2])};
        for (int i = tmpTimeSplit.length - 1, j = 2; i >= 0; i--, j--) {
            time[j] += Integer.parseInt(tmpTimeSplit[i]);
        }
        if (time[2] >= 60) {
            time[1] += time[2] / 60;
            time[2] %= 60;
        }
        if (time[1] >= 60) {
            time[0] += time[1] / 60;
            time[1] %= 60;
        }
        return time[0] + ":" + time[1] + ":" + time[2];
    }

    public static String formatTime(String time) {
        if (validateTime(time)) {
            return time;
        }
        if (time.matches("([0-9]{1,3}:){0,1}([0-9]{1,2}:[0-9]{1,2})")) {
            String[] split = time.split(":");
            for (int i = 0; i < split.length; i++) {
                if (split[i].length() == 1) {
                    split[i] = "0" + split[i];
                }
            }
            return split[0] + ":" + split[1] + ":" + split[2];
        }
        return "00:00:00";
    }

    public static boolean validateTime(String time) {
        return time.matches(timeRegex);
    }
}
