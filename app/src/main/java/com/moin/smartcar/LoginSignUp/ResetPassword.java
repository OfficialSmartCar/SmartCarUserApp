package com.moin.smartcar.LoginSignUp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.moin.smartcar.R;
import com.moin.smartcar.Utility.MoinUtils;

public class ResetPassword extends AppCompatActivity {

    private ImageView glassImage;
    private EditText verificationCode,password,confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Toolbar myToolbar = (Toolbar)findViewById(R.id.resetPasswordAppBar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.resetPassword));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        glassImage = (ImageView)findViewById(R.id.glassImageView);
        MoinUtils.getReference().setGlassyBitmap(glassImage);

        verificationCode = (EditText)findViewById(R.id.resetPasswordVerificationCodeEditText);
        password = (EditText)findViewById(R.id.resetPasswordEditText);
        confirmPassword = (EditText)findViewById(R.id.resetConfirmPasswordEditText);
    }

    public void verifyTheCodeAndPasswords(View view) {

        if (verificationCode.getText().length() == 0){
            showMessage(getResources().getString(R.string.blankVerificationCOde));
            verificationCode.requestFocus();
            verificationCode.append("");
            return;
        }

        if (password.getText().length() == 0){
            showMessage(getResources().getString(R.string.blankPassword));
            password.requestFocus();
            password.append("");
            return;
        }

        if (!confirmPassword.getText().toString().equalsIgnoreCase(password.getText().toString())){
            showMessage(getResources().getString(R.string.passwordDontMatch));
            confirmPassword.requestFocus();
            confirmPassword.append("");
            return;
        }
    }

    private void showMessage(String str){
        MoinUtils.getReference().showMessage(ResetPassword.this,str);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
