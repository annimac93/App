package com.iiht.student.nearme;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.*;

public class ProfileActivity extends MenuActivity{

    String userChoosenTask;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    CircleImageView ivprofile;
    EditText edusername,edemail,edmobile,edpassword;
    Button save;
    String username,email,mobile,password,userid;
    byte[] imgbyte = null;
    DBO obj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(com.iiht.student.nearme.R.layout.activity_profile,content);
        setTitle("Profile");

        Intent i = getIntent();

        userid = i.getStringExtra("userid");

        obj=new DBO(ProfileActivity.this, null, null, 1);
        ivprofile = (CircleImageView)findViewById(com.iiht.student.nearme.R.id.img_changepropic);
        edusername = (EditText)findViewById(com.iiht.student.nearme.R.id.editText_changeuname);
        edemail = (EditText) findViewById(com.iiht.student.nearme.R.id.editText_changeemail);
        edmobile = (EditText) findViewById(com.iiht.student.nearme.R.id.editText_changemob);
        edpassword = (EditText)findViewById(com.iiht.student.nearme.R.id.editText_changepwd);


        save = (Button) findViewById(com.iiht.student.nearme.R.id.btn_update);

        displayData();

        ivprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });



        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username=edusername.getText().toString();

                email = edemail.getText().toString();
                mobile = edmobile.getText().toString();
                password = edpassword.getText().toString();
                if(username.length()>0 &&  email.length()>0 && mobile.length() > 0 && password.length()>0 && imgbyte.length > 0)
                {
                    saveData();
                }
                else
                {
                    AlertDialog.Builder alertBuilder=new AlertDialog.Builder(ProfileActivity.this);
                    alertBuilder.setTitle("Invalid Data");
                    alertBuilder.setMessage("Please, Enter valid data");
                    alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                        }
                    });
                    alertBuilder.create().show();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.getMenu().getItem(2).setChecked(true);
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

    private void displayData() {

        Cursor mCursor = obj.getuserbyid(Integer.valueOf(userid));
//        Cursor mcur = obj.getbyuserid(Integer.valueOf(userid));

        if (mCursor.moveToFirst()) {
            do {
                username = mCursor.getString(1);

                email = mCursor.getString(2);
                mobile = mCursor.getString(3);
                imgbyte = mCursor.getBlob(5);


                edusername.setText(username);
                edemail.setText(email);
                edmobile.setText(mobile);
                Bitmap bm;
//                Toast.makeText(getActivity(),imgbyte.toString(),Toast.LENGTH_SHORT).show();
                bm = BitmapFactory.decodeByteArray(imgbyte, 0, imgbyte.length);
                ivprofile.setImageBitmap(bm);


            } while (mCursor.moveToNext());
        }

//        if (mcur.moveToFirst()) {
//            do {
//
//                imgbyte = mcur.getBlob(1);
//                Bitmap bm;
//                bm = BitmapFactory.decodeByteArray(imgbyte, 0, imgbyte.length);
//                ivprofile.setImageBitmap(bm);
//            } while (mcur.moveToNext());
//        }




        mCursor.close();
    }

    private void saveData(){


        if(obj.updateuser(Integer.valueOf(userid),username,email,mobile,password,imgbyte))
        {
            Toast.makeText(getApplicationContext(),"Profile Updated",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Profile Not Updated",Toast.LENGTH_SHORT).show();
        }

//        Intent i =new Intent(SignupActivity.this,MenuActivity.class);
//        startActivity(i);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }
    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=checkPermission(ProfileActivity.this);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask="Take Photo";
                    if(result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask="Choose from Library";
                    if(result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }


    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            imgbyte = bytes.toByteArray();
            fo.write(imgbyte);
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Toast.makeText(getContext(),String.valueOf(imgbyte),Toast.LENGTH_SHORT).show();
        ivprofile.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                imgbyte = bytes.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        Toast.makeText(getContext(),String.valueOf(imgbyte),Toast.LENGTH_SHORT).show();
        ivprofile.setImageBitmap(bm);
    }

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public  boolean checkPermission(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}
