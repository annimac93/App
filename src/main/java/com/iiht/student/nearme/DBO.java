package com.iiht.student.nearme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBO extends SQLiteOpenHelper {

    SQLiteDatabase db;
    ContentValues cv;

    public DBO(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "Nearme", factory, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table tbluser"+"(userid integer primary key autoincrement, username text, email text, mobile text,password text,photo blob )");
        db.execSQL("create table tblplace "+"(placeid integer primary key autoincrement ,userid integer , name text, vicinity text, lat text, lng text,photo blob )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists tbluser");
        db.execSQL("drop table if exists tblplace");
        db.close();
    }

    public boolean addUser(String username,  String email, String mobile, String password, byte[] bytePhoto)
    {
        db=this.getWritableDatabase();
        cv=new ContentValues();
        cv.put("username", username);
        cv.put("email", email);
        cv.put("mobile",mobile);
        cv.put("password",password);
        cv.put("photo",bytePhoto);
        db.insert("tbluser", null, cv);
        db.close();
        return  true;
    }

    //update records
    public boolean updateuser(Integer userid,String username, String email, String mobile, String password, byte[] bytePhoto)
    {
        db=this.getWritableDatabase();
        cv=new ContentValues();
        cv.put("username", username);
        cv.put("email", email);
        cv.put("mobile",mobile);
        cv.put("password",password);
        cv.put("photo",bytePhoto);
        db.update("tbluser", cv, "userid=?", new String[]{Integer.toString(userid)} );

        return true;
    }

    public Cursor getid(String username,  String email, String mobile)
    {
        db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select userid from tbluser where username='"+ username+"' and email='"+email+"' and mobile='"+mobile+"'", null);
        return res;
    }

    //show records
    public Cursor getuserbyid(int userid)
    {
        db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from tbluser where userid="+ userid, null);
        return res;
    }

    public Cursor getunemail()
    {
        db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select userid,username ,email, mobile from tbluser ", null);
        return res;
    }
    public Cursor getunpw()
    {
        db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select userid,username ,password from tbluser ", null);
        return res;
    }

    public Integer deleteplace(Integer placeid)
    {
        db=this.getWritableDatabase();
        return db.delete("tblplace", "placeid=?", new String[]{Integer.toString(placeid)});
    }

    public Cursor getalluser()
    {
        db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from tbluser", null);
        return res;
    }

    public boolean addPlace(Integer userid, String name, String vicinity, String lat, String lng,  byte[] bytePhoto)
    {
        db=this.getWritableDatabase();
        cv=new ContentValues();
        cv.put("userid", userid);
        cv.put("name", name);
        cv.put("vicinity", vicinity);
        cv.put("lat",lat);
        cv.put("lng",lng);
        cv.put("photo",bytePhoto);
        db.insert("tblplace", null, cv);
        db.close();
        return  true;
    }

    public Cursor getallbyuserid(Integer userid)
    {
        db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from tblplace where userid="+ userid, null);
        return res;
    }

    public Cursor getbyuser_place(Integer placeid,Integer userid)
    {
        db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from tblplace where placeid="+ placeid +" and userid="+userid, null);
        return res;
    }
    public Cursor getbyuserid(Integer userid)
    {
        db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select placeid,name, photo from tblplace where userid="+ userid, null);
        return res;
    }

    public Cursor getallplace()
    {
        db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from tblplace", null);
        return res;
    }
}
