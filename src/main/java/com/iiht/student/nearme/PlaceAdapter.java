package com.iiht.student.nearme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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


public class PlaceAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<String> place_image;
    private ArrayList<byte[]> place_img;
    private ArrayList<String> place_name;
    private String from;
    private  int total;
    byte[] img;
    Bitmap bm;

    public PlaceAdapter(Context c, ArrayList<String> place_image,ArrayList<String> place_name ,String from)
    {
        this.mContext = c;
        this.place_image = place_image;
        this.place_name = place_name;
        this.from = from;
//    this.total = total;
//        Toast.makeText(c,String.valueOf(place_name),Toast.LENGTH_SHORT).show();

//        if(android.os.Build.VERSION.SDK_INT>9)
//        {
//            StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }

    }



    @Override
    public int getCount()
    {
//        Toast.makeText(mContext,"total size : "+total,Toast.LENGTH_LONG).show();
        return place_name.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Holder mHolder;
        LayoutInflater layoutInflater;

        if(convertView == null)
        {
            layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(com.iiht.student.nearme.R.layout.listcell, null);
            //final Organization currentOrg = organizationlist.get(pos).getOrganization();
            mHolder = new Holder();

            mHolder.image_place = (ImageView) convertView.findViewById(com.iiht.student.nearme.R.id.imageView_place);
            mHolder.text_placename = (TextView) convertView.findViewById(com.iiht.student.nearme.R.id.textView_placename);

//            mHolder.image_place.setImageResource(R.drawable.no_bg);

            convertView.setTag(mHolder);
        }
        else
        {
            mHolder = (Holder)convertView.getTag();
        }

        if(from.equalsIgnoreCase("map"))
        {
            String photo_url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photoreference="+ place_image.get(position) +"&key=AIzaSyClvmT1pUito9fm7azRO-fgfa8Jyo00qMc";

            if (mHolder.image_place != null) {
                new ImageDownloaderTask(mHolder.image_place).execute(photo_url);
            }

        }
        else {
            Drawable placeholder = mHolder.image_place.getContext().getResources().getDrawable(com.iiht.student.nearme.R.drawable.no_bg);
            mHolder.image_place.setImageDrawable(placeholder);
        }

        mHolder.text_placename.setText(place_name.get(position));



        return convertView;
    }



    public class Holder{

        ImageView image_place;
        TextView text_placename;

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
