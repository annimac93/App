package com.iiht.student.nearme;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.apache.http.HttpStatus;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class PlaceActivity extends AppCompatActivity implements OnMapReadyCallback {

    String place_name, place_image_ref, place_vicinity, place_lat, place_lng;
    byte[] imgplace;

    ImageView img_place;
    TextView txt_name , txt_address;
    Button add_fav;
    GoogleMap mMap;
    String userid;
    Marker mCurrLocationMarker;
    DBO obj;
    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

        img_place = (ImageView)findViewById(R.id.image_place);
        txt_name = (TextView)findViewById(R.id.text_placename);
        txt_address = (TextView) findViewById(R.id.text_placeadd);
        add_fav = (Button) findViewById(R.id.btn_addtofav);

        obj = new DBO(PlaceActivity.this,null,null,1);

        Intent i = getIntent();
        place_name = i.getStringExtra("place_name");
        place_image_ref = i.getStringExtra("place_image");
        place_vicinity = i.getStringExtra("place_vicinity");
        place_lat = i.getStringExtra("place_lat");
        place_lng = i.getStringExtra("place_lng");
       userid = i.getStringExtra("userid");


        if(place_image_ref.equalsIgnoreCase("noimage")){
            Drawable placeholder = img_place.getContext().getResources().getDrawable(R.drawable.no_bg);
            img_place.setImageDrawable(placeholder);
            Bitmap bitmap = ((BitmapDrawable)placeholder).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            imgplace = stream.toByteArray();
            // Toast.makeText(mContext," in if Position is : " + position ,Toast.LENGTH_LONG).show();
        }else{
            String photo_url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photoreference="+ place_image_ref +"&key=AIzaSyClvmT1pUito9fm7azRO-fgfa8Jyo00qMc";

//            Toast.makeText(getActivity(),mParam1 + " , " + mParam2,Toast.LENGTH_SHORT).show();
            if (img_place != null) {
                new ImageTask(img_place).execute(photo_url);
            }
        }


        txt_name.setText(place_name);
        txt_address.setText(place_vicinity);




        add_fav.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            savedata();
        }
    });

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

    void savedata()
    {
        Cursor mCursor = obj.getallbyuserid(Integer.parseInt(userid));



        if (mCursor.moveToFirst()) {

            do {


                if(place_name.equalsIgnoreCase(mCursor.getString(2)) || place_vicinity.equalsIgnoreCase(mCursor.getString(3)))
                {
//                    Toast.makeText(PlaceActivity.this,"in if",Toast.LENGTH_SHORT).show();
                    flag = 1;
                    break;
                }
                else
                {
//                    Toast.makeText(PlaceActivity.this,"in else",Toast.LENGTH_SHORT).show();
                    flag = 0;
                    break;
                }

            } while (mCursor.moveToNext());


        }
        else
        {
//            Toast.makeText(PlaceActivity.this,"in else",Toast.LENGTH_SHORT).show();

        }
        if(flag == 0)
        {
            if(obj.addPlace(Integer.parseInt(userid),place_name,place_vicinity,place_lat,place_lng,imgplace))
            {
                Toast.makeText(getApplicationContext(),"Place Added to your Favourite list.",Toast.LENGTH_SHORT).show();

            }
            else
            {
                Toast.makeText(getApplicationContext(),"Insertion Failed ",Toast.LENGTH_SHORT).show();
            }
        }
         if(flag == 1)
        {
            Toast.makeText(getApplicationContext(),"Place already added ",Toast.LENGTH_SHORT).show();
        }

    }

    class ImageTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;

        public ImageTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadBitmap(params[0]);
        }

        private Bitmap downloadBitmap(String url) {
            HttpURLConnection urlConnection = null;
            try {
                URL uri = new URL(url);
                urlConnection = (HttpURLConnection) uri.openConnection();
                int statusCode = urlConnection.getResponseCode();
                if (statusCode != HttpStatus.SC_OK) {
                    return null;
                }

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);




//                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
//                    imgplace = bytes.toByteArray();
                    return bitmap;
                }
            } catch (Exception e) {
                urlConnection.disconnect();
                Log.w("ImageDownloader", "Error downloading image from " + url);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    if (bitmap != null) {
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                        imgplace = bytes.toByteArray();
                        imageView.setImageBitmap(bitmap);
                    } else {
                        Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.no_bg);
                        imageView.setImageDrawable(placeholder);
                    }
                }
            }
        }
    }

    public void onBackPressed() {
        Intent i = new Intent(PlaceActivity.this, MapActivity.class);
        i.putExtra("userid",userid);
        startActivity(i);

    }
}
