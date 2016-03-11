package com.moin.smartcar.User;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by macpro on 1/8/16.
 */
public class CarInfoStr implements Parcelable{
    public String carName;
    public String carBrand;
    public String carModel;
    public String yearOfMaufacture;
    public String carRegNo;
    public String carVariant;
    public String carId;
    public int isPremium;
    public int status;

    public CarInfoStr(){

    }


    protected CarInfoStr(Parcel in) {
        carName = in.readString();
        carBrand = in.readString();
        carModel = in.readString();
        yearOfMaufacture = in.readString();
        carRegNo = in.readString();
        carVariant = in.readString();
        carId = in.readString();
        isPremium = in.readInt();
        status = in.readInt();
    }

    public static final Creator<CarInfoStr> CREATOR = new Creator<CarInfoStr>() {
        @Override
        public CarInfoStr createFromParcel(Parcel in) {
            return new CarInfoStr(in);
        }

        @Override
        public CarInfoStr[] newArray(int size) {
            return new CarInfoStr[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(carName);
        dest.writeString(carBrand);
        dest.writeString(carModel);
        dest.writeString(yearOfMaufacture);
        dest.writeString(carRegNo);
        dest.writeString(carVariant);
        dest.writeString(carId);
        dest.writeInt(isPremium);
        dest.writeInt(status);
    }
}
