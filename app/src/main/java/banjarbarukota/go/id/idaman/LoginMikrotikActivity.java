package banjarbarukota.go.id.idaman;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import androidx.appcompat.app.AppCompatActivity;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Wahyu on 2/15/2018.
 */

public class LoginMikrotikActivity extends AppCompatActivity {
    WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.singlewv);

        StrictMode.ThreadPolicy strictModeThreadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(strictModeThreadPolicy);

        Intent intent = getIntent();
        int tOut = Integer.parseInt(intent.getStringExtra("time_out"));
        String SERVER_MIKROTIK = intent.getStringExtra("server_mikrotik");

        myWebView = new WebView(this);
        myWebView = findViewById(R.id.mWV);
        myWebView.setWebViewClient(new WebViewClient());
        WebSettings ws = myWebView.getSettings();
        ws.setJavaScriptEnabled(true);
        myWebView.loadUrl("http://"+SERVER_MIKROTIK+"/login");
        new CountDownTimer(tOut, 1000) {

            public void onTick(long millisUntilFinished) {
                //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
//                Intent homin = new Intent(getBaseContext(), HomeActivity.class);
//                startActivity(homin);
                finish();
            }
        }.start();
    }
}
