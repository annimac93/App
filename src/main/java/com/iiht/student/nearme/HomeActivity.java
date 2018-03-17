package com.iiht.student.nearme;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class HomeActivity extends MenuActivity {
    String userid;
    String placename,placeimg,placeid;
    byte[] imgbyte = null;

    ArrayList<String> alplacename = new ArrayList<String>();
    ArrayList<String> alplaceid = new ArrayList<String>();
    ArrayList<byte[]> alplacephoto = new ArrayList<byte[]>();



    TextView nodata;
    ListView fav_place_list;
    DBO obj;

    FavAdapter favadapter;
    private AlertDialog.Builder build;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(com.iiht.student.nearme.R.layout.activity_home,content);
        setTitle("Favourite");

        Intent i = getIntent();

        userid = i.getStringExtra("userid");
//        nodata = (TextView)findViewById(R.id.textView_nodata);
        fav_place_list = (ListView)findViewById(com.iiht.student.nearme.R.id.listview_favplace);

        obj=new DBO(HomeActivity.this, null, null, 1);



        displayData();




    }

    private void displayData() {

        Cursor mCursor = obj.getbyuserid(Integer.parseInt(userid));

        alplaceid.clear();
        alplacename.clear();
        alplacephoto.clear();
        if (mCursor.moveToFirst()) {

//            fav_place_list.setVisibility(View.VISIBLE);
//            nodata.setVisibility(View.INVISIBLE);

            do {
                placeid = mCursor.getString(0);
                placename = mCursor.getString(1);
                imgbyte = mCursor.getBlob(2);


                alplaceid.add(placeid);
                alplacename.add(placename);
                alplacephoto.add(imgbyte);

            } while (mCursor.moveToNext());

            favadapter = new FavAdapter(HomeActivity.this,alplacephoto,alplacename);
//            favadapter.notifyDataSetChanged();
            fav_place_list.setAdapter(favadapter);
//            fav_place_list.setScrollingCacheEnabled(false);



            fav_place_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent i = new Intent(HomeActivity.this,FavActivity.class);
                    i.putExtra("placeid",alplaceid.get(position));
                    i.putExtra("userid",userid);
                    startActivity(i);

                }
            });

            fav_place_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               final int arg2, long arg3) {
                    Log.e("@@@@@","long click");

                    build = new AlertDialog.Builder(HomeActivity.this);
                    build.setTitle("Delete " + alplacename.get(arg2));
                    build.setMessage("Do you want to delete ?");
                    build.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    Toast.makeText(
                                            getApplicationContext(),
                                            alplacename.get(arg2)
                                                    + " is deleted.", Toast.LENGTH_SHORT).show();

                                    obj.deleteplace(Integer.valueOf(alplaceid.get(arg2)));
//                                fav_place_list.removeAllViews();
                                displayData();

//                                    favadapter.notifyDataSetChanged();
//

                                    dialog.cancel();
                                }
                            });

                    build.setNegativeButton("No",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = build.create();
                    alert.show();

                    return true;
                }
            });



        }
        else
        {

                Toast.makeText(getApplicationContext(),"No data found. Add your favourite place.",Toast.LENGTH_LONG).show();
////            nodata.setVisibility(View.VISIBLE);
//            fav_place_list.setVisibility(View.INVISIBLE);
        }


        mCursor.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.getMenu().getItem(1).setChecked(true);
    }

    private  static long back_pressed;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(com.iiht.student.nearme.R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(back_pressed + 2000 > System.currentTimeMillis())
            {
//                super.onBackPressed();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                System.exit(0);
            }
            else
            {
                Toast.makeText(getBaseContext(),"Press once again to exit", Toast.LENGTH_SHORT).show();
                back_pressed = System.currentTimeMillis();
            }

        }
    }

}
