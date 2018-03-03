package com.xhrd.mobile.hybrid.framework.Manager.properties;

import android.util.SparseArray;

import com.xhrd.mobile.hybrid.annotation.JavascriptFunction;
import com.xhrd.mobile.hybrid.framework.PluginBase;
import com.xhrd.mobile.hybrid.framework.PluginData;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by lilong on 15/6/26.
 */
public class PropertiesManager extends PluginBase {
    private static final String PROPERTIES_JS =
            "{"+
                "id:%d," +
                "hostId:%d," +
                "putProperty:function(key,value){" +
                "if (arguments == null || arguments.length == 0)" +
                "return; " +
                "var ret = exec('RDCloud://" + PropertiesManager.class.getName() + "/call/'+this.hostId, ['putProperty',this.id,key,value], true, false);" +
                "return ret;" +
                "}, " +
                "getProperty:function(key){" +
                "if (arguments == null || arguments.length == 0)" +
                "return; " +
                "var ret = exec('RDCloud://" + PropertiesManager.class.getName() + "/call/'+this.hostId, ['getProperty',this.id,key], true, false);" +
                "return ret;" +
                "}, " +
                "deleteProperty:function(key){" +
                "if (arguments == null || arguments.length == 0)" +
                "return; " +
                "var ret = exec('RDCloud://" + PropertiesManager.class.getName() + "/call/'+this.hostId, ['deleteProperty',this.id,key], true, false);" +
                "return ret;" +
                "}, " +
                    "save:function(){" +
                    "var ret = exec('RDCloud://" + PropertiesManager.class.getName() + "/call/'+this.hostId, ['save',this.id], true, false);" +
                    "return ret;" +
                    "}, " +
                "clean:function(){" +
                "exec('RDCloud://" + PropertiesManager.class.getName() + "/call/'+this.hostId, ['clean',this.id], false, false);" +
                "}}";

    private AtomicInteger mInteger = new AtomicInteger();
    private SparseArray<PropertiesHelper> proMap = new SparseArray<PropertiesHelper>();

    @Override
    public void addMethodProp(PluginData data) {
        data.addMethod("openProperties", true);
        data.addMethod("call", new String[]{"method", "id"}, false, true);
    }

    @JavascriptFunction
    public String openProperties(String windowName, String[] params) {
        String parentName = params[0];
        String propertiesName = params[1];
        int id = mInteger.getAndIncrement();
        PropertiesHelper propertiesHelper = new PropertiesHelper(parentName,propertiesName);
        proMap.put(id,propertiesHelper);
        return String.format(PROPERTIES_JS, id, getId());
    }

    @JavascriptFunction
    public String call(String windowName, String[] params) {
        String method = params[0];
        int id = Integer.parseInt(params[1]);
        PropertiesHelper helper = proMap.get(id);
        if (helper == null) {
            return null;
        }
        if (method.equals("putProperty")) {
            return helper.putProperty(params[2],params[3])+"";
        } else if(method.equals("getProperty")) {
            return helper.getProperty(params[2])+"";
        } else if(method.equals("deleteProperty")) {
            return helper.deleteProperty(params[2])+"";
        } else if(method.equals("clean")) {
              helper.clean();
        }else if(method.equals("save")) {
             return helper.save()+"";
        }
        return  null;
    }

    @Override
    public String getDefaultDomain() {
        return "properties";
    }

    @Override
    public PluginData.Scope getScope() {
        return PluginData.Scope.app;
    }

}
