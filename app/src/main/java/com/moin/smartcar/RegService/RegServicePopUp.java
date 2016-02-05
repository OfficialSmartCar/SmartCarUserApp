package com.moin.smartcar.RegService;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.moin.smartcar.R;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegServicePopUp extends Fragment {

    private FragmentManager myFragmentManager;
    private TextView datePickerTextView;
    private Button closeButton,bookButton;
    private Context myContext;
    regularserviceInterface my_regularserviceInterface;
    private ScrollView myScrollView;


    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date)
        {
//            Toast.makeText(SampleActivity.this,
//                    mFormatter.format(date), Toast.LENGTH_SHORT).show();
            Log.d("asd", date + "");

            String time = date.getTime()+"";
            String temp = date.toGMTString();

            Log.d("asd","asd");

            String str = date+"";
            String[] arr = str.split(" ");
            String Day = arr[0];
            String Month = arr[1];
            String Date1 = arr[2];
            String Time = arr[3];
            String Year = arr[5];
            String invalid = arr[4];

            String finalstr =  Date1 +"/" + Month + "/" + Year +"  " +Time;
            datePickerTextView.setText(finalstr);
        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel()
        {

        }
    };

    public void setMy_regularserviceInterface(regularserviceInterface my_regularserviceInterface) {
        this.my_regularserviceInterface = my_regularserviceInterface;
    }

    public void setMyFragmentManager(FragmentManager myFragmentManager) {
        this.myFragmentManager = myFragmentManager;
    }

    public void setMyContext(Context myContext) {
        this.myContext = myContext;
    }

    public RegServicePopUp() {
        // Required empty public constructor
    }

    public static RegServicePopUp getInstance(){
        return new RegServicePopUp();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reg_service_pop_up, container, false);

        datePickerTextView = (TextView)view.findViewById(R.id.datePickerView);
        datePickerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SlideDateTimePicker.Builder(myFragmentManager)
                        .setListener(listener)
                        .setInitialDate(new Date())
                        .setMinDate(new Date())
                                //.setMaxDate(maxDate)
                        .setIs24HourTime(false)
                                //.setTheme(SlideDateTimePicker.HOLO_DARK)
//                                .setIndicatorColor(Color.parseColor("#990000"))
                        .build()
                        .show();
            }
        });

        closeButton = (Button)view.findViewById(R.id.closebutton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeTheFragment();
            }
        });

        bookButton = (Button)view.findViewById(R.id.bookButton);
        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateBooking();
            }
        });

        myScrollView = (ScrollView)view.findViewById(R.id.mainScrollview);

        return view;
    }

    public void ScrollToTop(){
        myScrollView.scrollTo(0,-100);
    }

    private void closeTheFragment(){
        my_regularserviceInterface.closeTheFragment();
    }

    private void initiateBooking(){
        my_regularserviceInterface.initiateBooking(datePickerTextView.getText().toString());
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private TextView passedTextView;

        @SuppressLint("ValidFragment")
        public DatePickerFragment(TextView someTextView){
            this.passedTextView = someTextView;
        }

        public DatePickerFragment(){

        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            passedTextView.setText(""+day+"/"+(month+1)+"/"+year);

        }
    }

    public interface regularserviceInterface{
        public void closeTheFragment();
        public void initiateBooking(String dateTime);
    }


}
