package banjarbarukota.go.id.idaman;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import banjarbarukota.go.id.GlobalClass;
import banjarbarukota.go.id.idaman.Utility.Constants;
import banjarbarukota.go.id.idaman.Utility.Utility;
import info.vividcode.android.zxing.CaptureActivity;
import info.vividcode.android.zxing.CaptureActivityIntents;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pub.devrel.easypermissions.EasyPermissions;

//import com.google.android.gms.auth.api.signin.GoogleSignIn;
//import com.google.android.gms.auth.api.signin.GoogleSignInClient;

public class MainLoginActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks{

    Button btnLoginNip, btnDaftar, btnAsTamu;
    TextView info, txtNip, txtPassword;
    String SERVER_KITA = "http://10.20.30.110:81/idaman/";
    String SERVER_MIKROTIK = "10.10.10.1";
    String mac,ssid, ip, logged_with, login_token, granted;
    static final int RC_SIGN_IN = 15;


    public static SharedPreferences preferenceSetting;
    public static SharedPreferences.Editor preferenceEditor;

    private static final int PREFERENCE_MODE_PRIVATE = 0;

    //GoogleSignInClient mGoogleSignInClient;

    String[] akunperms = { "android.permission.GET_ACCOUNTS","android.permission.MANAGE_ACCOUNTS"};

    long unixTime = System.currentTimeMillis() / 1000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main_login);


        StrictMode.ThreadPolicy strictModeThreadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(strictModeThreadPolicy);


        info = (TextView) findViewById( R.id.info );

        btnLoginNip = (Button) findViewById(R.id.btn_login_nip);
        btnDaftar = (Button) findViewById(R.id.btn_daftar);
        btnAsTamu = (Button) findViewById(R.id.btnAsTamu);
        txtNip = (TextView) findViewById(R.id.txt_nip);
        txtPassword = (TextView) findViewById(R.id.txt_password);


        Intent i = getIntent();

        if(i.getStringExtra("state").equalsIgnoreCase("logout")){
            logout();
        }

//        Intent in = new Intent(this,LoginMikrotikActivity.class);
//        in.putExtra("time_out","500");
//        startActivity(in);


        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo coninfo = manager.getConnectionInfo();
        mac = coninfo.getMacAddress();
        ip = Formatter.formatIpAddress(coninfo.getIpAddress());
        ssid = coninfo.getSSID();
        ssid = ssid.replace("\"", "");
        //granted = "no";
        Log.d("ssid", ip);
        String seq = ip;
        if(ip.length()>7){
            seq = ip.substring(0,8);
        }

        if(seq.equals("10.10.10")||seq.equals("10.30.30")||seq.equals("10.30.31")){
            //Toast.makeText(this,"diskominfo",Toast.LENGTH_LONG).show();
            SERVER_KITA = "http://10.20.30.110:81/idaman/";
            SERVER_MIKROTIK = "10.20.24.1";
            ((GlobalClass) getApplication()).saveStringPref("SERVER_KITA",SERVER_KITA);
            ((GlobalClass) getApplication()).saveStringPref("SERVER_MIKROTIK",SERVER_MIKROTIK);
        }else{
            //Toast.makeText(getApplicationContext(),"Jaringan Luar",Toast.LENGTH_LONG).show();
            SERVER_KITA = "https://siapkk.banjarbarukota.go.id";
            ((GlobalClass) getApplication()).saveStringPref("SERVER_KITA",SERVER_KITA);
        }

        if (!EasyPermissions.hasPermissions(MainLoginActivity.this, akunperms)) {
            EasyPermissions.requestPermissions(MainLoginActivity.this, "Meminta Ijin Akses, Menyimpan Data Login Anda.", 15, akunperms);
        }

        if (getPref("granted")==null){
            ((GlobalClass) getApplication()).saveStringPref("granted","no");
        }

        granted = getPref("granted");
        // Toast.makeText(getBaseContext(),granted,Toast.LENGTH_LONG).show();



        if(granted.equals("yes")){
            //grantMe();
            //Toast.makeText(this,getPref("password"),Toast.LENGTH_LONG).show();
            //LogMeIn(getPref("logged_with"),getPref("password"));
            //SatuLogin(((GlobalClass) getApplication()).getStringPref("username"),((GlobalClass) getApplication()).getStringPref("password"));
            Constants.TERIMA_NOTIF = Constants.USER_TERIMA_NOTIF;
            Intent homin = new Intent(this, Main2Activity.class);
            startActivity(homin);
            finish();

        }else{
            pilihAkunSatuLogin();
            ((GlobalClass) getApplication()).saveStringPref("nama_tampilan", "");
            ((GlobalClass) getApplication()).saveStringPref("nama_lengkap", "");
            ((GlobalClass) getApplication()).saveStringPref("login_pakai", "");
            ((GlobalClass) getApplication()).saveStringPref("foto", "");
        }




        //getLogNip();

        //login pakai NIP

        btnLoginNip.setOnClickListener(new View.OnClickListener() {
            @Override
            //@AfterPermissionGranted(RC_ACCOUNT)
            public void onClick(View view) {

                if(txtNip.getText().length()>0 && txtPassword.getText().length() > 0) {
//                LogMeIn(txtNip.getText().toString(),txtPassword.getText().toString());
                    SatuLogin(txtNip.getText().toString(),txtPassword.getText().toString());
                }else{
                    Toast.makeText(getApplicationContext(), "Isi Dulu !", Toast.LENGTH_LONG).show();
                }
            }
        });

        //login pakai NIP
        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//               Intent dafin  = new Intent(MainLoginActivity.this, DaftarActivity.class);
//               startActivity(dafin);
                openUrl("http://siapkk.banjarbarukota.go.id/admin/register");
            }
        });

        btnAsTamu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GlobalClass) getApplication()).saveStringPref("nama_tampilan", "TAMU");
                ((GlobalClass) getApplication()).saveStringPref("login_pakai", "tamu");
                ((GlobalClass) getApplication()).saveStringPref("foto", "");
                Constants.TERIMA_NOTIF = false;

                Intent homin = new Intent(getBaseContext(), Main2Activity.class);
                startActivity(homin);
                finish();
            }
        });




    }


    @JavascriptInterface
    public void openUrl(String openurl)
    {
        Intent localIntent = new Intent(getBaseContext(), WebViewActivity.class);

        String params[] = openurl.split(",");
        if(params.length>1) {
            localIntent.putExtra("openurl", params[0]);
            localIntent.putExtra("startTracker", params[1]);
        }else{
            localIntent.putExtra("openurl", openurl);
        }
        startActivity(localIntent);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 37) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                String value = data.getStringExtra("SCAN_RESULT");
                String[] eval = value.split(",");
                HashMap<String, String> params = new HashMap<>();
                params.put("mac_orig", mac);
                params.put("logged_with", logged_with);
                params.put("login_token", login_token);
                params.put("mac", eval[0].toString());
                params.put("ip", eval[1].toString());
                params.put("ssid", ssid);
                grantThis(SERVER_KITA + "scan.php", params);

            } else if (resultCode == Activity.RESULT_CANCELED) {
                //Toast.makeText(getApplicationContext(), "Scan Gagal ! Silahkan Coba Lagi.", Toast.LENGTH_LONG).show();
            }
        }if (requestCode == RC_SIGN_IN) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleSignInResult(task);
        } else {

        }
    }

//    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
//        try {
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//
//            // Signed in successfully, show authenticated UI.
//            //updateUI(account);
//            Log.w("GSIGN", "signInResult=" + account.toString());
//        } catch (ApiException e) {
//            // The ApiException status code indicates the detailed failure reason.
//            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.w("GSIGN", "signInResult:failed code=" + e.getStatusCode());
//            //updateUI(null);
//        }
//    }

    private final OkHttpClient client = new OkHttpClient();

    //Meloginakan
    public void grantThis(String URL, HashMap<String, String> params){
       // Toast.makeText(this, params.toString(), Toast.LENGTH_LONG).show();
        Log.d("params", params.toString());

        FormBody.Builder builder = new FormBody.Builder();
        for ( Map.Entry<String, String> entry : params.entrySet() ) {
            builder.add( entry.getKey(), entry.getValue());
        }

        RequestBody formBody = builder.build();

        Request request = new Request.Builder()
                .url(URL)
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            Log.d("scan",response.body().string());
            Intent in = new Intent(this,LoginMikrotikActivity.class);
            in.putExtra("time_out","3000");
            in.putExtra("server_mikrotik",SERVER_MIKROTIK);
            startActivity(in);

        } catch (IOException e) {
            Log.d("scan",e.toString());
            e.printStackTrace();
        }
        //finish();
    }

    public void addIdamanAccount(String nik, String password, Bundle userData){
        AccountManager accountManager = AccountManager.get(MainLoginActivity.this); //this is Activity
        Account account = new Account(nik + "\n" ,"banjarbarukota.go.id.idaman");
        boolean success = accountManager.addAccountExplicitly(account,password,userData);
        if(success){
            Log.d("Idaman","Account created");
        }else{
            Log.d("Idaman","Account creation failed. Look at previous logs to investigate");
        }
    }

    public void pilihAkunSatuLogin(){
        //if (EasyPermissions.hasPermissions(MainLoginActivity.this, akunperms)) {
            final ArrayList<String> gUsernameList = new ArrayList<String>();
            final AccountManager accountManager = AccountManager.get(this);
            final Account[] accounts = accountManager.getAccountsByType("banjarbarukota.go.id.idaman");

            //Log.d("idaman account", accounts.toString());

            gUsernameList.clear();
            if(accounts.length>0) {
                //loop
                for (Account account : accounts) {
                    gUsernameList.add(account.name);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Pilih Akun Satu Login");

                ListView lv = new ListView(this);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>
                        (this, android.R.layout.simple_list_item_1, android.R.id.text1, gUsernameList);

                lv.setAdapter(adapter);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String selnik = gUsernameList.get(position).toString();
                        String selpass = accountManager.getUserData(accounts[position], "password");
                        Log.d("Satu login", gUsernameList.get(position) + "(" + accountManager.getUserData(accounts[position], "password") + ")");
                        txtNip.setText(selnik);
                        txtPassword.setText(selpass);
                        btnLoginNip.performClick();
                    }
                });

           builder.setView(lv);
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        lv.per
//                    }
//                });
                builder.setNegativeButton("Gunakan Akun Lain", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                final Dialog dialog = builder.create();
                dialog.show();
            }
//        }else{
//            EasyPermissions.requestPermissions(MainLoginActivity.this, "Meminta Ijin Akses", 15, akunperms);
//        }
    }

    public void SatuLogin(String nik,String password) {
        String URL =SERVER_KITA + "/api/siapkk";

        String unixTime = Utility.unixTime();
        String userAgent = System.getProperty("http.agent");

        FormBody.Builder builder = new FormBody.Builder();
        builder.add("nik", nik);
        builder.add("password", password);

        RequestBody formBody = builder.build();

        Request request = new Request.Builder()
                .url(URL)
                .addHeader("X-Authorization-Token", Utility.md5(Constants.SECRET_KEY+unixTime+userAgent))
                .addHeader("X-Authorization-Time",unixTime)
                .addHeader("User-Agent",userAgent)
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String respbody = response.body().string();;
            Log.d("Siapkk Login", respbody);
            String jsondata = respbody;
            //System.out.println(response.body().string());
            try {
                JSONObject jobj = new JSONObject(jsondata);
                Integer success = 0;
                if(jobj.getString("api_status")=="false"){
                    success = 0;
                }else if(jobj.getString("api_status")=="true"){
                    success = 1;
                }else{
                    success = jobj.getInt("api_status");
                }

                if(success==1){
                String user_id = jobj.getString("id");
                String nama_lengkap = jobj.getString("name");
                String nama_tampilan = jobj.getString("display_name");
                String foto = jobj.getString("photo");
                String email = jobj.getString("email");
                String nip = jobj.getString("nip");
                String level = jobj.getString("id_cms_privileges");
                String cms_privileges_name = jobj.getString("cms_privileges_name");
                Integer cms_privileges_is_superadmin = jobj.getInt("cms_privileges_is_superadmin");
                String district_id = jobj.getString("district_id");
                String village_id = jobj.getString("village_id");
                String rt = jobj.getString("rt");
                String rw = jobj.getString("rw");
                String kelurahan = jobj.getString("kelurahan");
                String kecamatan = jobj.getString("kecamatan");
                String icon = jobj.getString("icon");

                    ((GlobalClass) getApplication()).saveStringPref("user_id", user_id);
                    ((GlobalClass) getApplication()).saveStringPref("foto", foto);
                    ((GlobalClass) getApplication()).saveStringPref("nama_lengkap", nama_lengkap);
                    ((GlobalClass) getApplication()).saveStringPref("nama_tampilan", nama_tampilan);
                    ((GlobalClass) getApplication()).saveStringPref("username", nik);
                    ((GlobalClass) getApplication()).saveStringPref("password", password);
                    ((GlobalClass) getApplication()).saveStringPref("level", level);
                    ((GlobalClass) getApplication()).saveStringPref("cms_privileges_name", cms_privileges_name);
                    ((GlobalClass) getApplication()).saveStringPref("alamat", "RT." + rt + ",RW." + rw + "," + kelurahan + "," + kecamatan);
                    ((GlobalClass) getApplication()).saveStringPref("icon", icon);
                    ((GlobalClass) getApplication()).saveStringPref("login_pakai", "nik");
                    final Bundle userData = new Bundle();
                    userData.putString("password", password);
                    userData.putString("foto", foto);
                    userData.putString("nama_lengkap", nama_lengkap);
                    userData.putString("nama_tampilan", nama_tampilan);
                    userData.putString("username", nik);
                    userData.putString("level", level);
                    userData.putString("cms_privileges_name", cms_privileges_name);
                    userData.putInt("cms_privileges_is_superadmin", cms_privileges_is_superadmin);
                    userData.putString("district_id", district_id);
                    userData.putString("village_id", village_id);
                    userData.putString("rt", rt);
                    userData.putString("rw", rw);
                    userData.putString("kecamatan", kecamatan);
                    userData.putString("kelurahan", kelurahan);
                    userData.putString("icon", icon);
                    granted = "yes";
                    Constants.TERIMA_NOTIF = Constants.USER_TERIMA_NOTIF;
                    savePref();

                    addIdamanAccount(nik, password, userData);

                    Intent homin = new Intent(getBaseContext(), Main2Activity.class);
                    startActivity(homin);
                    finish();
                }else{
                    granted ="no";
                    Toast.makeText(getApplicationContext(), "Data anda salah/tidak terdaftar.", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Data anda salah/tidak terdaftar.", Toast.LENGTH_LONG).show();
            }

//            JSONArray userarr = jobj.getJSONArray("user");
//            JSONObject juser = new JSONObject(userarr.get(0).toString());


//            Intent in = new Intent(this,LoginMikrotikActivity.class);
//            in.putExtra("time_out","3000");
//            in.putExtra("server_mikrotik",SERVER_MIKROTIK);
//            startActivity(in);

        } catch (IOException e) {
            Log.d("scan", e.toString());
            e.printStackTrace();
        }
    }

    //login diri sendiri dari android
    public void LogMeIn(String nip, String password){
        String epass = get_SHA_512_SecurePassword(password, "");
        //Toast.makeText(this, mac + "/" + ip + "/" + ssid ,Toast.LENGTH_LONG).show();
        Log.d("password", password);
        Log.d("sha", epass);
        Log.d("server", SERVER_KITA);

        Request request = new Request.Builder()
                .url(SERVER_KITA + "get_user.php?username="+ nip +"&password=" + epass )
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String jsondata = response.body().string();
           // System.out.println(response.body().string());
            try {
                JSONObject jobj = new JSONObject(jsondata);
                String success = jobj.getString("success");

                if(success.equals("1")){

                    Log.d("sukses", "yes");
                    HashMap<String, String> params = new HashMap<>();
                    logged_with = nip;
                    login_token = nip;
                    params.put("mac_orig",mac);
                    params.put("logged_with",logged_with);
                    params.put("login_token",login_token);
                    params.put("mac",mac);
                    params.put("ip",ip);
                    params.put("ssid",ssid);
                    grantThis(SERVER_KITA + "scan.php", params);
                    granted = "yes";
                    Constants.TERIMA_NOTIF = Constants.USER_TERIMA_NOTIF;

                    JSONArray userarr = jobj.getJSONArray("user");
                    JSONObject juser = new JSONObject(userarr.get(0).toString());
                    ((GlobalClass) getApplication()).saveStringPref("foto", juser.getString("foto"));
                    ((GlobalClass) getApplication()).saveStringPref("nama_lengkap", juser.getString("nama_lengkap"));
                    ((GlobalClass) getApplication()).saveStringPref("username", juser.getString("username"));
                    ((GlobalClass) getApplication()).saveStringPref("level", juser.getString("level"));

                    savePref();
                    Intent homin = new Intent(getBaseContext(), Main2Activity.class);
                    startActivity(homin);
                    finish();
                }else{
                    Log.d("sukses", jobj.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        //finish();
    }

    public void grantMe(){
        HashMap<String, String> params = new HashMap<>();
        logged_with = getPref("nip");
        login_token = getPref("nip");
        params.put("mac_orig",mac);
        params.put("logged_with",logged_with);
        params.put("login_token",login_token);
        params.put("mac",mac);
        params.put("ip",ip);
        params.put("ssid",ssid);
        grantThis(SERVER_KITA + "scan.php", params);
    }

    public void startScanner(){
        // Membuat intent baru untuk memanggil CaptureActivity bawaan ZXing
                Intent captureIntent = new Intent(getBaseContext(), CaptureActivity.class);

                // Kemudian kita mengeset pesan yang akan ditampilkan ke user saat menjalankan QRCode scanning
                CaptureActivityIntents.setPromptMessage(captureIntent, "Barcode scanning...");

                // Melakukan startActivityForResult, untuk menangkap balikan hasil dari QR Code scanning
                startActivityForResult(captureIntent, 37);
    }

    public void logout(){
        txtNip.setText("");
        txtPassword.setText("");
        logged_with = "";
        login_token = "";
        granted = "no";
        mac = "";
        ip = "";
        ssid = "";
        ((GlobalClass) getApplication()).saveStringPref("user_id", "");
        ((GlobalClass) getApplication()).saveStringPref("foto", "");
        ((GlobalClass) getApplication()).saveStringPref("nama_lengkap", "");
        ((GlobalClass) getApplication()).saveStringPref("nama_tampilan", "");
        ((GlobalClass) getApplication()).saveStringPref("username", "");
        ((GlobalClass) getApplication()).saveStringPref("level", "");
        ((GlobalClass) getApplication()).saveStringPref("nik", "");
        ((GlobalClass) getApplication()).saveStringPref("cms_privileges_name", "");
        ((GlobalClass) getApplication()).saveStringPref("alamat", "");
        ((GlobalClass) getApplication()).saveStringPref("icon", "");
        ((GlobalClass) getApplication()).saveStringPref("login_pakai", "");
        Constants.TERIMA_NOTIF = false;
        savePref();
    }

    public void savePref(){
        ((GlobalClass) getApplication()).saveStringPref("password", txtPassword.getText().toString());
        ((GlobalClass) getApplication()).saveStringPref("logged_with", logged_with);
        ((GlobalClass) getApplication()).saveStringPref("nip", txtNip.getText().toString());
        ((GlobalClass) getApplication()).saveStringPref("login_token", login_token);
        ((GlobalClass) getApplication()).saveStringPref("granted", granted);
        ((GlobalClass) getApplication()).saveStringPref("ip",ip);
        ((GlobalClass) getApplication()).saveStringPref("ssid",ssid);
        ((GlobalClass) getApplication()).saveStringPref("mac",mac);
//        preferenceSetting = getPreferences(PREFERENCE_MODE_PRIVATE);
//        preferenceEditor = preferenceSetting.edit();
//
//        preferenceEditor.putString("password", txtPassword.getText().toString());
//        preferenceEditor.putString("logged_with", logged_with);
//        preferenceEditor.putString("nip", txtNip.getText().toString());
//        preferenceEditor.putString("login_token", login_token);
//        preferenceEditor.putString("granted", granted);
//        preferenceEditor.putString("mac",mac);
//        preferenceEditor.putString("ip",ip);
//        preferenceEditor.putString("ssid",ssid);
//        preferenceEditor.commit();
    }

    public String getPref(String key){
//        preferenceSetting = getPreferences(PREFERENCE_MODE_PRIVATE);
//        String resPrep = preferenceSetting.getString(key,"");
        String resPrep = ((GlobalClass) getApplication()).getStringPref(key);
        return resPrep;
    }

    public void getLogNip(){
        preferenceSetting = getPreferences(PREFERENCE_MODE_PRIVATE);
        txtNip.setText(((GlobalClass) getApplication()).getStringPref("nip"));
        txtPassword.setText(((GlobalClass) getApplication()).getStringPref("password"));
    }

    public String get_SHA_512_SecurePassword(String passwordToHash, String   salt){
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return generatedPassword;
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        if(requestCode==15){
            if(EasyPermissions.hasPermissions(MainLoginActivity.this, akunperms)) {
                pilihAkunSatuLogin();
            }
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
}
