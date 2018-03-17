package com.iiht.student.nearme;

import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    NavigationView navigationView;
    FrameLayout content;
    Toolbar toolbar;
    String userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(com.iiht.student.nearme.R.layout.activity_menu);
        toolbar = (Toolbar) findViewById(com.iiht.student.nearme.R.id.toolbar);
        setSupportActionBar(toolbar);

        content = (FrameLayout) findViewById(com.iiht.student.nearme.R.id.content_frame);
        getLayoutInflater().inflate(com.iiht.student.nearme.R.layout.activity_home,content);

        DrawerLayout drawer = (DrawerLayout) findViewById(com.iiht.student.nearme.R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, com.iiht.student.nearme.R.string.navigation_drawer_open, com.iiht.student.nearme.R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(com.iiht.student.nearme.R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

       Intent i = getIntent();

        userid = i.getStringExtra("userid");



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

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.getMenu().getItem(0).setChecked(true);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object

        if (id == R.id.nav_home) {
            Intent i = new Intent(MenuActivity.this, HomeActivity.class);
            i.putExtra("userid",userid);
            startActivity(i);
            // Handle the camera action
        } else if (id == R.id.nav_map) {
            Intent i = new Intent(MenuActivity.this, MapActivity.class);
            i.putExtra("userid",userid);
            startActivity(i);

        } else if (id == R.id.nav_profile) {
            Intent i = new Intent(MenuActivity.this, ProfileActivity.class);
            i.putExtra("userid",userid);
            startActivity(i);
        } else if (id == R.id.nav_logout) {
            userid = "";
//            Intent i = new Intent(MenuActivity.this, FirstActivity.class);
//            startActivity(i);
            Intent intent = new Intent(MenuActivity.this, FirstActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            finishAffinity();
            startActivity(intent);

        } else if (id == R.id.nav_share) {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Foodz Near Me");
            String sAux = "\nLet me recommend you this application\n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id="+appPackageName+" \n\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "choose one"));

        } else if (id == R.id.nav_rate) {

            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
