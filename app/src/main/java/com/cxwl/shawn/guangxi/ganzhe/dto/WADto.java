package com.cxwl.shawn.guangxi.ganzhe.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * 交流互动-专家联盟
 * @author shawn_sun
 */
public class WADto implements Parcelable {

    public String name;
    public String imgUrl;
    public String dataUrl;
    public String localviewId;
    public String userId;//用户id
    public String userName;
    public String expertId;//专家id
    public String questionId;//问题id
    public String breif;//专家简介
    public String label;//专家标签
    public String research;//研究领域
    public String shanchang;//擅长
    public String content;
    public String isexpert;//1为专家，2不是专家
    public String phone;//手机号
    public String ask;
    public String answer;
    public String title,time;
    public List<String> imgList = new ArrayList<>();//图片集合
    public List<WADto> commentList = new ArrayList<>();

    public WADto() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.imgUrl);
        dest.writeString(this.dataUrl);
        dest.writeString(this.localviewId);
        dest.writeString(this.userId);
        dest.writeString(this.userName);
        dest.writeString(this.expertId);
        dest.writeString(this.questionId);
        dest.writeString(this.breif);
        dest.writeString(this.label);
        dest.writeString(this.research);
        dest.writeString(this.shanchang);
        dest.writeString(this.content);
        dest.writeString(this.isexpert);
        dest.writeString(this.phone);
        dest.writeString(this.ask);
        dest.writeString(this.answer);
        dest.writeString(this.title);
        dest.writeString(this.time);
        dest.writeStringList(this.imgList);
        dest.writeTypedList(this.commentList);
    }

    protected WADto(Parcel in) {
        this.name = in.readString();
        this.imgUrl = in.readString();
        this.dataUrl = in.readString();
        this.localviewId = in.readString();
        this.userId = in.readString();
        this.userName = in.readString();
        this.expertId = in.readString();
        this.questionId = in.readString();
        this.breif = in.readString();
        this.label = in.readString();
        this.research = in.readString();
        this.shanchang = in.readString();
        this.content = in.readString();
        this.isexpert = in.readString();
        this.phone = in.readString();
        this.ask = in.readString();
        this.answer = in.readString();
        this.title = in.readString();
        this.time = in.readString();
        this.imgList = in.createStringArrayList();
        this.commentList = in.createTypedArrayList(WADto.CREATOR);
    }

    public static final Creator<WADto> CREATOR = new Creator<WADto>() {
        @Override
        public WADto createFromParcel(Parcel source) {
            return new WADto(source);
        }

        @Override
        public WADto[] newArray(int size) {
            return new WADto[size];
        }
    };
}
