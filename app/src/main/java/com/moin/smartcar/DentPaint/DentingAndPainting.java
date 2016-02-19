package com.moin.smartcar.DentPaint;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.moin.smartcar.MyBookings.navUserBookings;
import com.moin.smartcar.R;
import com.moin.smartcar.SingeltonData.DataSingelton;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DentingAndPainting extends AppCompatActivity {

    @Bind(R.id.checkBox1)
    ImageView checkBox1;
    @Bind(R.id.checkBox2)
    ImageView checkBox2;
    @Bind(R.id.checkBox3)
    ImageView checkBox3;
    @Bind(R.id.checkBox4)
    ImageView checkBox4;
    @Bind(R.id.check1)View checkSelector1;
    @Bind(R.id.check2)View checkSelector2;
    @Bind(R.id.check3)View checkSelector3;
    @Bind(R.id.check4)View checkSelector4;
    @Bind(R.id.attachImages)View attachImages;
    @Bind(R.id.numberOfImages)TextView numberOfImages;
    private Boolean val1, val2, val3, val4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denting_and_painting);

        ButterKnife.bind(this);

        DataSingelton.getMy_SingeltonData_Reference().imagesSelected = new ArrayList<>();

        android.support.v7.widget.Toolbar myToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.dentandpaint));

        navUserBookings navFragment = (navUserBookings) getSupportFragmentManager().findFragmentById(R.id.navigationDrawer_fragment);
        navFragment.setUp(R.id.navigationDrawer_fragment, (DrawerLayout) findViewById(R.id.drawerLayout), myToolbar, getResources().getString(R.string.dentandpaint));

        val1 = val2 = val3  = val4 = false;


        checkSelector1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                val1 = !val1;
                setValueForCheckBox1();
            }
        });

        checkSelector2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                val2 = !val2;
                setValueForCheckBox2();
            }
        });

        checkSelector3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                val3 = !val3;
                setValueForCheckBox3();
            }
        });

        checkSelector4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                val4 = !val4;
                setValueForCheckBox4();
            }
        });

        attachImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DentingAndPainting.this,DentPaintHome.class));
                overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
            }
        });

    }

    private void setValueForCheckBox1() {
        if (val1) {
            checkBox1.setImageResource(R.drawable.check1);
        } else {
            checkBox1.setImageResource(R.drawable.check0);
        }
    }

    private void setValueForCheckBox2() {
        if (val2) {
            checkBox2.setImageResource(R.drawable.check1);
        } else {
            checkBox2.setImageResource(R.drawable.check0);
        }
    }

    private void setValueForCheckBox3() {
        if (val3) {
            checkBox3.setImageResource(R.drawable.check1);
        } else {
            checkBox3.setImageResource(R.drawable.check0);
        }
    }

    private void setValueForCheckBox4() {
        if (val4) {
            checkBox4.setImageResource(R.drawable.check1);
        } else {
            checkBox4.setImageResource(R.drawable.check0);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (DataSingelton.getMy_SingeltonData_Reference().imagesSelected.size() > 0){
            numberOfImages.setText(DataSingelton.getMy_SingeltonData_Reference().imagesSelected.size() + " Images Attached");
        }else{
            numberOfImages.setText("");

        }
    }

    public void startSendingData(View view) {
        Toast.makeText(DentingAndPainting.this,"Start Uploading Data",Toast.LENGTH_LONG).show();
    }
}
