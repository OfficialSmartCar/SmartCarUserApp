package com.moin.smartcar.Booking;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moin.smartcar.R;
import com.moin.smartcar.SingeltonData.DataSingelton;

import org.w3c.dom.Text;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PremiumCar extends AppCompatActivity {

    private DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();
    @Bind(R.id.img3)ImageView img3;
    @Bind(R.id.messageTextView)TextView messageTextView;
    @Bind(R.id.textView1)TextView textView1;
    @Bind(R.id.textView2)TextView textView2;
    @Bind(R.id.textView3)TextView textView3;


    private void setFonts(){
        messageTextView.setTypeface(mySingelton.myCustomTypeface);
        textView1.setTypeface(mySingelton.myCustomTypeface);
        textView2.setTypeface(mySingelton.myCustomTypeface);
        textView3.setTypeface(mySingelton.myCustomTypeface);

    }


    @OnClick(R.id.cell1)void clickImg1(View view){
        initiateCall(getResources().getString(R.string.number1));
    }

    @OnClick(R.id.cell2)void clickImg2(View view){
        initiateCall(getResources().getString(R.string.number2));
    }

    @OnClick(R.id.cell3)void clickImg3(View view){
        initiateCall(getResources().getString(R.string.number3));
    }

    private void initiateCall(String str){
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", str, null));
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_car);
        mySingelton.PremiumSelection = 1;

        ButterKnife.bind(this);

        Toolbar myToolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Premium Car Holder");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewGroup.LayoutParams params = img3.getLayoutParams();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        params.width = width/2;

        img3.setLayoutParams(params);

        setFonts();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.scaleincrease, R.anim.slide_right_out);
    }
}
