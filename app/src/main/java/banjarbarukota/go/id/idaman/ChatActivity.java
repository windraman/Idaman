package banjarbarukota.go.id.idaman;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
//import android.support.v7.widget.Toolbar;


import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.EmojiTextView;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import banjarbarukota.go.id.GlobalClass;
import banjarbarukota.go.id.idaman.Adapter.Message;
import banjarbarukota.go.id.idaman.Adapter.MessageAdapter;
import banjarbarukota.go.id.idaman.Utility.CircleTransform;
import banjarbarukota.go.id.idaman.Utility.OkHttpGetHandler;
import banjarbarukota.go.id.idaman.Utility.ResizeImage;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pub.devrel.easypermissions.EasyPermissions;

import static banjarbarukota.go.id.idaman.Utility.Constants.CLOUD_SERVER;
import static banjarbarukota.go.id.idaman.Utility.Constants.LOCAL_SERVER;

public class ChatActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    //private EmojiTextView editText;

    ContentValues values;

    ArrayList<ContentValues> data;

    private MessageAdapter messageAdapter;
    private ListView messagesView;

    ArrayList<Integer> readedList;
    ArrayList<String> terlibatList;

    public Handler handler = null;
    public static Runnable runnable = null;

    String key_id ;
    String nama_table;

    TextView namaLokasi;
    TextView tags;
    TextView jumlahKomentar;

    ImageView imgChat;

    ImageButton imbAttach;

    ResizeImage rim;

    VideoView videoChatPreview;

    private static MediaType MEDIA_TYPE = MediaType.parse("image/jpg");

    public static final int PICK_IMAGE_CODE = 100;
    public static final int DS_PHOTO_EDITOR_REQUEST_CODE = 200;

    private static final int REQUEST_EXTERNAL_STORAGE_CODE = 1000;

    public static final String OUTPUT_PHOTO_DIRECTORY = String.valueOf(R.string.app_name);

    private static final int GALLERY_REQUEST_CODE = 1 ;
    private static final int CAMERA_REQUEST_CODE = 2;
    private static final int VIDEO_TRIM = 101;

    private String filePath;

    ConstraintLayout clImagePreview;
    ImageView imgPreview;
    ImageButton imbDeleteImage, imbKeyboard, imbSendMessage;

    EmojiEditText emojiEditText;
    LinearLayout emoHolder;

    EmojiPopup emojiPopup = null;

    RecyclerView rvChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiManager.install(new GoogleEmojiProvider());

        setContentView(R.layout.activity_chat);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Toolbar chatToolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(chatToolbar);
        // This is where we write the mesage
       // editText = (EmojiTextView) findViewById(R.id.emojiEditText);
        namaLokasi = (TextView) findViewById(R.id.textContent);
        tags = (TextView) findViewById(R.id.textOwner);
        imgChat = (ImageView) findViewById(R.id.imgChat);
        jumlahKomentar = (TextView) findViewById(R.id.textJumlahKomentar);

        imbAttach = (ImageButton) findViewById(R.id.imbAttach);
        filePath = "kosong";
        clImagePreview = (ConstraintLayout) findViewById(R.id.layoutImagePreview);
        imgPreview =(ImageView) findViewById(R.id.imgChatPreview);
        videoChatPreview = (VideoView) findViewById(R.id.videoChatPreview);
        imbDeleteImage = (ImageButton) findViewById(R.id.imbDeleteImage);
        emojiEditText = (EmojiEditText) findViewById(R.id.emojiEditText);
        imbKeyboard = (ImageButton) findViewById(R.id.imbKeyboard);
        //imbSendMessage = (ImageButton) findViewById(R.id.imbSendMessage);

        emoHolder = (LinearLayout) findViewById(R.id.emoHolder);

        emojiPopup = EmojiPopup.Builder.fromRootView(emoHolder).build(emojiEditText);

        imbDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideImagePreview();
            }
        });

        imbKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!emojiPopup.isShowing()) {
                    emojiPopup.toggle();
                }else{
                    emojiPopup.dismiss();
                }
            }
        });

//        imbSendMessage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                sendMessage();
//            }
//        });



        messageAdapter = new MessageAdapter(this);
        messagesView = (ListView) findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);

        readedList = new ArrayList<Integer>();
        terlibatList = new ArrayList<String>();

        messagesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Message message= (Message) adapterView.getAdapter().getItem(i);
                String photo_url = message.getMessageData().get("gambar").toString();
                if(!photo_url.equals("null")) {
                    Intent intent = new Intent(ChatActivity.this, PhotoViewActivity.class);
                    intent.putExtra("photo_url", photo_url);
                    startActivity(intent);
                }
            }
        });


//        if (Intent.ACTION_SEND.equals(action) && type != null) {
//            if ("text/plain".equals(type)) {
//                handleSendText(i); // Handle text being sent
//            } else if (type.startsWith("image/")) {
//                handleSendImage(i); // Handle single image being sent
//            }
//        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
//            if (type.startsWith("image/")) {
//               handleSendMultipleImages(i); // Handle multiple images being sent
//            }
//        } else {
//            //Handle other intents, such as being started from the home screen
//        }

        Intent i = getIntent();
        key_id = i.getStringExtra("key_id");
        nama_table = i.getStringExtra("nama_table");
        //Log.d("data",passed.toString());
        String type = i.getStringExtra("type");
        String data = i.getStringExtra("data");

        getTentang(key_id, nama_table);
        getMessage(key_id, nama_table);


        if(type!=null) {
            if (type.equals("text")) {
                emojiEditText.setText(data);
            } else if (type.equals("image")) {
                Uri passedUri = Uri.parse(data);
                dsPhotoEdit(passedUri);
            }
        }

        imbAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                getMessage(key_id, nama_table);
                handler.postDelayed(runnable, 5000);
            }
        };

        handler.postDelayed(runnable, 5000);
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            emojiEditText.setText(sharedText);
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            emojiEditText.setText(imageUri.toString());
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
        }
    }

    private final OkHttpClient client = new OkHttpClient();

    public void sendMessage(View view) {
        String message = emojiEditText.getText().toString();

        if(message.length()>0) {
            String URL = CLOUD_SERVER + "/api/add_komentar";

            RequestBody builder = null;
            if(!filePath.equals("kosong")) {
                Log.d("pesan", filePath);
                String fileName=filePath.substring(filePath.lastIndexOf("/")+1);
                MEDIA_TYPE = MediaType.parse(getFileMimeType(filePath));
                builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("nama_table", nama_table)
                        .addFormDataPart("key_id", key_id)
                        .addFormDataPart("user_id", ((GlobalClass) getApplication()).getStringPref("user_id"))
                        .addFormDataPart("isi", message)
                        .addFormDataPart("terlibat", terlibatList.toString()
                                .replace("[", "")  //remove the right bracket
                                .replace("]", "")  //remove the left bracket
                                .trim())
                        .addFormDataPart("gambar", fileName,
                                RequestBody.create(MEDIA_TYPE, new File(filePath)))
                        .addFormDataPart("jenis_gambar", getFileMimeType(filePath))
                        .build();
            }else{
                builder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("nama_table", nama_table)
                        .addFormDataPart("key_id", key_id)
                        .addFormDataPart("user_id", ((GlobalClass) getApplication()).getStringPref("user_id"))
                        .addFormDataPart("isi", message)
                        .addFormDataPart("terlibat", terlibatList.toString()
                                .replace("[", "")  //remove the right bracket
                                .replace("]", "")  //remove the left bracket
                                .trim())
                        .build();
            }

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
                   // Log.d("kirim pesan", jobj.toString());
                    if (success == 1) {
                        emojiEditText.setText("");
                        emojiPopup.dismiss();
                        hideImagePreview();
                        hideKeyboard(this);
                    } else {
                       // hideImagePreview();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                Log.d("scan", e.toString());
                e.printStackTrace();
            }
        }


    }

    public void getTentang(String key_id, String nama_table){
        Request request = new Request.Builder()
                .url(CLOUD_SERVER + "/api/komentar?key_id="+key_id+"&nama_table="+nama_table)
                .build();

        OkHttpGetHandler okget = new OkHttpGetHandler();

//        try (Response response = client.newCall(request).execute()) {
//            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
//            String jsondata = response.body().string();
            // System.out.println(response.body().string());
            try {
//                JSONObject jobj = new JSONObject(jsondata);
                JSONObject jobj = okget.getTentang(key_id,nama_table);
                String success = jobj.getString("api_status");
                if(success.equals("1")){
                    JSONObject dataar = jobj.getJSONObject("tentang");
                    if(dataar.length()>0){
                        namaLokasi.setText(dataar.getString("nama"));
                        tags.setText(dataar.getString("tags"));
                        Picasso.get()
                                .load( CLOUD_SERVER + "/" + dataar.getString("gambar_utama"))
                                .transform(new CircleTransform())
                                .into(imgChat);
                    }
                    String jKomentar = jobj.get("count").toString();
                    Log.d("jKomentar", jKomentar);
                    if(!jKomentar.equals("0")){
                        jumlahKomentar.setText(jKomentar + " komentar");
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

    public void getMessage(String key_id, String nama_table){
        Request request = new Request.Builder()
                .url(CLOUD_SERVER + "/api/komentar?key_id="+key_id+"&nama_table="+nama_table+"&req_by="+((GlobalClass) getApplication()).getStringPref("user_id"))
                .build();

        OkHttpGetHandler okget = new OkHttpGetHandler();

//        try (Response response = client.newCall(request).execute()) {
//            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
//            String jsondata = response.body().string();
            // System.out.println(response.body().string());
            try {
//                JSONObject jobj = new JSONObject(jsondata);
                JSONObject jobj = okget.getMessage(key_id,nama_table,((GlobalClass) getApplication()).getStringPref("user_id"));
                String success = jobj.getString("api_status");
                if(success.equals("1")){
                    JSONArray dataar = jobj.getJSONArray("data");
                    JSONArray jsonArray = (JSONArray)jobj.getJSONArray("terlibat");
                    if (jsonArray != null) {
                        int len = jsonArray.length();
                        for (int i=0;i<len;i++){
                            if(!terlibatList.contains(jsonArray.get(i).toString())) {
                                if(!jsonArray.get(i).toString().equals(((GlobalClass) getApplication()).getStringPref("username"))) {
                                    terlibatList.add(jsonArray.get(i).toString());
                                }
                            }
                        }
                        Log.d("terlibat",terlibatList.toString());
                    }
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
                            if(!readedList.contains(dataar.getJSONObject(j).getInt("id"))) {
                                data.add(values);
                                readedList.add(dataar.getJSONObject(j).getInt("id"));
                            }else{
                                //Log.d("pesan", "sudah ada");
                            }
                        }
                        if(data.size()>0) {
                            onMessage(data);
                        }
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


    public void onMessage(ArrayList<ContentValues> data) {
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

    private void selectImage() {
        final CharSequence[] options = { "Ambil Foto", "Pilih dari Gallery","Batal" };
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("Tambah Gambar!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Ambil Foto"))
                {
                    captureFromCamera();
                }
                else if (options[item].equals("Pilih dari Gallery"))
                {
                    pickFromGallery();
                }
                else if (options[item].equals("Batal")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    private void pickFromGallery(){
        String[] perms = { "android.permission.READ_EXTERNAL_STORAGE"};
        if (EasyPermissions.hasPermissions(ChatActivity.this, perms)) {
            //Create an Intent with action as ACTION_PICK
            Intent intent = new Intent(Intent.ACTION_PICK);
            // Sets the type as image/*. This ensures only components of type image are selected
            intent.setType("image/* video/*");
            //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
            String[] mimeTypes = {"image/jpeg", "image/png", "video/mp4", "video/flv","video/mkv"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            // Launching the Intent
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        }else{
            EasyPermissions.requestPermissions(ChatActivity.this, "Meminta Ijin Akses", GALLERY_REQUEST_CODE, perms);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = null;
        File image = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "IDAMAN_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        }else{
            Date date = new Date(String.valueOf(new Date()));
            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
            timeStamp = dateFormat.format(date);
            image = new File(Environment.getExternalStorageDirectory() +
                    File.separator + "_" + timeStamp);
        }

        filePath = image.getAbsolutePath();
        // Save a file: path for use with ACTION_VIEW intents

        return image;
    }

    private void captureFromCamera() {
        String[] perms = { "android.permission.CAMERA","android.permission.WRITE_EXTERNAL_STORAGE"};
        if (EasyPermissions.hasPermissions(ChatActivity.this, perms)) {

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    Log.e("camera",ex.toString());
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    File f = photoFile;
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
                }
            }
        }else{
            EasyPermissions.requestPermissions(ChatActivity.this, "Meminta Ijin Akses", CAMERA_REQUEST_CODE, perms);
        }
    }

    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    //data.getData return the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    Log.d("selctedImage", selectedImage.toString());
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    // Get the cursor
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();
                    //Get the column index of MediaStore.Images.Media.DATA
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    //Gets the String value in the column
                    String imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();

                    // Set the Image in ImageView after decoding the String

                    filePath = imgDecodableString;
//                    File f = new File(filePath);
                    if(getFileMimeType(filePath).contains("image")) {
                        dsPhotoEdit(selectedImage);
                    }else  if(getFileMimeType(filePath).contains("video")) {
                        showImagePreview(filePath,false);
                    }
                    Log.d("potopat", filePath);
                    break;
                case CAMERA_REQUEST_CODE:
                    File f = new File(filePath);
                    Uri takenIcon = Uri.fromFile(f);
//                    Picasso.get()
//                            .load(f)
//                            .transform(new CircleTransform())
//                            .into(imgPoto);
                    dsPhotoEdit(takenIcon);
                    break;
                case DS_PHOTO_EDITOR_REQUEST_CODE:
                    Uri outputUri = data.getData();
                    String outputPath = outputUri.getPath();
                    filePath = outputPath;
                    String fileName=filePath.substring(filePath.lastIndexOf("/")+1);
                    rim = new ResizeImage();
                    filePath = rim.resizeAndCompressImageBeforeSend(this,filePath,fileName);

                    showImagePreview(filePath, true);

                    break;
                case VIDEO_TRIM:
                    if (data != null) {
                        filePath = data.getExtras().getString("INTENT_VIDEO_FILE");

                        showImagePreview(filePath, true);
                    }

                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + requestCode);
            }
        }
    }

    public void showImagePreview(String filePath, Boolean trimmed){
        String mime = getFileMimeType(filePath);
        if(mime.contains("image")) {
            File f = new File(filePath);
            Picasso.get()
                    .load(f)
                    .into(imgPreview);
            clImagePreview.setVisibility(View.VISIBLE);
            videoChatPreview.setVisibility(View.GONE);
            imgPreview.setVisibility(View.VISIBLE);
        }else  if(mime.contains("video")) {
            if(trimmed){
                videoChatPreview.setVideoPath(filePath);
                imgPreview.setVisibility(View.GONE);
                videoChatPreview.setVisibility(View.VISIBLE);
                videoChatPreview.start();
                clImagePreview.setVisibility(View.VISIBLE);
            }else {
                File file = new File(filePath);
                if (file.exists()) {
                    startActivityForResult(new Intent(this,
                                    VideoTrimmerActivity.class).putExtra("EXTRA_PATH", filePath),
                            VIDEO_TRIM);
                    overridePendingTransition(0, 0);
                } else {
                    Toast.makeText(this, "Please select proper video", Toast.LENGTH_LONG);
                }
                clImagePreview.setVisibility(View.GONE);
            }
        }
    }

    public void hideImagePreview(){
        clImagePreview.setVisibility(View.GONE);
        filePath = "kosong";
    }

    public String getFileMimeType(String url){
        File file = new File(url);
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String mimeType = fileNameMap.getContentTypeFor(file.getName());

        return mimeType;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void dsPhotoEdit(Uri uri){
        String[] perms = { "android.permission.READ_EXTERNAL_STORAGE"};
        if (EasyPermissions.hasPermissions(ChatActivity.this, perms)) {
            Intent dsPhotoEditorIntent = new Intent(this, DsPhotoEditorActivity.class);
            dsPhotoEditorIntent.setData(uri);

            // This is optional. By providing an output directory, the edited photo
            // will be saved in the specified folder on your device's external storage;
            // If this is omitted, the edited photo will be saved to a folder
            // named "DS_Photo_Editor" by default.
            dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, OUTPUT_PHOTO_DIRECTORY);

            // You can also hide some tools you don't need as below
            int[] toolsToHide = {DsPhotoEditorActivity.TOOL_PIXELATE, DsPhotoEditorActivity.TOOL_ORIENTATION};
            dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE, toolsToHide);

            startActivityForResult(dsPhotoEditorIntent, DS_PHOTO_EDITOR_REQUEST_CODE);
        }else{
            EasyPermissions.requestPermissions(ChatActivity.this, "Meminta Ijin Akses", DS_PHOTO_EDITOR_REQUEST_CODE, perms);
        }
    }

    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        super.onDestroy();
        handler.removeCallbacks(runnable);
        //Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
        Runtime.getRuntime().gc();
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
