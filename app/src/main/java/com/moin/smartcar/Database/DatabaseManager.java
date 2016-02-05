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

    DataSingelton mySingelton = DataSingelton.getMy_SingeltonData_Reference();

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


    private static final String CREATE_TABLE_USER = "CREATE TABLE "
            + TABLE_USER + "(" + TABLE_USER_USERNAME + " TEXT," + TABLE_USER_USEREMAILID
            + " TEXT," + TABLE_USER_MobileNumber + " TEXT," + TABLE_USER_Address
            + " TEXT," + TABLE_USER_USERIMAGEURL + " TEXT," + TABLE_USER_USERID + " TEXT" + ")";


    //------------------------------------------------------------------------- Car Info Table

    private static final String TABLE_CAR = "car_table";
    private static final String TABLE_CarId = "CarId";
    private static final String TABLE_CarBrand = "CarBrand";
    private static final String TABLE_CarModel = "CarModel";
    private static final String TABLE_CARYearOfManufacture = "YearOfManufacture";
    private static final String TABLE_CarRegNumber = "CarRegNumber";
    private static final String TABLE_CARVariant = "Variant";


    private static final String CREATE_TABLE_CAR = "CREATE TABLE "
            + TABLE_CAR + "(" + TABLE_CarId + " TEXT," + TABLE_CarBrand
            + " TEXT," + TABLE_CarModel + " TEXT," + TABLE_CARYearOfManufacture
            + " TEXT," + TABLE_CarRegNumber + " TEXT," + TABLE_CARVariant + " TEXT" + ")";






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
        values.put(TABLE_USER_USERID, mySingelton.mobileNumber);
        values.put(TABLE_USER_USERID, mySingelton.address);

        // insert row
        long todo_id = db.insert(TABLE_USER, null, values);

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
            mySingelton.userId = c.getString(c.getColumnIndex(TABLE_USER_USEREMAILID));
            mySingelton.userImageLink = c.getString(c.getColumnIndex(TABLE_USER_USERIMAGEURL));
            mySingelton.mobileNumber = c.getString(c.getColumnIndex(TABLE_USER_MobileNumber));
            mySingelton.address = c.getString(c.getColumnIndex(TABLE_USER_Address));
            return true;
        } catch (Exception e) {
            String exc = e.toString();
            return false;
        }
    }

    public void deleteUserInfo() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_USER;
        db.execSQL(query);
    }

    // CAR Table OPERATIONS -----------------------------------------------------------------------

    public long InsertIntoCarTables(ArrayList<CarInfoStr> str) {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(TABLE_USER_USERNAME,mySingelton.userName );
        values.put(TABLE_USER_USEREMAILID, mySingelton.userEmailId);
        values.put(TABLE_USER_USERIMAGEURL, mySingelton.userImageLink);
        values.put(TABLE_USER_USERID, mySingelton.userId);
        values.put(TABLE_USER_USERID, mySingelton.mobileNumber);
        values.put(TABLE_USER_USERID, mySingelton.address);
        // insert row
        long todo_id = db.insert(TABLE_USER, null, values);

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
                obj.carBrand = (c.getString(c.getColumnIndex(TABLE_CarBrand)));
                obj.carModel = (c.getString(c.getColumnIndex(TABLE_CarModel)));
                obj.yearOfMaufacture = (c.getString(c.getColumnIndex(TABLE_CARYearOfManufacture)));
                obj.carRegNo = (c.getString(c.getColumnIndex(TABLE_CarRegNumber)));
                obj.carVariant = (c.getString(c.getColumnIndex(TABLE_CARVariant)));
                obj.carVariant = (c.getString(c.getColumnIndex(TABLE_CarBrand)));
                list.add(obj);
            } while (c.moveToNext());
            return list;
        } catch (Exception e) {
            String exc = e.toString();
            return null;
        }
    }

    public void deleteAllCars() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_CAR;
        db.execSQL(query);
    }


}
