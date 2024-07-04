package banjarbarukota.go.id.idaman;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

import banjarbarukota.go.id.GlobalClass;
import banjarbarukota.go.id.idaman.Adapter.Message;
import banjarbarukota.go.id.idaman.Adapter.NotifAdapter;
import banjarbarukota.go.id.idaman.Adapter.NotifAdapter;
import banjarbarukota.go.id.idaman.Adapter.Notifikasi;
import banjarbarukota.go.id.idaman.Utility.OkHttpGetHandler;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static banjarbarukota.go.id.idaman.Utility.Constants.CLOUD_SERVER;

public class NotifikasiActivity extends AppCompatActivity {

    private EditText editText;

    ContentValues values;

    ArrayList<ContentValues> data;

    private NotifAdapter notifAdapter;
    private ListView notifView;

    ArrayList<Integer> readedList;
    ArrayList<String> terlibatList;

    public Handler handler = null;
    public static Runnable runnable = null;

    String nik ;
    String status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifs);
        // This is where we write the mesage
        //editText = (EditText) findViewById(R.id.editText);

        notifAdapter = new NotifAdapter(this);
        notifView = (ListView) findViewById(R.id.notifs_view);
        notifView.setAdapter(notifAdapter);



        readedList = new ArrayList<Integer>();
        terlibatList = new ArrayList<String>();

        Intent i = getIntent();
        nik = ((GlobalClass) getApplication()).getStringPref("username");
        status = "terima";

        getNotif(nik, status);

//        handler = new Handler();
//        runnable = new Runnable() {
//            public void run() {
//                getNotif(key_id, nama_table);
//                handler.postDelayed(runnable, 7000);
//            }
//        };
//
//        handler.postDelayed(runnable, 1000);

//        notifView.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                //Log.d("buzzklik",((TextView)adapterView.getSelectedView().findViewById(R.id.link)).getText().toString());
//
//            }
//        });
    }

    private final OkHttpClient client = new OkHttpClient();


    public void getNotif(String nik, String status){
//        Request request = new Request.Builder()
//                .url(CLOUD_SERVER + "/api/ambil_notif?nik="+nik+"&status="+status)
//                .build();

        OkHttpGetHandler okget = new OkHttpGetHandler();

//        try (Response response = client.newCall(request).execute()) {
//            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
//            String jsondata = response.body().string();

            // System.out.println(response.body().string());
            try {
                //JSONObject jobj = new JSONObject(jsondata);
                JSONObject jobj = okget.getNotif(nik, ((GlobalClass) getApplication()).getStringPref("cms_privileges_name"), status);
                String success = jobj.getString("api_status");
                if(success.equals("1")){
                    JSONArray dataar = jobj.getJSONArray("data");

                    if(dataar.length()>0){
                        data = new ArrayList<ContentValues>();
                        for (int j = 0; j < dataar.length(); j++) {
                            values = new ContentValues();
                            JSONObject jdata = dataar.getJSONObject(j);
                            Iterator<String> keys = jdata.keys();
                            while (keys.hasNext()){
                                String key = (String) keys.next();
                                String value = jdata.get(key).toString();
                                values.put(key,value);
                            }
                            data.add(values);
                        }

                        onNotif(data);
                    }
                }else{
                    Log.d("sukses", "tidak");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    public void onNotif(ArrayList<ContentValues> data) {
        // To transform the raw JsonNode into a POJO we can use an ObjectMapper
        // member.clientData is a MemberData object, let's parse it as such
       // Log.d("gatum", data.toString());
        for (int posisi = 0;posisi < data.size(); posisi++) {
            ContentValues baris = data.get(posisi);
            final Notifikasi notifikasi = new Notifikasi(baris.get("content").toString(), baris);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifAdapter.add(notifikasi);
                    // scroll the ListView to the last added element
                    //notifView.setSelection(notifView.getCount() - 1);
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        super.onDestroy();
//        handler.removeCallbacks(runnable);
//        //Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
//        Runtime.getRuntime().gc();
    }
}
