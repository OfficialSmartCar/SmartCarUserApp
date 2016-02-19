package com.moin.smartcar.RegService;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.moin.smartcar.R;
import com.moin.smartcar.SingeltonData.DataSingelton;
import com.moin.smartcar.Utility.MoinUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegularServiceDetail extends AppCompatActivity {

    private static final TimeInterpolator sDecelerator = new DecelerateInterpolator();
    private static final TimeInterpolator sAccelerator = new AccelerateInterpolator();
    private static final String PACKAGE_NAME = MoinUtils.getReference().Package;
    private static final int ANIM_DURATION = 500;
    @Bind(R.id.mainContainer)View mainContainerView;
    @Bind(R.id.parent) View parentView;
    @Bind(R.id.title) TextView titleTextView;
    @Bind(R.id.content) TextView subTitleTextView;
    @Bind(R.id.taxTypeTextView) TextView taxType;
    @Bind(R.id.costTextVew) TextView costTextView;
    @Bind(R.id.closebutton1) View closeBackground;
    int mLeftDelta;
    int mTopDelta;
    float mWidthScale;
    float mHeightScale;

    private RegServiceStr myStr;

    private DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regular_service_detail);
        ButterKnife.bind(this);


        myStr = new RegServiceStr();
        myStr.TaskId = mySingelton.regServiceTaskId;
        myStr.NameOfTask = mySingelton.regServicetitle;
        myStr.InfoOfTask = mySingelton.regServicesubTitle;
        myStr.CostOfTask = mySingelton.regServiceCost;
        myStr.TaxType = mySingelton.regServiceTaxType;
        myStr.TaxPercentage = mySingelton.regServiceTaxPercentage;

        Bundle bundle = getIntent().getExtras();

        final int thumbnailTop = bundle.getInt(PACKAGE_NAME + ".top");
        final int thumbnailLeft = bundle.getInt(PACKAGE_NAME + ".left");
        final int thumbnailWidth = bundle.getInt(PACKAGE_NAME + ".width");
        final int thumbnailHeight = bundle.getInt(PACKAGE_NAME + ".height");

        titleTextView.setText(myStr.NameOfTask);
        subTitleTextView.setText(myStr.InfoOfTask);
        taxType.setText(myStr.TaxType + " : " + myStr.TaxPercentage + "%");
        costTextView.setText("Rs." + myStr.CostOfTask + "");

        if (savedInstanceState == null) {
            ViewTreeObserver observer = mainContainerView.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    mainContainerView.getViewTreeObserver().removeOnPreDrawListener(this);

                    // Figure out where the thumbnail and full size versions are, relative
                    // to the screen and each other
                    int[] screenLocation = new int[2];
                    mainContainerView.getLocationOnScreen(screenLocation);
                    mLeftDelta = thumbnailLeft - screenLocation[0];
                    mTopDelta = thumbnailTop - screenLocation[1];

                    // Scale factors to make the large version the same size as the thumbnail
                    mWidthScale = (float) thumbnailWidth / mainContainerView.getWidth();
                    mHeightScale = (float) thumbnailHeight / mainContainerView.getHeight();

                    runEnterAnimation();

                    return true;
                }
            });
        }
    }

    private void runEnterAnimation(){
        final long duration = (long) (ANIM_DURATION * 1);

        mainContainerView.setPivotX(0);
        mainContainerView.setPivotY(0);
        mainContainerView.setScaleX(mWidthScale);
        mainContainerView.setScaleY(mHeightScale);
        mainContainerView.setTranslationX(mLeftDelta);
        mainContainerView.setTranslationY(mTopDelta);
        closeBackground.setAlpha(0);

        mainContainerView.animate().setDuration(duration).
                scaleX(1).scaleY(1).
                translationX(0).translationY(0).
                setInterpolator(sDecelerator).
                withEndAction(new Runnable() {
                    public void run() {
                        // Animate the description in after the image animation
                        // is done. Slide and fade the text in from underneath
                        // the picture.
                        closeBackground.setTranslationY(-closeBackground.getHeight());
                        closeBackground.animate().setDuration(duration/2).
                                translationY(0).alpha(1).
                                setInterpolator(sDecelerator);
                    }
                });

        ObjectAnimator colorizer = ObjectAnimator.ofFloat(RegularServiceDetail.this,
                "saturation", 0, 1);
        colorizer.setDuration(duration);
        colorizer.start();

    }


    @Override
    public void onBackPressed() {
        finish();
    }

    @OnClick(R.id.closeButtonDetail) void close(View view){
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

}
