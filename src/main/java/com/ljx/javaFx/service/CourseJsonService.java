package com.ljx.javaFx.service;

import com.ljx.javaFx.utils.TimeUtil;

/**
 * @author lijx
 * @date 2019/3/14 - 17:36
 */
public class CourseJsonService {

    public String convert(String raw, String time) {
        if(!raw.startsWith("curl")){
            return "error";
        }
        //处理时间
        String[] times = time.split(":");
        if (times.length <= 1 || times.length > 3) {
            return "时间不合法";
        }
        String finalTime = null;
        int seconds = 0;

        i:
        if (times.length == 2) {
            if (Integer.parseInt(times[0]) >= 60) {
                finalTime = TimeUtil.formatTime(Integer.parseInt(times[0]) / 60 + ":" + Integer.parseInt(times[0]) % 60 + ":" + times[1]);
                seconds = Integer.parseInt(times[0]) * 60 + Integer.parseInt(times[1]);
                break i;
            }
            finalTime = "00:" + time;
            seconds = Integer.parseInt(times[0]) * 60 + Integer.parseInt(times[1]);
        } else {
            finalTime = time;
            seconds = Integer.parseInt(times[0]) * 3600 + Integer.parseInt(times[1]) * 60 + Integer.parseInt(times[2]);
        }

        //处理cookie，一分为三
        int cookieSta = raw.indexOf("Cookie:");
        int cookieEnd = raw.indexOf("-H", cookieSta);
        String[] split = raw.substring(cookieSta, cookieEnd).split(";");
        StringBuilder tmp = new StringBuilder();
        tmp.append(split[0]);
        tmp.append("' \n-H 'Cookie:");
        tmp.append(split[1]);
        tmp.append("' \n-H 'Cookie:");
        tmp.append(split[2]);
        //替换cookie
        StringBuilder result = new StringBuilder(raw);
        result.replace(cookieSta, cookieEnd, tmp.toString());

        //处理--data-binary
        int dataEnd = raw.indexOf("--compressed");
        int dataSta = raw.indexOf("--data-binary");
        String data = raw.substring(dataSta + 15, dataEnd);
        String[] split1 = data.split(";");
        //初始化StringBuilder
        tmp.delete(0, tmp.length());
        for (int i = 1; i < split1.length; i++) {
            tmp.append("-F ");
            tmp.append(split1[i].substring(7, split1[i].indexOf("\\r\\n\\r\\n") - 1));
            tmp.append("=");
            tmp.append(split1[i].substring(split1[i].indexOf("\\r\\n\\r\\n") + 8, split1[i].indexOf("\\r\\n-")));
            tmp.append(" \\");
        }
        if (tmp.indexOf("incomplete") != -1) {
            tmp.delete(tmp.indexOf("incomplete"), tmp.indexOf("incomplete") + 2);
        }
        //替换时间
        if (tmp.lastIndexOf("sessionTime=") != -1) {
            tmp.replace(tmp.lastIndexOf("sessionTime=") + 12, tmp.lastIndexOf("sessionTime=") + 20, finalTime);
        }
        if (tmp.lastIndexOf("location=") != -1) {
            tmp.replace(tmp.lastIndexOf("location") + 9, tmp.length(), seconds + "");
        }
        //处理完成，替换data
        result.replace(result.indexOf("--data-binary"), result.length(), tmp.toString());
        return result.toString();
    }
}
