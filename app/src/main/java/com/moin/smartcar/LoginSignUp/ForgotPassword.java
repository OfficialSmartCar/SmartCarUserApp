package com.moin.smartcar.LoginSignUp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.moin.smartcar.R;
import com.moin.smartcar.Utility.MoinUtils;

public class ForgotPassword extends AppCompatActivity {

    private EditText emailIdEditTex;

    private ImageView glassImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Toolbar myToolbar = (Toolbar)findViewById(R.id.forgotPasswordAppBar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        glassImage = (ImageView)findViewById(R.id.glassImageView);
        MoinUtils.getReference().setGlassyBitmap(glassImage);

        emailIdEditTex = (EditText)findViewById(R.id.forgotPasswordEmailEditText);
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

    public void validateEmailAnsSendMail(View view) {

        if (emailIdEditTex.getText().toString().length() == 0){
            MoinUtils.getReference().showMessage(ForgotPassword.this,"Please Enter Email Id");
            emailIdEditTex.requestFocus();
            emailIdEditTex.append("");
            return;
        }

        if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailIdEditTex.getText().toString()).matches()){
            startActivity(new Intent(ForgotPassword.this,ResetPassword.class));
            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
        }
        else {
            MoinUtils.getReference().showMessage(ForgotPassword.this,"Entered Email Id Is Invalid");
        }
    }
}
