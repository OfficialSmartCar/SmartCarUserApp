package com.moin.smartcar.Support;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by macpro on 1/27/16.
 */
public class FaqStr implements Parcelable{
    public String mainTitle;
    ArrayList<String> subTitle;
    ArrayList<String> internalStructire;

    public FaqStr(){

    }

    protected FaqStr(Parcel in) {
        mainTitle = in.readString();
        subTitle = in.createStringArrayList();
        internalStructire = in.createStringArrayList();
    }

    public static final Creator<FaqStr> CREATOR = new Creator<FaqStr>() {
        @Override
        public FaqStr createFromParcel(Parcel in) {
            return new FaqStr(in);
        }

        @Override
        public FaqStr[] newArray(int size) {
            return new FaqStr[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mainTitle);
        dest.writeStringList(subTitle);
        dest.writeStringList(internalStructire);
    }
}
