package com.xhrd.mobile.hybrid.engine;

import android.os.Build;
import android.webkit.WebView;

import com.xhrd.mobile.hybrid.Config;

/**
 * Javascript脚本类，封装了系统提供的js接口，以及插件的js接口。
 */
public class HybridScript {
    /**
     * 通用协议，框架通过迭代所有内部功能/外部插件来执行
     */
    public static final String JS_SCHEMA = "hybrid";
    /**
     * 内部功能调用协议
     */
    public static final String JS_SCHEMA_0 = "hybrid0";
    /**
     * 外部插件调用协议
     */
    public static final String JS_SCHEMA_1 = "hybrid1";
    /**
     * 框架域名
     */
    public static final String RD = "rd";
    public static final String RD_FRAMEWORK = "RD.framework";
    /**
     * 组件域名
     */
    public static final String RD_COMPONENT = "RD.component";
    /**
     * 插件域名
     */
    public static final String RD_PLUGIN = "RD.plugin";

    public static final String RD_UI = "RD.ui";


    /**
     * 建立js对象，加载前注入
     */
    public static String Pre_RDScript = "javascript:" +
            //定义域名
            "RD = {}; " +
            "RD.internal = {}; " +
            "RD.component = {};" +
            "RD.component.internal = {};" +
            "RD.plugin = {};" +
            "RD.plugin.internal = {};" +
            "RD.ui = {};" +
            "RD.ui.internal = {};" +
            "RD.framework = {};" +
            "RD.framework.internal = {};" +
            //加载时替换这个占位符
            "rd = {onLoad:function(){}, onForeground:function(){}, onBackground:function(){}, onDestroy:function(){}};" +
            //"rd.Window = {name : '%s'};" +
            "";

    private final static String MAPScript = "" +
            "function Map() {\n" +
            "    this.elements = new Array();\n" +
            " \n" +
            "    this.size = function() {\n" +
            "        return this.elements.length;\n" +
            "    };\n" +
            " \n" +
            "    this.isEmpty = function() {\n" +
            "        return (this.elements.length < 1);\n" +
            "    };\n" +
            " \n" +
            "    this.clear = function() {\n" +
            "        this.elements = new Array();\n" +
            "    };\n" +
            " \n" +
            "    this.put = function(_key, _value) {\n" +
            "        this.elements.push( {\n" +
            "            key : _key,\n" +
            "            value : _value\n" +
            "        });\n" +
            "    };\n" +
            " \n" +
            "    this.remove = function(_key) {\n" +
            "        var bln = false;\n" +
            "        try {\n" +
            "            for (i = 0; i < this.elements.length; i++) {\n" +
            "                if (this.elements[i].key == _key) {\n" +
            "                    this.elements.splice(i, 1);\n" +
            "                    return true;\n" +
            "                }\n" +
            "            }\n" +
            "        } catch (e) {\n" +
            "            bln = false;\n" +
            "        }\n" +
            "        return bln;\n" +
            "    };\n" +
            " \n" +
            "    this.get = function(_key) {\n" +
            "        try {\n" +
            "            for (i = 0; i < this.elements.length; i++) {\n" +
            "                if (this.elements[i].key == _key) {\n" +
            "                    return this.elements[i].value;\n" +
            "                }\n" +
            "            }\n" +
            "        } catch (e) {\n" +
            "            return null;\n" +
            "        }\n" +
            "    };\n" +
            " \n" +
            "    this.element = function(_index) {\n" +
            "        if (_index < 0 || _index >= this.elements.length) {\n" +
            "            return null;\n" +
            "        }\n" +
            "        return this.elements[_index];\n" +
            "    };\n" +
            " \n" +
            "    this.containsKey = function(_key) {\n" +
            "        var bln = false;\n" +
            "        try {\n" +
            "            for (i = 0; i < this.elements.length; i++) {\n" +
            "                if (this.elements[i].key == _key) {\n" +
            "                    bln = true;\n" +
            "                }\n" +
            "            }\n" +
            "        } catch (e) {\n" +
            "            bln = false;\n" +
            "        }\n" +
            "        return bln;\n" +
            "    };\n" +
            " \n" +
            "    this.containsValue = function(_value) {\n" +
            "        var bln = false;\n" +
            "        try {\n" +
            "            for (i = 0; i < this.elements.length; i++) {\n" +
            "                if (this.elements[i].value == _value) {\n" +
            "                    bln = true;\n" +
            "                }\n" +
            "            }\n" +
            "        } catch (e) {\n" +
            "            bln = false;\n" +
            "        }\n" +
            "        return bln;\n" +
            "    };\n" +
            " \n" +
            "    this.values = function() {\n" +
            "        var arr = new Array();\n" +
            "        for (i = 0; i < this.elements.length; i++) {\n" +
            "            arr.push(this.elements[i].value);\n" +
            "        }\n" +
            "        return arr;\n" +
            "    };\n" +
            " \n" +
            "    this.keys = function() {\n" +
            "        var arr = new Array();\n" +
            "        for (i = 0; i < this.elements.length; i++) {\n" +
            "            arr.push(this.elements[i].key);\n" +
            "        }\n" +
            "        return arr;\n" +
            "    };\n" +
            "}\n" +
            "";

    private static String CallbackScript = "" +
            "RD.internal.nativeCallbacks = {" +
            "   token : 0," +
            "   subToken : 0," +
            "   callbacks : new Map()," +
            "   get : function(ko) { var m = this.callbacks.get(ko.t); return m.get(ko.st); }," +
            "   put : function(key, cb) { " +
            "       if(key == null){key = this.token++;}" +
            "       var m = this.callbacks.get(key); " +
            "       if(!m){" +
            "           m = new Map();" +
            "           this.callbacks.put(key, m);" +
            "       }" +
            "       m.put(this.subToken, cb);" +
            "       var ret = {t : key, st : this.subToken}; " +
            "       this.subToken++;" +
            "       return ret;" +
            "   }," +
            "   removeOne : function(t) { this.callbacks.remove(t);}," +
            "   remove : function(t, st) { var m = this.callbacks.get(t); if(m.isEmpty()){this.callbacks.remove(t);} else {m.remove(st); }}," +
            "   clear : function() { this.callbacks.clear();}" +
            "}; \n" +
            "";

    /**
     * 系统js接口代码
     */
    public static String RDScript = "javascript:" +
            MAPScript +
            CallbackScript +
            //将js元素转换成String数组，以便本地代码处理。
            "function ja2sa(a){" +
                "var l = a.length;" +
                "var t = new Array(l);" +
                "var token = null;" +
                "for(var i = 0; i<l; i++){" +
                    "var b = a[i];" +
                    "if(typeof(b) == 'number'){" +
                        "t[i] = b.toString();" +
                    "} else if(b instanceof Function){" +
                        "var ko = RD.internal.nativeCallbacks.put(token, b);" +
//                        "console.log('ko: ' + JSON.stringify(ko) + ', ' + b.toString());" +
                        "token = ko.t;" +
//                        "console.log('token: ' + token);" +
                        "t[i] = 'RD.internal.nativeCallbacks.get(' + JSON.stringify(ko) + ')_' + ko.t + '_' + ko.st;" +
                        //"t[i] = JSON.stringify(b);" +
                    "} else if(b instanceof Object){" +
                        "t[i] = JSON.stringify(b);" +
                    "}else{" +
                        "t[i] = b;" +
                    "}" +
                "}" +
                "return t;" +
            "};" +

            "function ja2s(a){" +
                "console.log('pre a: ' + a);" +
                "return JSON.stringify(ja2sa(a));" +
            "};" +

            "function exec(){" +
                "var schema = arguments[0];" +
                "var paramStr = ja2s(arguments[1]);" +
                "var isReturn = arguments[2];" +
                "var convertReturn = arguments[3];" +

                "console.log('paramStr: '+paramStr);" +
                "var ret = prompt(schema, paramStr);" +
                (Config.DEBUG ? "console.log('ret--------> '+ret);" : "") +
                "if(convertReturn){" +
                    "try{" +
                        "return eval('('+ret+')');" +
                    "}catch(e){" +
                        (Config.DEBUG ? "console.log('eval error source: '+ret);" : "") +
                        "return null;" +
                    "}" +
                "}else if(isReturn){" +
                    (Config.DEBUG ? "console.log('typeof ret: ' + typeof(ret));" : "") +
                    "return ret;" +
                "}" +
            "};" +
            "";

    static void jsFix(WebView view) {
//        Log.e("-->", Build.DEVICE);
//        Log.e("-->", Build.PRODUCT);
//        Log.e("-->", Build.MODEL);
        //NOTE2 outerWidth outerHeight不对
        if (Build.MODEL.equalsIgnoreCase("GT-N7102") && Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR2) {
            view.loadUrl("javascript: Window.outerWidth /= 2; Window.outerHeight /= 2;");
        }
    }
}
