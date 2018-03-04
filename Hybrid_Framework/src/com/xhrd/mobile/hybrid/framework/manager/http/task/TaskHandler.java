package com.xhrd.mobile.hybrid.framework.manager.http.task;

/**
 * Author: wyouflf
 * Time: 2014/05/23
 */
public interface TaskHandler {

    boolean supportPause();

    boolean supportResume();

    boolean supportCancel();

    void pause();

    void resume();

    void cancel();

    boolean isPaused();

    boolean isCancelled();
}
