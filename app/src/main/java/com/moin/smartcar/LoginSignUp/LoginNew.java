package com.moin.smartcar.LoginSignUp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.moin.smartcar.Database.DatabaseManager;
import com.moin.smartcar.HomePage;
import com.moin.smartcar.Network.VolleySingelton;
import com.moin.smartcar.R;
import com.moin.smartcar.SingeltonData.DataSingelton;
import com.moin.smartcar.User.CarInfoStr;
import com.moin.smartcar.Utility.MoinUtils;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LoginNew extends AppCompatActivity {

    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();
    private int backPressed = 0;
    private Runnable task;
    private EditText emailIdEditTExt, passwordEdittext;
    private Button loginButton;
    private TextView signUpTextView;

    private View loadingView;
    private AVLoadingIndicatorView loadingIndicator;

    private ImageView rememberMeCheckBox;
    private Boolean rememberMeValue;

    private DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);



        emailIdEditTExt = (EditText) findViewById(R.id.newLoginUsername);
        passwordEdittext = (EditText) findViewById(R.id.newLoginPasswordEditText);

        loginButton = (Button) findViewById(R.id.newLoginButton);

        signUpTextView = (TextView) findViewById(R.id.newLoginSIgnUp);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndLogin();
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginNew.this, SignUp.class));
                overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
            }
        });

        loadingView = findViewById(R.id.loadignView);
        loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.loadingIndicator);
        hideLoadingView();

        rememberMeCheckBox = (ImageView) findViewById(R.id.rememberMeCheckBox);
        rememberMeValue = false;
        rememberMeCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rememberMeValue = !rememberMeValue;
                changeCheckBoxContent();
            }
        });

    }

    private void changeCheckBoxContent() {
        if (rememberMeValue) {
            rememberMeCheckBox.setImageResource(R.drawable.check1);
        } else {
            rememberMeCheckBox.setImageResource(R.drawable.check0);
        }
    }

    private void validateAndLogin() {

//        if (emailIdEditTExt.getText().toString().equalsIgnoreCase("Moin")) {
//            if (passwordEdittext.getText().toString().equalsIgnoreCase("moin")) {
//
//                Runnable task2 = new Runnable() {
//                    @Override
//                    public void run() {
//                        startActivity(new Intent(LoginNew.this, HomePage.class));
//                    }
//                };
//                worker.schedule(task2, 500, TimeUnit.MILLISECONDS);
//
//                return;
//            }
//        }

        if (emailIdEditTExt.getText().toString().length() == 0) {
            showMessage(getResources().getString(R.string.blankEmailId));
            emailIdEditTExt.requestFocus();
            emailIdEditTExt.append("");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailIdEditTExt.getText().toString()).matches()) {
            showMessage(getResources().getString(R.string.invalidEmail));
            emailIdEditTExt.requestFocus();
            emailIdEditTExt.append("");
            return;
        }

        if (passwordEdittext.getText().length() == 0) {
            showMessage(getResources().getString(R.string.blankPassword));
            passwordEdittext.requestFocus();
            passwordEdittext.append("");
            return;
        }

        JSONObject params = new JSONObject();
        try {
            params.put("EmailId", emailIdEditTExt.getText().toString());
            params.put("Password", passwordEdittext.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, DataSingelton.LoginUrl, params,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            String message = response.getString("ErrorMessage");
                            if (!status.equalsIgnoreCase("Error")) {
                                saveUserInfoToDB(response);
                            } else {
                                hideLoadingWithMessage(message);
                            }
                            hideLoadingView();
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
        loginRequest.setRetryPolicy(policy);
        VolleySingelton.getMy_Volley_Singelton_Reference().getRequestQueue().add(loginRequest);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (backPressed == 0) {
                backPressed = 1;
                showMessage(getResources().getString(R.string.exitMessage));
                task = new Runnable() {
                    public void run() {
                        backPressed = 0;
                    }
                };
                worker.schedule(task, 2, TimeUnit.SECONDS);
                return false;
            } else {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return false;
            }
        }

        return super.onKeyDown(keyCode, event);

    }

    private void saveUserInfoToDB(JSONObject response) {
        try {
            mySingelton.userCarList = new ArrayList<>();
            mySingelton.userName = response.getString("UserName");
            mySingelton.userId = response.getString("UserId");
            mySingelton.userEmailId = response.getString("EmailId");
            mySingelton.userImageLink = response.getString("ImageUrl");
            mySingelton.address = response.getString("Address");
            mySingelton.mobileNumber = response.getString("MobileNumber");

            JSONArray arr = response.getJSONArray("carList");
            for (int i = 0; i < arr.length(); i++) {
                CarInfoStr myStr = new CarInfoStr();
                JSONObject myObj = arr.getJSONObject(i);
                myStr.carName = myObj.getString("CarName");
                myStr.carBrand = myObj.getString("CarBrand");
                myStr.carModel = myObj.getString("CarModel");
                myStr.yearOfMaufacture = myObj.getString("YearOfManufacture");
                myStr.carRegNo = myObj.getString("CarRegNumber");
                myStr.carVariant = myObj.getString("Variant");
                myStr.carId = (myObj.getString("CarId"));
                mySingelton.userCarList.add(myStr);
            }
        } catch (Exception e) {
            showMessage("Error In Server Data " + e.getMessage());
        }

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (rememberMeValue) {
                        DatabaseManager db = new DatabaseManager(LoginNew.this);
                        db.deleteAllCars();
                        db.deleteUserInfo();
                        db.InsertIntoUserTable();
                        db.InsertIntoCarTables(mySingelton.userCarList);
                    }
                    hideLoadingView();
                    startActivity(new Intent(LoginNew.this, HomePage.class));
                    overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
                }
            }).run();
        } catch (Exception e) {
            showMessage("Error Saving");
        }
    }


    public void nacvigateToForgotPassword(View view) {
        startActivity(new Intent(LoginNew.this, ForgotPassword.class));
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
        MoinUtils.getReference().showMessage(LoginNew.this, msg);
    }

    private void showMessage(String msg) {
        MoinUtils.getReference().showMessage(LoginNew.this, msg);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            DatabaseManager db = new DatabaseManager(LoginNew.this);
            db.deleteAllCars();
            db.deleteUserInfo();
        } catch (Exception e) {

        }

    }
}
