package com.ruiqin.downloadlibrary.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ruiqin.shen
 * 类说明：
 */

public class UpdateInfo implements Parcelable {

    /**
     * Force : true
     * Url : http
     * Tips : Test
     */

    private boolean Force;
    private String Url;
    private String version;
    private String Tips;

    public UpdateInfo(String url, String version, String tips) {
        Url = url;
        this.version = version;
        Tips = tips;
    }

    public UpdateInfo(boolean force, String url, String version, String tips) {
        Force = force;
        Url = url;
        this.version = version;
        Tips = tips;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isForce() {
        return Force;
    }

    public void setForce(boolean Force) {
        this.Force = Force;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String Url) {
        this.Url = Url;
    }

    public String getTips() {
        return Tips;
    }

    public void setTips(String Tips) {
        this.Tips = Tips;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.Force ? (byte) 1 : (byte) 0);
        dest.writeString(this.Url);
        dest.writeString(this.version);
        dest.writeString(this.Tips);
    }

    protected UpdateInfo(Parcel in) {
        this.Force = in.readByte() != 0;
        this.Url = in.readString();
        this.version = in.readString();
        this.Tips = in.readString();
    }

    public static final Parcelable.Creator<UpdateInfo> CREATOR = new Parcelable.Creator<UpdateInfo>() {
        @Override
        public UpdateInfo createFromParcel(Parcel source) {
            return new UpdateInfo(source);
        }

        @Override
        public UpdateInfo[] newArray(int size) {
            return new UpdateInfo[size];
        }
    };
}
