package com.gameblowmind.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adefruandta.spinningwheel.SpinningWheelView;
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.gameblowmind.R;

import java.io.IOException;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {
    private SpinningWheelView wheelView;
    private RelativeLayout rotate;
    private static final String ADMOB_APP_ID = "ca-app-pub-6610497664170714~8078830415";
    private InterstitialAd mInterstitialAd;
    private TextView tvPoints;
    private boolean rxStatus = false;
    private Button btnWithdraw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //MobileAds.initialize(this, ADMOB_APP_ID);
        //loadAdsReq();
        wheelView = findViewById(R.id.wheel);
        tvPoints = findViewById(R.id.tvPoints);
        btnWithdraw = findViewById(R.id.btnWithdraw);
        rotate = findViewById(R.id.rlSpinArea);

        // Can be array string or list of object
        wheelView.setItems(R.array.robux);

        // Set listener for rotation event
        wheelView.setOnRotationListener(new SpinningWheelView.OnRotationListener<String>() {
            // Call once when start rotation
            @Override
            public void onRotation() {
                Log.d("XXXX", "On Rotation");
                loadAdsReq();
            }

            // Call once when stop rotation
            @Override
            public void onStopRotation(String item) {
                Log.d("XXXX", "On Rotation");
                if (item.equalsIgnoreCase("R$")) {
                    //Toast.makeText(MainActivity.this, item, Toast.LENGTH_SHORT).show();
                    //startSound();
                    rxStatus = true;
                } else {
                    rxStatus = false;

                }
                if (!isDialogOpen)
                    showAds();
            }
        });
        // If true: user can rotate by touch
        // If false: user can not rotate by touch
        wheelView.setEnabled(false);
        wheelView.rotate(50, generateRandomInt(10000, 5000), 50);
        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // max angle 50
                // duration 10 second
                // every 50 ms rander rotation
                wheelView.rotate(50, generateRandomInt(12000, 7000), 50);
            }
        });
        final long points = AppPreferences.getPoints(MainActivity.this);
        tvPoints.setText(String.valueOf(points + " pts"));
        if (points >= 5000 || dev) {
            btnWithdraw.setVisibility(View.VISIBLE);
        } else {
            btnWithdraw.setVisibility(View.GONE);
        }
        btnWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (points >= 5000 || dev) {

                    showWithdrawDialog();
                } else {
                    Toast.makeText(MainActivity.this, "You dont have sufficient points to withdraw", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    boolean dev = false;
    boolean isDialogOpen = false;
    private void showWithdrawDialog() {
        final EditText mobileEditText = new EditText(this);
        mobileEditText.setHint("Enter Bkash Number");
        mobileEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        final EditText pointsEditText = new EditText(MainActivity.this);
        pointsEditText.setHint("Enter points");
        pointsEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        LinearLayout mainLout = new LinearLayout(MainActivity.this);
        mainLout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        mainLout.setLayoutParams(lp);
        mainLout.addView(mobileEditText);
        mainLout.addView(pointsEditText);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Withdraw points")
                .setMessage("(Per 1000 pts = 10tk)")
                .setView(mainLout)
                .setCancelable(false)
                .setPositiveButton("Withdraw", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mobileNo = String.valueOf(mobileEditText.getText());
                        String totalPoints = String.valueOf(pointsEditText.getText());
                        if (validatePhone(mobileNo)) {
                            if (!TextUtils.isEmpty(totalPoints) || totalPoints != "") {
                                if (Long.valueOf(totalPoints) >= 5000 || dev) {
                                    sendMail(mobileNo, totalPoints);
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(MainActivity.this, "Minimum 5000 pts needed to withdraw", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Enter valid points", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Mobile number is not valid", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isDialogOpen = false;
                rotate.performClick();
            }
        });
        isDialogOpen = true;
    }

    boolean validatePhone(String phone) {
        if (android.util.Patterns.PHONE.matcher(phone).matches()) {
            return true;
        } else return false;
    }

    Locale getCurrentLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return getResources().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            return getResources().getConfiguration().locale;
        }
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    private void sendMail(String mobileNumber, final String points) {
        Locale locale = getCurrentLocale();
        String phoneData = locale.getCountry() + "\n" +
                getDeviceName() + "\n" +
                Build.MANUFACTURER + "\n" +
                Build.VERSION.RELEASE + "\n" +
                Build.BRAND;

        BackgroundMail.newBuilder(this)
                .withUsername("tajmultipletrading@gmail.com")
                .withPassword("Multiple312")
                .withMailto("tajmultipletrading@gmail.com")
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject("withdraw_request_from" + mobileNumber)
                .withProcessVisibility(true)
                .withBody("Mobile Number: " + mobileNumber + "\n" +
                        "Withdraw points request: " + points + "\n" +
                        "Payable: " + (Long.valueOf(points) / 100) + "\n" +
                        "----------------------------------------------" + "\n" +
                        phoneData
                )
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        //do some magic
                        long updatePoints = AppPreferences.getPoints(MainActivity.this) - Long.valueOf(points);
                        AppPreferences.savePoints(MainActivity.this, updatePoints);
                        updatePoints();
                        Toast.makeText(MainActivity.this, "you got 5 pts bonus for withdraw", Toast.LENGTH_SHORT).show();
                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        //do some magic
                    }
                })
                .send();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadAdsReq();
    }

    private void loadAdsReq() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6554363642445395/4010149938");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
                startTimerToAutoClose();
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.

            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
                updatePoints();
                rotate.performClick();

            }
        });
    }


    private void startTimerToAutoClose() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updatePoints();
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                finish();
            }
        }, generateRandomInt(15000, 10000));
    }

    private void updatePoints() {
        if (rxStatus) {
            long points = AppPreferences.getPoints(MainActivity.this);
            points = points + 5;
            tvPoints.setText(String.valueOf(points + " pts"));
            AppPreferences.savePoints(MainActivity.this, points);
        }
    }

    private void showAds() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();

        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
            rotate.performClick();
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
