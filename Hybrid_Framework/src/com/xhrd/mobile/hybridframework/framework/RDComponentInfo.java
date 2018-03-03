package com.xhrd.mobile.hybridframework.framework;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by lang on 15/4/16.
 */
public class RDComponentInfo implements Parcelable{
    public String name;
    public String className;
    public String description;
    public String version;
    public String bgcolor;
    public String url;
    public String absUrl;
    public String entry;
    public String path;

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(name);
        dest.writeString(className);
        dest.writeString(version);
        dest.writeString(bgcolor);
        dest.writeString(url);
        dest.writeString(absUrl);
        dest.writeString(entry);
        dest.writeString(path);
    }

    public static  final Creator<RDComponentInfo> CREATOR = new Creator<RDComponentInfo>() {
        @Override
        public RDComponentInfo createFromParcel(Parcel in) {
            RDComponentInfo componentInfo =  new RDComponentInfo();
            componentInfo.description=in.readString();
            componentInfo.name = in.readString();
            componentInfo.className = in.readString();
            componentInfo.description = in.readString();
            componentInfo.bgcolor = in.readString();
            componentInfo.url=in.readString();
            componentInfo.absUrl=in.readString();
            componentInfo.entry=in.readString();
            componentInfo.path=in.readString();
            return componentInfo;
        }

        @Override
        public RDComponentInfo[] newArray(int size) {
            return new RDComponentInfo[size];
        }
    };

    @Override
    public String toString() {
        return "RDComponentInfo{" +
                "absUrl='" + absUrl + '\'' +
                ", name='" + name + '\'' +
                ", className='" + className + '\'' +
                ", description='" + description + '\'' +
                ", version='" + version + '\'' +
                ", bgcolor='" + bgcolor + '\'' +
                ", url='" + url + '\'' +
                ", entry='" + entry + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
