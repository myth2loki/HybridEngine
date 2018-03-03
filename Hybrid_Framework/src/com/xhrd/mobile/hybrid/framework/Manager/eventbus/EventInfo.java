package com.xhrd.mobile.hybrid.framework.Manager.eventbus;

import com.xhrd.mobile.hybrid.engine.RDCloudView;

/**
 * Created by lilong on 15/5/21.
 */
class EventInfo {
    public String function;
    public RDCloudView rdCloudView;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventInfo eventInfo = (EventInfo) o;

        if (function != null ? !function.equals(eventInfo.function) : eventInfo.function != null)
            return false;
        return !(rdCloudView != null ? !rdCloudView.equals(eventInfo.rdCloudView) : eventInfo.rdCloudView != null);

    }

    @Override
    public int hashCode() {
        int result = function != null ? function.hashCode() : 0;
        result = 31 * result + (rdCloudView != null ? rdCloudView.hashCode() : 0);
        return result;
    }
}
