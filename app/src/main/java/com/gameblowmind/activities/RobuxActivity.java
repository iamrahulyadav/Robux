package com.gameblowmind.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.gameblowmind.R;


public class RobuxActivity extends Activity {
//    com.Robuxfreeforfreetixfreeappspinwheel
    private ImageButton ibtnRobux;
    private static final String ROBUX_URL = "http://bit.ly/2vt4YMt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robux);
        ibtnRobux = findViewById(R.id.ibtnRobux);
        ibtnRobux.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebPage(ROBUX_URL);
            }
        });
    }


    public void openWebPage(String url) {
        try {
            Uri webpage = Uri.parse(url);
            Intent myIntent = new Intent(Intent.ACTION_VIEW, webpage);
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request. Please install a web browser or check your URL.", Toast.LENGTH_LONG).show();
            //e.printStackTrace();
        }
    }
}
