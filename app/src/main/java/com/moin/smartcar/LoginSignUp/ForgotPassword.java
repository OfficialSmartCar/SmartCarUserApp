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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class ForgotPassword extends AppCompatActivity {

    private EditText emailIdEditTex;

    private ImageView glassImage;
    private View loadingView;
    private AVLoadingIndicatorView loadingIndicator;

    @Bind(R.id.messageTextView1)TextView messageTextView1;
    @Bind(R.id.sendButton)Button sendButton;
    @Bind(R.id.messageTextView2)TextView messageTextView2;

    private void setFonts(){
        messageTextView1.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
        emailIdEditTex.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
        sendButton.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
        messageTextView2.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ButterKnife.bind(this);

        DataSingelton.getMy_SingeltonData_Reference().passwordReset = 0;
        DataSingelton.getMy_SingeltonData_Reference().forgotPasswordEmailId = "";

        loadingView = findViewById(R.id.loadignView);
        loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.loadingIndicator);
        hideLoadingView();

        Toolbar myToolbar = (Toolbar)findViewById(R.id.forgotPasswordAppBar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        glassImage = (ImageView)findViewById(R.id.glassImageView);
        MoinUtils.getReference().setGlassyBitmap(glassImage);

        emailIdEditTex = (EditText)findViewById(R.id.forgotPasswordEmailEditText);

        setFonts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (DataSingelton.getMy_SingeltonData_Reference().passwordReset == 1){
            DataSingelton.getMy_SingeltonData_Reference().passwordReset = 0;
            finish();
        }
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

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailIdEditTex.getText().toString()).matches()){
            MoinUtils.getReference().showMessage(ForgotPassword.this,"Entered Email Id Is Invalid");
            emailIdEditTex.requestFocus();
            emailIdEditTex.append("");
            return;
        }


        JSONObject params = new JSONObject();
        try {
            params.put("EmailId", emailIdEditTex.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest forgotPasswordRequest = new JsonObjectRequest(Request.Method.POST, DataSingelton.forgotPasswordURL, params,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("Status");
                            String message = response.getString("ErrorMessage");
                            if (!status.equalsIgnoreCase("Error")) {
                                DataSingelton.getMy_SingeltonData_Reference().forgotPasswordEmailId = emailIdEditTex.getText().toString();
                                hideLoadingView();
                                startActivity(new Intent(ForgotPassword.this,ResetPassword.class));
                                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
                                //navigate

                            } else {
                                hideLoadingWithMessage(message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            hideLoadingWithMessage("There was some problem please try again");
//                            showError("There Was Some Problem Please Try Again After Some Time");
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
        forgotPasswordRequest.setRetryPolicy(policy);
        VolleySingelton.getMy_Volley_Singelton_Reference().getRequestQueue().add(forgotPasswordRequest);

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
        MoinUtils.getReference().showMessage(ForgotPassword.this, msg);
    }

    private void showMessage(String msg) {
        MoinUtils.getReference().showMessage(ForgotPassword.this, msg);
    }
}
