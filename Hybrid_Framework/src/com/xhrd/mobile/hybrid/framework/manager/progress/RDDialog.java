package com.xhrd.mobile.hybrid.framework.manager.progress;

import android.app.Dialog;
import android.content.Context;

import com.xhrd.mobile.hybrid.engine.HybridResourceManager;

/**
 * Created by maxinliang on 15/6/28.
 */
public class RDDialog extends Dialog{

    //public int height;
    //public int width;

    public RDDialog(Context context) {
        super(context, HybridResourceManager.getInstance().getStyleId("progressRDDialog"));
    }

    
    public RDDialog(Context context, int theme) {
        super(context, theme);
    }
}
