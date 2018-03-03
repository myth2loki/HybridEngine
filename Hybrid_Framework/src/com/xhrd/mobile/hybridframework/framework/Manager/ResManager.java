package com.xhrd.mobile.hybridframework.framework.Manager;

import com.xhrd.mobile.hybridframework.annotation.JavascriptFunction;
import com.xhrd.mobile.hybridframework.engine.RDCloudView;
import com.xhrd.mobile.hybridframework.engine.HybridActivity;
import com.xhrd.mobile.hybridframework.framework.HybridEnv;
import com.xhrd.mobile.hybridframework.framework.PluginBase;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import com.xhrd.mobile.hybridframework.framework.PluginData;

import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

/**
 * 资源管理
 * @author liutengteng
 *
 */
public class ResManager extends PluginBase {

	private  static final String TAG = ResManager.class.getSimpleName();
    /**
	 * 资源协议
	 */
	public static final String[] F_RES_SCHEMAS = new String[] { "res","cpts","cpt","data","cache" };
	/**
	 * res协议对应的主机路径数组，空主机名用协议名字替代
	 */
	public static final String[] F_RES_PATH_LIST = new String[] {
			"hybrid/app/component",
			"cache",
			"data",
			"hybrid/plugin",
			"res",//hybrid/app
			"i18n" };
	public static final String[] F_CPTS_PATH_LIST = new String[] {
		"cpts"};//hybrid/app
	public static final String[] F_CPT_PATH_LIST = new String[] {
	"cpt"};//hybrid/app/component

	public static final String[] F_DATA_PATH_LIST = new String[] {
	"data"};//hybrid/app/component
	public static final String[] F_CACHE_PATH_LIST = new String[] {
	"cache"};//hybrid/app/component
	/**
	 * 资源协议映射的真实路径
	 */
	public String[] F_FILE_PATH_LIST = null;
//	public String[] F_CPTS_PATH_LIST = null;
//	public String[] F_CPT_PATH_LIST = null;
	// // 资源路径
    public static final Uri COMPONENTS_URI = Uri.parse(F_RES_SCHEMAS[0] + "://" + F_RES_PATH_LIST[0]);
    public static final Uri CACHE_URI = Uri.parse(F_RES_SCHEMAS[0] + "://" + F_RES_PATH_LIST[1]);
    public static final Uri DATA_URI = Uri.parse(F_RES_SCHEMAS[0] + "://" + F_RES_PATH_LIST[2]);
    public static final Uri PLUGIN_URI = Uri.parse(F_RES_SCHEMAS[0] + "://" + F_RES_PATH_LIST[3]);
    public static final Uri APP_URI = Uri.parse(F_RES_SCHEMAS[0] + "://" + F_RES_PATH_LIST[4]);
    public static final Uri I18N_URI = Uri.parse(F_RES_SCHEMAS[0] + "://" + F_RES_PATH_LIST[5]);

	/**
	 * 协议对应的真实路径
	 */
	public String F_PATH;

	public HashMap<String, HashMap<String, String>> mSchemaMap;
	public static ResManager mInstance;
    public ResManager() {
        if (mSchemaMap == null) {
            F_PATH = getF_PATH();
            F_FILE_PATH_LIST = new String[]{
                    "/hybrid/app/component",
                    "/cache",
                    "/hybrid/data",
                    "/hybrid/plugin",
                    "/hybrid/app",
                    "/hybrid/app/i18n"};
        }
		init();
    }

	protected String getF_PATH() {
		return HybridEnv.getApplicationContext().getFilesDir().getParent();
	}

	protected void init() {
		mSchemaMap = new HashMap<String, HashMap<String, String>>();
		addSchema(F_RES_SCHEMAS[0], F_RES_PATH_LIST, F_FILE_PATH_LIST);
		addSchema(F_RES_SCHEMAS[1], F_CPTS_PATH_LIST, new String[]{"/hybrid/app/component"});
		addSchema(F_RES_SCHEMAS[2], F_CPT_PATH_LIST, new String[]{"/hybrid/app/component"});
		addSchema(F_RES_SCHEMAS[3], F_DATA_PATH_LIST, new String[]{"/hybrid/data"});
		addSchema(F_RES_SCHEMAS[4], F_CACHE_PATH_LIST, new String[]{"/hybrid/cache"});
	}

	public synchronized static ResManager getInstance() {
		return ResManagerFactory.getResManager();
    }

	synchronized static ResManager getInstanceInternal() {
		if (mInstance == null) {
			mInstance = new ResManager();
		}
		return mInstance;
	}

	/**
	 * 把对应的协议、路径整合到键值对里面
	 * 
	 * @param pSchema
	 *            协议
	 * @param pRes
	 *            映射路径
	 * @param pPath
	 *            真实路径
	 */
	private void addSchema(String pSchema, String[] pRes, String[] pPath) {
		int len = pRes.length;
		HashMap<String, String> hostMap = new HashMap<String, String>();
		for (int i = 0; i < len; i++) {
			String path = "";
			if(pSchema.equals("cache")) {
				if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
					path = HybridEnv.getApplicationContext().getExternalCacheDir().getAbsolutePath();//+File.separator+pPath[i];;//Environment.getExternalFilesDir();
				}else {
					path = F_PATH + pPath[i];
				}
			}else {
				path = F_PATH + pPath[i];
			}

			String res = pRes[i];
			if (res.isEmpty()) {
				res = pSchema;
			}
			hostMap.put(res, path);
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
			file = null;
		}
		mSchemaMap.put(pSchema, hostMap);
	}

	/**
	 * 检查协议
	 * 
	 * @param pSchema
	 * @return
	 */
	public boolean isSchemaExist(String pSchema) {
		if (pSchema != null) {
			return mSchemaMap.get(pSchema) == null ? false : true;
		}
		return false;
	}

	/**
	 * 判断SDCard是否存在 [当没有外挂SD卡时，内置ROM也被识别为存在sd卡]
	 * 
	 * @return
	 */
	public boolean isSdCardExist() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	/**
	 * 获取sd卡容量
	 * 
	 * @return
	 */
	public long getSDAllSize() {
		return getSDAllSize(F_PATH);
	}

	/**
	 * 获取sd卡容量
	 * 
	 * @param pPath
	 * @return
	 */
	public long getSDAllSize(String pPath) {
		StatFs sf = new StatFs(pPath);
		// 获取单个数据块的大小(Byte)
		long blockSize = sf.getBlockSize();
		// 获取所有数据块数
		long allBlocks = sf.getBlockCount();
		// 返回SD卡大小
		return (allBlocks * blockSize) / 1024 / 1024; // 单位MB
	}

	/**
	 * 获取sd卡剩余空间
	 * 
	 * @return
	 */
	public long getSDFreeSize() {
		return getSDFreeSize(F_PATH);
	}

	/**
	 * 获取sd卡剩余空间
	 * 
	 * @param pPath
	 * @return
	 */
	public long getSDFreeSize(String pPath) {
		StatFs sf = new StatFs(pPath);
		// 获取单个数据块的大小(Byte)
		long blockSize = sf.getBlockSize();
		// 空闲的数据块的数量
		long freeBlocks = sf.getAvailableBlocks();
		// 返回SD卡空闲大小
		return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
	}

	/**
	 * 检查协议是否存在
	 * 
	 * @param pPath
	 * @return
	 */
	private boolean checkSchemaExist(String pPath) {
		Uri uri = Uri.parse(pPath);
		if (uri != null) {
			String schema = uri.getScheme();
			for (String s : F_RES_SCHEMAS) {
				if (s.equals(schema))
					return true;
			}
		}
		return false;
	}

	/**
	 * copy文件
	 * 
	 * @param pFileName
	 *            文件名称
	 * @param pTargetDir
	 *            目标位置
	 * @return
	 * @throws IOException
	 */
	public boolean copyFile(String pFileName, String pTargetDir)
			throws IOException {
		File file = new File(pFileName);
		if (file.exists() && file.isFile()) {
			File toFile = new File(pTargetDir);
			if (!toFile.exists()) {
				toFile.mkdirs();
			}

			return copyFile(file, toFile);
		}
		return false;
	}

	/**
	 * copy文件
	 * 
	 * @param pFile
	 *            文件对象
	 * @param pTargetFile
	 *            目标位置对象
	 * @return
	 * @throws IOException
	 */
	public boolean copyFile(File pFile, File pTargetFile)
			throws IOException {
		if (pFile.exists() && pFile.isFile()) {
			if (!pTargetFile.exists()) {
				pTargetFile.mkdirs();
			}

			BufferedInputStream inBuffStream = new BufferedInputStream(
					new FileInputStream(pFile));

			BufferedOutputStream outBuffStream = new BufferedOutputStream(
					new FileOutputStream(pTargetFile));

			byte[] bytes = new byte[1024 * 5];
			int len = 0;
			while ((len = inBuffStream.read(bytes)) != -1) {
				outBuffStream.write(bytes, 0, len);
			}

			inBuffStream.close();
			outBuffStream.flush();
			outBuffStream.close();
			return true;
		}
		return false;
	}

	/**
	 * copy目录
	 * 
	 * @param pPath
	 *            copy目录
	 * @param pTargetDir
	 *            目标位置
	 * @return
	 * @throws IOException
	 */
	public boolean copyPath(String pPath, String pTargetDir)
			throws IOException {
		new File(pTargetDir).mkdirs();
		File file = new File(pPath);
		if (file != null) {
			File[] files = file.listFiles();
			int len = files.length;
			for (int i = 0; i < len; i++) {
				File f = files[i];
				String fileName = f.getName();
				if (f.isDirectory()) {
					// 目标文件
					File targetFile = new File(
							new File(pTargetDir).getAbsolutePath()
									+ File.separator + fileName);
					if (!copyFile(f, targetFile))
						return false;

				} else {
					String oldDir = pPath + File.separator + fileName;
					String tarDir = pTargetDir + File.separator + fileName;
					if (!copyPath(oldDir, tarDir))
						return false;
				}
			}
		}
		return true;
	}

    public String getPath(String path) {
        return getPath(null,path);
    }

    public String getPath(Uri uri) {
		if (mSchemaMap == null) {
			init();
		}
        return getPath(uri.toString());
    }

//	/**
//	 * 获取真实路径
//	 *
//	 * @param path 协议路径
//	 * @return
//	 */
//	public String getPath(windowName,String path) {
//		return getPath(windowName,new String[]{path});
//	}

	/**
	 * 获取真实路径
	 * 
	 * @param pDirs 协议路径,数组格式
	 * @return
	 */
	@JavascriptFunction
	public String getPath(String view,String[] pDirs) {
		if(pDirs != null && pDirs.length>0) {
			return getPath(getTargetView(view), pDirs[0]);
		}
		return null;
	}


	public String getPath(RDCloudView view,String schemaPath) {

		if(schemaPath==null)
		{
			return null;
		}


		String dir = schemaPath;
		Uri uri = Uri.parse(dir);
		if (uri == null)
			return null;
		String schema = uri.getScheme();
		String host = uri.getHost();//"res://i.png"
		String path = uri.getPath();
		if(path!=null&&path.length()>0)
		{
			path = host + path;
		}else
			path = host;
		HashMap<String, String> hostMap = mSchemaMap.get(schema);
		if(hostMap!=null)
		{
			String component = "";
			if(schema.equals("cpt"))
			{
				if(view == null) {
					view = this.getTargetView();
					if(view == null) {
						try {
							view = HybridActivity.getInstance().getContainer().getComponentList().peekLast().getWindowList().peekLast().getmMainView();
						} catch (Exception e) {
							Log.e(TAG, "get current window failed", e);
//							return null;
						}
					}
				}
				if(view != null) {
					component = "/" + view.getRDCloudWindow().getRDCloudComponent().getName();
				}
			}else {
				android.util.Log.e(TAG, "cpt:" + schema + ",view:" + view);
			}
			Iterator<String> iterator = hostMap.keySet().iterator();// mSchemaMap.keySet().iterator();
			boolean isExist = false;
			while (iterator.hasNext()) {
				String key = iterator.next();
				if (path.startsWith(key))
				// if(pDir.startsWith(key))
				{
					String value = hostMap.get(key);
					if (value != null) {
						isExist = true;
						return dir.replace(schema + "://" + key, value);
					}
				}
			}
			if(!isExist)
			{
				String value = hostMap.get(schema);
				if (value != null) {
					if(path.indexOf(".")>-1)
					{
						if(component.isEmpty()) {
							value +="/";
						}
						else {
							value += component+"/";
						}
						return dir.replace(schema + "://", value);
					}else {
						return dir.replace(schema + "://", value+component+"/");
					}
				}
			}
		}
		return dir;
	}

	public boolean isLegalSchema(String pSchemaName) {
		if(pSchemaName!=null) {
			return isLegalSchema(Uri.parse(pSchemaName));
		}
		return false;
	}

	public boolean isLegalSchema(Uri pUri) {
		if(pUri==null) {
			return false;
		}
		String schema = pUri.getScheme();
		if(schema!=null && mSchemaMap.containsKey(schema)) {
			return true;
		}
		return false;
	}

	public boolean termination() {
		return false;
	}

    @Override
    public PluginData.Scope getScope() {
        return PluginData.Scope.app;
    }

    @Override
	public void addMethodProp(PluginData data) {
        data.addMethodWithReturn("getPath");
        data.addMethod("termination");
	}
    
    @Override
    public String getDefaultDomain() {
        return "resource";
    }
}
