package banjarbarukota.go.id.idaman;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import banjarbarukota.go.id.GlobalClass;
import banjarbarukota.go.id.idaman.Adapter.CustomSpinner;
import banjarbarukota.go.id.idaman.Adapter.CustomSpinnerAdapter;
import banjarbarukota.go.id.idaman.Adapter.Kategori;
import banjarbarukota.go.id.idaman.Adapter.KategoriAdapter;
import banjarbarukota.go.id.idaman.Adapter.ListChat;
import banjarbarukota.go.id.idaman.Adapter.ListChatAdapter;
import banjarbarukota.go.id.idaman.Adapter.MemberAdapter;
import banjarbarukota.go.id.idaman.Utility.ItemClickSupport;
import banjarbarukota.go.id.idaman.Utility.OkHttpGetHandler;

import static banjarbarukota.go.id.idaman.Utility.Constants.API_SERVER;
import static banjarbarukota.go.id.idaman.Utility.Constants.CLOUD_SERVER;

public class PilihChatActivity extends AppCompatActivity {
    OkHttpGetHandler okHttpGetHandler;
    ListChatAdapter listChatAdapter;
    RecyclerView rvListChat;
    List<ListChat> listChat = new ArrayList<>();
    String passType, passString, passNamaTable, passId;
    Intent senti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_chat);

        senti = getIntent();
        String action = senti.getAction();
        String type = senti.getType();
        passType = null;
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = senti.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    passType = "text";
                    passString = sharedText;
                }
            } else if (type.startsWith("image/")) {
                Uri imageUri = (Uri) senti.getParcelableExtra(Intent.EXTRA_STREAM);
                if (imageUri != null) {
                    passType = "image";
                    passString = imageUri.toString();
                }
            }
//        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
//            //not supported yet
//            if (type.startsWith("image/")) {
//                ArrayList<Uri> imageUris = senti.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
//                if (imageUris != null) {
//                    passType = "images";
//                    passString = imageUris.toString();
//                }
//            }
        } else {
            //Handle other intents, such as being started from the home screen
        }

        okHttpGetHandler = new OkHttpGetHandler();

        rvListChat = (RecyclerView) findViewById(R.id.rvListChat);

        listChatAdapter = new ListChatAdapter(listChat);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvListChat.setLayoutManager(mLayoutManager);
        rvListChat.setItemAnimator(new DefaultItemAnimator());
        rvListChat.setAdapter(listChatAdapter);

        rvListChat.addItemDecoration(new DividerItemDecoration(rvListChat.getContext(), DividerItemDecoration.VERTICAL));

        loadListChat();

    }

    public void loadListChat(){
       JSONObject jListChat = okHttpGetHandler.getListChat(((GlobalClass) getApplication()).getStringPref("user_id"));
       //if(!listChat.isEmpty()) {
           Log.d("listchat", jListChat.toString());

           JSONArray dataArray = null;
           try {

               dataArray = (JSONArray) jListChat.getJSONArray("detail");
               if (dataArray.length() > 0) {
                   for (int d = 0; d < dataArray.length(); d++) {
                       JSONObject jdata = dataArray.getJSONObject(d);
                       String nama_table = jdata.getString("nama_table");
                       String title, description, picture;
                       title = "";
                       description = "";
                       picture = "null";

                           title = jdata.getString("nama");
                           description =  jdata.getString("jterlibat") + " partisipan" ;
                           picture = CLOUD_SERVER + "/" + jdata.getString("gambar_utama");

                       final ListChat listChatItem = new ListChat(jdata.getString("id"), title, picture, description, nama_table);
                       listChat.add(listChatItem);
                   }
                   listChatAdapter.notifyDataSetChanged();
               }
           } catch (JSONException e) {
               e.printStackTrace();
           }

           ItemClickSupport.addTo(rvListChat).setOnItemClickListener(
                   new ItemClickSupport.OnItemClickListener() {
                       @Override
                       public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                           passId = listChat.get(position).getId();
                           passNamaTable = listChat.get(position).getNamaTable();
                           Log.d("list_chat_selected", passNamaTable);
                           openChat(passId, passNamaTable);
                           finish();
                       }
                   }
           );
      // }
    }

    public void openChat(String key_id,String nama_table)
    {
        Intent chatIntent = new Intent(getBaseContext(), ChatActivity.class);
        chatIntent.putExtra("key_id", key_id);
        chatIntent.putExtra("nama_table", nama_table);
        chatIntent.putExtra("type", passType);
        chatIntent.putExtra("data", passString);
        startActivity(chatIntent);
    }
}
