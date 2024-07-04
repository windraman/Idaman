package banjarbarukota.go.id.idaman;

/**
 * Created by Wahyu on 3/26/2018.
 */

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import banjarbarukota.go.id.GlobalClass;
import banjarbarukota.go.id.idaman.Utility.Constants;
import banjarbarukota.go.id.idaman.Utility.LocationMonitoringService;
import banjarbarukota.go.id.idaman.Utility.ResponsiveScrollView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pub.devrel.easypermissions.EasyPermissions;

//import android.support.design.widget.FloatingActionButton;


public class WebViewActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener , SensorEventListener, EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    Integer lasthit = Integer.valueOf(0);
    MenuItem nav_admin;
    Menu nav_menu;
    MenuItem nav_stats;
    private WebView myWebView;

    Integer chit = Integer.valueOf(0);

    private static final String TAG = WebViewActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private boolean mAlreadyStartedService = false;

    // record the compass picture angle turned
    private float currentDegree = 0f;

    // device sensor manager
    private SensorManager mSensorManager;

    String mylat = "";
    String mylng = "";
    Integer mydegree = 0;

    int mAzimuth;
    private Sensor mRotationV, mAccelerometer, mMagnetometer;
    boolean haveSensor = false, haveSensor2 = false;
    float[] rMat = new float[9];
    float[] orientation = new float[3];
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    String startTracker = "";

    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int FILECHOOSER_RESULTCODE = 1;
    private static final int KATEGORI_RESULTCODE = 111;

    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;

    FloatingSearchView mSearchView;

    String mauke;


    BroadcastReceiver bc = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String latitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LATITUDE);
            String longitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LONGITUDE);

            if (latitude != null && longitude != null) {
                mylat = latitude;
                mylng = longitude;
                myWebView.loadUrl("javascript:myLocation("+latitude+","+longitude+")");
                if(Constants.SEND_MY_LOCATION){
                    sendMyLoc(latitude,longitude);
                }
                //mMsgView.setText(getString(R.string.msg_location_service_started) + "\n Latitude : " + latitude + "\n Longitude: " + longitude);
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
//                super.onActivityResult(requestCode, resultCode, data);
//                return;
//            }
            if (requestCode == INPUT_FILE_REQUEST_CODE || mFilePathCallback != null) {
                Uri[] results = null;
                // Check that the response is a good one

                if (resultCode == Activity.RESULT_OK) {
                    if (data == null) {
                        // If there is not data, then we may have taken a photo
                        if (mCameraPhotoPath != null) {
                            results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                        }
                    } else {
                        String dataString = data.getDataString();
                        if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }
                mFilePathCallback.onReceiveValue(results);
                mFilePathCallback = null;
            }
            if (requestCode == KATEGORI_RESULTCODE) {
                if(resultCode == Activity.RESULT_OK) {
                    myWebView.loadUrl("javascript:getPlaces('" + data.getStringExtra("cari") + "')");
                    mSearchView.setLeftMenuOpen(false);
                }
            }
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (null == this.mUploadMessage) {
                    return;
                }
                Uri result = null;
                try {
                    if (resultCode != RESULT_OK) {
                        result = null;
                    } else {
                        // retrieve from the private variable if the intent is null
                        result = data == null ? mCapturedImageURI : data.getData();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "activity :" + e,
                            Toast.LENGTH_LONG).show();
                }
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
            if (requestCode == KATEGORI_RESULTCODE) {
                if(resultCode == Activity.RESULT_OK) {
                    myWebView.loadUrl("javascript:getPlaces('" + data.getStringExtra("cari") + "')");
                }
            }
        }
        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.singlewv);


        myWebView = new WebView(this);
        myWebView = (WebView) findViewById(R.id.mWV);


        myWebView.setWebViewClient (new myWebViewClient());
        myWebView.setWebChromeClient(new ChromeClient());

        WebSettings s = myWebView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
//        s.setAppCacheEnabled(false);
//        s.setCacheMode(WebSettings.LOAD_NO_CACHE);
        s.setLoadWithOverviewMode(true);
        s.setAllowFileAccess(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            s.setAllowUniversalAccessFromFileURLs(true);
            s.setAllowFileAccessFromFileURLs(true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            myWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else if(Build.VERSION.SDK_INT >=11 && Build.VERSION.SDK_INT < 19) {
            myWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        Intent i = getIntent();
        final String username = i.getStringExtra("username");
        final String openurl = i.getStringExtra("openurl");
        mauke = i.getStringExtra("openurl");
        startTracker = i.getStringExtra("startTracker");

        myWebView.addJavascriptInterface(this, "Android");
        //myWebView.loadUrl("file:///android_asset/www/index.html");
        myWebView.loadUrl(openurl);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE))
            { myWebView.setWebContentsDebuggingEnabled(true); }
        }



        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);

        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                //get suggestions based on newQuery
                if(newQuery.length()>3) {
                    myWebView.loadUrl("javascript:getPlaces('" + newQuery + "')");
                }
                //pass them on to the search view
               // mSearchView.swapSuggestions(newSuggestions);
            }
        });

        mSearchView.setOnLeftMenuClickListener(
                new FloatingSearchView.OnLeftMenuClickListener() {
                    @Override
                    public void onMenuOpened() {
                        Intent kategoriIntent = new Intent(getApplicationContext(), MenuKategoriActivity.class);
                        startActivityForResult(kategoriIntent,KATEGORI_RESULTCODE);
                    }

                    @Override
                    public void onMenuClosed() {

                    }
                } );

        if(startTracker != null ){
            if(startTracker.equals("nyalakan")){
             //   runLocationService();
                startStep1();
                mSearchView.setVisibility(View.VISIBLE);
            }else if(startTracker.equals("nyala")){
                //runLocationService();
                startStep1();
                mSearchView.setVisibility(View.GONE);
            }
            //mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        }else{
            mSearchView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onRationaleAccepted(int requestCode) {

    }

    @Override
    public void onRationaleDenied(int requestCode) {

    }


    public class ChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }
        // For Android 5.0
        public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, WebChromeClient.FileChooserParams fileChooserParams) {
            // Double check that we don't have any existing callbacks

                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePath;
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.e(TAG, "Unable to create Image File", ex);
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }
                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");
                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }
                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);


            return true;
        }
    }

    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        String[] perms = { "android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE" };
        if (EasyPermissions.hasPermissions(WebViewActivity.this, perms)) {
            mUploadMessage = uploadMsg;
            // Create AndroidExampleFolder at sdcard
            // Create AndroidExampleFolder at sdcard
            File imageStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES)
                    , "AndroidExampleFolder");
            if (!imageStorageDir.exists()) {
                // Create AndroidExampleFolder at sdcard
                imageStorageDir.mkdirs();
            }
            // Create camera captured image file path and name
            File file = new File(
                    imageStorageDir + File.separator + "IMG_"
                            + String.valueOf(System.currentTimeMillis())
                            + ".jpg");
            mCapturedImageURI = Uri.fromFile(file);
            // Camera capture image intent
            final Intent captureIntent = new Intent(
                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            // Create file chooser intent
            Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
            // Set camera intent to file chooser
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                    , new Parcelable[] { captureIntent });
            // On select image call onActivityResult method of activity
            startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
         }else{
            EasyPermissions.requestPermissions(WebViewActivity.this, "Meminta Ijin Akses", 15, perms);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        File imageFile = null;
        String[] perms = { "android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE" };
        if (EasyPermissions.hasPermissions(WebViewActivity.this, perms)) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            imageFile = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

        }else{
            EasyPermissions.requestPermissions(WebViewActivity.this, "Meminta Ijin Akses", 15, perms);
        }
        return imageFile;
    }

    public class myWebViewClient extends WebViewClient {
        ProgressDialog progressDialog;
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("mailto:")) {
                // Could be cleverer and use a regex
                //Open links in new browser
                view.getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                // Here we can open new activity
                return true;
            }else {
                // Stay within this webview and load url
                view.loadUrl(url);
                return true;
            }
        }
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // Then show progress  Dialog
            // in standard case YourActivity.this
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(WebViewActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();
            }
        }
        // Called when all page resources loaded
        public void onPageFinished(WebView view, String url) {
            try {
                // Close progressDialog
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
                if (view.getUrl().equals("https://siapkk.banjarbarukota.go.id/admin"))
                {
                    myWebView.clearHistory();
                    Log.d("Bersih","yes");
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @JavascriptInterface
    public void clearWVHistory(){
        myWebView.clearHistory();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
                myWebView.goBack();
                return true;
        }else{
           Log.d("selesai","iya");
            finish();
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    @JavascriptInterface
    public void closeSoftKeyboard(){

            if (mSearchView.isSearchBarFocused()) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
                mSearchView.clearFocus();
            }
//            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
//            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            //imm.hideSoftInputFromWindow(mSearchView.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

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

    @JavascriptInterface
    public void openLokasiDetail(String id)
    {
        Intent localIntent = new Intent(getBaseContext(), TempatDetailActivity.class);
        localIntent.putExtra("id", id);
        startActivity(localIntent);
    }

    @JavascriptInterface
    public void openChat(String key_id,String nama_table)
    {
        Intent localIntent = new Intent(getBaseContext(), ChatActivity.class);
        localIntent.putExtra("key_id", key_id);
        localIntent.putExtra("nama_table", nama_table);
        startActivity(localIntent);
    }

    @JavascriptInterface
    public void backToLogin()
    {
//        Intent localIntent = new Intent(getBaseContext(), MainLoginActivity.class);
//        startActivity(localIntent);
        finish();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rMat, event.values);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(rMat, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(rMat, orientation);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }

        mAzimuth = Math.round(mAzimuth);
       // compass_img.setRotation(-mAzimuth);

        String where = "NW";

        if (mAzimuth >= 350 || mAzimuth <= 10)
            where = "N";
        if (mAzimuth < 350 && mAzimuth > 280)
            where = "NW";
        if (mAzimuth <= 280 && mAzimuth > 260)
            where = "W";
        if (mAzimuth <= 260 && mAzimuth > 190)
            where = "SW";
        if (mAzimuth <= 190 && mAzimuth > 170)
            where = "S";
        if (mAzimuth <= 170 && mAzimuth > 100)
            where = "SE";
        if (mAzimuth <= 100 && mAzimuth > 80)
            where = "E";
        if (mAzimuth <= 80 && mAzimuth > 10)
            where = "NE";

        mydegree = mAzimuth;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }



    @JavascriptInterface
    public String getSiapkk(){
        String username = ((GlobalClass) getApplication()).getStringPref("username");
        String password = ((GlobalClass) getApplication()).getStringPref("password");

        return username +","+password+","+mauke;
    }


    @JavascriptInterface
    public String getServer() {
        return  "http://banjarbarukota.go.id/bjbadmin/public/api/"; //this.dm.ambilSetup().get(8).toString();
    }

    @JavascriptInterface
    public String getMyLocation() {
        return  mylat + "," + mylng;
    }

    @JavascriptInterface
    public String getMyIcon() {
        return  ((GlobalClass) getApplication()).getStringPref("icon");
    }

    @JavascriptInterface
    public void getDirection(String lat, String lng) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+lat+","+lng);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }else {
            Toast.makeText(getApplicationContext(),"Aplikasi Google Maps tidak ditemukan !", Toast.LENGTH_LONG);
        }
    }

    @JavascriptInterface
    public int getDegree(){
        return mydegree;
    }

    public boolean isPopupOn()
    {
        return  true ;//Boolean.valueOf(this.dm.ambilSaved("popup").get(2).toStringw()).booleanValue();
    }

    @JavascriptInterface
    public void openPengumuman(String paramString)
    {
        if (isPopupOn())
        {
//            this.dm.addRowSaved(paramString, "1");
//            Intent localIntent = new Intent(getBaseContext(), PengumumanActivity.class);
//            localIntent.putExtra("idberita", paramString);
//            startActivity(localIntent);
        }
    }

    @JavascriptInterface
    public void setLastHit()
    {
        Log.d("lasthit", this.chit.toString());
        this.lasthit = this.chit;
    }

    @JavascriptInterface
    public void shareTo(String paramString)
    {
        Intent localIntent = new Intent("android.intent.action.SEND");
        localIntent.setType("text/plain");
        localIntent.putExtra("android.intent.extra.TEXT", paramString);
        startActivity(Intent.createChooser(localIntent, "Bagikan Ke..."));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Logout();
            return true;
        }
        if (id == R.id.action_option) {
//            Intent i = new Intent(this, OptionActivity.class);
//            startActivity(i);
            return true;
        }
        if (id == R.id.action_refresh) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void Logout() {
        //dm.updateSetup("", "", "", "", "", "", "", "", (long) 1);
        Intent i = new Intent(WebViewActivity.this, LoginMikrotikActivity.class);
        startActivity(i);
        finish();
    }

    @JavascriptInterface
    public String getMe()
    {

        return "me";
    }

    @JavascriptInterface
    public String getReadedInfo(String paramString)
    {
        String str = "0";
//        if (this.dm.ambilSaved(paramString).size() > 0) {
//            str = this.dm.ambilSaved(paramString).get(1).toString();
//        }
        return str;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_About) {
////            Intent in = new Intent(getBaseContext(), AboutActivity.class);
////            startActivity(in);
//        } else if (id == R.id.nav_Admin) {
////            if (Integer.parseInt(dm.ambilSetup().get(3).toString()) == 1) {
//////                Intent in = new Intent(getBaseContext(), AdminActivity.class);
//////                startActivity(in);
////            } else {
////                Toast.makeText(this, "Anda tidak punya hak akses !", Toast.LENGTH_LONG).show();
////            }
//
//        } else if (id == R.id.nav_Statistik) {
////            if (Integer.parseInt(dm.ambilSetup().get(3).toString()) == 1) {
//////                Intent in = new Intent(getBaseContext(), StatistikActivity.class);
//////                startActivity(in);
////            } else {
////                Toast.makeText(this, "Anda tidak punya hak akses !", Toast.LENGTH_LONG).show();
////            }
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @JavascriptInterface
    public void openDetail(String idberita, String mode) {
        //Toast.makeText(getBaseContext(),idberita,Toast.LENGTH_LONG).show();
//        Intent in = new Intent(getBaseContext(), DetailActivity.class);
//        in.putExtra("idberita", idberita);
//        in.putExtra("mode", mode);
        //in.putExtra("idgrade", ProtokolActivity.dm.ambilSetup().get(4).toString());
        //in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //startActivity(in);
    }


    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
            //Toast.makeText(getApplicationContext(), "Please wait, it may take a few minute...", Toast.LENGTH_SHORT).show();
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        myWebView.onResume();
        startStep1();
        if(startTracker != null && startTracker.equals("nyalakan")){
            startStep1();

            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            startCompass();
        }

        // for the system's orientation sensor registered listeners
    }


    /**
     * Step 1: Check Google Play services
     */
    private void startStep1() {

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
        AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this);
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

        return permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED;

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


        // Provide an additional rationale to the img_user. This would happen if the img_user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale || shouldProvideRationale2) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(WebViewActivity.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the img_user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(WebViewActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
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

    private final OkHttpClient client = new OkHttpClient();

    public void sendMyLoc(String lat, String lng) {
        String URL = "https://siapkk.banjarbarukota.go.id/api/kirim_loc";

        FormBody.Builder builder = new FormBody.Builder();
        builder.add("nik", ((GlobalClass) getApplication()).getStringPref("username"));
        builder.add("lat", lat);
        builder.add("lng", lng);

        RequestBody formBody = builder.build();

        Request request = new Request.Builder()
                .url(URL)
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            //Log.d("Siapkk Login", response.body().string());
            String jsondata = response.body().string();
            // System.out.println(response.body().string());
            try {
                JSONObject jobj = new JSONObject(jsondata);
                Integer success = jobj.getInt("api_status");
                if(success==1){

                }else{

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } catch (IOException e) {
            Log.d("scan", e.toString());
            e.printStackTrace();
        }
    }

    public void startCompass() {
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null) {
            if ((mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) || (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null)) {
                noSensorsAlert();
            }
            else {
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                haveSensor = mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
                haveSensor2 = mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
            }
        }
        else{
            mRotationV = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            haveSensor = mSensorManager.registerListener(this, mRotationV, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void noSensorsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Your device doesn't support the Compass.")
                .setCancelable(false)
                .setNegativeButton("Close",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        stopCompass();
                    }
                });
        alertDialog.show();
    }

    public void stopCompass() {
        if (haveSensor) {
            mSensorManager.unregisterListener(this, mRotationV);
        }
        else {
            mSensorManager.unregisterListener(this, mAccelerometer);
            mSensorManager.unregisterListener(this, mMagnetometer);
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
        myWebView.onPause();
        //myWebView.pauseTimers();
        super.onPause();
    }



    @Override
    public void onDestroy() {

        //Stop location sharing service to app server.........
        if(startTracker != null && startTracker.equals("nyalakan")){
            //stopService(new Intent(this, LocationMonitoringService.class));
            //mAlreadyStartedService = false;
            //Ends................................................
            stopCompass();
            //stopLocationService();
            Log.d("selesai","destroy");
        }


        super.onDestroy();
    }
}
