package com.xhrd.mobile.hybridframework.framework.Manager;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by langjinbin on 15/6/30.
 */
public class AppLoader {
    private static final String NAME_HYBRID = "hybrid";
    private static final String NAME_APP = "app";
    private static final String NAME_PLUGIN = "plugin";
    private static final String NAME_RD = "rd";
    private static final String NAME_APPLICATION = "application.xml";
    private static final String NAME_COMPONENT = "component";
    private static final String RELATIVE_PATH = NAME_HYBRID + File.separator
            + NAME_APP;
    private String rdPath;
    private String innerHybridPath;


    private String internalPath;
    private String externalPath;
    private Context mContext;
    private ResManager mLoaderRM = AppLoaderResManager.getInstance();
    private ResManager mRM = ResManager.getInstance();

    private static AppLoader mInstance = null;

    public static AppLoader getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AppLoader(context);

        }
        return mInstance;
    }

    private AppLoader(Context context) {
        mContext = context;
        init();
    }


    private void init() {
        internalPath = mRM.getPath(ResManager.APP_URI);
        externalPath = mLoaderRM.getPath(ResManager.APP_URI);
        rdPath = new File(externalPath).getParentFile().getAbsolutePath();
        innerHybridPath = "";

//        internalPath = mContext.getFilesDir().getParent() + File.separator
//                + RELATIVE_PATH;
//        externalPath = new File(Environment.getExternalStorageDirectory(),
//                NAME_RD + File.separator + NAME_APP).getAbsolutePath();
//
//        rdPath = Environment.getExternalStorageDirectory().getPath() + File.separator + NAME_RD;
//        innerHybridPath = mContext.getFilesDir().getParent() + File.separator + NAME_HYBRID;

        syncPlugins();



    }

    // 首先判断sd卡上是否存在应用数据，如存在加载sd卡的数据，返回。
    // 再判断当前应用目录是否存在应用数据，如存在加载内部数据，返回。
    // 否则加载assets下的应用数据
    public String getAppPath() {
        if (existsSd()) {
            if (appOnExternal()) {
                return externalPath;
            }
        }

        if (appOnInternal()) {
            return internalPath;
        }
        ///return "file:///android_asset/" + RELATIVE_PATH;
        return null;
    }

    /**
     * 是否存在sd卡
     *
     * @return
     */
    public boolean existsSd() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }

    /**
     * 外部存储是否存在应用数据
     *
     * @return
     */
    public boolean appOnExternal() {
        return existAppData(externalPath);
    }

    /**
     * 内部存储是否存在应用数据
     */
    public boolean appOnInternal() {
        return existAppData(internalPath);
    }

    /**
     * 判断是否存在应用数据
     *
     * @param dir
     * @return
     */
    public boolean existAppData(String dir) {
        if (TextUtils.isEmpty(dir)) {
            return false;
        }
        File file = new File(dir);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles(new FileFilter() {

                @Override
                public boolean accept(File pathname) {
                    return pathname.getName().equals(NAME_APPLICATION);
                }
            });

            return files != null && files.length == 1;
        }
        return false;
    }

    /**
     * @param in
     * @param out
     */
    public void write(InputStream in, OutputStream out) {
        byte[] buffer = new byte[1024];
        int length;
        try {
            while ((length = in.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, length);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }

                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    /**
     * @param source      源文件或文件夹
     * @param destination 目标文件或文件夹
     */
    public void copy(File source, File destination) {
        if (source!=null&&source.isDirectory()) {
            File[] files = source.listFiles();
            for (File file : files) {
                copy(file, new File(destination, file.getName()));
            }
        } else {
            try {
                if (source!=null&&source.exists()&&source.isFile()){
                    if (!destination.getParentFile().exists()) {
                        destination.getParentFile().mkdirs();
                    }
                    FileInputStream in = new FileInputStream(source);
                    FileOutputStream out = new FileOutputStream(destination);
                    write(in, out);
                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    /**
     * 判断  /sdcard/rd/hybrid/plugin 是否有修改
     * 如有修改，拷贝到 /data/data/packagName/hybrid/plugin下
     */
    public void syncPlugins() {
        File sdPlugins = new File(mLoaderRM.getPath(ResManager.PLUGIN_URI));
        File dataPlugins = new File(mRM.getPath(ResManager.PLUGIN_URI));
//        File sdPlugins = new File("/sdcard", NAME_RD + File.separator + NAME_PLUGIN);
//        File dataPlugins = new File(mContext.getFilesDir().getParentFile(), NAME_HYBRID + File.separator + NAME_PLUGIN);
        if (sdPlugins.lastModified() != dataPlugins.lastModified()) {
            copy(sdPlugins, dataPlugins);
        }
    }

    public String getComponentPath() {
        return getAppPath() + File.separator + NAME_COMPONENT;
    }

    public String getPath(String schemePath) {
        String path = ResManager.getInstance().getPath(schemePath);

        if (path != null && path.contains(innerHybridPath)) {
           return  path.replace(innerHybridPath,rdPath );
        }


        return path;
    }
}
