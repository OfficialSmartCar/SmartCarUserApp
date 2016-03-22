package com.moin.smartcar.SingeltonData;


import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.webkit.WebView;
import android.widget.TextView;

import com.moin.smartcar.AMC.AMCStr;
import com.moin.smartcar.MyBookings.DataStr.UpCommingStr;
import com.moin.smartcar.OwnServices.OwnServiceStr;
import com.moin.smartcar.RegService.RegServiceStr;
import com.moin.smartcar.User.CarInfoStr;

import java.util.ArrayList;

/**
 * Created by macpro on 10/13/15.
 */
public class DataSingelton {

    public static DataSingelton my_SingeltonData;
//    public static String baseUrl = "http://178.62.10.40:3000";
    public static String baseUrl = "http://178.62.84.115:3000";
    public static String getCustomeServices = baseUrl + "/getCustomServices";
    public static String getSupportInformation = baseUrl + "/getSupportInformation";
    public static String LoginUrl = baseUrl + "/userAuthentication";
    public static String UpdateUserDetails = baseUrl + "/updateUserDetails";
    public static String getBrandAndModels = baseUrl + "/getCarBrandAndModel";
    public static String CheckEmailPresence = baseUrl + "/checkPresenceForEmail";
    public static String SignUpUrl = baseUrl + "/signUp";
    public static String getExpressService = baseUrl + "/getExpressServices";
    public static String getAMC = baseUrl + "/getAMC";
    public static String booking = baseUrl + "/regularServiceBooking";
    public static String getAllbooking = baseUrl + "/getAllBookings";
    public static String cancelBooking = baseUrl + "/cancelBooking";
    public static String rescheduleAppointment = baseUrl + "/rescheduleAppointment";
    public static String checkIfCanReschedule = baseUrl + "/checkIfCanReschedule";
    public static String getNotificationList = baseUrl + "/getNotificationList";
    public static String reportBreakdown = baseUrl + "/reportBreakdown";
    public static String reportDentPaint = baseUrl + "/reportDentPaint";
    public static String uploadImageUrl = "http://184.95.55.236:8080/SmartCar/dentpaintImagesupload/";
    public static String supportUrl = baseUrl + "/support";
    public static String forgotPasswordURL = baseUrl + "/forgotPasswordURL";
    public static String resetPassword = baseUrl + "/resetPassword";
    public static String getUserDetails = baseUrl + "/getUserDetails";
    public static String checkIfCanBook = baseUrl + "/checkIfCanBook";

    public TextView moinTextView = null;

    public int firstLogin_moin;

    public String forgotPasswordEmailId;
    public int passwordReset;


    public int selectionOfScreen;
    public int screenHeight;
    public String userName;
    public String userImageLink;
//    public static String baseUrl = "http://192.168.1.122:3000";
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
    public Double myLat, myLong;
    public ArrayList<Bitmap> imagesSelected;
    public String breakdownReason;

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

    public CarInfoStr CarSelecetd;
    public Double AmmountToPay;
    public int paymentSelection;
    public ArrayList<RegServiceStr> regularServiceSelection;
    public ArrayList<OwnServiceStr> customServiceSelection;
    public ArrayList<AMCStr> amcServiceSelection;
    public WebView successWebView1;
    public UpCommingStr MyBookingData;
    public Boolean UpCommingOrOther;
    public int cancelledOrRescheduled;
    public String signUpSuccess;

    public String signUpOrAdd;
    public int PremiumSelection;

    public int notificationCount;

    public int FacebookLogin;
    public String key;

    public Typeface myCustomTypeface;
    public int bookingChanged;


    private DataSingelton() {
        userName = "";
        userId = "";
    }

    public static void ResetAll() {


    }

    public static DataSingelton getMy_SingeltonData_Reference() {
        if (my_SingeltonData == null) {
            my_SingeltonData = new DataSingelton();
        }
        return my_SingeltonData;
    }

    public FragmentManager myFragmentManager;
    public Context myContext;

}
