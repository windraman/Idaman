package banjarbarukota.go.id.idaman;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import banjarbarukota.go.id.GlobalClass;
import banjarbarukota.go.id.idaman.Adapter.Anggota;
import banjarbarukota.go.id.idaman.Adapter.AnggotaAdapter;
import banjarbarukota.go.id.idaman.Adapter.Member;
import banjarbarukota.go.id.idaman.Adapter.MemberAdapter;
import banjarbarukota.go.id.idaman.Utility.FilePath;
import banjarbarukota.go.id.idaman.Utility.OkHttpGetHandler;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pub.devrel.easypermissions.EasyPermissions;

import static banjarbarukota.go.id.idaman.Utility.Constants.CLOUD_SERVER;

public class  GrupDetailActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks{

    TextView TVNamaGrup, TVDeskripsiGrup, TVDibuatGrup, TVJenisGrup;
    Button btnGabung;
    FloatingActionButton btnEditPotoGrup;
    ImageView imgGrup;
    private static final int PICK_FILE_REQUEST = 1;
    private static final int TAKE_PICTURE_REQUEST = 2;

    private String selectedFilePath;

    private static final String IMGUR_CLIENT_ID = "...";
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpg");

    String pathgambar;
    String namafile;

    String grup_id="";

    private MemberAdapter memberAdapter;
    private AnggotaAdapter anggotaAdapter;

    private List<Anggota> anggotaList = new ArrayList<>();

    private RecyclerView anggotaRv;

    Toolbar toolbar;


    private ListView memberView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grup_detail_collapse);

        memberAdapter = new MemberAdapter(this);
        memberView = (ListView) findViewById(R.id.list_member);
        memberView.setAdapter(memberAdapter);


        anggotaRv = (RecyclerView) findViewById(R.id.rvAnggota);
        anggotaAdapter = new AnggotaAdapter(anggotaList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
        anggotaRv.setLayoutManager(mLayoutManager);

        anggotaRv.setAdapter(anggotaAdapter);

        TVNamaGrup = (TextView) findViewById(R.id.TVNamaGrup);
        TVDeskripsiGrup = (TextView) findViewById(R.id.TVDeskripsiGrup);
        TVDibuatGrup = (TextView) findViewById(R.id.TVDibuatGrup);
        TVJenisGrup = (TextView) findViewById(R.id.TVJenisGrup);

        btnGabung = (Button) findViewById(R.id.btnGabung);
        btnEditPotoGrup = (FloatingActionButton) findViewById(R.id.imgBtnEditPoto);
        imgGrup = (ImageView) findViewById(R.id.imgGrup);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);

        Intent i = getIntent();
        grup_id = i.getStringExtra("grup_id");

        getGrupDetail(grup_id, ((GlobalClass) getApplication()).getStringPref("username"));

        btnGabung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //openChat(grup_id,"grup");
                joinGrup(grup_id);
            }
        });

        btnEditPotoGrup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });
    }

    @SuppressLint("RestrictedApi")
    public void getGrupDetail(String id, String username) {
        OkHttpGetHandler okget = new OkHttpGetHandler();

        try {

            JSONObject jobj = okget.getGrupDetail(id,username);
            TVNamaGrup.setText(jobj.getString("nama_grup"));
            TVDeskripsiGrup.setText(jobj.getString("deskripsi"));
            TVDibuatGrup.setText(jobj.getString("created_at"));
            TVJenisGrup.setText(jobj.getString("jenis_grup"));

            toolbar.setTitle(jobj.getString("nama_grup"));

            new DownloadImageFromInternet(imgGrup).execute(jobj.getString("poto_grup"));

            if (jobj.getString("admin").equals("1")) {
                btnEditPotoGrup.setVisibility(View.VISIBLE);
            } else {
                btnEditPotoGrup.setVisibility(View.GONE);
            }

            JSONArray jsonArray = (JSONArray) jobj.getJSONArray("anggota");

            ArrayList<ContentValues> data;
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
                    onMember(data);
                    Log.d("anggota",data.toString());
                }


               // Log.d("Grup Detail", resp);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onMember(ArrayList<ContentValues> data) {
        // To transform the raw JsonNode into a POJO we can use an ObjectMapper
        // member.clientData is a MemberData object, let's parse it as such
        boolean ikut = false;
        for (int posisi = 0;posisi < data.size(); posisi++) {
            ContentValues baris = data.get(posisi);
            Log.d("anggota :",baris.get("id").toString());
            boolean belongsToCurrentUser = baris.get("id").toString().equals(((GlobalClass) getApplication()).getStringPref("user_id"));
            if(belongsToCurrentUser){
                ikut = true;
            }
            final Member member= new Member(baris.get("name").toString(), baris, belongsToCurrentUser);
            //
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    memberAdapter.add(member);
                }
            });

            final Anggota anggota = new Anggota(baris.get("id").toString(), baris.get("nik").toString(), baris.get("name").toString(),baris.get("photo").toString());
            anggotaList.add(anggota);

            anggotaAdapter.notifyDataSetChanged();
        }
        if(ikut){
           // joinGrup(grup_id);
        }
    }

    public void joinGrup(String grup_id) {

        OkHttpClient client = new OkHttpClient();

        String URL = CLOUD_SERVER + "/api/join_grup";

        FormBody.Builder builder = new FormBody.Builder();
        builder.add("grup_id", grup_id);
        builder.add("username", ((GlobalClass) getApplication()).getStringPref("username"));

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
            } catch (JSONException e) {
                e.printStackTrace();
            }
            openChat(grup_id,"grup");
            finish();
        } catch (IOException e) {
            Log.d("scan", e.toString());
            e.printStackTrace();
        }

    }

    @JavascriptInterface
    public void openChat(String key_id,String nama_table)
    {
        Intent localIntent = new Intent(getBaseContext(), ChatActivity.class);
        localIntent.putExtra("key_id", key_id);
        localIntent.putExtra("nama_table", nama_table);
        startActivity(localIntent);
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

    private void showFileChooser() {
        Intent intent = new Intent();
        //sets the select file to all types of files
        intent.setType("*/*");
        //allows to select data and return it
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //starts new activity to select file and return data
        startActivityForResult(Intent.createChooser(intent, "Choose File to Upload.."), PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_FILE_REQUEST) {
                if (data == null) {
                    //no data present
                    return;
                }

                Uri selectedFileUri = data.getData();
                selectedFilePath = FilePath.getPath(this, selectedFileUri);
                Log.i("Upload", "Selected File Path:" + selectedFilePath);



                if (selectedFilePath != null && !selectedFilePath.equals("")) {
                    String filename=selectedFilePath.substring(selectedFilePath.lastIndexOf("/")+1);
                    imgGrup.setImageURI(Uri.parse(selectedFilePath));
                    uploadFile(selectedFilePath,filename);
//                    Picasso.get()
//                            .load(new File(selectedFilePath))
//                            .into(imgGrup);

                } else {
                    Toast.makeText(this, "Cannot upload file to server", Toast.LENGTH_SHORT).show();
                }
            }else if (requestCode == TAKE_PICTURE_REQUEST) {

            }
        }
    }

    public void uploadFile(String filePath, String fileName){
        OkHttpClient client = new OkHttpClient();

        String URL = CLOUD_SERVER + "/api/poto_grup";

//        FormBody.Builder builder = new FormBody.Builder();
//        builder.add("id", grup_id);

        RequestBody builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", grup_id)
                .addFormDataPart("poto_grup", fileName,
                        RequestBody.create(MEDIA_TYPE_PNG, new File(filePath)))
                .build();

        RequestBody formBody = builder;

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
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            Log.d("scan", e.toString());
            e.printStackTrace();
        }
    }



    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    @Override
    public void onRationaleAccepted(int requestCode) {

    }

    @Override
    public void onRationaleDenied(int requestCode) {

    }
}
