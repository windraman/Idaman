package banjarbarukota.go.id.idaman;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import banjarbarukota.go.id.idaman.Adapter.Kategori;
import banjarbarukota.go.id.idaman.Adapter.KategoriAdapter;
import banjarbarukota.go.id.idaman.Utility.ItemClickSupport;
import banjarbarukota.go.id.idaman.Utility.OkHttpGetHandler;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static banjarbarukota.go.id.idaman.Utility.Constants.CLOUD_SERVER;

public class MenuKategoriActivity extends AppCompatActivity {
    private List<Kategori> kategoriList = new ArrayList<>();
    private RecyclerView kategoriRecyclerView;
    private KategoriAdapter kAdapter;

    static MenuKategoriActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_menu_kategori);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        kategoriRecyclerView = (RecyclerView) findViewById(R.id.ortu_recycler_view);

        kAdapter = new KategoriAdapter(kategoriList, getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        kategoriRecyclerView.setLayoutManager(mLayoutManager);
        kategoriRecyclerView.setItemAnimator(new DefaultItemAnimator());
        kategoriRecyclerView.setAdapter(kAdapter);

        kategoriRecyclerView.addItemDecoration(new DividerItemDecoration(kategoriRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        prepareKategoriData();
    }

    private void prepareKategoriData() {
        String url = CLOUD_SERVER + "/api/list_kategori?aktif=1";

        OkHttpGetHandler okget = new OkHttpGetHandler();

        Response response = null;
        try {
            JSONObject jobj = okget.getListKategori();
            if(jobj.getJSONArray("data").length()>0) {
                JSONArray dataArray = (JSONArray) jobj.getJSONArray("data");

                ArrayList<ContentValues> anak;

                if (dataArray.length() > 0) {
                    for (int d = 0; d < dataArray.length(); d++) {
                        JSONObject jdata = dataArray.getJSONObject(d);

                        JSONArray jsonArray = (JSONArray) jdata.getJSONArray("anak");
                        anak = new ArrayList<ContentValues>();
                        ContentValues values;
                        if (jsonArray.length() > 0) {
                            anak = new ArrayList<ContentValues>();
                            for (int j = 0; j < jsonArray.length(); j++) {
                                values = new ContentValues();
                                JSONObject adata = jsonArray.getJSONObject(j);

                                Iterator<String> keys = adata.keys();
                                while (keys.hasNext()) {
                                    String key = (String) keys.next();
                                    String value = adata.get(key).toString();
                                    values.put(key, value);
                                }
                                anak.add(values);
                            }
                            if (anak.size() > 0) {
                                // onMember(data);

                            }
                        }

                        Kategori kategori = new Kategori(jdata.getString("id"), jdata.getString("nama"), jdata.getString("icon"), anak);
                        kategoriList.add(kategori);

                        kAdapter.notifyDataSetChanged();
                    }
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        ItemClickSupport.addTo(kategoriRecyclerView).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Log.d("kiliklik",kategoriList.get(position).getTextOrtu());
                        cariKategori(kategoriList.get(position).getTextOrtu());
                    }

                }
        );

    }

    public static MenuKategoriActivity getInstance() {
        return instance;
    }

    public void cariKategori(String cari){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("cari",cari);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

}
