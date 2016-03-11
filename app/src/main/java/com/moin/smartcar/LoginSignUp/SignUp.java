package com.moin.smartcar.LoginSignUp;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.moin.smartcar.Network.VolleySingelton;
import com.moin.smartcar.R;
import com.moin.smartcar.SingeltonData.DataSingelton;
import com.moin.smartcar.Utility.MoinUtils;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignUp extends AppCompatActivity {

    private EditText emailId,password,confirmPassword,username;
    private View passwordContainer;
    private ImageView glassyImage;
    private Button signUpButton;
    private int signUpCheck = 0;
    private DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();

    private ViewGroup.LayoutParams passwordContainerParams;

    private View alreayHaveAccountView;

    private View loadingView;
    private AVLoadingIndicatorView loadingIndicator;

    @Bind(R.id.message1)TextView message1;
    @Bind(R.id.message2)TextView message2;

    private void setFonts(){
        emailId.setTypeface(mySingelton.myCustomTypeface);
        password.setTypeface(mySingelton.myCustomTypeface);
        confirmPassword.setTypeface(mySingelton.myCustomTypeface);

        signUpButton.setTypeface(mySingelton.myCustomTypeface);
        message1.setTypeface(mySingelton.myCustomTypeface);
        message2.setTypeface(mySingelton.myCustomTypeface);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        Toolbar myToolbar = (Toolbar)findViewById(R.id.signUpAppBar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.signUpText));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        glassyImage = (ImageView)findViewById(R.id.glassImageView);
        MoinUtils.getReference().setGlassyBitmap(glassyImage);

//        username = (EditText)findViewById(R.id.newSignUpUsername);
        emailId = (EditText)findViewById(R.id.newSignUpUseremail);
        password = (EditText)findViewById(R.id.newsignUpPasswordEditText);
        confirmPassword = (EditText)findViewById(R.id.newsignUpConfirmPasswordEditText);

        passwordContainer = findViewById(R.id.passwordContainer);

        passwordContainerParams = passwordContainer.getLayoutParams();
        passwordContainer.setLayoutParams(new LinearLayout.LayoutParams(0,0));

//        password.setEnabled(false);
//        confirmPassword.setEnabled(false);
//        password.setFocusable(false);
//        confirmPassword.setFocusable(false);

        signUpButton = (Button)findViewById(R.id.signUpButton);
        signUpButton.setText("Check Availability");

        loadingView = findViewById(R.id.loadignView);
        loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.loadingIndicator);
        hideLoadingView();

        alreayHaveAccountView = findViewById(R.id.alreayHaveAccount);
        alreayHaveAccountView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setFonts();
    }

    private void showPasswordEditText(){
        passwordContainer.setLayoutParams(passwordContainerParams);
//        password.setEnabled(true);
//        confirmPassword.setEnabled(true);
//        password.setFocusable(true);
//        confirmPassword.setFocusable(true);



        emailId.setEnabled(false);
        emailId.setFocusable(false);
        emailId.setTextColor(getResources().getColor(R.color.m0in_grey));
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

    public void validateAndSignUp(View view) {

        if (signUpCheck == 0){

            if (emailId.getText().toString().length() == 0){
                showMessage(getResources().getString(R.string.blankEmailId));
                emailId.requestFocus();
                emailId.append("");
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailId.getText().toString()).matches()){
                showMessage(getResources().getString(R.string.invalidEmail));
                return;
            }

            checkPresenceInServer();
        }else{
            if (emailId.getText().toString().length() == 0){
                showMessage(getResources().getString(R.string.blankEmailId));
                emailId.requestFocus();
                emailId.append("");
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailId.getText().toString()).matches()){
                showMessage(getResources().getString(R.string.invalidEmail));
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

//            showMessage("Everything OK");

            Intent myIntent = new Intent(SignUp.this,SignUpAdditionalDataPartOne.class);
            myIntent.putExtra("emailIdPassed",emailId.getText().toString());
            myIntent.putExtra("passwordIdPassed",password.getText().toString());
            startActivity(myIntent);
            overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);

        }
    }

    private void checkPresenceInServer(){
        JSONObject params = new JSONObject();
        try {
            params.put("EmailId", emailId.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest checkEmailPresence = new JsonObjectRequest(Request.Method.POST, mySingelton.CheckEmailPresence, params,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("Status");
                            String message = response.getString("ErrorMessage");
                            if (status.equalsIgnoreCase("Ok")) {
                                //
                                signUpButton.setText("Continue");
                                showPasswordEditText();
                                hideLoadingView();
                                signUpCheck = 1;
                            } else {
                                hideLoadingWithMessage(message);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            hideLoadingWithMessage("There was some problem please try again");
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoadingWithMessage("You Are Offline");
                    }
                }
        );
        showLoadingView();
        RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        checkEmailPresence.setRetryPolicy(policy);
        VolleySingelton.getMy_Volley_Singelton_Reference().getRequestQueue().add(checkEmailPresence);
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
        MoinUtils.getReference().showMessage(SignUp.this, msg);
    }

    private void showMessage(String str){
        MoinUtils.getReference().showMessage(SignUp.this,str);
    }
}
