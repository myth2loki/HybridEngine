package com.xhrd.mobile.hybrid.util;

import android.util.Log;

import com.xhrd.mobile.hybrid.engine.RDEncryptHelper;
import com.xhrd.mobile.hybrid.framework.manager.ResManagerFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by maxinliang on 15/8/25.
 */
public class AppKeyXmlUtil {

//    <config pluginName="JPush">
//        <param key="JPUSH_CHANNEL" value="91zhushou"></param> 
//        <android> 
//                 <param key="JPUSH_APPKEY" value="15ff246ss546546546545jg36"></param> 
//        </android>
//    </config>

    // 存放所有appkey
    private static Map<String, Map<String, String>> appKeys;


    private static final String pluginConfigsXmlPath = "res://pluginConfigs.xml";

    /**
     * 获取pluginConfigs中配置的appKey
     *
     * @param domain
     * @return map集合
     */
    public static Map<String, String> getPluginAppKeyMap(String domain) {

        if(appKeys == null) {
            appKeys = new HashMap<>();
            try {
                String configPath = ResManagerFactory.getResManager().getPath(pluginConfigsXmlPath);
                InputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(configPath);

                    inputStream = RDEncryptHelper.getDecryptInputStream(inputStream);// 解密
                    Document document = XmlUtil.xmlPath2Doc(inputStream);
                    if (document == null) {
                        Log.e("read xml", "读取pluginConfigs.xml---》document为null");
                        return null;
                    }
                    Element root_main = document.getDocumentElement();
                    NodeList items = root_main.getChildNodes();
                    int item_number = items.getLength();
                    for (int i = 1; i < item_number; i = i + 2) {
                        Node node = items.item(i);
                        if (Element.class.isInstance(node)) {
                            Element e = (Element) node;
                            Map<String, String> map = new HashMap<String, String>();
                            parseElement(e, map);
                            appKeys.put(e.getAttribute("pluginName"), map);
                        }
                    }
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return appKeys.get(domain);
    }

    /**
     * 解析Element
     *
     * @param element
     * @param map
     */
    private static void parseElement(Element element, Map<String, String> map) {
        NodeList items = element.getChildNodes();
        int item_number = items.getLength();
        for (int i = 1; i < item_number; i = i + 2) {
            Node node = items.item(i);
            if (Element.class.isInstance(node)) {
                Element e = (Element) node;
                if ("param".equals(e.getTagName())) {
                    map.put(e.getAttribute("key"), e.getAttribute("value"));
                } else if ("android".equals(e.getTagName())) {
                    parseElement(e, map);
                }
            }
        }
    }


}
