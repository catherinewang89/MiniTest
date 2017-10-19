package com.example.alice.getappinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this,"begin get app info",1).show();
        startService(getServiceIntent());
        finish();
    }
    private Intent getServiceIntent() {
        Intent serviceintent = new Intent();
        serviceintent.setClass(this,GetAppInfoService.class);
        return serviceintent;
    }
}
