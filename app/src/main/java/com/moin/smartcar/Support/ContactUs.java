package com.moin.smartcar.Support;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.moin.smartcar.R;
import com.moin.smartcar.SingeltonData.DataSingelton;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContactUs extends AppCompatActivity {

    @Bind(R.id.phoneNumberEditText)EditText phoneNumber;
    @Bind(R.id.alternatePhoneNumberEditText)EditText alternatePhoneNumber;
    @Bind(R.id.emailEditText)EditText emailEditText;
    @Bind(R.id.mainQueryEditText)EditText mainQueryEditText;

    @Bind(R.id.img3)
    ImageView img3;


    private DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        ButterKnife.bind(this);

        Toolbar myToolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Contact Us");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        phoneNumber.setText(mySingelton.mobileNumber);
        emailEditText.setText(mySingelton.userEmailId);
        phoneNumber.setEnabled(false);
        emailEditText.setEnabled(false);


        ViewGroup.LayoutParams params = img3.getLayoutParams();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        params.width = width / 2;

        img3.setLayoutParams(params);

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

    private void initiateCall(String str) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", str, null));
        startActivity(intent);
    }

    @OnClick(R.id.cell1)
    void clickImg1(View view) {
        initiateCall(getResources().getString(R.string.number1));
    }

    @OnClick(R.id.cell2)
    void clickImg2(View view) {
        initiateCall(getResources().getString(R.string.number2));
    }

    @OnClick(R.id.cell3)
    void clickImg3(View view) {
        initiateCall(getResources().getString(R.string.number3));
    }
}
