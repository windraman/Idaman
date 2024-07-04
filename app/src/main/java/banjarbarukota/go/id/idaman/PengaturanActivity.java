package banjarbarukota.go.id.idaman;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.CopyOnWriteArrayList;

import banjarbarukota.go.id.idaman.Utility.Constants;

public class PengaturanActivity extends AppCompatActivity {

    Switch switchNotif, switchShowSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pengaturan);

        switchNotif = (Switch) findViewById(R.id.switchNotif);
        switchNotif.setChecked(Constants.TERIMA_NOTIF);
        switchNotif.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Constants.USER_TERIMA_NOTIF = b;
                Constants.TERIMA_NOTIF = Constants.USER_TERIMA_NOTIF;
            }
        });

        switchShowSplash = (Switch) findViewById(R.id.switchShowSplash);
        switchShowSplash.setChecked(Constants.SHOW_SPLASH);
        switchNotif.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Constants.SHOW_SPLASH = b;
            }
        });
    }
}
