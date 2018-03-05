package com.xhrd.mobile.hybrid.framework;

import android.os.Parcel;
import android.os.Parcelable;

public class RDApplicationInfo implements Parcelable{
    public long id;
    public String name;
    public String description;
    public RDApplicationAuthorInfo author;// 作者信息
//    public String email;
//    public String tel;
//    public String address;
    public String icon;
    public boolean obfuscation;
    public String orientation;
    public String version;
    public String bgcolor;
    public String entry;
    public String appkey;
    public String logServer;
    public int logPort;

    public RDApplicationInfo(){

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
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeParcelable(author, flags);
        dest.writeString(icon);
        dest.writeInt(obfuscation ? 1 : 0);
        dest.writeString(orientation);
        dest.writeString(version);
        dest.writeString(bgcolor);
        dest.writeString(entry);
        dest.writeString(appkey);
        dest.writeString(logServer);
        dest.writeInt(logPort);

    }


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

    public static final Creator<RDApplicationInfo> CREATOR = new Creator<RDApplicationInfo>() {
        @Override
        public RDApplicationInfo createFromParcel(Parcel in) {
            RDApplicationInfo appInfo =  new RDApplicationInfo();
            appInfo.id = in.readLong();
            appInfo.name=in.readString();
            appInfo.description=in.readString();
            appInfo.author=in.readParcelable(RDApplicationAuthorInfo.class.getClassLoader());
            appInfo.icon=in.readString();
            appInfo.obfuscation=in.readInt() == 1;
            appInfo.orientation = in.readString();
            appInfo.version = in.readString();
            appInfo.bgcolor = in.readString();
            appInfo.entry = in.readString();
            appInfo.appkey = in.readString();
            appInfo.logServer = in.readString();
            appInfo.logPort = in.readInt();
            return appInfo;
        }

        @Override
        public RDApplicationInfo[] newArray(int size) {
            return new RDApplicationInfo[size];
        }
    };

    @Override
    public String toString() {
        return "RDApplicationInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", author=" + author +
                ", icon='" + icon + '\'' +
                ", obfuscation=" + obfuscation +
                ", orientation='" + orientation + '\'' +
                ", version='" + version + '\'' +
                ", bgcolor='" + bgcolor + '\'' +
                ", entry='" + entry + '\'' +
                ", appkey='" + appkey + '\'' +
                ", logServer='" + logServer + '\'' +
                ", logPort=" + logPort +
                '}';
    }
}
