package com.moin.smartcar.LoginSignUp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.moin.smartcar.LoginSignUp.IntroFragments.FRG2;
import com.moin.smartcar.LoginSignUp.IntroFragments.FRG3;
import com.moin.smartcar.LoginSignUp.IntroFragments.Frg1;
import com.moin.smartcar.Pager.CustomPager;
import com.moin.smartcar.R;
import com.moin.smartcar.Utility.MoinUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class LoginInitial extends AppCompatActivity {

    private int backPressed = 0;

    private Runnable task;
    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();

    private CustomPager mPager;

    Frg1 frg1 = Frg1.getInstance();
    FRG2 frg2 = FRG2.getInstance();
    FRG3 frg3 = FRG3.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_initial);

        mPager = (CustomPager)findViewById(R.id.viewPagerLoginInitial);
        mPager.setPagingEnabled(true);
        mPager.setScrollDurationFactor(3);
        mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

//        TitlePageIndicator titleIndicator = (TitlePageIndicator)findViewById(R.id.titles);
//        titleIndicator.setViewPager(pager);

    }

    public void navigateToLoginPage(View view) {

//        startActivity(new Intent(LoginInitial.this,UserBookings.class));
        new Thread(new Runnable() {
            @Override
            public void run() {
            }
        }).run();

        Runnable task2 = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LoginInitial.this,LoginNew.class));

//                startActivity(new Intent(LoginInitial.this,RegularService.class));

            }
        };

        task = new Runnable() {
            public void run() {
                finish();
            }
        };
        worker.schedule(task, 2, TimeUnit.SECONDS);
        worker.schedule(task2,500,TimeUnit.MILLISECONDS);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        String[] tabs;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {

                case 0:return frg1;
                case 1:return frg2;
                case 2:return frg3;
                default: return frg1;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (backPressed == 0) {
                backPressed = 1;
                MoinUtils.getReference().showMessage(LoginInitial.this, "Press Back Again To Exit");
//                Toast.makeText(this,"Press Back Again To Exit",Toast.LENGTH_SHORT).show();
                task = new Runnable() {
                    public void run() {
                        backPressed = 0;
                    }
                };
                worker.schedule(task, 2, TimeUnit.SECONDS);
                return false;
            } else {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }

        return super.onKeyDown(keyCode, event);

    }
}
