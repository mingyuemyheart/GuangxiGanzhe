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
    public float x10 = 0;//x轴坐标点
    public float y10 = 0;//y轴坐标点
    public float x20 = 0;//x轴坐标点
    public float y20 = 0;//y轴坐标点
    public float x30 = 0;//x轴坐标点
    public float y30 = 0;//y轴坐标点
    public float x40 = 0;//x轴坐标点
    public float y40 = 0;//y轴坐标点
    public float x50 = 0;//x轴坐标点
    public float y50 = 0;//y轴坐标点
    public String dayPre;//日降水
    public String temp60,temp150,temp300;//温度
    public String grotemp30,grotemp50;//土壤温度
    public String humidity60,humidity150,humidity300;//相对湿度
    public String grohumidity20,grohumidity40;//土壤体积含水率
    public String title;

    //气温、相对湿度、小时累计雨量、5cm土壤体积含水量、2分钟平均风速、地面温度
    public String TEMPL,RELA_HUMIL,BUCKET_ACC_RAIN_COUNTL,SMVP_5CM_AVEL,SMVP_10CM_AVEL,SMVP_20CM_AVEL,SMVP_40CM_AVEL,AVE_WS_2MINL,LAND_PT_TEMPL;//上一年
    public String TEMP,RELA_HUMI,BUCKET_ACC_RAIN_COUNT,SMVP_5CM_AVE,AVE_WS_2MIN,LAND_PT_TEMP;//当前年

    //相关数据查询
//    public String TEMP;//气温
//    public String RELA_HUMI;//相对湿度
    public String MOST_WD;//最大风速
    public String MOST_WS;//最大风向
    public String MAX_WD;//极大风速
    public String MAX_WS;//极大风向
//    public String SMVP_5CM_AVE;//5cm土壤体积含水量(%)
    public String SMVP_10CM_AVE;//10cm土壤体积含水量(%)
    public String SMVP_20CM_AVE;//20cm土壤体积含水量(%)
    public String SMVP_30CM_AVE;//30cm土壤体积含水量(%)
    public String SMVP_40CM_AVE;//40cm土壤体积含水量(%)
    public String SMVP_50CM_AVE;//40cm土壤体积含水量(%)
    public String LAND_10CM_TEMP;//10cm地温(℃)
    public String LAND_15CM_TEMP;//15cm地温(℃)
    public String LAND_20CM_TEMP;//20cm地温(℃)
    public String LAND_30CM_TEMP;//30cm地温(℃)
    public String LAND_40CM_TEMP;//40cm地温(℃)

    public String SoilRH,RH,Fc,Fi,P,ET0,Kc,ForecastDays,I;

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
        dest.writeFloat(this.x10);
        dest.writeFloat(this.y10);
        dest.writeFloat(this.x20);
        dest.writeFloat(this.y20);
        dest.writeFloat(this.x30);
        dest.writeFloat(this.y30);
        dest.writeFloat(this.x40);
        dest.writeFloat(this.y40);
        dest.writeFloat(this.x50);
        dest.writeFloat(this.y50);
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
        dest.writeString(this.SMVP_10CM_AVEL);
        dest.writeString(this.SMVP_20CM_AVEL);
        dest.writeString(this.SMVP_40CM_AVEL);
        dest.writeString(this.AVE_WS_2MINL);
        dest.writeString(this.LAND_PT_TEMPL);
        dest.writeString(this.TEMP);
        dest.writeString(this.RELA_HUMI);
        dest.writeString(this.BUCKET_ACC_RAIN_COUNT);
        dest.writeString(this.SMVP_5CM_AVE);
        dest.writeString(this.AVE_WS_2MIN);
        dest.writeString(this.LAND_PT_TEMP);
        dest.writeString(this.MOST_WD);
        dest.writeString(this.MOST_WS);
        dest.writeString(this.MAX_WD);
        dest.writeString(this.MAX_WS);
        dest.writeString(this.SMVP_10CM_AVE);
        dest.writeString(this.SMVP_20CM_AVE);
        dest.writeString(this.SMVP_30CM_AVE);
        dest.writeString(this.SMVP_40CM_AVE);
        dest.writeString(this.SMVP_50CM_AVE);
        dest.writeString(this.LAND_10CM_TEMP);
        dest.writeString(this.LAND_15CM_TEMP);
        dest.writeString(this.LAND_20CM_TEMP);
        dest.writeString(this.LAND_30CM_TEMP);
        dest.writeString(this.LAND_40CM_TEMP);
        dest.writeString(this.SoilRH);
        dest.writeString(this.RH);
        dest.writeString(this.Fc);
        dest.writeString(this.Fi);
        dest.writeString(this.P);
        dest.writeString(this.ET0);
        dest.writeString(this.Kc);
        dest.writeString(this.ForecastDays);
        dest.writeString(this.I);
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
        this.x10 = in.readFloat();
        this.y10 = in.readFloat();
        this.x20 = in.readFloat();
        this.y20 = in.readFloat();
        this.x30 = in.readFloat();
        this.y30 = in.readFloat();
        this.x40 = in.readFloat();
        this.y40 = in.readFloat();
        this.x50 = in.readFloat();
        this.y50 = in.readFloat();
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
        this.SMVP_10CM_AVEL = in.readString();
        this.SMVP_20CM_AVEL = in.readString();
        this.SMVP_40CM_AVEL = in.readString();
        this.AVE_WS_2MINL = in.readString();
        this.LAND_PT_TEMPL = in.readString();
        this.TEMP = in.readString();
        this.RELA_HUMI = in.readString();
        this.BUCKET_ACC_RAIN_COUNT = in.readString();
        this.SMVP_5CM_AVE = in.readString();
        this.AVE_WS_2MIN = in.readString();
        this.LAND_PT_TEMP = in.readString();
        this.MOST_WD = in.readString();
        this.MOST_WS = in.readString();
        this.MAX_WD = in.readString();
        this.MAX_WS = in.readString();
        this.SMVP_10CM_AVE = in.readString();
        this.SMVP_20CM_AVE = in.readString();
        this.SMVP_30CM_AVE = in.readString();
        this.SMVP_40CM_AVE = in.readString();
        this.SMVP_50CM_AVE = in.readString();
        this.LAND_10CM_TEMP = in.readString();
        this.LAND_15CM_TEMP = in.readString();
        this.LAND_20CM_TEMP = in.readString();
        this.LAND_30CM_TEMP = in.readString();
        this.LAND_40CM_TEMP = in.readString();
        this.SoilRH = in.readString();
        this.RH = in.readString();
        this.Fc = in.readString();
        this.Fi = in.readString();
        this.P = in.readString();
        this.ET0 = in.readString();
        this.Kc = in.readString();
        this.ForecastDays = in.readString();
        this.I = in.readString();
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
