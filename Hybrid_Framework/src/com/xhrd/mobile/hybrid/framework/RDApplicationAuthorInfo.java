package com.xhrd.mobile.hybrid.framework;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by az on 15/12/16.
 */
public class RDApplicationAuthorInfo implements Parcelable {
    public String name;
    public String email;
    public String tel;
    public String address;

    public RDApplicationAuthorInfo() {

    }

    @Override
    public String toString() {
        return "RDApplicationAuthorInfo{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", tel='" + tel + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeString(this.tel);
        dest.writeString(this.address);
    }

    protected RDApplicationAuthorInfo(Parcel in) {
        this.name = in.readString();
        this.email = in.readString();
        this.tel = in.readString();
        this.address = in.readString();
    }

    public static final Parcelable.Creator<RDApplicationAuthorInfo> CREATOR = new Parcelable.Creator<RDApplicationAuthorInfo>() {
        public RDApplicationAuthorInfo createFromParcel(Parcel source) {
            return new RDApplicationAuthorInfo(source);
        }

        public RDApplicationAuthorInfo[] newArray(int size) {
            return new RDApplicationAuthorInfo[size];
        }
    };
}
