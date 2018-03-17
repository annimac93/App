package com.iiht.student.nearme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.Holder> {
    private Context mContext;
    private ArrayList<String> place_image;
    private ArrayList<String> place_name;
    private ArrayList<String> place_vicinity;
    private ArrayList<String> place_lat;
    private ArrayList<String> place_lng;
    String userid;
    private String from;
    private int total;
    byte[] img;
    Bitmap bm;

    public PlacesAdapter(Context c, ArrayList<String> place_image, ArrayList<String> place_name,ArrayList<String> place_vicinity,ArrayList<String> place_lat,ArrayList<String> place_lng,String userid, String from) {
        this.mContext = c;
        this.place_image = place_image;
        this.place_name = place_name;
        this.place_vicinity = place_vicinity;
        this.place_lat = place_lat;
        this.place_lng = place_lng;
        this.userid = userid;
        this.from = from;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.listcell, parent, false);
        return new Holder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {
        holder.image_place.setImageResource(R.drawable.no_bg);
        String photo_url = null;
        if(from.equalsIgnoreCase("map"))
        {
            if(place_image.get(position).equalsIgnoreCase("noimage")){
                Drawable placeholder = holder.image_place.getContext().getResources().getDrawable(com.iiht.student.nearme.R.drawable.no_bg);
                holder.image_place.setImageDrawable(placeholder);
//                Toast.makeText(mContext," in if Position is : " + position ,Toast.LENGTH_LONG).show();
            }else{
                photo_url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photoreference="+ place_image.get(position) +"&key=AIzaSyClvmT1pUito9fm7azRO-fgfa8Jyo00qMc";
//                Toast.makeText(mContext,"in else Position is : " + position ,Toast.LENGTH_LONG).show();
                if (holder.image_place != null) {
                    new ImageDownloaderTask(holder.image_place).execute(photo_url);
                }
            }
//
        }
        else {
            Drawable placeholder = holder.image_place.getContext().getResources().getDrawable(com.iiht.student.nearme.R.drawable.no_bg);
            holder.image_place.setImageDrawable(placeholder);
        }

        holder.text_placename.setText(place_name.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(mContext,"name : " + place_name.get(position) ,Toast.LENGTH_LONG).show();
                Intent placeintent = new Intent(mContext,PlaceActivity.class);
                            placeintent.putExtra("place_name",place_name.get(position));
                            placeintent.putExtra("place_image",place_image.get(position));
                            placeintent.putExtra("place_vicinity",place_vicinity.get(position));
                            placeintent.putExtra("place_lat",place_lat.get(position));
                            placeintent.putExtra("place_lng",place_lng.get(position));
                            placeintent.putExtra("userid",userid);
                            mContext.startActivity(placeintent);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return place_name.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        ImageView image_place;
        TextView text_placename;

        public Holder(View v) {
            super(v);
            image_place = (ImageView) v.findViewById(R.id.imageView_place);
            text_placename = (TextView) v.findViewById(R.id.textView_placename);
        }
    }

    class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;

        public ImageDownloaderTask(ImageView imageView) {
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
//                if (statusCode != HttpStatus.SC_OK) {
//                    return null;
//                }

                if (statusCode != 200) {
                    return null;
                }

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
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
                        imageView.setImageBitmap(bitmap);
                    } else {
                        Drawable placeholder = imageView.getContext().getResources().getDrawable(com.iiht.student.nearme.R.drawable.no_bg);
                        imageView.setImageDrawable(placeholder);
                    }
                }
            }
        }
    }
}
