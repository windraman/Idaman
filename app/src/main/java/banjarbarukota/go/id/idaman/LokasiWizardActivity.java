package banjarbarukota.go.id.idaman;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import banjarbarukota.go.id.GlobalClass;
import banjarbarukota.go.id.idaman.Adapter.PageAdapter;
import banjarbarukota.go.id.idaman.Adapter.Poto;
import banjarbarukota.go.id.idaman.Fragments.Langkah1Fragment;
import banjarbarukota.go.id.idaman.Fragments.Langkah3Fragment;
import banjarbarukota.go.id.idaman.Utility.Constants;
import banjarbarukota.go.id.idaman.Utility.LocationMonitoringService;
import banjarbarukota.go.id.idaman.Utility.OkHttpPostHandler;
import pub.devrel.easypermissions.BuildConfig;
import pub.devrel.easypermissions.EasyPermissions;


public class LokasiWizardActivity extends FragmentActivity {
    private static ViewPager viewPager;
    private PageAdapter mAdapter;
    private static Context context;
    static int curItem = 0;
    public LokasiWizardActivity lokasiWizardActivity;

    public String gps, lat, lon, tags, nama, deskripsi, jalan, nomor, regency_id, district_id, village_id, kode_pos, telepon, website, default_icon, mylat, mylng, created_by;

    ImageButton next, prev;
    TextView titleFagment;

    public static final String TAG = LokasiWizardActivity.class.getSimpleName();

    public static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

   public List<Poto> potoList= new ArrayList<>();
    private boolean mAlreadyStartedService = false;

    OkHttpPostHandler okpost;

    BroadcastReceiver bc = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String latitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LATITUDE);
            String longitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LONGITUDE);

            if (latitude != null && longitude != null) {
                mylat = latitude;
                mylng = longitude;
                //Langkah1Fragment
                if(Constants.SEND_MY_LOCATION){
                    //mylat = latitude;
                    //mylng = longitude;
                    // sendMyLoc(latitude,longitude);
                }
                //mMsgView.setText(getString(R.string.msg_location_service_started) + "\n Latitude : " + latitude + "\n Longitude: " + longitude);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        LokasiWizardActivity.context = getApplicationContext();

        setContentView(R.layout.entry_wizard);

        mAlreadyStartedService = false;

        mylat = null;
        mylng = null;

        lat = null;
        lon = null;

        regency_id = "6372";
        default_icon = "1";
        viewPager = (ViewPager) findViewById(R.id.StepPager);
        mAdapter = new PageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);


        titleFagment= (TextView) findViewById(R.id.titleFragment);

        next = (ImageButton) findViewById(R.id.btnNext);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curItem<2){
                    viewPager.setCurrentItem(curItem+1);
                }else {
                    Log.d("simpan","simpan");
                    saveLokasi();
                }
            }
        });

        prev = (ImageButton) findViewById(R.id.btnPrev);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curItem>0){
                    viewPager.setCurrentItem(curItem-1);
                }else{
                    finish();
                }
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected

                curItem = position;

                if(curItem==0){
                    titleFagment.setText("Pilih Lokasi (" + (position+1) + ")");
                }else if(curItem==1){
                    titleFagment.setText("Isi data (" + (position+1) + ")");
                }else if(curItem==2){
                    titleFagment.setText("Upload gambar (" + (position+1) + ")");
                }

                if(curItem==2){
                    next.setImageResource(R.drawable.ic_save_white_24dp);
                    prev.setVisibility(View.VISIBLE);
                }else if(curItem<2){
                    next.setImageResource(R.drawable.ic_navigate_next_black_24dp);
                }else if(curItem==0){
                    prev.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        startStep1();

    }

    public static Context getAppContext() {
        return LokasiWizardActivity.context;
    }

    public void setFragment(int index){
        String[] perms = { "android.permission..WRITE_EXTERNAL_STORAGE"};
        if (EasyPermissions.hasPermissions(this, perms)) {
            viewPager.setCurrentItem(index);
            Log.d("fragmet : ", String.valueOf(index));
        }else{
            EasyPermissions.requestPermissions(this, "Meminta Ijin Akses", 17, perms);
            return;
        }
    }

    public static int getFragment(){
        return curItem;
    }

    public void saveLokasi(){
        okpost = new OkHttpPostHandler();
        Log.d("isi", lat + "," + lon + "," + tags + "," + nama + "," + jalan + "," + nomor + "," + kode_pos + "," + telepon + "," + website + "," + default_icon + "," + regency_id +  "," + district_id +","+ village_id );
        String newId = okpost.nuloc(tags,nama, deskripsi,lat,lon,jalan,nomor,regency_id,district_id,village_id,kode_pos,telepon,website,default_icon,((GlobalClass) getApplication()).getStringPref("user_id"));
        //String newId = okpost.nuloc(nama, deskripsi);
        if(newId!=null ) {

            if (potoList.size() > 0) {
                View v;
                ArrayList<String> mannschaftsnamen = new ArrayList<String>();
                EditText edtCaption;
                TextView tvPath;
                for (int i = 0; i < Langkah3Fragment.potoRecyclerView.getChildCount(); i++) {
                    v = Langkah3Fragment.potoRecyclerView.getChildAt(i);
                    edtCaption = (EditText) v.findViewById(R.id.edtCaptionPotoItem);
                    tvPath = (TextView) v.findViewById(R.id.tvRealPotoPath);
                    Log.d("caption", edtCaption.getText().toString());
                    Log.d("path", tvPath.getText().toString());
                    String postres = okpost.uploadMedia("lokasi",newId,tvPath.getText().toString(),tvPath.getTag().toString(),edtCaption.getText().toString(),((GlobalClass) getApplication()).getStringPref("user_id"));
                    Log.d("upload_media",postres);
                }
//                for (Poto p : potoList) {
//                    Log.d("potoupload", p.getPotoPath());
//                    //String potoCaption =
//                    String postres = okpost.uploadMedia("lokasi",newId,p.getPotoUri().getPath(),p.getPotoName(),p.getPotoCaption(),((GlobalClass) getApplication()).getStringPref("user_id"));
//                    Log.d("upload_media",postres);
//                 }
            }
            finish();
        }else{
            Toast.makeText(this.lokasiWizardActivity,"Gagal, menambah data. Silahkan Coba Lagi.", Toast.LENGTH_LONG).show();
        }
    }

    @JavascriptInterface
    public  void  runLocationService(){
        Intent intent = new Intent(this, LocationMonitoringService.class);
        startService(intent);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                bc, new IntentFilter(LocationMonitoringService.ACTION_LOCATION_BROADCAST)
        );
    }

    @JavascriptInterface
    public  void  stopLocationService(){
        if (mAlreadyStartedService) {

            //mMsgView.setText(R.string.msg_location_service_started);
            Log.d("stoploc","locstop");


            //Start location sharing service to app server.........
            Intent intent = new Intent(this, LocationMonitoringService.class);
            stopService(intent);

            LocalBroadcastManager.getInstance(this).unregisterReceiver(
                    bc);

            mAlreadyStartedService = false;
            //Ends................................................
        }

    }

    /**
     * Step 1: Check Google Play services
     */
    public void startStep1() {
        Log.d("setp1","on");
        //Check whether this user has installed Google play service which is being used by Location updates.
        if (isGooglePlayServicesAvailable()) {

            //Passing null to indicate that it is executing for the first time.
            startStep2(null);

        } else {
            Toast.makeText(getApplicationContext(), R.string.no_google_playservice_available, Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Step 2: Check & Prompt Internet connection
     */
    private Boolean startStep2(DialogInterface dialog) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            promptInternetConnect();
            return false;
        }


        if (dialog != null) {
            dialog.dismiss();
        }

        //Yes there is active internet connection. Next check Location is granted by user or not.

        if (checkPermissions()) { //Yes permissions are granted by the user. Go to the next step.
            startStep3();
        } else {  //No user has not granted the permissions yet. Request now.
            requestPermissions();
        }
        return true;
    }

    /**
     * Show A Dialog with button to refresh the internet state.
     */
    private void promptInternetConnect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_alert_no_intenet);
        builder.setMessage(R.string.msg_alert_no_internet);

        String positiveText = getString(R.string.btn_label_refresh);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        //Block the Application Execution until user grants the permissions
                        if (startStep2(dialog)) {

                            //Now make sure about location permission.
                            if (checkPermissions()) {

                                //Step 2: Start the Location Monitor Service
                                //Everything is there to start the service.
                                startStep3();
                            } else if (!checkPermissions()) {
                                requestPermissions();
                            }

                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Step 3: Start the Location Monitor Service
     */
    private void startStep3() {

        //And it will be keep running until you close the entire application from task manager.
        //This method will executed only once.

        if (!mAlreadyStartedService) {

            //mMsgView.setText(R.string.msg_location_service_started);
            Log.d("step3","jalan");

            //Start location sharing service to app server.........

            runLocationService();

            mAlreadyStartedService = true;
            //Ends................................................
        }
    }

    /**
     * Return the availability of GooglePlayServices
     */
    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(this, status, 2404).show();
            }
            return false;
        }
        return true;
    }


    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        int permissionState2 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        int permissionState3 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED && permissionState3 == PackageManager.PERMISSION_GRANTED;

    }

    /**
     * Start permissions requests.
     */
    private void requestPermissions() {

        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

        boolean shouldProvideRationale2 =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        boolean shouldProvideRationale3 =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);


        // Provide an additional rationale to the img_user. This would happen if the img_user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale || shouldProvideRationale2 || shouldProvideRationale3 ) {
            Log.i(LokasiWizardActivity.TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(LokasiWizardActivity.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the img_user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If img_user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Log.i(TAG, "Permission granted, updates requested, starting location updates");
                startStep3();

            } else {
                // Permission denied.

                // Notify the img_user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the img_user for permission (device policy or "Never ask
                // again" prompts). Therefore, a img_user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    @Override
    public void onStop () {
        //do your stuff here
        Log.d("selesai","stop");
        super.onStop();
    }

    @Override
    protected void onPause() {
        // if(startTracker != null && startTracker.equals("nyalakan")){
        //stopService(new Intent(this, LocationMonitoringService.class));
        //mAlreadyStartedService = false;
        //Ends................................................
        //stopCompass();


        Log.d("selesai", "pause");
        //}
        stopLocationService();
        super.onPause();
    }

}
