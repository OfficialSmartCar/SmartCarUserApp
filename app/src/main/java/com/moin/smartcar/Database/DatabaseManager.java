package com.moin.smartcar.Database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.moin.smartcar.SingeltonData.DataSingelton;
import com.moin.smartcar.User.CarInfoStr;

import java.util.ArrayList;



/**
 * Created by macpro on 2/1/16.
 */
public class DatabaseManager extends SQLiteOpenHelper {

    private final static String DB_Name = "MyClass.db";
    private static final int DATABASE_VERSION = 1;
    //------------------------------------------------------------------------- USER TABLE INFO
    private static final String TABLE_USER = "user_table";
    private static final String TABLE_USER_USERNAME = "UserName";
    private static final String TABLE_USER_USEREMAILID = "UserEMAILID";
    private static final String TABLE_USER_USERIMAGEURL = "UserImageUrl";
    private static final String TABLE_USER_USERID = "UserID";
    private static final String TABLE_USER_MobileNumber = "UserMobile";
    private static final String TABLE_USER_Address = "UserAddess";
    private static final String TABLE_USER_Notification = "UserNotification";
    private static final String CREATE_TABLE_USER = "CREATE TABLE "
            + TABLE_USER + "(" + TABLE_USER_USERNAME + " TEXT," + TABLE_USER_USEREMAILID
            + " TEXT," + TABLE_USER_MobileNumber + " TEXT," + TABLE_USER_Address
            + " TEXT," + TABLE_USER_Notification + " TEXT," + TABLE_USER_USERIMAGEURL + " TEXT," + TABLE_USER_USERID + " TEXT" + ")";
    private static final String TABLE_CAR = "car_table";


    //------------------------------------------------------------------------- Car Info Table
    private static final String TABLE_CarName = "CarName";
    private static final String TABLE_CarId = "CarId";
    private static final String TABLE_CarBrand = "CarBrand";
    private static final String TABLE_CarModel = "CarModel";
    private static final String TABLE_CARYearOfManufacture = "YearOfManufacture";
    private static final String TABLE_CarRegNumber = "CarRegNumber";
    private static final String TABLE_CARVariant = "Variant";
    private static final String TABLE_CARisPremium = "IsPremium";

    private static final String CREATE_TABLE_CAR = "CREATE TABLE "
            + TABLE_CAR + "(" + TABLE_CarId + " TEXT," + TABLE_CarName
            + " TEXT," + TABLE_CarBrand + " TEXT," + TABLE_CarModel + " TEXT," + TABLE_CARYearOfManufacture
            + " TEXT," + TABLE_CarRegNumber + " TEXT," + TABLE_CARisPremium + " TEXT," + TABLE_CARVariant + " TEXT" + ")";
    DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();






    public DatabaseManager(Context context) {
        super(context, DB_Name, null, DATABASE_VERSION);
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_CAR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAR);
        onCreate(db);
    }




    // USER Table OPERATIONS -----------------------------------------------------------------------

    public long InsertIntoUserTable() {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(TABLE_USER_USERNAME,mySingelton.userName );
        values.put(TABLE_USER_USEREMAILID, mySingelton.userEmailId);
        values.put(TABLE_USER_USERIMAGEURL, mySingelton.userImageLink);
        values.put(TABLE_USER_USERID, mySingelton.userId);
        values.put(TABLE_USER_MobileNumber, mySingelton.mobileNumber);
        values.put(TABLE_USER_Address, mySingelton.address);
        values.put(TABLE_USER_Notification, mySingelton.UserNotificationToken);

        // insert row
        long todo_id = db.insert(TABLE_USER, null, values);
        db.close();
        return todo_id;
    }

    public Boolean getUserInfo() {
        Boolean check = false;
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_USER;
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        try {
            mySingelton.userName = c.getString(c.getColumnIndex(TABLE_USER_USERNAME));
            mySingelton.userEmailId = c.getString(c.getColumnIndex(TABLE_USER_USEREMAILID));
            mySingelton.userId = c.getString(c.getColumnIndex(TABLE_USER_USERID));
            mySingelton.userImageLink = c.getString(c.getColumnIndex(TABLE_USER_USERIMAGEURL));
            mySingelton.mobileNumber = c.getString(c.getColumnIndex(TABLE_USER_MobileNumber));
            mySingelton.address = c.getString(c.getColumnIndex(TABLE_USER_Address));
            mySingelton.UserNotificationToken = c.getString(c.getColumnIndex(TABLE_USER_Notification));
            db.close();
            return true;
        } catch (Exception e) {
            String exc = e.toString();
            db.close();
            return false;
        }
    }

    public void deleteUserInfo() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_USER;
        db.execSQL(query);
        db.close();
    }

    // CAR Table OPERATIONS -----------------------------------------------------------------------

    public long InsertIntoCarTables(ArrayList<CarInfoStr> str) {
        long todo_id = -1;
        for (int i = 0; i < str.size(); i++) {
            CarInfoStr currentStr = str.get(i);
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(TABLE_CarId, currentStr.carId);
            values.put(TABLE_CarName, currentStr.carName);
            values.put(TABLE_CarBrand, currentStr.carBrand);
            values.put(TABLE_CarModel, currentStr.carModel);
            values.put(TABLE_CARYearOfManufacture, currentStr.yearOfMaufacture);
            values.put(TABLE_CarRegNumber, currentStr.carRegNo);
            values.put(TABLE_CARVariant, currentStr.carVariant);
            values.put(TABLE_CARisPremium, currentStr.isPremium + "");

            // insert row
            todo_id = db.insert(TABLE_CAR, null, values);
            if (todo_id == -1) {
                db.close();
                return -1;
            }
            db.close();
        }
        return todo_id;
    }

    public ArrayList<CarInfoStr> garCarInfo() {

        ArrayList<CarInfoStr> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_CAR;
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        try {
            do {
                CarInfoStr obj = new CarInfoStr();
                obj.carName = (c.getString(c.getColumnIndex(TABLE_CarName)));
                obj.carBrand = (c.getString(c.getColumnIndex(TABLE_CarBrand)));
                obj.carModel = (c.getString(c.getColumnIndex(TABLE_CarModel)));
                obj.yearOfMaufacture = (c.getString(c.getColumnIndex(TABLE_CARYearOfManufacture)));
                obj.carRegNo = (c.getString(c.getColumnIndex(TABLE_CarRegNumber)));
                obj.carVariant = (c.getString(c.getColumnIndex(TABLE_CARVariant)));
                obj.carVariant = (c.getString(c.getColumnIndex(TABLE_CarBrand)));
                obj.isPremium = Integer.parseInt((c.getString(c.getColumnIndex(TABLE_CARisPremium))));
                list.add(obj);
            } while (c.moveToNext());
            db.close();
            return list;
        } catch (Exception e) {
            String exc = e.toString();
            db.close();
            return null;
        }

    }

    public void deleteAllCars() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_CAR;
        db.execSQL(query);
        db.close();
    }


}
