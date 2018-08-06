package robuxcom.lilzip.robux.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import robuxcom.lilzip.robux.R;

public class RobuxActivity extends Activity {

    private TextView tvRobux;
    private static final String ROBUX_URL="http://bit.ly/2vlTlGO";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robux);
        tvRobux=findViewById(R.id.tvRobux);
        tvRobux.setPaintFlags(tvRobux.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvRobux.setOnClickListener(new View.OnClickListener() {
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
            Toast.makeText(this, "No application can handle this request. Please install a web browser or check your URL.",  Toast.LENGTH_LONG).show();
            //e.printStackTrace();
        }
    }
}
