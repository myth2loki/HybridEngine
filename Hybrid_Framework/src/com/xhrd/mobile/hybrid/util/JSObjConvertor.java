package com.xhrd.mobile.hybrid.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理Javascript工具类。
 * Created by wangqianyu on 15/4/29.
 */
public class JSObjConvertor {
    private static final Map<Class<?>, List<Field>> mStoredFieldMap = Collections.synchronizedMap(new HashMap<Class<?>, List<Field>>());

    /**
     * 将一个实例的属性转换成js object。<br/>
     * 目前只转换当前类中public成员变量。
     * @param obj
     * @return
     */
    public static String convertJS(Object obj) {
        if (obj == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Field f : getAllFields(obj.getClass())) {
            f.setAccessible(true);
            sb.append(f.getName()).append(":");
            Object val = null;
            try {
                val = f.get(obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (val == null) {
                sb.append("null");
            } else if (f.getType().isPrimitive()) {
                sb.append(val);
            } else if (f.getType() == String.class) {
                sb.append("'").append(val).append("'");
            } else {
                sb.append(convertJS(val));
            }
            sb.append(",");
        }
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("}");
        return sb.toString();
    }

//    private static List<Field> getAllFields(Class<?> clazz) {
//        if (mStoredFieldMap.containsKey(clazz)) {
//            return mStoredFieldMap.get(clazz);
//        }
//
//        List<Field> fieldList = new ArrayList<Field>();
//        Class<?> cls = clazz;
//        do {
//            fieldList.addAll(Arrays.asList(cls.getDeclaredFields()));
//        } while ((cls = cls.getSuperclass()) != null);
//        mStoredFieldMap.put(clazz, fieldList);
//        return fieldList;
//    }

    private static List<Field> getAllFields(Class<?> clazz) {
        if (mStoredFieldMap.containsKey(clazz)) {
            return mStoredFieldMap.get(clazz);
        }

        List<Field> fieldList = new ArrayList<Field>();
        Class<?> cls = clazz;
        fieldList.addAll(Arrays.asList(cls.getFields()));
        mStoredFieldMap.put(clazz, fieldList);
        return fieldList;
    }
}
