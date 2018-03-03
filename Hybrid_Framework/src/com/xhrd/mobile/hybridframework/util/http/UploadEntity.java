package com.xhrd.mobile.hybridframework.util.http;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * wrapper of file which will be uploaded. 
 * @author wangqianyu
 *
 */
public class UploadEntity {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UploadEntity other = (UploadEntity) obj;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	/**
	 * alias of uploading file.
	 */
	public String name;
	/**
	 * file which will be uploaded.
	 */
	public File file;
    /**
     * 额外的描述信息，键值对。
     */
    public Map<String, String> extras = new HashMap<String, String>();
}
