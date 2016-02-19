package com.moin.smartcar.User;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

public class AddCarInfo extends AppCompatActivity {

    ArrayList<String> carBrand = new ArrayList<>();
    ArrayList<String> carModel = new ArrayList<>();
    ArrayList<String> carVariants = new ArrayList<>();
    AutoCompleteTextView brandAutocompleteTextView,modelAutocompleteTextView,variantAutoCompleteTextView;
    @Bind(R.id.appbar) Toolbar myToolbar;
    @Bind(R.id.CarNumberTextView) EditText carNumberEditText;
    @Bind(R.id.carNameTextView) EditText carNameTextView;
    @Bind(R.id.yearOfManufacture) EditText yearOfManufacture;
    private ArrayAdapter<String> brandAdapter, modelAdapter, variantAdapter;
    private CarInfoStr recievedCarInfo = null;
    private int indexSelection = -1;

    private View loadingView;
    private AVLoadingIndicatorView loadingIndicator;

    private ArrayList<CarBrand> serverList = new ArrayList<>();

    private Boolean newChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enterFromBottomAnimation();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car_info);

        ButterKnife.bind(this);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("New Car");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingView = findViewById(R.id.loadignView);
        loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.loadingIndicator);
        hideLoadingView();
        hideLoadingView();

        newChange = true;

        carVariants = new ArrayList<>();
        carVariants.add("Petrol");
        carVariants.add("Diesel");
//
//        variantAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,carVariants);
//
        brandAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoComplete);
//
        modelAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id.modelAutoompleteTextFeild);

        variantAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.variantTextView);
//        variantAutoCompleteTextView.setAdapter(variantAdapter);
//        variantAutoCompleteTextView.setThreshold(1);

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

        modelAutocompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                variantAutoCompleteTextView.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {
                getVariantForModel();
                variantAutoCompleteTextView.setEnabled(true);
            }
        });

        yearOfManufacture.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 4) {
                    validateNumber();
                    carNumberEditText.requestFocus();
                    carNumberEditText.append("");
                    if (s.length() == 4) {
                        int number = Integer.parseInt(s.toString());
                        if (number < 1980 || number > 2030) {
                            yearOfManufacture.setText("");
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        carNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String[] arr = s.toString().split("-");

                if (arr.length > 1) {
                    if (s.toString().length() >= 13) {
                        variantAutoCompleteTextView.requestFocus();
                        variantAutoCompleteTextView.append("");
                    }
                } else {
                    if (s.toString().length() >= 10) {
                        variantAutoCompleteTextView.requestFocus();
                        variantAutoCompleteTextView.append("");
                    }
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        carNumberEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    changeTheDataInEditText();
                }
            }
        });


//        getVariantForModel

        try{
            Intent recieveingIntent = getIntent();
            recievedCarInfo = recieveingIntent.getParcelableExtra("carSelection");
            if (DataSingelton.getMy_SingeltonData_Reference().carSelectionIndex != -1){
                indexSelection = DataSingelton.getMy_SingeltonData_Reference().carSelectionIndex;
            }
            carNameTextView.setText(recievedCarInfo.carName);
            brandAutocompleteTextView.setText(recievedCarInfo.carBrand);
            modelAutocompleteTextView.setText(recievedCarInfo.carModel);
            yearOfManufacture.setText(recievedCarInfo.yearOfMaufacture);
            carNumberEditText.setText(recievedCarInfo.carRegNo);
            variantAutoCompleteTextView.setText(recievedCarInfo.carVariant);
            carNameTextView.requestFocus();
            carNameTextView.append("");

        }catch (Exception e){
            recievedCarInfo = null;
        }

    }

    private void changeTheDataInEditText() {

        try {
            String toCapCharacters = carNumberEditText.getText().toString().toUpperCase();
            toCapCharacters = toCapCharacters.replace(" ", "");
            toCapCharacters = toCapCharacters.replace("-", "");
            String part1 = toCapCharacters.subSequence(0, 2).toString();
            String part2 = toCapCharacters.subSequence(2, 4).toString();
            String part3 = toCapCharacters.subSequence(4, 6).toString();
            String part4 = toCapCharacters.substring(6, 10).toString();

            if (toCapCharacters.length() == 2 || toCapCharacters.length() == 5 || toCapCharacters.length() == 8) {
                toCapCharacters += "-";
            }

            carNumberEditText.setText(part1 + "-" + part2 + "-" + part3 + "-" + part4);

        } catch (Exception e) {
            showMessage("Please Enter Valid Registration Number");
        }


    }

    private void validateNumber() {
        try {
            int number1 = Integer.parseInt(yearOfManufacture.getText().toString());
            if (number1 > 9999) {
                yearOfManufacture.setText("");
            }
        } catch (Exception e) {

        }
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

    private void getVariantForModel() {
        String model = modelAutocompleteTextView.getText().toString();
        try {
            int index = -1;
            CarBrand myBrand = new CarBrand();
            CarModel myModel = new CarModel();
            for (int i = 0; i < serverList.size(); i++) {
                if (serverList.get(i).carBrand.equalsIgnoreCase(brandAutocompleteTextView.getText().toString())) {
                    myBrand = serverList.get(i);
                }
            }

            for (int i = 0; i < myBrand.carModelList.size(); i++) {
                if (myBrand.carModelList.get(i).carModel.equalsIgnoreCase(model)) {
                    myModel = myBrand.carModelList.get(i);
                }
            }
            carVariants = new ArrayList<>();

            for (int i = 0; i < myModel.carVariantList.size(); i++) {
                carVariants.add(myModel.carVariantList.get(i));
            }
            variantAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, carVariants);
            variantAutoCompleteTextView.setAdapter(variantAdapter);
            variantAutoCompleteTextView.setThreshold(1);
        } catch (Exception e) {
            Log.d("exc", "some exception");
        }
    }

//    private void getCarBrands(){
//        carBrand.add("SX4");
//        carBrand.add("Audi R8");
//        carBrand.add("BMW i8");
//        carBrand.add("Lamborgini Gallardo");
//        carBrand.add("Mustang GT 500");
//        carBrand.add("Pagani");
//    }
//
//    private void getCarModels(){
//        carModel.add("Red");
//        carModel.add("Green");
//        carModel.add("Blue");
//        carModel.add("Grey");
//        carModel.add("Black");
//    }
//


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
//                            showError("There Was Some Problem Please Try Again After Some Time");
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

    @OnClick(R.id.SaveButton)void inttiateSaveButton(){
//        if (autoComplete.getText().toString().length() == 0){
//            autoComplete.requestFocus();
//            autoComplete.append("");
//            showErr("Please Enter Car Name");
//            return;
//        }
//
//        if (carNumberEditText.getText().toString().length() == 0){
//            carNumberEditText.requestFocus();
//            carNumberEditText.append("");
//            showErr("Please Enter Registration Number");
//            return;
//        }
//
//        if (carBarndEditText.getText().toString().length() == 0){
//            carBarndEditText.requestFocus();
//            carBarndEditText.append("");
//            showErr("Please Enter Car Brand");
//            return;
//        }
//        if (carTypeEditText.getText().toString().length() == 0){
//            carTypeEditText.requestFocus();
//            carTypeEditText.append("");
//            showErr("Please Enter Car Type");
//            return;
//        }
//        if (colorAutoCompleteTextView.getText().toString().length() == 0){
//            colorAutoCompleteTextView.requestFocus();
//            colorAutoCompleteTextView.append("");
//            showErr("Please Enter Car Color");
//            return;
//        }

        int check = checkValidation();
        if (check == 0){
            return;
        }else{
            DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();

            if(recievedCarInfo != null){
                mySingelton.userCarList.get(indexSelection).carName = carNameTextView.getText().toString();
                mySingelton.userCarList.get(indexSelection).carBrand = brandAutocompleteTextView.getText().toString();
                mySingelton.userCarList.get(indexSelection).carModel = modelAutocompleteTextView.getText().toString();
                mySingelton.userCarList.get(indexSelection).yearOfMaufacture = yearOfManufacture.getText().toString();
                mySingelton.userCarList.get(indexSelection).carRegNo = carNumberEditText.getText().toString();
                mySingelton.userCarList.get(indexSelection).carVariant = variantAutoCompleteTextView.getText().toString();
                if (mySingelton.userCarList.get(indexSelection).status == 1){
                    mySingelton.userCarList.get(indexSelection).status = 1;
                }else{
                    mySingelton.userCarList.get(indexSelection).status = 2;
                }
            }else{
                CarInfoStr myStr = new CarInfoStr();
                myStr.carName = carNameTextView.getText().toString();
                myStr.carBrand = brandAutocompleteTextView.getText().toString();
                myStr.carModel = modelAutocompleteTextView.getText().toString();
                myStr.yearOfMaufacture = yearOfManufacture.getText().toString();
                myStr.carRegNo = carNumberEditText.getText().toString();
                myStr.carVariant = variantAutoCompleteTextView.getText().toString();
                myStr.status = 1;
                DataSingelton.getMy_SingeltonData_Reference().userCarList.add(myStr);
            }
        }
        finish();
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
            showErr("Please enter car variant");
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
        if (carNumberEditText.getText().toString().length() != 13) {
            showErr("Please enter valid Reg Number");
            return 0;
        }
        return 1;
    }



    private void showErr(String msg){
        MoinUtils.getReference().showMessage(this,msg);
    }

    protected void enterFromBottomAnimation(){
        overridePendingTransition(R.anim.activity_open_translate_from_bottom, R.anim.activity_no_animation);
    }

    protected void exitToBottomAnimation(){
        overridePendingTransition(R.anim.activity_no_animation, R.anim.activity_close_translate_to_bottom);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        DataSingelton.getMy_SingeltonData_Reference().carSelectionIndex = -1;
        exitToBottomAnimation();
        super.finish();
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
        MoinUtils.getReference().showMessage(AddCarInfo.this, msg);
    }

    private void showMessage(String msg) {
        MoinUtils.getReference().showMessage(AddCarInfo.this, msg);
    }

    private class CarBrand {
        public String carBrand;
        public ArrayList<CarModel> carModelList;
    }

    private class CarModel {
        public String carModel;
        public ArrayList<String> carVariantList;
    }
}
