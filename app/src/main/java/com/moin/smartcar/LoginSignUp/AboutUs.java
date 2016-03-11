package com.moin.smartcar.LoginSignUp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.moin.smartcar.R;
import com.moin.smartcar.SingeltonData.DataSingelton;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AboutUs extends AppCompatActivity {


    @Bind(R.id.textView1)
    TextView textView1;
    @Bind(R.id.textView2)
    TextView textView2;
    @Bind(R.id.textView3)
    TextView textView3;
    @Bind(R.id.textView4)
    TextView textView4;
    @Bind(R.id.textView5)
    TextView textView5;

    private void setFonts() {
        textView1.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
        textView2.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
        textView3.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
        textView4.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
        textView5.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        ButterKnife.bind(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("ABOUT SMART CAR");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

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
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }
}
