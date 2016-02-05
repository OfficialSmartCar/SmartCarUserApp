package com.moin.smartcar.Support;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

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

    }

    @OnClick(R.id.callButton)void navigateToCall(View view){
        startActivity(new Intent(ContactUs.this, Calling.class));
        overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
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
