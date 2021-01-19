package com.ljx.javaFx.service;

import com.ljx.javaFx.utils.TimeUtil;
import com.ljx.javaFx.utils.VideoUtil;

import java.io.File;

/**
 * @author lijx
 * @date 2019/4/12 - 11:26
 */
public class TimeStatisticsService {

    private String totalTime = "00:00:00";

    public String timeStatistics(String inputText) {
        String[] times = inputText.split("\\s");
        for (int i = 0; i < times.length; i++) {
            if (times[i].trim().matches(TimeUtil.timeRegex)) {
                totalTime = TimeUtil.timeSum(totalTime, times[i]);
            }
        }
        return totalTime;
    }

    public String dirTimeStatistics(File dir) {
        long totaltime = getDirVideoTime(dir);
        int hour = (int) (totaltime / 3600);
        int minute = (int) (totaltime % 3600) / 60;
        int second = (int) (totaltime - hour * 3600 - minute * 60);
        String result = hour + ":" + minute + ":" + second;
        return TimeUtil.formatTime(result);
    }

    private boolean isVidoe(String fileName) {
        return fileName.endsWith(".avi") || fileName.endsWith(".mp4") || fileName.endsWith(".flv") || fileName.endsWith(".wmv");
    }

    private long getDirVideoTime(File dir) {
        long l = 0;
        if (dir == null) {
            return l;
        }
        File[] files = dir.listFiles();
        if (files == null) {
            return l;
        }
        for (File i : files) {
            if (i.isFile() && isVidoe(i.getName())) {
                l += VideoUtil.getVideoTimeSeconds(i);
            } else {
                l += getDirVideoTime(i);
            }
        }
        return l;
    }

    public String getTotalTime() {
        return TimeUtil.formatTime(totalTime);
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }
}
