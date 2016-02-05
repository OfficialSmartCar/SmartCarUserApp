package com.moin.smartcar;

import android.content.Intent;
import android.view.KeyEvent;

import com.daimajia.androidanimations.library.Techniques;
import com.moin.smartcar.Database.DatabaseManager;
import com.moin.smartcar.LoginSignUp.LoginInitial;
import com.moin.smartcar.LoginSignUp.LoginNew;
import com.moin.smartcar.SingeltonData.DataSingelton;
import com.moin.smartcar.User.CarInfoStr;
import com.moin.smartcar.Utility.MoinUtils;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.cnst.Flags;
import com.viksaa.sssplash.lib.model.ConfigSplash;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AwesomeSplash {

    private Runnable task;
    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();

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

    @Override
    public void animationsFinished() {
        DataSingelton.getMy_SingeltonData_Reference().userName = "";
        DatabaseManager db = new DatabaseManager(MainActivity.this);
        Boolean check = db.getUserInfo();

        ArrayList<CarInfoStr> list = new ArrayList<>();
        list = db.garCarInfo();
        if (list != null){
            DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();
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
    }




}
