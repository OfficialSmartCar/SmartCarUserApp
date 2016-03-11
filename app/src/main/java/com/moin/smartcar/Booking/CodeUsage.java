package com.moin.smartcar.Booking;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.moin.smartcar.R;
import com.moin.smartcar.SingeltonData.DataSingelton;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CodeUsage extends AppCompatActivity {

    @Bind(R.id.textView1)TextView textView1;
    @Bind(R.id.textView2)TextView textView2;
    @Bind(R.id.textView3)TextView textView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_usage);

        ButterKnife.bind(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        textView1.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
        textView2.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
        textView3.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.scaleincrease, R.anim.slide_right_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
