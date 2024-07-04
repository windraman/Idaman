package banjarbarukota.go.id.idaman;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import banjarbarukota.go.id.GlobalClass;
import banjarbarukota.go.id.idaman.Adapter.Member;
import banjarbarukota.go.id.idaman.Adapter.Message;
import banjarbarukota.go.id.idaman.Adapter.MessageAdapter;
import banjarbarukota.go.id.idaman.Adapter.SliderImage;
import banjarbarukota.go.id.idaman.Adapter.SliderImageAdapter;
import banjarbarukota.go.id.idaman.Adapter.ViewPagerAdapter;
import banjarbarukota.go.id.idaman.Utility.OkHttpGetHandler;
import banjarbarukota.go.id.idaman.Utility.OkHttpPostHandler;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pub.devrel.easypermissions.EasyPermissions;

import static banjarbarukota.go.id.idaman.Utility.Constants.CLOUD_SERVER;




public class TempatDetailActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    String tempat_id, lat, lng, telepon;

    ArrayList<String> images = new ArrayList<>();

    ArrayList<ContentValues> data, gkdata;

    TextView tvNamaTempat, tvTags, tvAlamat, tvTelepon, tvSuka;
    ImageButton imbGetDirect, imbKomentar, imbShare, imbTelepon, imbLike;

    LinearLayout lSuka;

    OkHttpPostHandler postHandler;

    ViewPager vp;

    RecyclerView rvChatImage;

    private List<SliderImage> sliderImageList= new ArrayList<>();

    private SliderImageAdapter sliderImageAdapter;


    ArrayList<ContentValues> lmsgdata;

    private MessageAdapter messageAdapter;
    private ListView messagesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiManager.install(new GoogleEmojiProvider());
        setContentView(R.layout.activity_tempat_detail_collapse);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        prepareView();

        Intent i = getIntent();
        tempat_id = i.getStringExtra("id");

        getTempatDetail(tempat_id);

        ViewPager viewPager = findViewById(R.id.view_pager);
      //  if(data.size()>0) {
            viewPager.setVisibility(View.VISIBLE);
            ViewPagerAdapter adapter = new ViewPagerAdapter(this, images);
            viewPager.setAdapter(adapter);
//        }else{
//            viewPager.setVisibility(View.GONE);
//        }


    }

    public void prepareView(){
        vp = (ViewPager) findViewById(R.id.view_pager);

        tvNamaTempat = (TextView) findViewById(R.id.tvNamaTempat);
        tvTags = (TextView) findViewById(R.id.tvTags);
        tvAlamat = (TextView) findViewById(R.id.tvAlamat);
        tvTelepon = (TextView) findViewById(R.id.tvTelepon);

        imbLike = (ImageButton) findViewById(R.id.imbLike);
        imbLike.setTag("netral");

        lSuka= (LinearLayout) findViewById(R.id.sukaLayout);
        tvSuka = (TextView) findViewById(R.id.tvSuka);

        imbGetDirect = (ImageButton) findViewById(R.id.imbGetDirect);
        imbKomentar= (ImageButton) findViewById(R.id.imbKomentar);
        imbShare = (ImageButton) findViewById(R.id.imbShare);
        imbTelepon= (ImageButton) findViewById(R.id.imbTelepon);

        rvChatImage = (RecyclerView) findViewById(R.id.rvChatImage);
        sliderImageAdapter = new SliderImageAdapter(sliderImageList, getApplicationContext());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
        rvChatImage.setLayoutManager(mLayoutManager);

        rvChatImage.setAdapter(sliderImageAdapter);

        messageAdapter = new MessageAdapter(this);
        messagesView = (ListView) findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);

        imbGetDirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDirection(lat,lng);
            }
        });

        imbLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postHandler = new OkHttpPostHandler();
                if(imbLike.getTag().equals("netral")){
                        imbLike.setImageResource(R.drawable.ic_thumb_red);
                        String newid = postHandler.sukai(((GlobalClass) getApplication()).getStringPref("user_id"),"lokasi",tempat_id,"1");
                        imbLike.setTag(newid);
                }  else{
                    postHandler.sukai_rem(imbLike.getTag().toString());
                    imbLike.setImageResource(R.drawable.ic_thumb);
                    imbLike.setTag("netral");
                }
            }
        });

        imbKomentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChat(tempat_id,"lokasi");
            }
        });

        imbShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareTo(lat,lng);
            }
        });

        imbTelepon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("telepon", telepon);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getApplicationContext(),"Nomor Telepon di Salin Ke Clipboard", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getTempatDetail(String id) {
        String myId = ((GlobalClass) getApplication()).getStringPref("user_id");
        String url = CLOUD_SERVER + "/api/cari_lokasi?id=" + id + "&user_id=" + myId;

       OkHttpGetHandler okget = new OkHttpGetHandler();
//
//        OkHttpClient client = new OkHttpClient();
//
//        // code request code here
//        Request request = new Request.Builder()
//                .url(url)
//                .build();
//
//        client = ProgressManager.getInstance().with(new OkHttpClient.Builder())
//                .build();

       // Response response = null;
        try {
            //response = client.newCall(request).execute();
            //String resp = response.body().string();

            JSONObject jobj = okget.getTempatDetail(myId,id);
            String jsonData = (String) jobj.getJSONArray("data").get(0).toString();
            JSONObject jobj0 = new JSONObject(jsonData);
           // Log.d("Lokasi Detail", jobj0.toString() + "myid :" + myId );
            tvNamaTempat.setText(jobj0.getString("nama"));
            tvTags.setText(jobj0.getString("tags"));
            if(!jobj0.getString("ilike").equals("0")){
                imbLike.setImageResource(R.drawable.ic_thumb_red);
                imbLike.setTag(jobj0.getString("ilike"));
               // Log.d("liked", jobj0.get("ilike").toString());
            }
            if(!jobj0.getString("jpenyuka").equals("0")){
                tvSuka.setText(jobj0.getString("jpenyuka"));
                lSuka.setVisibility(View.VISIBLE);
            }else{
                lSuka.setVisibility(View.GONE);
            }
            tvAlamat.setText(jobj0.getString("jalan") + " " + jobj0.getString("nomor") + " " + jobj0.getString("kelurahan") + " " + jobj0.getString("kecamatan"));
            tvTelepon.setText(jobj0.getString("telepon"));

            telepon = jobj0.getString("telepon");

            lat = jobj0.getString("lat");
            lng = jobj0.getString("lon");

//            new GrupDetailActivity.DownloadImageFromInternet(imgGrup).execute(jobj.getString("poto_grup"));
//
//            if (jobj.getString("admin").equals("1")) {
//                btnEditPotoGrup.setVisibility(View.VISIBLE);
//            } else {
//                btnEditPotoGrup.setVisibility(View.GONE);
//            }

            JSONArray jsonArray = (JSONArray) jobj0.getJSONArray("gambar");
            //Log.d("gambar",String.valueOf(jsonArray.length()));
            data = new ArrayList<>();
            ContentValues values;
            if (jsonArray.length() > 0) {
                data = new ArrayList<ContentValues>();
                for (int j = 0; j < jsonArray.length(); j++) {
                    values = new ContentValues();
                    JSONObject jdata = jsonArray.getJSONObject(j);

                    Iterator<String> keys = jdata.keys();
                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        String value = jdata.get(key).toString();
                        values.put(key, value);
                    }
                    data.add(values);
                }
                if (data.size() > 0) {
                    onGambar(data);
                    vp.setVisibility(View.VISIBLE);
                }
            }else{
                Log.d("gambar","mkosong");
                vp.setVisibility(View.GONE);
            }

            JSONArray gkjsonArray = (JSONArray) jobj0.getJSONArray("gambarkomen");
            //Log.d("gambar",String.valueOf(jsonArray.length()));
            gkdata = new ArrayList<>();
            ContentValues gkvalues;
            if (gkjsonArray.length() > 0) {
                gkdata = new ArrayList<ContentValues>();
                for (int j = 0; j < gkjsonArray.length(); j++) {
                    gkvalues = new ContentValues();
                    JSONObject jdata = gkjsonArray.getJSONObject(j);

                    Iterator<String> keys = jdata.keys();
                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        String value = jdata.get(key).toString();
                        gkvalues.put(key, value);
                    }
                    gkdata.add(gkvalues);
                }
                if (gkdata.size() > 0) {
                    onGambarKomen(gkdata);
                    rvChatImage.setVisibility(View.VISIBLE);
                }
            }else{
                Log.d("gambar","mkosong");
                rvChatImage.setVisibility(View.GONE);
            }

            JSONArray lmsgjsonArray = (JSONArray) jobj0.getJSONArray("lastkomen");

            lmsgdata = new ArrayList<>();
            ContentValues lmsgvalues;
            if (lmsgjsonArray.length() > 0) {
                lmsgdata = new ArrayList<ContentValues>();
                for (int j = 0; j < lmsgjsonArray.length(); j++) {
                    lmsgvalues = new ContentValues();
                    JSONObject jdata = lmsgjsonArray.getJSONObject(j);

                    Iterator<String> keys = jdata.keys();
                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        String value = jdata.get(key).toString();
                        lmsgvalues.put(key, value);
                        Log.d(key,value);
                    }
                    lmsgdata.add(lmsgvalues);
                }
                if (lmsgdata.size() > 0) {
                    onLastMessage(lmsgdata);
                    messagesView.setVisibility(View.VISIBLE);
                }
            }else{
                Log.d("lmsg","mkosong");
                messagesView.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void onGambar(ArrayList<ContentValues> data) {
        // To transform the raw JsonNode into a POJO we can use an ObjectMapper
        // member.clientData is a MemberData object, let's parse it as such
        images.clear();
        for (int posisi = 0;posisi < data.size(); posisi++) {
            ContentValues baris = data.get(posisi);
            String val = CLOUD_SERVER + "/" + baris.get("file").toString();
            images.add(val);
        }
    }

    public void onGambarKomen(ArrayList<ContentValues> data) {
        // To transform the raw JsonNode into a POJO we can use an ObjectMapper
        // member.clientData is a MemberData object, let's parse it as such
        boolean ikut = false;
        for (int posisi = 0;posisi < data.size(); posisi++) {
            ContentValues baris = data.get(posisi);
            //Log.d("gambarkomen :",baris.get("gambar").toString());

            final SliderImage sliderImage= new SliderImage(baris.get("id").toString(), baris.get("gambar").toString(), baris.get("isi").toString());
            sliderImageList.add(sliderImage);

            sliderImageAdapter.notifyDataSetChanged();
        }

    }

    public void onLastMessage(ArrayList<ContentValues> data) {
        // To transform the raw JsonNode into a POJO we can use an ObjectMapper
        // member.clientData is a MemberData object, let's parse it as such

        for (int posisi = 0;posisi < data.size(); posisi++) {
            ContentValues baris = data.get(posisi);
            // if the clientID of the message sender is the same as our's it was sent by us
            boolean belongsToCurrentUser = baris.get("user_id").toString().equals(((GlobalClass) getApplication()).getStringPref("user_id"));
            // since the message body is a simple string in our case we can use json.asText() to parse it as such
            // if it was instead an object we could use a similar pattern to data parsing
            final Message message = new Message(baris.get("isi").toString(), baris, belongsToCurrentUser);
            //Log.d("komentar :",baris.get("isi").toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageAdapter.add(message);
                    // scroll the ListView to the last added element
                    messagesView.setSelection(messagesView.getCount() - 1);
                }
            });
        }
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

    public void openChat(String key_id,String nama_table)
    {
        if(((GlobalClass) getApplication()).getStringPref("login_pakai").equals("nik")) {
            Intent localIntent = new Intent(getBaseContext(), ChatActivity.class);
            localIntent.putExtra("key_id", key_id);
            localIntent.putExtra("nama_table", nama_table);
            startActivity(localIntent);
        }else {
            Toast.makeText(getApplicationContext(),"Silahkan Logout dan Login Kembali dengan NIK",Toast.LENGTH_LONG );
        }
    }

    @JavascriptInterface
    public void shareTo(String lat, String lng)
    {
        Intent localIntent = new Intent("android.intent.action.SEND");
        localIntent.setType("text/plain");
        String mapParams = "https://wa.me/?text=https://maps.google.com/maps?saddr=" + lat + "," + lng;
        localIntent.putExtra("android.intent.extra.TEXT", mapParams);
        startActivity(Intent.createChooser(localIntent, "Bagikan Ke..."));
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
