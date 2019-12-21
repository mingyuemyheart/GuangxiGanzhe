package com.cxwl.shawn.guangxi.ganzhe.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class FarmDto implements Parcelable {

    public String name,imgUrl,addr,type,area,period,output,manager,disInfo,latLng;
    public List<String> imgUrls = new ArrayList<>();

    public FarmDto() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.imgUrl);
        dest.writeString(this.addr);
        dest.writeString(this.type);
        dest.writeString(this.area);
        dest.writeString(this.period);
        dest.writeString(this.output);
        dest.writeString(this.manager);
        dest.writeString(this.disInfo);
        dest.writeString(this.latLng);
        dest.writeStringList(this.imgUrls);
    }

    protected FarmDto(Parcel in) {
        this.name = in.readString();
        this.imgUrl = in.readString();
        this.addr = in.readString();
        this.type = in.readString();
        this.area = in.readString();
        this.period = in.readString();
        this.output = in.readString();
        this.manager = in.readString();
        this.disInfo = in.readString();
        this.latLng = in.readString();
        this.imgUrls = in.createStringArrayList();
    }

    public static final Creator<FarmDto> CREATOR = new Creator<FarmDto>() {
        @Override
        public FarmDto createFromParcel(Parcel source) {
            return new FarmDto(source);
        }

        @Override
        public FarmDto[] newArray(int size) {
            return new FarmDto[size];
        }
    };
}
