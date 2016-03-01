package com.moin.smartcar.MyBookings;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.moin.smartcar.AMC.AMCListing;
import com.moin.smartcar.DentPaint.DentingAndPainting;
import com.moin.smartcar.LoginSignUp.LoginNew;
import com.moin.smartcar.NavDrawer.NavigationDrawerRecyclerViewAdapter;
import com.moin.smartcar.Notification.NotificationHome;
import com.moin.smartcar.OwnServices.MyOwnService;
import com.moin.smartcar.R;
import com.moin.smartcar.RegService.RegularServiceListing;
import com.moin.smartcar.ReportBreakdown.BreakdownCategory;
import com.moin.smartcar.Support.SupportHome;
import com.moin.smartcar.User.Profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class navUserBookings extends Fragment implements NavigationDrawerRecyclerViewAdapter.MyClickListener{

    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();
    public String file_Name = "file_name";
    public String Key_User_Learned_Drawer = "userLearnedDrawer";
    private String myTitle;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private boolean mUserLearnedDrawer;
    private boolean fromSavedState;
    private View containerView;
    private Runnable task;
    private RecyclerView recyclerView;
    private NavigationDrawerRecyclerViewAdapter myAdapter;
    private List<String> AllData = Collections.emptyList();


    public navUserBookings() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer = Boolean.valueOf(getSharedPreferences(getActivity(), Key_User_Learned_Drawer, "false"));
        if (savedInstanceState != null) {
            fromSavedState = true;
        }
    }

    public void refreshHeader() {
        myAdapter.notifyDataSetChanged();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nav_user_bookings, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        AllData = getData();
        myAdapter = new NavigationDrawerRecyclerViewAdapter(getActivity(), AllData);
        myAdapter.setMyClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(myAdapter);
        return view;
    }


    public void setSharedPreferences(Context context, String Key, String Value) {
        SharedPreferences sp = context.getSharedPreferences(file_Name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Key, Value);
        editor.apply();
    }

    public String getSharedPreferences(Context context, String Key, String Value) {
        SharedPreferences sp = context.getSharedPreferences(file_Name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        return (sp.getString(Key, Value)).toString();
    }

    private List<String> getData(){
        List<String> data = new ArrayList<>();

        String[] titles = getResources().getStringArray(R.array.navTabs);
        for (int i = 0; i < titles.length; i++) {
            data.add(titles[i%titles.length]);
        }

        return data;
    }

    @Override
    public void ItemClicked(View view, int position) {

        int navigated = 0;

        Runnable task_close_drawer = new Runnable() {
            @Override
            public void run() {

            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            }
        }).run();
//        mDrawerLayout.closeDrawer(Gravity.LEFT);

        switch (position) {
            case 0:
                if (!this.myTitle.toString().equalsIgnoreCase("My Bookings")) {
                    navigated = 1;

                    Runnable openActivity = new Runnable() {
                        @Override
                        public void run() {
                            Intent myIntent = new Intent(getActivity(),UserBookings.class);
                            myIntent.putExtra("check","1");
                            startActivity(new Intent(getActivity(), UserBookings.class));
                        }
                    };

                    worker.schedule(openActivity,400,TimeUnit.MILLISECONDS);

//                    getActivity().finish();
                }
                break;
            case 1:
                if (!this.myTitle.toString().equalsIgnoreCase("Regular Service")) {
                    navigated = 1;

                    Runnable openActivity = new Runnable() {
                        @Override
                        public void run() {
                            Intent myIntent = new Intent(getActivity(),RegularServiceListing.class);
                            myIntent.putExtra("check","1");
                            startActivity(myIntent);
                        }
                    };

                    worker.schedule(openActivity,400,TimeUnit.MILLISECONDS);


//                    getActivity().finish();
                }
                break;
            case 2:
                if (!this.myTitle.toString().equalsIgnoreCase(getResources().getString(R.string.customservice))) {
                    navigated = 1;

                    Runnable openActivity = new Runnable() {
                        @Override
                        public void run() {
                            Intent myIntent = new Intent(getActivity(),MyOwnService.class);
                            myIntent.putExtra("check","1");
                            startActivity(myIntent);
                        }
                    };
                    worker.schedule(openActivity, 400, TimeUnit.MILLISECONDS);
                }
                break;
            case 3:
                if (!this.myTitle.toString().equalsIgnoreCase(getResources().getString(R.string.dentandpaint))) {
                    navigated = 1;

                    Runnable openActivity = new Runnable() {
                        @Override
                        public void run() {
                            Intent myIntent = new Intent(getActivity(),DentingAndPainting.class);
                            myIntent.putExtra("check","1");
                            startActivity(myIntent);
                        }
                    };
                    worker.schedule(openActivity, 400, TimeUnit.MILLISECONDS);
                }
                break;
            case 4:
                if (!this.myTitle.toString().equalsIgnoreCase(getResources().getString(R.string.reportbreakdown))) {
                    navigated = 1;

                    Runnable openActivity = new Runnable() {
                        @Override
                        public void run() {
                            Intent myIntent = new Intent(getActivity(),BreakdownCategory.class);
                            myIntent.putExtra("check","1");
                            startActivity(myIntent);
                        }
                    };
                    worker.schedule(openActivity, 400, TimeUnit.MILLISECONDS);
                }
                break;
            case 6:
                if (!this.myTitle.toString().equalsIgnoreCase(getResources().getString(R.string.notification))) {
                    navigated = 1;

                    Runnable openActivity = new Runnable() {
                        @Override
                        public void run() {
                            Intent myIntent = new Intent(getActivity(), NotificationHome.class);
                            myIntent.putExtra("check", "1");
                            startActivity(myIntent);
                        }
                    };
                    worker.schedule(openActivity, 400, TimeUnit.MILLISECONDS);
                }
                break;
            case 5:
                if (!this.myTitle.toString().equalsIgnoreCase(getResources().getString(R.string.AMC))) {
                    navigated = 1;

                    Runnable openActivity = new Runnable() {
                        @Override
                        public void run() {
                            Intent myIntent = new Intent(getActivity(), AMCListing.class);
                            myIntent.putExtra("check", "1");
                            startActivity(myIntent);
                        }
                    };
                    worker.schedule(openActivity, 400, TimeUnit.MILLISECONDS);
                }
                break;
            case 7:
                if (!this.myTitle.toString().equalsIgnoreCase(getResources().getString(R.string.profile))) {
                    navigated = 1;

                    Runnable openActivity = new Runnable() {
                        @Override
                        public void run() {
                            Intent myIntent = new Intent(getActivity(),Profile.class);
                            myIntent.putExtra("check","1");
                            startActivity(myIntent);
                        }
                    };
                    worker.schedule(openActivity, 400, TimeUnit.MILLISECONDS);
                }
                break;
            case 12:
//                if (!this.myTitle.toString().equalsIgnoreCase(getResources().getString(R.string.support))) {
//                    navigated = 1;


                    new AlertDialog.Builder(getActivity())
                            .setTitle("")
                            .setMessage("Are you sure you want to logout ?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Runnable openActivity = new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent myIntent = new Intent(getActivity(), LoginNew.class);
                                            myIntent.putExtra("check", "1");
                                            startActivity(myIntent);
                                        }
                                    };
                                    worker.schedule(openActivity, 400, TimeUnit.MILLISECONDS);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();


//                }
                break;
            case 8:
                if (!this.myTitle.toString().equalsIgnoreCase(getResources().getString(R.string.support))) {
                    navigated = 1;

                    Runnable openActivity = new Runnable() {
                        @Override
                        public void run() {
                            Intent myIntent = new Intent(getActivity(),SupportHome.class);
                            myIntent.putExtra("check","1");
                            startActivity(myIntent);
                        }
                    };
                    worker.schedule(openActivity, 400, TimeUnit.MILLISECONDS);
                }
                break;

            default:
                Toast.makeText(getActivity(), "Not Created Yet", Toast.LENGTH_SHORT).show();
        }

        if (navigated == 1){

            task = new Runnable() {
                @Override
                public void run() {
                    getActivity().finish();
                }
            };

            if (!this.myTitle.toString().equalsIgnoreCase("HOME")) {
                worker.schedule(task, 1500, TimeUnit.MILLISECONDS);
            }
        }



    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar, String myTitle) {

        this.myTitle = myTitle;
        myAdapter.setTitle(myTitle);
        containerView = getActivity().findViewById(fragmentId);

        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    setSharedPreferences(getActivity(), Key_User_Learned_Drawer, "true");
                }
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
//                if(slideOffset<0.4){
//                    toolbar.setAlpha(1-slideOffset);
//                }
            }
        };

        if (!mUserLearnedDrawer && !fromSavedState) {
//            mDrawerLayout.openDrawer(containerView);
        } else {
            mDrawerLayout.closeDrawer(containerView);
        }

        mDrawerLayout.post(new Runnable() {

            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public void hideDrawer(){
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }


}
