package com.moin.smartcar.LoginSignUp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ResetPassword extends AppCompatActivity {

    private ImageView glassImage;
    private EditText verificationCode,password,confirmPassword;
    private View loadingView;
    private AVLoadingIndicatorView loadingIndicator;
    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();

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

        loadingView = findViewById(R.id.loadignView);
        loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.loadingIndicator);
        hideLoadingView();
    }

    public void verifyTheCodeAndPasswords(View view) {

        if (verificationCode.getText().toString().length() == 0){
            showMessage(getResources().getString(R.string.blankVerificationCOde));
            verificationCode.requestFocus();
            verificationCode.append("");
            return;
        }

        if (password.getText().toString().length() == 0){
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

        JSONObject params = new JSONObject();
        try {
            params.put("EmailId", DataSingelton.getMy_SingeltonData_Reference().forgotPasswordEmailId);
            params.put("tempPassword", verificationCode.getText().toString());
            params.put("newPassword", password.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest forgotPasswordRequest = new JsonObjectRequest(Request.Method.POST, DataSingelton.resetPassword, params,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("Status");
                            String message = response.getString("ErrorMessage");
                            if (!status.equalsIgnoreCase("Error")) {
                                hideLoadingView();
                                DataSingelton.getMy_SingeltonData_Reference().passwordReset = 1;
                                showMessage("Password Successfully Reset");
                                closeCurrentActivity();
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

    private void showMessage(String str){
        MoinUtils.getReference().showMessage(ResetPassword.this, str);
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
        MoinUtils.getReference().showMessage(ResetPassword.this, msg);
    }

    private void closeCurrentActivity(){
        Runnable task1 = new Runnable() {
            @Override
            public void run() {
                finish();
            }
        };

        worker.schedule(task1,1500, TimeUnit.MILLISECONDS);

    }

}
