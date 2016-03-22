package com.moin.smartcar.LoginSignUp;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
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
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.plus.Plus;
//import com.google.android.gms.plus.model.people.Person;
import com.moin.smartcar.Database.DatabaseManager;
import com.moin.smartcar.HomePage;
import com.moin.smartcar.Network.VolleySingelton;
import com.moin.smartcar.Notification.SetUp.QuickstartPreferences;
import com.moin.smartcar.Notification.SetUp.RegistrationIntentService;
import com.moin.smartcar.R;
import com.moin.smartcar.SingeltonData.DataSingelton;
import com.moin.smartcar.User.CarInfoStr;
import com.moin.smartcar.Utility.MoinUtils;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LoginNew extends AppCompatActivity {

    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private int backPressed = 0;
    private Runnable task;
    private EditText emailIdEditTExt, passwordEdittext;
    private Button loginButton;
    private TextView signUpTextView,rememeberMeLabel,forgotPasswordTextView,textView4;
    private View loadingView;
    private AVLoadingIndicatorView loadingIndicator;
    private ImageView rememberMeCheckBox;
    private Boolean rememberMeValue;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();

    private CallbackManager callbackManager;
    private ImageView FBLOginImage;


    //for Google Plus SignIn
    private SignInButton signInButton;
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 0;
    private static GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private boolean mIntentInProgress;
    private boolean mShouldResolve;
    private ConnectionResult connectionResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);

        getAllDataForNotifications();


        emailIdEditTExt = (EditText) findViewById(R.id.newLoginUsername);
        passwordEdittext = (EditText) findViewById(R.id.newLoginPasswordEditText);
        rememeberMeLabel = (TextView)findViewById(R.id.rememeberMeLabel);
        textView4 = (TextView)findViewById(R.id.textView4);
        forgotPasswordTextView = (TextView)findViewById(R.id.forgotPasswordTextView);
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

        // initialize facebookSignIn
        FacebookSdk.sdkInitialize(LoginNew.this);
        FBLOginImage = (ImageView)findViewById(R.id.imageView10);
        initializeFacebookSignIn();
        FBLOginImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                LoginManager.getInstance().logInWithReadPermissions(LoginNew.this, Arrays.asList("public_profile", "user_friends", "email"));
            }
        });

        //google signIn
//        signInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
//        signInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                google_signIn();
//            }
//        });
//        signInButton.setSize(SignInButton.SIZE_STANDARD);
////        initializeGoogleSignIn();
//        buildGoogleApiClient();
//        mGoogleApiClient.connect();
        setFonts();
    }

    private void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(Plus.API)
//                .addScope(Plus.SCOPE_PLUS_LOGIN)
//                .build();
//        mGoogleApiClient.connect();
    }

    private void setFonts(){

        signUpTextView.setTypeface(mySingelton.myCustomTypeface);
        emailIdEditTExt.setTypeface(mySingelton.myCustomTypeface);
        passwordEdittext.setTypeface(mySingelton.myCustomTypeface);
        rememeberMeLabel.setTypeface(mySingelton.myCustomTypeface);
        loginButton.setTypeface(mySingelton.myCustomTypeface);
        forgotPasswordTextView.setTypeface(mySingelton.myCustomTypeface);
        textView4.setTypeface(mySingelton.myCustomTypeface);
    }

    private void initializeFacebookSignIn(){
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Login");
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.v("LoginActivity", response.toString());

                                        // Application code
                                        try {
                                            String email = object.getString("email");
                                            emailIdEditTExt.setText("" + email);
                                            mySingelton.key = "passkey@123_m";
//                                            checkPresenceInServer();
                                            performLogin(email, "passkey@123_m");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender, birthday");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginNew.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginNew.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void checkPresenceInServer(){
        JSONObject params = new JSONObject();
        try {
            params.put("EmailId", emailIdEditTExt.getText().toString());
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
                                Intent myIntent = new Intent(LoginNew.this,SignUpAdditionalDataPartOne.class);
                                myIntent.putExtra("emailIdPassed",emailIdEditTExt.getText().toString());
                                myIntent.putExtra("passwordIdPassed","passkey@123_m");
                                startActivity(myIntent);
                                overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
                            } else {
                                hideLoadingWithMessage(message);
                            }
                            hideLoadingView();
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

    protected void onStop() {
        super.onStop();
//        if (mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }
    }

    private void initializeGoogleSignIn(){

//        mGoogleApiClient = new GoogleApiClient.Builder(LoginNew.this)
//                .addConnectionCallbacks(LoginNew.this)
//                .addOnConnectionFailedListener(LoginNew.this)
//                .addApi(Plus.API)
//                .addScope(Plus.SCOPE_PLUS_LOGIN)
//                .build();
//        mGoogleApiClient.connect();
    }

    private void resolveSignInError() {
//        if (connectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                connectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        mGoogleApiClient.connect();
    }

    private void google_signIn() {
            resolveSignInError();
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS){
            buildGoogleApiClient();
            if (!mGoogleApiClient.isConnecting()){
                mGoogleApiClient.connect();
            }
        }
        else {

        }
    }


    private void getAllDataForNotifications() {
        PrintHashKey();
        LoadNotificationsDetails();
    }

    public void PrintHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.moin.smartcar",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }


    private void LoadNotificationsDetails() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
//                    mInformationTextView.setText(getString(R.string.gcm_send_message));
                } else {
//                    mInformationTextView.setText(getString(R.string.token_error_message));
                }
            }
        };

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("error", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
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

        mySingelton.key = passwordEdittext.getText().toString();
        performLogin(emailIdEditTExt.getText().toString(),passwordEdittext.getText().toString());
    }

    private void performLogin(final String useremailId, final String key){
        JSONObject params = new JSONObject();
        try {
            params.put("EmailId", useremailId);
            params.put("Password", key);
            params.put("userNotificationId", mySingelton.UserNotificationToken);
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
                                saveUserInfoToDB(response,key);
                                hideLoadingView();
                            } else {
                                if (key.equalsIgnoreCase("passkey@123_m")){
                                    checkPresenceInServer();
                                }else{
                                    hideLoadingWithMessage(message);
                                }
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

    private void saveUserInfoToDB(JSONObject response, final String key) {
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
                myStr.isPremium = Integer.parseInt(myObj.getString("isPremium"));
                mySingelton.userCarList.add(myStr);
            }
        } catch (Exception e) {
            showMessage("Error In Server Data " + e.getMessage());
        }

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (rememberMeValue || key.equalsIgnoreCase("passkey@123_m")) {
                        DatabaseManager db = new DatabaseManager(LoginNew.this);
                        db.InsertIntoUserTable();
                        db.InsertIntoCarTables(mySingelton.userCarList);
                    }
                    hideLoadingView();
                    startActivity(new Intent(LoginNew.this, HomePage.class));
                    overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
                    killSelf();
                }
            }).run();
        } catch (Exception e) {
            showMessage("Error Saving");
        }
    }

    private void killSelf() {
        Runnable task23 = new Runnable() {
            @Override
            public void run() {
                finish();
            }
        };
        worker.schedule(task23, 2000, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
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
        mySingelton.notificationCount = 0;
        mySingelton.signUpOrAdd = "signUp";
        try {
            DatabaseManager db = new DatabaseManager(LoginNew.this);
            db.deleteAllCars();
            db.deleteUserInfo();
        } catch (Exception e) {

        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
        try {
            if (mySingelton.signUpSuccess.equalsIgnoreCase("Confirm")) {
                mySingelton.signUpSuccess = "";
                performLogin(mySingelton.userEmailId,mySingelton.key);
            }
        } catch (Exception e) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    public void navigateToAboutUsPage(View view) {
        startActivity(new Intent(LoginNew.this, AboutUs.class));
        overridePendingTransition(R.anim.activity_slide_right_in, R.anim.scalereduce);
    }

//    @Override
//    public void onConnectionFailed(ConnectionResult result) {
////        Log.d(TAG, "onConnectionFailed:" + connectionResult);
////        hideLoadingView();
////        MoinUtils.getReference().showMessage(LoginNew.this, "onConnectionFailed:" + connectionResult);
//        if (!result.hasResolution()) {
//            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
//                    0).show();
//            return;
//        }
//
//        if (!mIntentInProgress) {
//
//            connectionResult = result;
//
//            if (mShouldResolve) {
//
//                resolveSignInError();
//            }
//        }
//
//    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            MoinUtils.getReference().showMessage(LoginNew.this,"SignIn SUccessful");
        } else {
            MoinUtils.getReference().showMessage(LoginNew.this, "SignOut SUccessful");

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);

        try{
            if (requestCode == RC_SIGN_IN) {
                if (resultCode != RESULT_OK) {
                    mShouldResolve = false;
                }

                mIntentInProgress = false;

                if (!mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }

                try {
//                    if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
//                        Person person = Plus.PeopleApi
//                                .getCurrentPerson(mGoogleApiClient);
//                        String personName = person.getDisplayName();
//                        String personPhotoUrl = person.getImage().getUrl();
//                        String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
//
////                tvName.setText(personName);
//                        emailIdEditTExt.setText(email);
//
//                        Toast.makeText(getApplicationContext(),
//                                "You are Logged In " + personName,             Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(getApplicationContext(),
//                                "Couldnt Get the Person Info", Toast.LENGTH_SHORT).show();
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){

        }

        try{
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }catch (Exception e){

        }

    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (!result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
//            emailIdEditTExt.setText(getString(R.string.signed_in_fmt, acct.getEmail()));
//            emailIdEditTExt.setText(getString(R.string.loginText,acct.getEmail()));
            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

//    @Override
//    public void onConnected(Bundle bundle) {
//        mShouldResolve = false;
//        try {
//            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
//                Person person = Plus.PeopleApi
//                        .getCurrentPerson(mGoogleApiClient);
//                String personName = person.getDisplayName();
//                String personPhotoUrl = person.getImage().getUrl();
//                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
//
////                tvName.setText(personName);
//                emailIdEditTExt.setText(email);
//
//                Toast.makeText(getApplicationContext(),
//                        "You are Logged In " + personName,             Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(getApplicationContext(),
//                        "Couldnt Get the Person Info", Toast.LENGTH_SHORT).show();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        signOutUI();
//    }

    private void signOutUI() {
//        signInButton.setVisibility(View.GONE);
//        tvNotSignedIn.setVisibility(View.GONE);
//        signOutButton.setVisibility(View.VISIBLE);
//        viewContainer.setVisibility(View.VISIBLE);
    }

    private void signInUI() {
//        signInButton.setVisibility(View.VISIBLE);
//        tvNotSignedIn.setVisibility(View.VISIBLE);
//        signOutButton.setVisibility(View.GONE);
//        viewContainer.setVisibility(View.GONE);
    }

//    @Override
//    public void onConnectionSuspended(int i) {
//        mGoogleApiClient.connect();
//        signInUI();
//    }
}
