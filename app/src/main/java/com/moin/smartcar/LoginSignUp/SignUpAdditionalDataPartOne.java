package com.moin.smartcar.LoginSignUp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.moin.smartcar.R;
import com.moin.smartcar.Utility.MoinUtils;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpAdditionalDataPartOne extends AppCompatActivity {

    @Bind(R.id.newSignUpUseremail)EditText emailIdEditText;
    @Bind(R.id.newSignUpUsername)EditText userNameEditText;
    @Bind(R.id.newsignUpMobileEditText)EditText mobileEditText;
    @Bind(R.id.newsignUpAddressEditText)EditText addressEditText;

    private Intent dataRecieved;
    private String passwordPassed;

    @Bind(R.id.loadignView)View loadingView;
    @Bind(R.id.loadingIndicator)AVLoadingIndicatorView loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_additional_data_part_one);
        ButterKnife.bind(SignUpAdditionalDataPartOne.this);

        Toolbar myToolbar = (Toolbar)findViewById(R.id.signUpAppBar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.signUpText));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dataRecieved = getIntent();
        emailIdEditText.setText(dataRecieved.getStringExtra("emailIdPassed"));
        passwordPassed = dataRecieved.getStringExtra("passwordIdPassed");

        ImageView glassyImage = (ImageView)findViewById(R.id.glassImageView);
        MoinUtils.getReference().setGlassyBitmap(glassyImage);

        hideLoadingView();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.scaleincrease, R.anim.slide_right_out);
    }

    @OnClick(R.id.signUpButton)void continueCLicked(View view){
        if (userNameEditText.getText().toString().length() == 0){
            showMessage("Please enter user name");
            userNameEditText.requestFocus();
            userNameEditText.append("");
            return;
        }
        if (mobileEditText.getText().toString().length() == 0){
            showMessage("Please enter mobile number");
            mobileEditText.requestFocus();
            mobileEditText.append("");
            return;
        }

        if (mobileEditText.getText().toString().length() != 10){
            showMessage("Incorrect mobile number");
            mobileEditText.requestFocus();
            mobileEditText.append("");
            return;
        }

        if (addressEditText.getText().toString().length() == 0){
            showMessage("Please enter address");
            addressEditText.requestFocus();
            addressEditText.append("");
            return;
        }

        Intent myIntent = new Intent(SignUpAdditionalDataPartOne.this,SignUpAdditionalPartTwo.class);
        myIntent.putExtra("emailIdPassed1",emailIdEditText.getText().toString());
        myIntent.putExtra("passwordPassed1",passwordPassed);
        myIntent.putExtra("usernamePassed1",userNameEditText.getText().toString());
        myIntent.putExtra("mobilenumberpassed1",mobileEditText.getText().toString());
        myIntent.putExtra("addresspassed1",addressEditText.getText().toString());
        startActivity(myIntent);
        overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
    }


    private void hideLoadingView() {
        loadingView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.GONE);
    }

    private void showLoadingView() {
        loadingView.setVisibility(View.VISIBLE);
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    private void hideLoadingWithMessage(String msg) {
        hideLoadingView();
        MoinUtils.getReference().showMessage(SignUpAdditionalDataPartOne.this, msg);
    }

    private void showMessage(String str){
        MoinUtils.getReference().showMessage(SignUpAdditionalDataPartOne.this,str);
    }
}
