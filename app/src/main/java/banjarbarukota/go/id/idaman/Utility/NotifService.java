package banjarbarukota.go.id.idaman.Utility;

/**
 * Created by Wahyu on 3/26/2018.
 */

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.*;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import banjarbarukota.go.id.GlobalClass;
import banjarbarukota.go.id.idaman.Main2Activity;
import banjarbarukota.go.id.idaman.NotifikasiActivity;
import banjarbarukota.go.id.idaman.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static banjarbarukota.go.id.idaman.Utility.Constants.CLOUD_SERVER;

public class NotifService extends Service {

    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;

    String result= null;

    String SERVER_KITA = "http://www.banjarbarukota.go.id/aset/";

    String url_login = "https://simaya.go.id/v51/login/cek_login.php";
    String url_dashboard = "https://simaya.go.id/v51/page/index.php?menu=dasboard";
    String url_disposisi_masuk = "https://simaya.go.id/v51/page/surat_masuk_disposisi.php";
    String url_surat_masuk = "https://simaya.go.id/v51/page/surat_masuk.php";
    String url_konsep_surat_keluar = "https://simaya.go.id/v51/page/surat_keluar_konsep.php";

    List<String> dispohead = Arrays.asList("no", "tgl_disposisi", "tgl_selesai", "kecepatan", "keamanan","tgl_surat", "pengirim_disposisi","nomor_surat","perihal_orig","status");
    List<String> suratmasukhead = Arrays.asList("no", "no_surat_agenda_orig", "tgl_terima", "tgl_surat", "jenis_pegirim_klasifikasi","asal_surat", "tujuan_surat","perihal_orig","status");
    List<String> konsephead = Arrays.asList("no", "no_surat", "tgl_surat", "jenis", "pemeriksa","atas_nama", "perihal_orig","aksi");
    LinkedHashMap<String,String> listitem;



    ContentValues values;

    ArrayList<ContentValues> data;

    public static CookieManager manager;

    public static  List<HttpCookie> cookies;

    public static String htmlResult = null;
    public static final String DISPOSISI_CHANNEL = "disposisi";


    @Override
    public void onCreate() {
        StrictMode.ThreadPolicy strictModeThreadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(strictModeThreadPolicy);


        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                //HtmlReaderActivity.myWebView.loadUrl("javascript:Android.setCurrentUrl(document.URL);");
//                Toast.makeText(context, "cek", Toast.LENGTH_LONG).show();
//                HashMap<Integer,HashMap<String,String>> allDispoMasuk = new HashMap<Integer, HashMap<String, String>>(dm.ambilAllDispoMasuk()) ;
               // Log.d("Cek Notif", ((GlobalClass) getApplication()).getStringPref("cms_privileges_name"));
                if(Constants.TERIMA_NOTIF) {
                    cekNotifMasuk();
                }
                handler.postDelayed(runnable, 7000);
            }
        };

        handler.postDelayed(runnable, 1000);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        //handler.removeCallbacks(runnable);
        //Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
        Runtime.getRuntime().gc();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startid) {
       // Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();
    }

    private final OkHttpClient client = new OkHttpClient();

    public void cekNotifMasuk(){
        if(isNetworkConnected()) {
            //Log.d("syntax",SERVER_KITA + "ambil_bbuzz.php?last="+Integer.parseInt(((GlobalClass) getApplication()).getStringPref("lastnotif"))+"&target=>0");
            Request request = new Request.Builder()
                    .url(CLOUD_SERVER + "/api/ambil_notif?nik=" + ((GlobalClass) getApplication()).getStringPref("username") + "&priv=" + ((GlobalClass) getApplication()).getStringPref("cms_privileges_name") + "&status=baru")
                    .build();

            OkHttpGetHandler okget = new OkHttpGetHandler();

//        try (Response response = client.newCall(request).execute()) {
//            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
//            String jsondata = response.body().string();
//            // System.out.println(response.body().string());
            try {
                //JSONObject jobj = new JSONObject(jsondata);
                JSONObject jobj = okget.getNotif(((GlobalClass) getApplication()).getStringPref("username"), ((GlobalClass) getApplication()).getStringPref("cms_privileges_name"), "baru");
                if(jobj.length() > 0) {
                    String success = jobj.getString("api_status");
                    if (success.equals("1")) {
                        JSONArray dataar = jobj.getJSONArray("data");
                        if (dataar.length() > 0) {
                            data = new ArrayList<ContentValues>();
                            for (int j = 0; j < dataar.length(); j++) {
                                values = new ContentValues();
                                JSONObject jdata = dataar.getJSONObject(j);
                                Iterator<String> keys = jdata.keys();
                                while (keys.hasNext()) {
                                    String key = (String) keys.next();
                                    String value = jdata.get(key).toString();
                                    values.put(key, value);
                                }
                                data.add(values);
                                ((GlobalClass) getApplication()).saveStringPref("lastnotif", values.get("id").toString());
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                showNotifO("B-BUZZ", values.get("content").toString(), data.size(), 2, data);
                            } else {
                                showNotif("B-BUZZ", values.get("content").toString(), data.size(), 2, data);
                            }
                        }
                    } else {
                        Log.d("sukses", "tidak");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        }else{
            Toast.makeText(getApplicationContext(),"Koneksi Server Terputus !",Toast.LENGTH_LONG).show();
        }
    }


    @SuppressLint("NewApi")
    private void showNotif(String judul, String isi, int line, int notifId, ArrayList<ContentValues> data){

        int mId = notifId;
        Uri soundUri = Uri.parse("android.resource://"+context.getPackageName()+"/"+R.raw.notif);
        NotificationCompat.Builder mBuilder = null;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(getNotificationIcon())
                    .setColor(getResources().getColor(R.color.colorAccent))
                    .setAutoCancel(true)
                    .setSound(soundUri);
        } else {
            mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(getNotificationIcon())
                    .setAutoCancel(true)
                    .setSound(soundUri);
        }

        if(data.size()>1){
            NotificationCompat.InboxStyle inboxStyle =
                    new NotificationCompat.InboxStyle(mBuilder);

            inboxStyle.setBigContentTitle(judul+ " (" + line + ")");

            if(data.size()>5){
                // Moves events into the expanded layout
                mBuilder.setContentTitle(judul + " (" + line + ")")
                        .setContentText(data.get(0).get("content").toString());
                for (int posisi = 0;posisi < 5; posisi++) {
                    ContentValues baris = data.get(posisi);
                    inboxStyle.addLine(baris.get("content").toString());
                }

                inboxStyle.setSummaryText("+" + (data.size()-5) + " " + judul);
            }else{
                mBuilder.setContentTitle(judul + " (" + line + ")")
                        .setContentText(data.get(0).get("content").toString());
                for (int posisi = 0;posisi < line; posisi++) {
                    ContentValues baris = data.get(posisi);
                    inboxStyle.addLine(baris.get("content").toString());
                }
            }

            // Moves the expanded layout object into the notification object.

            mBuilder.setStyle(inboxStyle);

        }

        if(data.size()==1){
            ContentValues baris = data.get(0);
            if(!baris.get("style").toString().equalsIgnoreCase("big")){
                mBuilder.setContentTitle(judul + " (" + line + ")")
                        .setContentText(data.get(0).get("content").toString());
            }else{
                mBuilder.setContentTitle("B-BUZZ")
                        .setContentText(data.get(0).get("content").toString())
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .setBigContentTitle(data.get(0).get("content").toString())
                                .bigPicture(getbmpfromURL(data.get(0).get("gambar").toString())));
            }

        }
        Intent resultIntent;
        resultIntent = new Intent(this, NotifikasiActivity.class);
//        if(data.get(0).get(2).toString().equals("baru")){
//            resultIntent = new Intent(this, DetailJQMActivity.class);
//            resultIntent.putExtra("id", data.get(0).get(1).toString());
//            resultIntent.putExtra("from", "notif");
//        }else{
//            resultIntent = new Intent(this, CommentActivity.class);
//            resultIntent.putExtra("tipe", data.get(0).get(2).toString());
//            resultIntent.putExtra("id", data.get(0).get(1).toString());
//            resultIntent.putExtra("from", "notif");
//        }
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, resultIntent, 0);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Main2Activity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =  (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        mNotificationManager.notify(mId, mBuilder.build());

    }

    @SuppressLint("NewApi")
    private void showNotifO(String judul, String isi, int line, int notifId, ArrayList<ContentValues> data){
        int mId = notifId;
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNotificationManager =  (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(DISPOSISI_CHANNEL,"Simaya", NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        //channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        //channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

        mNotificationManager.createNotificationChannel(channel);

        Notification.Builder mBuilder =
                new Notification.Builder(this,DISPOSISI_CHANNEL)
                        .setSmallIcon(getNotificationIcon())
                        .setAutoCancel(true)
                        .setSound(soundUri);

        if(data.size()>1){
            Notification.InboxStyle inboxStyle =
                    new Notification.InboxStyle(mBuilder);

            inboxStyle.setBigContentTitle(judul+ " (" + line + ")");

            if(data.size()>5){
                // Moves events into the expanded layout
                mBuilder.setContentTitle(judul + " (" + line + ")")
                        .setContentText(data.get(0).get("content").toString());
                for (int posisi = 0;posisi < 5; posisi++) {
                    ContentValues baris = data.get(posisi);
                    inboxStyle.addLine(baris.get("content").toString());
                }

                inboxStyle.setSummaryText("+" + (data.size()-5) + " " + judul);
            }else{
                mBuilder.setContentTitle(judul + " (" + line + ")")
                        .setContentText(data.get(0).get("content").toString());
                for (int posisi = 0;posisi < line; posisi++) {
                    ContentValues baris = data.get(posisi);
                    inboxStyle.addLine(baris.get("content").toString());
                }
            }

            // Moves the expanded layout object into the notification object.

            mBuilder.setStyle(inboxStyle);

        }
        String link_url = "";
        if(data.size()==1){
            ContentValues baris = data.get(0);
            if(!baris.get("style").toString().equalsIgnoreCase("big")){
                mBuilder.setContentTitle(judul + " (" + line + ")")
                        .setContentText(data.get(0).get("content").toString());
            }else{
                mBuilder.setContentTitle("B-BUZZ")
                        .setContentText(data.get(0).get("content").toString())
                        .setStyle(new Notification.BigPictureStyle()
                                .bigPicture(getbmpfromURL(data.get(0).get("gambar").toString())));
            }
            link_url = data.get(0).get("url").toString();
        }
        Intent resultIntent;
        resultIntent = new Intent(this, NotifikasiActivity.class);
        resultIntent.putExtra("link",link_url);
//        if(data.get(0).get(2).toString().equals("baru")){
//            resultIntent = new Intent(this, DetailJQMActivity.class);
//            resultIntent.putExtra("id", data.get(0).get(1).toString());
//            resultIntent.putExtra("from", "notif");
//        }else{
//            resultIntent = new Intent(this, CommentActivity.class);
//            resultIntent.putExtra("tipe", data.get(0).get(2).toString());
//            resultIntent.putExtra("id", data.get(0).get(1).toString());
//            resultIntent.putExtra("from", "notif");
//        }
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, resultIntent, 0);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(Main2Activity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        // mId allows you to update the notification later on.

        mNotificationManager.notify(mId, mBuilder.build());

    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.idaman_white : R.mipmap.ic_launcher;
    }

    public Bitmap getbmpfromURL(String surl){
        try {
            URL url = new URL(surl);
            HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
            urlcon.setDoInput(true);
            urlcon.connect();
            InputStream in = urlcon.getInputStream();
            Bitmap mIcon = BitmapFactory.decodeStream(in);
            return  mIcon;
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
