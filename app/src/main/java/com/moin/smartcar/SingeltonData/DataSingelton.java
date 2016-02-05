package com.moin.smartcar.SingeltonData;


import android.graphics.Bitmap;

import com.moin.smartcar.User.CarInfoStr;

import java.util.ArrayList;

/**
 * Created by macpro on 10/13/15.
 */
public class DataSingelton {

    public String userName;
    public String userImageLink;
    public String userId;
    public String mobileNumber;
    public String address;
    public String userEmailId;
    public ArrayList<CarInfoStr> userCarList;
    public ArrayList<CarInfoStr> deletedCars;

    //backupData
    public String backUpName;
    public String backUpAdress;
    public String backUpMobileNUmber;
    public int carSelectionIndex;

    public String UserNotificationToken;

    public Double myLat,myLong;

    public ArrayList<Bitmap>imagesSelected;

    public String breakdownReason;

    public static String baseUrl = "http://178.62.10.40:3000";
//    public static String baseUrl = "http://192.168.1.122:3000";

    public static String getServicesurl = baseUrl + "/getAllTasks";
    public static String getSupportInformation = baseUrl + "/getSupportInformation";
    public static String LoginUrl = baseUrl + "/userAuthentication";
    public static String UpdateUserDetails = baseUrl + "/updateUserDetails";
    public static String getBrandAndModels = baseUrl + "/getCarBrandAndModel";
    public static String CheckEmailPresence = baseUrl + "/checkPresenceForEmail";
    public static String SignUpUrl = baseUrl + "/signUp";
    public static String getExpressService = baseUrl + "/getExpressServices";

    public static DataSingelton my_SingeltonData;

    private DataSingelton() {
        userName = "";
        userId = "";
    }

    public static void ResetAll(){


    }

    public static DataSingelton getMy_SingeltonData_Reference() {
        if (my_SingeltonData == null) {
            my_SingeltonData = new DataSingelton();
        }
        return my_SingeltonData;
    }


    // data For Custom Services
    public String customServicetitle;
    public String customServicesubTitle;
    public String customServiceTaxType;
    public Double customServiceTaxPercentage;
    public Double customServiceCost;

    //data For Reg Service + AMC
    public String regServiceTaskId;
    public String regServicetitle;
    public String regServicesubTitle;
    public String regServiceTaxType;
    public Double regServiceTaxPercentage;
    public Double regServiceCost;


}
