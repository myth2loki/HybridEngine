package com.xhrd.mobile.hybridframework.framework;

import android.os.Handler;
import android.util.Log;

import com.xhrd.mobile.hybridframework.Config;
import com.xhrd.mobile.hybridframework.framework.Manager.ResManager;
import com.xhrd.mobile.hybridframework.framework.Manager.ResManagerFactory;
import com.xhrd.mobile.hybridframework.util.XmlUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link RDComponentInfo}管理器。<br/>
 * Created by wangqianyu on 15/6/11.
 */
public class RDComponentInfoManager {
    private static RDComponentInfoManager mInstance;

    private ResManager mResManager;
    private Map<String, RDComponentInfo> mComponentMap = new HashMap<String, RDComponentInfo>();
    private Handler mHandler;

    public synchronized static RDComponentInfoManager getInstance() {
        if (mInstance == null) {
            mInstance = new RDComponentInfoManager();
        }
        return mInstance;
    }

    private RDComponentInfoManager() {
        mResManager = ResManagerFactory.getResManager();
        mHandler = new Handler(HybridEnv.getApplicationContext().getMainLooper());
    }

    public void init() throws FileNotFoundException {
        String path = mResManager.getPath(ResManager.COMPONENTS_URI);

        File compFolder = new File(path);
        for (File folder : compFolder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        })) {
            File component = new File(folder, Constants.NAME_COMPONENT_XML);
            if (!component.exists()) {
                Log.d(RDComponentInfoManager.class.getSimpleName(), component.getAbsolutePath() + " is unavailable. ignored!");
                continue;
            }
            FileInputStream fis = null;
            RDComponentInfo info = null;
            try {
                try {
                    fis = new FileInputStream(component);
                    info = XmlUtil.readXml(fis, RDComponentInfo.class);
                    info.path = folder.getAbsolutePath();
                    mComponentMap.put(info.name, info);
                } finally {
                    if (fis != null) {
                        fis.close();
                    }
                }
            } catch (IOException e) {
                throw new FileNotFoundException(component.getAbsolutePath());
            }
        }
    }

    /**
     * 重新载入模块信息。
     */
    public void reload() {
        mComponentMap.clear();
        try {
            init();
        } catch (FileNotFoundException e) {

        }
    }

    /**
     * 通过名称获取模块实体类
     *
     * @param name 模块名
     * @return 模块实体
     */
    public RDComponentInfo getComponentInfo(String name) throws FileNotFoundException {
        RDComponentInfo info = mComponentMap.get(name);
        if (info == null) {
            ResManager rm = ResManagerFactory.getResManager();
            String path = rm.getPath(ResManager.COMPONENTS_URI);
            File compFolder = new File(path, name);

            File component = new File(compFolder, Constants.NAME_COMPONENT_XML);
            FileInputStream fis = null;
            try {
                try {
                    fis = new FileInputStream(component);
                    info = XmlUtil.readXml(fis, RDComponentInfo.class);
                } finally {
                    if (fis != null) {
                        fis.close();
                    }
                }
            } catch (IOException e) {
                throw new FileNotFoundException(e.getMessage());
            }
        }
        return info;
    }

    /**
     * 获取主模块信息
     *
     * @return
     */
    public RDComponentInfo getMainComponent() throws FileNotFoundException {
        RDApplicationInfo appInfo = null;
        String path = mResManager.getPath(ResManager.APP_URI);
        File compFolder = new File(path);

        File app = new File(compFolder, Constants.NAME_APP_XML);
        FileInputStream fis = null;
        try {
            try {
                fis = new FileInputStream(app);
                appInfo = XmlUtil.readXml(fis, RDApplicationInfo.class);
                return getComponentInfo(appInfo.entry);
            } finally {
                if (fis != null) {
                    fis.close();
                }
            }
        } catch (IOException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }
}
