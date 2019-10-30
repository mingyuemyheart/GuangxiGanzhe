package com.cxwl.shawn.guangxi.ganzhe.dto;

import android.os.Parcel;
import android.os.Parcelable;

public class FactDto implements Parcelable {

    public String stationName,stationId,imgUrl,imgUrlThumb,time;
    public boolean isSelected;
    public double lat,lng;
    public float foreGuangai, humidity;//预报灌溉量、湿度
    public float x = 0;//x轴坐标点
    public float y = 0;//y轴坐标点
    public String dayPre;//日降水
    public String temp60,temp150,temp300;//温度
    public String grotemp30,grotemp50;//土壤温度
    public String humidity60,humidity150,humidity300;//相对湿度
    public String grohumidity20,grohumidity40;//土壤体积含水率
    public String title;

    //气温、相对湿度、小时累计雨量、5cm土壤体积含水量、2分钟平均风速、地面温度
    public String TEMPL,RELA_HUMIL,BUCKET_ACC_RAIN_COUNTL,SMVP_5CM_AVEL,AVE_WS_2MINL,LAND_PT_TEMPL;//上一年
    public String TEMP,RELA_HUMI,BUCKET_ACC_RAIN_COUNT,SMVP_5CM_AVE,AVE_WS_2MIN,LAND_PT_TEMP;//当前年

    public FactDto() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.stationName);
        dest.writeString(this.stationId);
        dest.writeString(this.imgUrl);
        dest.writeString(this.imgUrlThumb);
        dest.writeString(this.time);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeFloat(this.foreGuangai);
        dest.writeFloat(this.humidity);
        dest.writeFloat(this.x);
        dest.writeFloat(this.y);
        dest.writeString(this.dayPre);
        dest.writeString(this.temp60);
        dest.writeString(this.temp150);
        dest.writeString(this.temp300);
        dest.writeString(this.grotemp30);
        dest.writeString(this.grotemp50);
        dest.writeString(this.humidity60);
        dest.writeString(this.humidity150);
        dest.writeString(this.humidity300);
        dest.writeString(this.grohumidity20);
        dest.writeString(this.grohumidity40);
        dest.writeString(this.title);
        dest.writeString(this.TEMPL);
        dest.writeString(this.RELA_HUMIL);
        dest.writeString(this.BUCKET_ACC_RAIN_COUNTL);
        dest.writeString(this.SMVP_5CM_AVEL);
        dest.writeString(this.AVE_WS_2MINL);
        dest.writeString(this.LAND_PT_TEMPL);
        dest.writeString(this.TEMP);
        dest.writeString(this.RELA_HUMI);
        dest.writeString(this.BUCKET_ACC_RAIN_COUNT);
        dest.writeString(this.SMVP_5CM_AVE);
        dest.writeString(this.AVE_WS_2MIN);
        dest.writeString(this.LAND_PT_TEMP);
    }

    protected FactDto(Parcel in) {
        this.stationName = in.readString();
        this.stationId = in.readString();
        this.imgUrl = in.readString();
        this.imgUrlThumb = in.readString();
        this.time = in.readString();
        this.isSelected = in.readByte() != 0;
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.foreGuangai = in.readFloat();
        this.humidity = in.readFloat();
        this.x = in.readFloat();
        this.y = in.readFloat();
        this.dayPre = in.readString();
        this.temp60 = in.readString();
        this.temp150 = in.readString();
        this.temp300 = in.readString();
        this.grotemp30 = in.readString();
        this.grotemp50 = in.readString();
        this.humidity60 = in.readString();
        this.humidity150 = in.readString();
        this.humidity300 = in.readString();
        this.grohumidity20 = in.readString();
        this.grohumidity40 = in.readString();
        this.title = in.readString();
        this.TEMPL = in.readString();
        this.RELA_HUMIL = in.readString();
        this.BUCKET_ACC_RAIN_COUNTL = in.readString();
        this.SMVP_5CM_AVEL = in.readString();
        this.AVE_WS_2MINL = in.readString();
        this.LAND_PT_TEMPL = in.readString();
        this.TEMP = in.readString();
        this.RELA_HUMI = in.readString();
        this.BUCKET_ACC_RAIN_COUNT = in.readString();
        this.SMVP_5CM_AVE = in.readString();
        this.AVE_WS_2MIN = in.readString();
        this.LAND_PT_TEMP = in.readString();
    }

    public static final Creator<FactDto> CREATOR = new Creator<FactDto>() {
        @Override
        public FactDto createFromParcel(Parcel source) {
            return new FactDto(source);
        }

        @Override
        public FactDto[] newArray(int size) {
            return new FactDto[size];
        }
    };
}
