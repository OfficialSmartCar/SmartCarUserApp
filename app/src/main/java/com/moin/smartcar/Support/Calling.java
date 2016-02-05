package com.moin.smartcar.Support;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.moin.smartcar.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Calling extends AppCompatActivity {

    @Bind(R.id.img3)ImageView img3;

    @OnClick(R.id.cell1)void clickImg1(View view){
        initiateCall(getResources().getString(R.string.number1));
    }

    @OnClick(R.id.cell2)void clickImg2(View view){
        initiateCall(getResources().getString(R.string.number2));
    }

    @OnClick(R.id.cell3)void clickImg3(View view){
        initiateCall(getResources().getString(R.string.number3));
    }

    private void initiateCall(String str){
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",str, null));
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);
        ButterKnife.bind(this);

        Toolbar myToolbar = (Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Call");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewGroup.LayoutParams params = img3.getLayoutParams();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        params.width = width/2;

        img3.setLayoutParams(params);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.scaleincrease, R.anim.slide_right_out);
    }


}
