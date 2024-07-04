package banjarbarukota.go.id.idaman;

import android.app.Activity;
import android.app.Application;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.googlecode.mp4parser.authoring.Edit;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import banjarbarukota.go.id.GlobalClass;
import banjarbarukota.go.id.idaman.Adapter.Berita;
import banjarbarukota.go.id.idaman.Adapter.BeritaAdapter;
import banjarbarukota.go.id.idaman.Adapter.ListChat;
import banjarbarukota.go.id.idaman.Adapter.ListChatAdapter;
import banjarbarukota.go.id.idaman.Adapter.Poto;
import banjarbarukota.go.id.idaman.Adapter.PotoAdapter;
import banjarbarukota.go.id.idaman.Utility.CircleTransform;
import banjarbarukota.go.id.idaman.Utility.Constants;
import banjarbarukota.go.id.idaman.Utility.ItemClickSupport;
import banjarbarukota.go.id.idaman.Utility.OkHttpGetHandler;
import banjarbarukota.go.id.idaman.Utility.ResizeImage;
import pub.devrel.easypermissions.EasyPermissions;

import static banjarbarukota.go.id.idaman.Utility.Constants.CLOUD_SERVER;

public class BeritaActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks{
    OkHttpGetHandler okHttpGetHandler;
    BeritaAdapter beritaAdapter;
    RecyclerView rvBerita;
    List<Berita> listBerita = new ArrayList<>();
    String passType, passString, passNamaTable, passId;
    Intent senti;
    SwipeRefreshLayout swpBerita;
    ArrayList<Integer> readedList;
    RecyclerView rvPostMedia;
    LinearLayout lPilihMedia, lBtnPost, lPilihAction, lBtnLangsung, lBtnMedia, lBtnCekin;
    ImageButton imbPilihGallery, imbPilihCamera, imbCLosePilihMedia;
    Button btnBatalPost, btnBagikanPost;
    ImageView imgUserPost;
    EditText mlEdtPost;

    private static final int GALLERY_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private static final int VIDEO_CAMERA_REQUEST_CODE = 3;
    private static final int VIDEO_TRIM = 4;
    private static  final  int DS_PHOTO_EDITOR_REQUEST_CODE = 5;

    public static final String OUTPUT_PHOTO_DIRECTORY = String.valueOf(R.string.app_name);

    public List<Poto> potoList= new ArrayList<>();
    private PotoAdapter potoAdapter;
    ResizeImage rim;

    private String photoFilePath;

    public interface ClickListener {

        void onPositionClicked(int position);

        void onLongClicked(int position);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_berita);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Toolbar toolbar = (Toolbar) findViewById(R.id.tbIdaman);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.idaman_white);
        toolbar.setTitle("Bakawan");

        okHttpGetHandler = new OkHttpGetHandler();

        rvBerita = (RecyclerView) findViewById(R.id.rvBerita);

        beritaAdapter = new BeritaAdapter(listBerita);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvBerita.setLayoutManager(mLayoutManager);
        rvBerita.setItemAnimator(new DefaultItemAnimator());
        rvBerita.setAdapter(beritaAdapter);

        rvBerita.addItemDecoration(new DividerItemDecoration(rvBerita.getContext(), DividerItemDecoration.VERTICAL));
        readedList = new ArrayList<Integer>();

        rvPostMedia = (RecyclerView) findViewById(R.id.rvHMedia);
        potoAdapter = new PotoAdapter(potoList);
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL, false);
        rvPostMedia.setLayoutManager(mLayoutManager2);
        rvPostMedia.setItemAnimator(new DefaultItemAnimator());
        rvPostMedia.setAdapter(potoAdapter);

        //rvPostMedia.addItemDecoration(new DividerItemDecoration(rvPostMedia.getContext(), DividerItemDecoration.HORIZONTAL));

        lPilihMedia = (LinearLayout) findViewById(R.id.lPilihMedia);
        lBtnPost = (LinearLayout) findViewById(R.id.lBtnPost);
        lBtnLangsung = (LinearLayout) findViewById(R.id.lbtnLangsung);
        lBtnMedia = (LinearLayout) findViewById(R.id.lbtnMedia);
        lBtnCekin = (LinearLayout) findViewById(R.id.lbtnCekin);
        lPilihAction = (LinearLayout) findViewById(R.id.lPilihAction);
        imbPilihGallery = (ImageButton)  findViewById(R.id.imbPilihGallery);
        imbPilihCamera = (ImageButton)  findViewById(R.id.imbPilihCamera);
        btnBatalPost = (Button) findViewById(R.id.btnBatalPost);
        btnBagikanPost = (Button) findViewById(R.id.btnBagikanPost);
        imgUserPost = (ImageView) findViewById(R.id.imgFotoPost);
        mlEdtPost = (EditText) findViewById(R.id.edtPost);
        imbCLosePilihMedia= (ImageButton) findViewById(R.id.imbClosePilihMedia);


        if(((GlobalClass) getApplication()).getStringPref("login_pakai").equals("nik") & !((GlobalClass) getApplication()).getStringPref("foto").equals("")) {
            Picasso.get()
                    .load(((GlobalClass) getApplication()).getStringPref("foto"))
                    .transform(new CircleTransform())
                    .into(imgUserPost);
        }


        mlEdtPost.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                   // mlEdtPost.setFocusableInTouchMode(true);
//                    mlEdtPost.setHeight(300);
                   lPilihAction.setVisibility(View.VISIBLE);
//                    imgUserPost.setVisibility(View.GONE);
                    lPilihMedia.setVisibility(View.GONE);
                }else{
//                    mlEdtPost.setHeight(100);
                    lPilihAction.setVisibility(View.GONE);
                    imgUserPost.setVisibility(View.VISIBLE);
                   // mlEdtPost.setFocusableInTouchMode(false);
                }
            }
        });

        lBtnLangsung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ilive = new Intent(getApplicationContext(), RtmpActivity.class);
                ilive.putExtra("isi",mlEdtPost.getText().toString());
                startActivity(ilive);
            }
        });

        lBtnMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lPilihMedia.setVisibility(View.VISIBLE);
                //mlEdtPost.clearFocus();
               // rvPostMedia.setVisibility(View.GONE);
                lBtnPost.setVisibility(View.GONE);
                //lPilihAction.setVisibility(View.VISIBLE);
            }
        });

        imbPilihGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFromGallery();
            }
        });

        imbPilihCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureFromCamera();
            }
        });

        imbCLosePilihMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lPilihMedia.setVisibility(View.GONE);
                lBtnPost.setVisibility(View.VISIBLE);
                lPilihAction.setVisibility(View.GONE);
               // potoList.clear();
            }
        });

        btnBagikanPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lPilihMedia.setVisibility(View.GONE);
                rvPostMedia.setVisibility(View.GONE);
                lBtnPost.setVisibility(View.VISIBLE);
                lPilihAction.setVisibility(View.GONE);
                //mlEdtPost.setHeight(100);
                imgUserPost.setVisibility(View.VISIBLE);
                mlEdtPost.clearFocus();
            }
        });

        swpBerita = (SwipeRefreshLayout) findViewById(R.id.swpBerita);
        swpBerita.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            public void onRefresh()
            {
                BeritaActivity.this.swpBerita.postDelayed(new Runnable()
                {
                    public void run()
                    {
                        loadListBerita();
                        BeritaActivity.this.swpBerita.setRefreshing(false);
                        BeritaActivity.this.swpBerita.invalidate();
                    }
                }, 2000L);
            }
        });

        loadListBerita();

    }


    public void loadListBerita(){
        //listBerita.clear();
       JSONObject jListBerita = okHttpGetHandler.getBerita(((GlobalClass) getApplication()).getStringPref("user_id"),((GlobalClass) getApplication()).getStringPref("username"));
       //if(!listChat.isEmpty()) {
//           Log.d("listberita", jListBerita.toString());

           JSONArray dataArray = null;
           try {

               dataArray = (JSONArray) jListBerita.getJSONArray("data");
               if (dataArray.length() > 0) {
                   for (int d = 0; d < dataArray.length(); d++) {
                       JSONObject jdata = dataArray.getJSONObject(d);
                       Integer readedId = Integer.valueOf(jdata.getString("id"));
                       if(!readedList.contains(readedId)) {
                           String nama_table = jdata.getString("nama_table");
                           String judul, isi, nama, poto_user, status, created_by, user_id, mode_id;
                           JSONArray media, lastpenyuka;
                           int jpenyuka,jkomen;
                           judul = "";
                           isi = "";

                           nama = "";
                           poto_user = "null";
                           user_id = "null";

                           mode_id = jdata.getString("mode_id");
                           created_by = jdata.getString("created_by");

                           JsonParser jsonParser = new JsonParser();
                           JsonObject juser = jsonParser.parse(created_by).getAsJsonObject();
                           nama = juser.get("display_name").getAsString();
                           poto_user = Constants.CLOUD_SERVER + "/" + juser.get("photo").getAsString();
                           user_id = juser.get("id").getAsString();

                           status = "";

                           media = new JSONArray();
                           Log.d("gbr",jdata.getJSONArray("gambar").toString());
                           if(!mode_id.equals("7")) {
                               media = (JSONArray) jdata.getJSONArray("gambar");
                           }else{
                               JSONObject lmedia = new JSONObject();
                               try {
                                   lmedia.put("id", "1");
                                   lmedia.put("id_ortu", jdata.getString("id"));
                                   lmedia.put("file", jdata.getString("gambar_utama"));
                                   lmedia.put("url", "");
                                   lmedia.put("caption", jdata.getString("isi"));
                                   lmedia.put("parent", "informasi");
                                   lmedia.put("created_at", jdata.getString("created_at"));
                                   lmedia.put("created_by", jdata.getString("created_by"));
                               } catch (JSONException e) {
                                   // TODO Auto-generated catch block
                                   e.printStackTrace();
                               }

                               media.put(lmedia);
                           }

                           lastpenyuka = new JSONArray();
                           lastpenyuka = (JSONArray) jdata.getJSONArray("penyuka");
                           jpenyuka = 0;
                           jpenyuka = Integer.parseInt(jdata.getString("jpenyuka"));
                           jkomen = 0;
                           jkomen = Integer.parseInt(jdata.getString("jkomen"));
                           final Berita listBeritaItem = new Berita(getApplicationContext(), user_id, jdata.getString("id"), jdata.getString("judul"), jdata.getString("isi"), nama, poto_user, status, jdata.getString("nama_table"), jdata.getString("ilike"), media, jpenyuka, lastpenyuka, ((GlobalClass) getApplication()).getStringPref("user_id"),mode_id,jkomen);
                           listBerita.add(0,listBeritaItem);
                           readedList.add(Integer.parseInt(jdata.getString("id")));
                       }
                   }
                   beritaAdapter.notifyDataSetChanged();
               }
           } catch (JSONException e) {
               e.printStackTrace();
           }

           ItemClickSupport.addTo(rvBerita).setOnItemClickListener(
                   new ItemClickSupport.OnItemClickListener() {
                       @Override
                       public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                           passId = listBerita.get(position).getId();
                           passNamaTable = listBerita.get(position).getNamaTable();
                           Log.d("list_berita_selected", passNamaTable);
                           //openChat(passId, passNamaTable);
                           //finish();
                       }

                   }
           );
      // }
    }

    private void pickFromGallery(){
        String[] perms = { "android.permission.READ_EXTERNAL_STORAGE"};
        if (EasyPermissions.hasPermissions(BeritaActivity.this, perms)) {
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
            EasyPermissions.requestPermissions(BeritaActivity.this, "Meminta Ijin Akses File", GALLERY_REQUEST_CODE, perms);
        }
    }

    private void captureFromCamera() {
        String[] perms = { "android.permission.CAMERA","android.permission.WRITE_EXTERNAL_STORAGE"};
        if (EasyPermissions.hasPermissions(getApplicationContext(), perms)) {
            Log.d("camera","action");
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    File f = photoFile;
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
                }
            }
        }else{
            EasyPermissions.requestPermissions(BeritaActivity.this, "Meminta Ijin Akses Camera", CAMERA_REQUEST_CODE, perms);
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

        // Save a file: path for use with ACTION_VIEW intents
        //if(buat.equals("photo")){
        photoFilePath = image.getAbsolutePath();
//        }else if(buat.equals("icon")){
//            iconFilePath = image.getAbsolutePath();
//        }

        return image;
    }

    private void tambahPoto(String potoId, String potoPath,String realPotoPath, String mime, Boolean trimmed){
        //resize image
        String fileName=realPotoPath.substring(realPotoPath.lastIndexOf("/")+1);
        Poto poto = null;
        if(mime.contains("image")) {
            //rim = new ResizeImage();
            //String editedPhotoFilePath = rim.resizeAndCompressImageBeforeSend(getApplicationContext(), realPotoPath, fileName);
            //Log.d("editedPath", editedPhotoFilePath);
            Uri editedUri = Uri.fromFile(new File(realPotoPath));
            String editedFileName = realPotoPath.substring(realPotoPath.lastIndexOf("/") + 1);

            //Log.d("editedFileName", editedFileName);
            //Log.d("editedUri", editedUri.getPath());

            poto = new Poto(potoId, editedFileName, realPotoPath,editedUri, mime);

            potoList.add(poto);
            potoAdapter.notifyDataSetChanged();

            rvPostMedia.scrollToPosition(potoList.size() - 1);
        }else if(mime.contains("video")) {
            if(!trimmed) {
                File file = new File(realPotoPath);
                if (file.exists()) {
                    startActivityForResult(new Intent(getApplicationContext(),
                                    VideoTrimmerActivity.class).putExtra("EXTRA_PATH", realPotoPath),
                            VIDEO_TRIM);
                    overridePendingTransition(0, 0);
                } else {
                    Toast.makeText(getApplicationContext(), "Please select proper video", Toast.LENGTH_LONG);
                }
            }else{
                Uri editedUri = Uri.fromFile(new File(realPotoPath));
                String editedFileName = realPotoPath.substring(realPotoPath.lastIndexOf("/") + 1);

                poto = new Poto(potoId, editedFileName, realPotoPath,editedUri, mime);

                potoList.add(poto);
                potoAdapter.notifyDataSetChanged();

                rvPostMedia.scrollToPosition(potoList.size() - 1);
            }

        }

        rvPostMedia.setVisibility(View.VISIBLE);
    }

    public void dsPhotoEdit(Uri uri){
        String[] perms = { "android.permission.READ_EXTERNAL_STORAGE"};
        if (EasyPermissions.hasPermissions(BeritaActivity.this, perms)) {
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
            EasyPermissions.requestPermissions(BeritaActivity.this, "Meminta Ijin Akses", DS_PHOTO_EDITOR_REQUEST_CODE, perms);
        }
    }

    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        super.onActivityResult(requestCode, resultCode, data);
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
                    String mime = getFileMimeType(imgDecodableString);
                    if (mime.contains("image")) {
                        dsPhotoEdit(selectedImage);
                        //tambahPoto("null", photoFilePath, imgDecodableString, mime, true);
                    } else if (mime.contains("video")) {
                        tambahPoto("null", photoFilePath, imgDecodableString, mime, false);
                    }
                    cursor.close();

                    break;
                case CAMERA_REQUEST_CODE:
                    File f = new File(photoFilePath);
                    selectedImage = Uri.fromFile(f);
                    dsPhotoEdit(selectedImage);
                    //tambahPoto("null", photoFilePath, photoFilePath, getFileMimeType(photoFilePath), true);
                    break;
                case DS_PHOTO_EDITOR_REQUEST_CODE:
                    Uri outputUri = data.getData();
                    String outputPath = outputUri.getPath();
                    photoFilePath = outputPath;
                    String fileName=photoFilePath.substring(photoFilePath.lastIndexOf("/")+1);
                    rim = new ResizeImage();
                    photoFilePath = rim.resizeAndCompressImageBeforeSend(this,photoFilePath,fileName);

                    tambahPoto("null", photoFilePath, photoFilePath, getFileMimeType(photoFilePath), true);

                    break;
                case VIDEO_TRIM:
                    if (data != null) {
                        String videoPath = data.getExtras().getString("INTENT_VIDEO_FILE");

                        tambahPoto("null", videoPath, videoPath, getFileMimeType(videoPath), true);
                    }

                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + requestCode);
            }
        }
    }

    public String getFileMimeType(String url){
        File file = new File(url);
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String mimeType = fileNameMap.getContentTypeFor(file.getName());

        return mimeType;
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if(requestCode==GALLERY_REQUEST_CODE){
            pickFromGallery();
        }
        if(requestCode==CAMERA_REQUEST_CODE){
            captureFromCamera();
        }
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
