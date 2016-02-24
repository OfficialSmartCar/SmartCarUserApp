package com.moin.smartcar.Support;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.moin.smartcar.Booking.BookingSuccess;
import com.moin.smartcar.Network.VolleySingelton;
import com.moin.smartcar.R;
import com.moin.smartcar.SingeltonData.DataSingelton;
import com.moin.smartcar.Utility.MoinUtils;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContactUs extends AppCompatActivity {

    @Bind(R.id.phoneNumberEditText)EditText phoneNumber;
    @Bind(R.id.alternatePhoneNumberEditText)EditText alternatePhoneNumber;
    @Bind(R.id.emailEditText)EditText emailEditText;
    @Bind(R.id.mainQueryEditText)EditText mainQueryEditText;


    @Bind(R.id.loadignView)
    View loadingView;
    @Bind(R.id.loadingIndicator)
    AVLoadingIndicatorView loadingIndicator;

    @Bind(R.id.img3)
    ImageView img3;


    private DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        ButterKnife.bind(this);

        mySingelton.successWebView = new WebView(this);
        mySingelton.successWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mySingelton.successWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (Build.VERSION.SDK_INT >= 11) {
            mySingelton.successWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        mySingelton.successWebView.getSettings().setJavaScriptEnabled(true);
        mySingelton.successWebView.loadUrl("file:///android_asset/index.html");

        Toolbar myToolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Contact Us");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        phoneNumber.setText(mySingelton.mobileNumber);
        emailEditText.setText(mySingelton.userEmailId);
        phoneNumber.setEnabled(false);
        emailEditText.setEnabled(false);

        hideLoading();

        ViewGroup.LayoutParams params = img3.getLayoutParams();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        params.width = width / 2;

        img3.setLayoutParams(params);

        alternatePhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 10) {
                    mainQueryEditText.requestFocus();
                    mainQueryEditText.append("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @OnClick(R.id.submitBUtton)
    void submitclicked(View view) {
        if (mainQueryEditText.getText().toString().length() == 0) {
            MoinUtils.getReference().showMessage(ContactUs.this, "Please Enter Your Query");
            return;
        }
        JSONObject params = new JSONObject();
        try {
            params.put("phoneNumber", DataSingelton.getMy_SingeltonData_Reference().mobileNumber);
            params.put("alternateNumber", alternatePhoneNumber.getText().toString());
            params.put("emailId", DataSingelton.getMy_SingeltonData_Reference().userEmailId);
            params.put("mainQuery", mainQueryEditText.getText().toString());
            params.put("userId", DataSingelton.getMy_SingeltonData_Reference().userId);
            params.put("userId", DataSingelton.getMy_SingeltonData_Reference().userId);
            params.put("userNotificationId", DataSingelton.getMy_SingeltonData_Reference().UserNotificationToken);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest getNotificationList = new JsonObjectRequest(Request.Method.POST, DataSingelton.getMy_SingeltonData_Reference().supportUrl, params,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("Status");
                            String message = response.getString("ErrorMessage");
                            if (!status.equalsIgnoreCase("Error")) {
                                hideLoading();
                                navigateToSuccessScreen();
                            } else {
                                hideLoadingWithMessage(message);
                            }
                            hideLoading();
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
        getNotificationList.setRetryPolicy(policy);
        VolleySingelton.getMy_Volley_Singelton_Reference().getRequestQueue().add(getNotificationList);
    }

    private void navigateToSuccessScreen() {
        Intent myIntent = new Intent(ContactUs.this, BookingSuccess.class);
        myIntent.putExtra("sourceIsContactUs", "YES");
        startActivity(myIntent);
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

    private void hideLoadingView() {
        loadingView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.GONE);
    }

    private void showLoadingView() {

        loadingView.setVisibility(View.VISIBLE);
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    private void hideLoadingWithMessage(String msg) {
        hideLoading();
        MoinUtils.getReference().showMessage(ContactUs.this, msg);
    }

    private void hideLoading() {
        try {
            hideLoadingView();

        } catch (Exception e) {
        }
    }
}
