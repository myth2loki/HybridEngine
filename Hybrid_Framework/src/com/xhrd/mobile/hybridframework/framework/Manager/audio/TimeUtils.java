package com.xhrd.mobile.hybridframework.framework.Manager.audio;

/**
 * Created by Administrator on 2015/12/9.
 */
public class TimeUtils {
    /**
     * 格式化时间
     * @param ms
     * @return
     */
    public static CharSequence formatTime(int ms) {
        if (ms >= 0) {
            final int totalSeconds = ms / 1000;
            final int hours = totalSeconds / 3600;
            final int minutes = (totalSeconds % 3600) / 60;
            final int second = ((totalSeconds % 3600) % 60);
            final StringBuffer sb = new StringBuffer();
            if (hours > 0) {
                if (hours <= 10) {
                    sb.append("0");
                }
                sb.append(hours).append(":");
            }
            if (minutes < 10) {
                sb.append("0");
            }
            sb.append(minutes).append(":");
            if (second < 10) {
                sb.append("0");
            }
            sb.append(second);
            return sb.toString();
        }
        return "";
    }
}
