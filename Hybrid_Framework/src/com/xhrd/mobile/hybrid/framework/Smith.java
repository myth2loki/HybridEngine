package com.xhrd.mobile.hybrid.framework;

import java.lang.reflect.Field;

public class Smith<T> {
	private Object mObj;
	private String mFieldName;

	private boolean mInited;
	private Field mField;

	public Smith(Object mObj, String fieldName) {
		if (mObj == null) {
			throw new IllegalArgumentException("mObj cannot be null");
		}
		this.mObj = mObj;
		this.mFieldName = fieldName;
	}

	private void prepare() {
		if (mInited)
			return;
		mInited = true;

		Class<?> c = mObj.getClass();
		while (c != null) {
			try {
				Field f = c.getDeclaredField(mFieldName);
				f.setAccessible(true);
				mField = f;
				return;
			} catch (Exception e) {
			} finally {
				c = c.getSuperclass();
			}
		}
	}

	public T get() throws NoSuchFieldException, IllegalAccessException,
			IllegalArgumentException {
		prepare();

		if (mField == null)
			throw new NoSuchFieldException();

		try {
			@SuppressWarnings("unchecked")
			T r = (T) mField.get(mObj);
			return r;
		} catch (ClassCastException e) {
			throw new IllegalArgumentException("unable to cast object");
		}
	}

	public void set(T val) throws NoSuchFieldException, IllegalAccessException,
			IllegalArgumentException {
		prepare();
		if (mField == null) {
            throw new NoSuchFieldException();
        }
		mField.set(mObj, val);
	}
}
