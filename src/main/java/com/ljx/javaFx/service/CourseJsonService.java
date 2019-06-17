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

    public static void main(String[] args) {
        String raw = "curl 'http://wsxy.chinaunicom.cn/api/learner/play/course/49143851/save' -H 'Pragma: no-cache' -H 'Origin: http://wsxy.chinaunicom.cn' -H 'X-XSRF-TOKEN: 94271ad5-f54d-4ee1-9bf3-f83f18cb0aea' -H 'Accept-Encoding: gzip, deflate' -H 'Accept-Language: zh-CN,zh;q=0.9' -H 'User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36' -H 'Content-Type: multipart/form-data; boundary=----WebKitFormBoundaryYlGRjiRQIJZ1Z2Mg' -H 'Accept: application/json, text/plain, */*' -H 'Cache-Control: no-cache' -H 'Referer: http://wsxy.chinaunicom.cn/learner/play/course/49143851;classroomId=49652446;courseDetailId=36638;learnerAttemptId=1553853131612' -H 'Cookie: XSRF-TOKEN=94271ad5-f54d-4ee1-9bf3-f83f18cb0aea; PRODSESSION=22607f82-a3e2-466e-b00b-90d5728eb3da; nai=1553853139000' -H 'Connection: keep-alive' --data-binary $'------WebKitFormBoundaryYlGRjiRQIJZ1Z2Mg\\r\\nContent-Disposition: form-data; name=\"rawStatus\"\\r\\n\\r\\nincomplete\\r\\n------WebKitFormBoundaryYlGRjiRQIJZ1Z2Mg\\r\\nContent-Disposition: form-data; name=\"credit\"\\r\\n\\r\\nno-credit\\r\\n------WebKitFormBoundaryYlGRjiRQIJZ1Z2Mg\\r\\nContent-Disposition: form-data; name=\"attemptToken\"\\r\\n\\r\\nc27ba663-d3dc-4e5c-8d5d-d940034feea1\\r\\n------WebKitFormBoundaryYlGRjiRQIJZ1Z2Mg\\r\\nContent-Disposition: form-data; name=\"learnerAttemptId\"\\r\\n\\r\\n1553853131612\\r\\n------WebKitFormBoundaryYlGRjiRQIJZ1Z2Mg\\r\\nContent-Disposition: form-data; name=\"course.id\"\\r\\n\\r\\n49143851\\r\\n------WebKitFormBoundaryYlGRjiRQIJZ1Z2Mg\\r\\nContent-Disposition: form-data; name=\"classroom.id\"\\r\\n\\r\\n49652446\\r\\n------WebKitFormBoundaryYlGRjiRQIJZ1Z2Mg\\r\\nContent-Disposition: form-data; name=\"rco.id\"\\r\\n\\r\\n38695067\\r\\n------WebKitFormBoundaryYlGRjiRQIJZ1Z2Mg\\r\\nContent-Disposition: form-data; name=\"sessionTime\"\\r\\n\\r\\n00:00:08\\r\\n------WebKitFormBoundaryYlGRjiRQIJZ1Z2Mg\\r\\nContent-Disposition: form-data; name=\"terminalType\"\\r\\n\\r\\nPC\\r\\n------WebKitFormBoundaryYlGRjiRQIJZ1Z2Mg\\r\\nContent-Disposition: form-data; name=\"location\"\\r\\n\\r\\n6.475371\\r\\n------WebKitFormBoundaryYlGRjiRQIJZ1Z2Mg--\\r\\n' --compressed";
        String time = "62:37";
        System.out.println(new CourseJsonService().convert(raw, time));
    }

}
