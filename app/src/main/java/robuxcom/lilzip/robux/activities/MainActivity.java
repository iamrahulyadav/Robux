package robuxcom.lilzip.robux.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.adefruandta.spinningwheel.SpinningWheelView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.IOException;
import java.util.Random;

import robuxcom.lilzip.robux.R;

public class MainActivity extends Activity {
    private SpinningWheelView wheelView;
    private RelativeLayout rotate;
    private static final String ADMOB_APP_ID = "ca-app-pub-6610497664170714~8078830415";
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, ADMOB_APP_ID);
        //loadAdsReq();
        wheelView = findViewById(R.id.wheel);
        rotate = findViewById(R.id.rlSpinArea);

        // Can be array string or list of object
        wheelView.setItems(R.array.robux);

        // Set listener for rotation event
        wheelView.setOnRotationListener(new SpinningWheelView.OnRotationListener<String>() {
            // Call once when start rotation
            @Override
            public void onRotation() {
                Log.d("XXXX", "On Rotation");
            }

            // Call once when stop rotation
            @Override
            public void onStopRotation(String item) {
                Log.d("XXXX", "On Rotation");
                if (item.equalsIgnoreCase("R$")) {
                    //Toast.makeText(MainActivity.this, item, Toast.LENGTH_SHORT).show();
                    startSound();
                } else {
                    showAds();
                }
            }
        });
        // If true: user can rotate by touch
        // If false: user can not rotate by touch
        wheelView.setEnabled(false);

        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // max angle 50
                // duration 10 second
                // every 50 ms rander rotation
                wheelView.rotate(50, generateRandomInt(5000, 3000), 50);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadAdsReq();
    }

    private void loadAdsReq() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6610497664170714/2443360358");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void showAds() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }

    int generateRandomInt(int maximum, int minimum) {
        Random rn = new Random();
        int range = maximum - minimum + 1;
        return rn.nextInt(range) + minimum;
    }

    private void startSound() {
        try {
            AssetFileDescriptor afd = getAssets().openFd("ic_spin_win.wav");
            MediaPlayer player = new MediaPlayer();
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            player.prepare();
            player.start();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    startActivity(new Intent(MainActivity.this, RobuxActivity.class));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
