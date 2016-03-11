package com.moin.smartcar.Booking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moin.smartcar.HomePage;
import com.moin.smartcar.R;
import com.moin.smartcar.SingeltonData.DataSingelton;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookingSuccess extends AppCompatActivity {

    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();
    private LinearLayout backgroundView;
    @Bind(R.id.textView2)
    TextView messageTextView;
    @Bind(R.id.goHomeButton)Button goHomeButton;
    private String fromBreakdown = "NO";
    private String fromDentPaint = "NO";
    private String sourceIsContactUs = "NO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_success);
        ButterKnife.bind(this);

        backgroundView = (LinearLayout) findViewById(R.id.successWebView);
        DataSingelton.getMy_SingeltonData_Reference().successWebView1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        WebView myWebView = DataSingelton.getMy_SingeltonData_Reference().successWebView1;
//        try{
//            backgroundView.addView(myWebView);
//        }catch (Exception e){
//            backgroundView.removeAllViews();
//            backgroundView.addView(myWebView);
//        }

        if(myWebView.getParent()!=null)
            ((ViewGroup)myWebView.getParent()).removeView(myWebView);
        backgroundView.addView(myWebView);


        fromBreakdown = "NO";
        fromDentPaint = "NO";
        sourceIsContactUs = "NO";

        try {
            sourceIsContactUs = getIntent().getStringExtra("sourceIsContactUs");
        } catch (Exception e) {
            sourceIsContactUs = "NO";
        }

        try {
            fromBreakdown = getIntent().getStringExtra("sourceIsBreakdown");
        } catch (Exception e) {
            fromBreakdown = "NO";
        }

        try {
            fromDentPaint = getIntent().getStringExtra("sourceIsDentPaint");
        } catch (Exception e) {
            fromDentPaint = "NO";
        }

        try {
            if (fromBreakdown.equalsIgnoreCase("YES")) {
                messageTextView.setText("Your breakdown request has been successfully submitted \n\n\n we will find someone who will help you in this emergency \n\n You will receive a notification or call from our executive");
            }
        } catch (Exception e) {

        }


        try {
            if (fromDentPaint.equalsIgnoreCase("YES")) {
                messageTextView.setText("Your request has been successfully submitted \n\n\n we will get back to you with a quote\n\n\n You will receive a notification or call from our executive");
            }
        } catch (Exception e) {

        }

        try {
            if (sourceIsContactUs.equalsIgnoreCase("YES")) {
                messageTextView.setText("Your query has been successfully submitted \n\n\nOur executive will get back to you to address any issues that you might have.");
            }
        } catch (Exception e) {

        }

        messageTextView.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
        goHomeButton.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
    }

    public void goBackToHome(View view) {
        startActivity(new Intent(BookingSuccess.this, HomePage.class));
        overridePendingTransition(R.anim.scaleincrease, R.anim.slide_right_out);

        Runnable task = new Runnable() {
            @Override
            public void run() {
//                DataSingelton.getMy_SingeltonData_Reference().successWebView = null;
                finish();
            }
        };


        worker.schedule(task, 2000, TimeUnit.MILLISECONDS);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return false;
        }
        return super.onKeyDown(keyCode, event);

    }
}
