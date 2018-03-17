package com.iiht.student.nearme;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//import com.google.android.gms.common.server.FavaDiagnosticsEntity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class FavActivity extends AppCompatActivity implements OnMapReadyCallback {
    String place_name, place_image_ref, place_vicinity, place_lat, place_lng;
    byte[] imgplace;

    ImageView img_place;
    TextView txt_name , txt_address;

    GoogleMap mMap;
    String userid,placeid;
    Marker mCurrLocationMarker;
    DBO obj;
    int flag = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.iiht.student.nearme.R.layout.activity_fav);

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(com.iiht.student.nearme.R.id.map3);
        mapFragment.getMapAsync(this);

        img_place = (ImageView)findViewById(com.iiht.student.nearme.R.id.image_favplace);
        txt_name = (TextView)findViewById(com.iiht.student.nearme.R.id.text_favname);
        txt_address = (TextView)findViewById(com.iiht.student.nearme.R.id.text_favadd);


        obj = new DBO(FavActivity.this,null,null,1);

        Intent i = getIntent();
        placeid = i.getStringExtra("placeid");
        userid = i.getStringExtra("userid");

        Cursor mCursor = obj.getbyuser_place(Integer.parseInt(placeid) , Integer.parseInt(userid));


        if (mCursor.moveToFirst()) {

            do {
                place_name = mCursor.getString(2);
                place_vicinity = mCursor.getString(3);
                place_lat = mCursor.getString(4);
                place_lng = mCursor.getString(5);
                imgplace = mCursor.getBlob(6);

                txt_name.setText(place_name);
                txt_address.setText(place_vicinity);

                Bitmap bm;
//                Toast.makeText(getActivity(),imgbyte.toString(),Toast.LENGTH_SHORT).show();
                bm = BitmapFactory.decodeByteArray(imgplace, 0, imgplace.length);
                img_place.setImageBitmap(bm);
            } while (mCursor.moveToNext());


        }

        else
        {
        Toast.makeText(getApplicationContext(), "Data not fetched" ,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        // Add a marker in Sydney and move the camera

        LatLng place = new LatLng(Double.valueOf(place_lat), Double.valueOf(place_lng));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(place);
        markerOptions.title(place_name);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }

    public void onBackPressed() {
        Intent i = new Intent(FavActivity.this, HomeActivity.class);
        i.putExtra("userid",userid);
        startActivity(i);

        }

}
