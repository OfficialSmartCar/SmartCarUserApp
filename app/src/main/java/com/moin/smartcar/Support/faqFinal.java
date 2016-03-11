package com.moin.smartcar.Support;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.moin.smartcar.R;
import com.moin.smartcar.SingeltonData.DataSingelton;

import butterknife.Bind;
import butterknife.ButterKnife;

public class faqFinal extends AppCompatActivity {

    @Bind(R.id.questionTextFeild)TextView questionTextView;
    @Bind(R.id.andwerTextFeild)TextView answerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq_final);
        ButterKnife.bind(this);

        Toolbar myToolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("FAQ");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent recieveingIntent = getIntent();
        questionTextView.setText(recieveingIntent.getStringExtra("faqQuestion"));
        answerTextView.setText(recieveingIntent.getStringExtra("faqAnswer"));


        questionTextView.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);
        answerTextView.setTypeface(DataSingelton.getMy_SingeltonData_Reference().myCustomTypeface);

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
}
