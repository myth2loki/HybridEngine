package com.xhrd.mobile.hybridframework.util.log;

/**
 * 日志处理，便于记录日志。
 * Created by wangqianyu on 16/2/15.
 */
public class LogFactory {
    /**
     * 获取日志实例。
     * @param clazz 关联的类
     * @return 日志实例
     */
    public static RDLog getLog(Class<?> clazz) {
        return new LogUtils(clazz);
    }
}
