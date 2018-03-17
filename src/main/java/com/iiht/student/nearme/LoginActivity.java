package com.iiht.student.nearme;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    EditText un, pw;
    Button login , signup;
    DBO obj;

    String username, password,userid;
    int flag=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(com.iiht.student.nearme.R.layout.activity_login);


        un = (EditText) findViewById(com.iiht.student.nearme.R.id.editText_un);
        pw = (EditText) findViewById(com.iiht.student.nearme.R.id.editText_pw);
        login = (Button) findViewById(com.iiht.student.nearme.R.id.btn_login);
        signup = (Button) findViewById(com.iiht.student.nearme.R.id.btn_sn);

        obj=new DBO(this, null, null, 1);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this , SignupActivity.class);
                startActivity(i);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username = un.getText().toString();
                password = pw.getText().toString();

                Cursor mCursor = obj.getunpw();

                if (mCursor.moveToFirst()) {
                    do {
                        userid = mCursor.getString(0);
                        String un1 = mCursor.getString(1);
                        String pw1 = mCursor.getString(2);

                        if(username.equalsIgnoreCase(un1) && password.equalsIgnoreCase(pw1))
                        {
                            flag = 1;
//                            Toast.makeText(getApplicationContext(),"match",Toast.LENGTH_SHORT).show();
                            un.setText(null);
                            pw.setText(null);

//                            Toast.makeText(getApplicationContext(),userid,Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(LoginActivity.this , MapActivity.class);
                            i.putExtra("userid",userid);
                            startActivity(i);
//                            finish();
                            break;
                        }
                        else
                        {
                            flag = 0;
                        }

                    } while (mCursor.moveToNext());



                }

                if(flag == 0)
                {
                    Toast.makeText(getApplicationContext(),"Please enter valid Username and Password",Toast.LENGTH_SHORT).show();
                }

            }
        });
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

}
