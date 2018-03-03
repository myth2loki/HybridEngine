package com.xhrd.mobile.hybrid.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import com.xhrd.mobile.hybrid.framework.HybridEnv;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by wangqianyu on 15/5/7.
 */
public class XmlUtil {
    /**
     * 从流中读出实例。
     * @param in
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T readXml(InputStream in, Class<T> cls) {
        try {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(in, "utf-8");
			while (parser.getEventType() == XmlPullParser.START_DOCUMENT){
				parser.next();
			}
            return readXmlPullParser(parser, cls);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

	/**
	 * 解析
	 * @param parser
	 * @param cls
	 * @param <T>
	 * @return
	 */
	private static <T> T readXmlPullParser(XmlPullParser parser, Class<T> cls) {
		try {
			Object xmlObj = cls.newInstance();
			readXmlAttribute(parser, cls, xmlObj);

			String startTag = parser.getName();
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String tag = parser.getName();
				if (eventType == XmlPullParser.START_TAG) {
					if (xmlObj != null) {
						Field field = findField(cls, tag);
						if (field != null) {
							setValue(parser, field, xmlObj, "");

							eventType = parser.getEventType();
							if (eventType == XmlPullParser.END_TAG) {
								if (startTag.equals(tag)){
									return cls.cast(xmlObj);
								}
							}
						}
					}
				} else if (eventType == XmlPullParser.END_TAG) {// 此判断，可能不会进入
					if (startTag.equals(tag)){
						return cls.cast(xmlObj);
					}
				}
				eventType = parser.next();
			}
			return cls.cast(xmlObj);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private static <T> void readXmlAttribute(XmlPullParser parser, Class<T> cls, Object xmlObj) throws XmlPullParserException {
		int attributeCount = parser.getAttributeCount();
		for (int i = 0; i < attributeCount; i++) {
            Field field = findField(cls, parser.getAttributeName(i));
			if (field != null) {
				setValue(null, field, xmlObj, parser.getAttributeValue(i));
			}
        }
	}

	private static String getValue(XmlPullParser parser, String v) {
		if (TextUtils.isEmpty(v) && parser != null){
			try {
				return parser.nextText();
			} catch (Exception e) {
				Log.e("read xml err", "name:" + parser.getName(), e);
			}
			return "";
		}else {
			return v;
		}
	}

	private static void setValue(XmlPullParser parser, Field field ,Object xmlObj, String v){
		try {
			Class<?> type = field.getType();
			if (type == byte.class || type == Byte.class) {
				field.setByte(xmlObj, Byte.parseByte(getValue(parser, v)));
			} else if (type == short.class || type == Short.class) {
				field.setShort(xmlObj, Short.parseShort(getValue(parser, v)));
			} else if (type == int.class || type == Integer.class) {
				field.setInt(xmlObj, Integer.parseInt(getValue(parser, v)));
			} else if (type == long.class || type == Long.class) {
				field.setLong(xmlObj, Long.parseLong(getValue(parser, v)));
			} else if (type == float.class || type == Float.class) {
				field.setFloat(xmlObj, Float.parseFloat(getValue(parser, v)));
			} else if (type == double.class || type == Double.class) {
				field.setDouble(xmlObj, Double.parseDouble(getValue(parser, v)));
			} else if (type == char.class || type == Character.class) {
				String value = getValue(parser, v);
				if (!TextUtils.isEmpty(value)) {
					field.setChar(xmlObj, value.charAt(0));
				}
			} else if (type == boolean.class || type == Boolean.class) {
				field.setBoolean(xmlObj, Boolean.parseBoolean(getValue(parser, v)));
			} else if (type == String.class) {
				field.set(xmlObj, getValue(parser, v));
			} else {
				if (parser != null){
					field.set(xmlObj, readXmlPullParser(parser, type));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Field findField(Class<?> cls, String name) {
        Field[] fields = cls.getFields();
        if (fields != null) {
            for (Field field : fields) {
                if (field.getName().equals(name)) {
                    return field;
                }
            }
        }
        return null;
        //throw new IllegalStateException("can not find field from Class: " + cls + " by name: " + name);
    }
    

    /**
     * 合并xml文件
     * @param pOldXmlPath 原始文件
     * @param pNewXmlPath 新文件
     * @param pOutXmlPath 输出路径及名称
     */
	public static void mergeXml(String pOldXmlPath,String pNewXmlPath,String pOutXmlPath) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		try {
			factory.setIgnoringElementContentWhitespace(true);

			DocumentBuilder db = factory.newDocumentBuilder();
			File oldFile = new File(pOldXmlPath);
			File newFile = new File(pNewXmlPath);
			if(oldFile!=null&&oldFile.exists()&&newFile!=null&&newFile.exists()) {
				Document oldDocument = db.parse(oldFile);
				Document newDocument = db.parse(newFile);
				
	
				Element oldRoot = oldDocument.getDocumentElement();
				Element newRoot = newDocument.getDocumentElement();
				if(oldRoot!=null&&newRoot!=null) {
					NodeList oldNodeList = oldRoot.getChildNodes();
					NodeList newNodeList = newRoot.getChildNodes();
					
					int newLength = newNodeList.getLength();
					int oldLength = oldNodeList.getLength();
					
					Map<String, Node> nodeMap = new HashMap<String, Node>();
					for(int j=1;j<oldLength;j=j+2) {
						Node oldNode = oldNodeList.item(j);
						NamedNodeMap attrMap = oldNode.getAttributes();
						if(attrMap!=null) {
							String domain = attrMap.item(0).getNodeValue();
							nodeMap.put(domain, oldNode);
						}
					}
	
					boolean isAddNode = false;
					for(int i=1;i<newLength;i=i+2) {
						Node node = newNodeList.item(i);
						NamedNodeMap attrMap = node.getAttributes();
						if(attrMap!=null) {
							String domain = attrMap.item(0).getNodeValue();
							if(!nodeMap.containsKey(domain)) {
								isAddNode = true;
								mergeNode(oldDocument,oldRoot,(Element)node);							
							}
						}
					}
					if(isAddNode) {
						saveXml(pOutXmlPath,oldDocument);
					}
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 合并属性及子节点
	 * @param pDoc document对象
	 * @param pMain 父节点对象
	 * @param pVice 子节点对象
	 */
	private static void mergeNode(Document pDoc,Element pMain,Element pVice) {
		NamedNodeMap nodeAttr = pVice.getAttributes();
		int attLen = nodeAttr.getLength();
		//创建节点
		Element element = pDoc.createElement(pVice.getNodeName());
		//添加属性
		for(int i=0;i<attLen;i++) {
			Node node = nodeAttr.item(i);
			element.setAttribute(node.getNodeName(), node.getNodeValue());
		}
		pMain.appendChild(element);

		//合并子结点
        NodeList childNodeList = pVice.getChildNodes();
        if(childNodeList!=null) {
	        int chindLength = childNodeList.getLength();
	        for (int j = 1; j < chindLength; j = j + 2) {
	            // 如果有子节点,则递归调用本方法
	            Element chindNode = (Element) childNodeList.item(j);
	            mergeNode(pDoc, element, chindNode);
	        }
        }
	}

	/**
	 * 保存xml文件
	 * @param pFileName 文件名称及路径
	 * @param pDoc document对象
	 */
	private static void saveXml(String pFileName, Document pDoc) {
		TransformerFactory transFactory = TransformerFactory.newInstance();
		try {
			Transformer transformer = transFactory.newTransformer();
			transformer.setOutputProperty("indent", "yes");

			DOMSource source = new DOMSource();
			source.setNode(pDoc);
			StreamResult result = new StreamResult();
			result.setOutputStream(new FileOutputStream(pFileName));

			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static XmlPullParser getXmlPullParser(String pPath) {

//		File file = new File(pPath);
//		if(file==null && !file.isFile()) {
//			return null;
//		}
		XmlPullParser xmlParser = null;

		try {
			InputStream mInputStream = getInputStream(pPath);
			if(mInputStream==null) {
				return null;
			}
			// 获得XmlPullParser解析器
			xmlParser = Xml.newPullParser();
			if (mInputStream != null) {
				xmlParser.setInput(mInputStream, "utf-8");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("read xml", e.getMessage());
		}
		return xmlParser;
	}

	public static InputStream getInputStream(String pFilePath) {
		InputStream inputStream = null;
		try {
			if (pFilePath.startsWith("/")) {
				File file = new File(pFilePath);
				if(file.exists()) {
					inputStream = new FileInputStream(file);// mContext.getAssets().open(pPath);//
					// "www/strings.xml"
				}
			}
			else {
				inputStream = HybridEnv.getApplicationContext().getAssets().open(pFilePath);// "www/strings.xml"
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("read xml", e.getMessage());
		}
		return inputStream;
	}

	public static Document xmlPath2Doc(InputStream inputStream) {
		Document doc = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
			doc = builder.parse(inputStream);
			return doc;
		} catch (Exception e) {
			Log.e("read xml", "read xxx failed", e);
		}
		return null;
	}


	/**
	 * 获取meta ApiKey
	 * @param context
	 * @param metaKey
	 * @return
	 */
	public static String getMetaValue(Context context, String metaKey) {
		Bundle metaData = null;
		String apiKey = null;
		if (context == null || metaKey == null) {
			return null;
		}
		try {
			ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			if (null != ai) {
				metaData = ai.metaData;
			}
			if (null != metaData) {
				apiKey = metaData.getString(metaKey);
			}
		} catch (PackageManager.NameNotFoundException e) {
			Log.e("get MetaValue", "getMetaValue", e);
		}
		return apiKey;
	}
}
