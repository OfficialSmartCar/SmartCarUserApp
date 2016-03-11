package com.moin.smartcar;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.util.Base64;
import android.util.Log;

import com.daimajia.androidanimations.library.Techniques;
import com.moin.smartcar.Database.DatabaseManager;
import com.moin.smartcar.LoginSignUp.LoginNew;
import com.moin.smartcar.SingeltonData.DataSingelton;
import com.moin.smartcar.User.CarInfoStr;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class MainActivity extends AwesomeSplash {

    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();
    private Runnable task;


    @Override
    public void initSplash(ConfigSplash configSplash) {
        configSplash.setBackgroundColor(R.color.colorPrimary); //any color you want form colors.xml
        configSplash.setAnimCircularRevealDuration(1000); //int ms
        configSplash.setRevealFlagX(Flags.REVEAL_RIGHT);  //or Flags.REVEAL_LEFT
        configSplash.setRevealFlagY(Flags.REVEAL_BOTTOM); //or Flags.REVEAL_TOP

        configSplash.setLogoSplash(R.drawable.logo); //or any other drawable
        configSplash.setAnimLogoSplashDuration(3000); //int ms
        configSplash.setAnimLogoSplashTechnique(Techniques.FadeInDown);

        configSplash.setTitleSplash("");
        configSplash.setTitleTextSize(25f); //float value
        configSplash.setAnimTitleDuration(0);
        configSplash.setAnimTitleTechnique(Techniques.BounceIn);


    }

//    private void getCOde(){
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.moin.smartcar",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("MOin KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//                Log.d("MOin KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            Log.d("MOin KeyHash:", "no");
//        } catch (NoSuchAlgorithmException e) {
//            Log.d("MOin KeyHash:", "no");
//        }
//    }

    @Override
    public void animationsFinished() {

//        getCOde();
        Typeface myCustomTypeface = Typeface.createFromAsset(getAssets(),"fonts/arial.ttf");
        DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface = myCustomTypeface;

        DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();
        mySingelton.signUpSuccess = "";

        mySingelton.notificationCount = 0;

        mySingelton.userName = "";
        DatabaseManager db = new DatabaseManager(MainActivity.this);
        Boolean check = db.getUserInfo();

        ArrayList<CarInfoStr> list = new ArrayList<>();
        mySingelton.userCarList = new ArrayList<>();
        list = db.garCarInfo();
        if (list != null){
            mySingelton.userCarList = new ArrayList<>();
            for (int i=0;i<list.size();i++){
                mySingelton.userCarList.add(list.get(i));
            }
        }
        if (DataSingelton.getMy_SingeltonData_Reference().userName.length()==0){
            startActivity(new Intent(MainActivity.this, LoginNew.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        else
        {
            startActivity(new Intent(MainActivity.this, HomePage.class));
            overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
        }
        db.closeDB();
    }
}
