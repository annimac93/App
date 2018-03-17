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
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.*;

public class SignupActivity extends AppCompatActivity {

    String userChoosenTask;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    CircleImageView ivprofile;
    EditText edusername,edpass,edemail,edmobile;
    Button cancel,signup;
    String username,password,email,mobile;
    byte[] imgbyte = null;
    String userid;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    DBO obj;
    String fromEmail, fromPassword, toEmail, emailSubject, emailBody;
    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(com.iiht.student.nearme.R.layout.activity_signup);

        obj=new DBO(this, null, null, 1);
        ivprofile = (CircleImageView)findViewById(com.iiht.student.nearme.R.id.img_propic);
        edusername = (EditText) findViewById(com.iiht.student.nearme.R.id.editText_username);
        edpass = (EditText) findViewById(com.iiht.student.nearme.R.id.editText_password);
        edemail = (EditText) findViewById(com.iiht.student.nearme.R.id.editText_email);
        edmobile = (EditText) findViewById(com.iiht.student.nearme.R.id.editText_mob);

        cancel = (Button) findViewById(com.iiht.student.nearme.R.id.btn_cancel);
        signup = (Button) findViewById(com.iiht.student.nearme.R.id.btn_signup);

        ivprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignupActivity.this,FirstActivity.class);
                startActivity(i);
//                finish();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username=edusername.getText().toString();
                password=edpass.getText().toString();
                email = edemail.getText().toString();
                mobile = edmobile.getText().toString();




                Cursor mCursor = obj.getunemail(); //userid, username, email , mobile


                if(username.length()>0 && password.length()>0 && email.length()>0 && mobile.length() > 0 )
                {
                    if(mobile.length() != 10)
                    {
                        Toast.makeText(getApplicationContext(),"Please enter valid phone number.",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if(email.matches(emailPattern))
                        {
                            if(imgbyte != null)
                            {
                                if (mCursor.moveToFirst()) {

                                    do {
                                        if(username.equalsIgnoreCase(mCursor.getString(1))  || email.equalsIgnoreCase(mCursor.getString(2)) || mobile.equalsIgnoreCase(mCursor.getString(3)))
                                        {
                                            flag = 1;
                                            break;
                                        }
                                        else
                                        {
                                            flag = 0;
                                            break;
                                        }

                                    } while (mCursor.moveToNext());


                                }
                                else
                                {
//            Toast.makeText(getActivity(),"in else",Toast.LENGTH_SHORT).show();

                                }

                                if(flag == 0)
                                {
//                                          Toast.makeText(getApplicationContext(),"in savedata : "+ String.valueOf(imgbyte),Toast.LENGTH_SHORT).show();
                                    saveData();
                                }
                                else if(flag == 1)
                                {
                                    Toast.makeText(getApplicationContext(),"This user is already exist.",Toast.LENGTH_SHORT).show();
                                }

                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Please select your profile picture.",Toast.LENGTH_SHORT).show();

                            }
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Please enter valid email.",Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                else
                {
                    AlertDialog.Builder alertBuilder=new AlertDialog.Builder(SignupActivity.this);
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

    private void saveData(){

        if(obj.addUser(username,email,mobile,password,imgbyte))
        {
            Toast.makeText(getApplicationContext(),"You have Signed up successfully",Toast.LENGTH_SHORT).show();

            Intent i =new Intent(SignupActivity.this,LoginActivity.class);
//            i.putExtra("userid",userid);
            startActivity(i);
            finish();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Insertion Failed ",Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        System.exit(0);
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
                    Toast.makeText(getApplicationContext(),"Please select your profile picture.",Toast.LENGTH_SHORT).show();
                    //code for deny
                }
                break;
        }
    }
    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=checkPermission(SignupActivity.this);
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
//        Toast.makeText(getApplicationContext(),String.valueOf(imgbyte),Toast.LENGTH_SHORT).show();
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

//        Toast.makeText(getApplicationContext(),String.valueOf(imgbyte),Toast.LENGTH_SHORT).show();
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
