package banjarbarukota.go.id.idaman.Utility;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.net.InetAddress;
import static banjarbarukota.go.id.idaman.Utility.Constants.MAIN_SERVER;

public class ServerCheck {
    public boolean isServerAvailable(View view) {
        try {
            InetAddress ipAddr = InetAddress.getByName(MAIN_SERVER);
            //You can replace it with your name

            return !ipAddr.equals("");

        } catch (Exception e) {
            Log.d("server",e.toString());
            Snackbar.make(view, "Koneksi ke server gagal ! Perikasa koneksi internet anda.", Snackbar.LENGTH_LONG)
                    .setAction("Ulangi", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            finish();
//                            startActivity(getIntent());
                        }
                    });
            return false;
        }
    }


}
