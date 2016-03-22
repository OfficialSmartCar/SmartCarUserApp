package com.moin.smartcar.LoginSignUp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.lguipeng.library.animcheckbox.AnimCheckBox;
import com.moin.smartcar.Network.VolleySingelton;
import com.moin.smartcar.R;
import com.moin.smartcar.SingeltonData.DataSingelton;
import com.moin.smartcar.Utility.MoinUtils;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpAdditionalPartTwo extends AppCompatActivity {

    ArrayList<String> carBrand = new ArrayList<>();
    ArrayList<String> carModel = new ArrayList<>();
    ArrayList<String> carVariants = new ArrayList<>();
    AutoCompleteTextView brandAutocompleteTextView, modelAutocompleteTextView, variantAutoCompleteTextView;
    @Bind(R.id.appbar) Toolbar myToolbar;
    @Bind(R.id.CarNumberTextView) EditText carNumberEditText;
    @Bind(R.id.carNameTextView) EditText carNameTextView;
    @Bind(R.id.yearOfManufacture) EditText yearOfManufacture;
    @Bind(R.id.loadignView) View loadingView;
    @Bind(R.id.loadingIndicator)AVLoadingIndicatorView loadingIndicator;
    @Bind(R.id.termsAndConditionsCheckBox)AnimCheckBox checkBox;
    private String userName, useremail, password, mobilenumber, address;
    private ArrayAdapter<String> brandAdapter, modelAdapter, variantAdapter;
    private ArrayList<CarBrand> serverList = new ArrayList<>();

    private DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();

    @OnClick(R.id.termsAndConditionsTextView)void showTermsAndConditions(View view){
        String url = "ww.google.com";
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_additional_part_two);
        ButterKnife.bind(SignUpAdditionalPartTwo.this);

        checkBox.setChecked(true, false);
        mySingelton.signUpSuccess = "";
        userName = getIntent().getStringExtra("usernamePassed1");
        useremail = getIntent().getStringExtra("emailIdPassed1");
        password = getIntent().getStringExtra("passwordPassed1");
        mobilenumber = getIntent().getStringExtra("mobilenumberpassed1");
        address = getIntent().getStringExtra("addresspassed1");


        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Add Car");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        hideLoadingView();

        carVariants = new ArrayList<>();
        carVariants.add("Petrol");
        carVariants.add("Diesel");

        variantAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,carVariants);
        brandAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoComplete);
        modelAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id.modelAutoompleteTextFeild);


        variantAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.variantTextView);
        variantAutoCompleteTextView.setAdapter(variantAdapter);
        variantAutoCompleteTextView.setThreshold(1);

        modelAutocompleteTextView.setEnabled(false);

        brandAutocompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                modelAutocompleteTextView.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {
                getModelsForBrand();
                modelAutocompleteTextView.setEnabled(true);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataFromServer();
    }

    private void getDataFromServer(){
        DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();

        JsonObjectRequest getServcesList = new JsonObjectRequest(Request.Method.GET, DataSingelton.getBrandAndModels,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("Status");
                            String message = response.getString("ErrorMessage");

                            if (!status.equalsIgnoreCase("Error")){
                                parseServerResponse(response);
                            }else{
                                hideLoadingViewWithMessage(message);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            hideLoadingViewWithMessage("There was some problem please try again");
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoadingViewWithMessage("There was some problem please try again");
                    }
                }

        );
        showLoadingView();
        RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        getServcesList.setRetryPolicy(policy);
        VolleySingelton.getMy_Volley_Singelton_Reference().getRequestQueue().add(getServcesList);
    }

    private void parseServerResponse(JSONObject response) throws JSONException {
        serverList = new ArrayList<>();
        JSONArray arr = response.getJSONArray("arr");
        for (int i=0;i<arr.length();i++){
            JSONObject myObj = arr.getJSONObject(i);
            CarBrand myStr = new CarBrand();
            myStr.carBrand = myObj.getString("BrandName");
            myStr.carModelList = new ArrayList<>();
            JSONArray internalArray = myObj.getJSONArray("ModelArr");
            for (int j=0;j<internalArray.length();j++){
                CarModel myModel = new CarModel();
                JSONObject internalObject = internalArray.getJSONObject(i);
                myModel.isPremium = Integer.parseInt( internalObject.getString("isPremium"));
                myModel.carModel = internalObject.getString("ModelName");
                JSONArray variantArray = internalObject.getJSONArray("models");
                myModel.carVariantList = new ArrayList<>();
                for (int k = 0; k < variantArray.length(); k++) {
                    myModel.carVariantList.add(variantArray.get(k).toString());
                }
                myStr.carModelList.add(myModel);
            }
            serverList.add(myStr);
        }
        hideLoadingView();
        splitIntobrandArray();
    }

    private void splitIntobrandArray(){

        carBrand = new ArrayList<>();
        for (int i=0;i<serverList.size();i++){
            carBrand.add(serverList.get(i).carBrand);
        }

        brandAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,carBrand);
        brandAutocompleteTextView.setAdapter(brandAdapter);
        brandAutocompleteTextView.setThreshold(1);
    }

    private void getModelsForBrand(){
        String brand = brandAutocompleteTextView.getText().toString();
        try{
            int index = -1;
            carModel = new ArrayList<>();
            for (int i=0;i<serverList.size();i++){
                if (brand.equalsIgnoreCase(serverList.get(i).carBrand)){
                    index = i;
                    break;
                }
            }
            CarBrand myBrand = serverList.get(index);

            for (int i=0;i<myBrand.carModelList.size();i++){
                carModel.add(myBrand.carModelList.get(i).carModel);
            }

            modelAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,carModel);
            modelAutocompleteTextView.setAdapter(modelAdapter);
            modelAutocompleteTextView.setThreshold(1);
        }catch (Exception e){

        }
    }

    private int checkValidation(){

        if (carNameTextView.getText().toString().length() == 0){
            showErr("Please enter car name");
            return 0;
        }
        if(brandAutocompleteTextView.getText().toString().length() == 0){
            showErr("Please enter car brand");
            return 0;
        }
        if(modelAutocompleteTextView.getText().toString().length() == 0){
            showErr("Please enter car model");
            return 0;
        }
        if(variantAutoCompleteTextView.getText().toString().length() == 0){
            showErr("Please enter fuel type");
            return 0;
        }
        if (carNumberEditText.getText().toString().length() == 0){
            showErr("Please enter car registration number");
            return 0;
        }
        if (yearOfManufacture.getText().toString().length() == 0){
            showErr("Please enter car year of manufacture");
            return 0;
        }

        if (!checkBox.isChecked()){
            showErr("You need to accept the terms and conditions");
            return 0;
        }

        return 1;
    }

    @OnClick(R.id.SaveButton)void initiateRegistration(View view){
        if (checkValidation() == 0){
            return;
        }else{
            JSONObject params = new JSONObject();
            try {

                mySingelton.userEmailId = useremail;
                mySingelton.key = password;

                params.put("EmailId", useremail);
                params.put("Password", password);
                params.put("Address", address);
                params.put("MobileNumber", mobilenumber);
                params.put("Name", userName);
                params.put("carName", carNameTextView.getText().toString());
                params.put("carBrand", brandAutocompleteTextView.getText().toString());
                params.put("carModel", modelAutocompleteTextView.getText().toString());
                params.put("yearOfManufacture", yearOfManufacture.getText().toString());
                params.put("carRegNumber", carNumberEditText.getText().toString());
                params.put("carVariant", variantAutoCompleteTextView.getText().toString());

                int isPremium = 0;
                for (int i=0;i<serverList.size();i++){
                    if (serverList.get(i).carBrand.equalsIgnoreCase(brandAutocompleteTextView.getText().toString())){
                        for (int j=0;j<serverList.get(i).carModelList.size();j++){
                            if (serverList.get(i).carModelList.get(j).carModel.equalsIgnoreCase(modelAutocompleteTextView.getText().toString())){
                                if (serverList.get(i).carModelList.get(j).isPremium == 1){
                                    isPremium = 1;
                                }
                            }
                        }
                    }
                }
                params.put("isPremium", isPremium);


            } catch (JSONException e) {
                e.printStackTrace();
            }


            JsonObjectRequest signUpRequest = new JsonObjectRequest(Request.Method.POST, DataSingelton.SignUpUrl, params,

                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String status = response.getString("Status");
                                String message = response.getString("ErrorMessage");
                                if (!status.equalsIgnoreCase("Error")) {
                                    hideLoadingViewWithMessage("SignUp Success");
                                    mySingelton.signUpSuccess = "Confirm";
                                    finish();
//                                    startActivity(new Intent(SignUpAdditionalPartTwo.this, LoginNew.class));
//                                    overridePendingTransition(R.anim.scaleincrease, R.anim.slide_right_out);
                                } else {
                                    hideLoadingViewWithMessage(message);
                                }
                                hideLoadingView();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                hideLoadingViewWithMessage("There was some problem please try again");
//                            showError("There Was Some Problem Please Try Again After Some Time");
                            }
                        }
                    },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            hideLoadingViewWithMessage("You Are Offline");
                        }
                    }
            );
            showLoadingView();
            RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            signUpRequest.setRetryPolicy(policy);
            VolleySingelton.getMy_Volley_Singelton_Reference().getRequestQueue().add(signUpRequest);
        }
    }

    private void showErr(String msg){
        MoinUtils.getReference().showMessage(this,msg);
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

    private void hideLoadingView() {
        loadingView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.GONE);
    }

    private void showLoadingView() {
        loadingView.setVisibility(View.VISIBLE);
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    private void hideLoadingViewWithMessage(String msg) {
        hideLoadingView();
        MoinUtils.getReference().showMessage(SignUpAdditionalPartTwo.this, msg);
    }

    private void showMessage(String msg) {
        MoinUtils.getReference().showMessage(SignUpAdditionalPartTwo.this, msg);
    }

    private class CarBrand {
        public String carBrand;
        public ArrayList<CarModel> carModelList;
    }

    private class CarModel {
        public String carModel;
        public int isPremium;
        public ArrayList<String> carVariantList;

        public CarModel(){
            this.isPremium = 0;
        }
    }
}
