package banjarbarukota.go.id.idaman;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import banjarbarukota.go.id.GlobalClass;
import banjarbarukota.go.id.idaman.Adapter.ViewPagerAdapter;
import banjarbarukota.go.id.idaman.Utility.CircleTransform;
import banjarbarukota.go.id.idaman.Utility.Constants;
import banjarbarukota.go.id.idaman.Utility.OkHttpGetHandler;
import banjarbarukota.go.id.idaman.Utility.OkHttpPostHandler;
import banjarbarukota.go.id.idaman.Utility.ResizeImage;
import banjarbarukota.go.id.idaman.Utility.Utility;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pub.devrel.easypermissions.EasyPermissions;

import static banjarbarukota.go.id.idaman.Utility.Constants.CLOUD_SERVER;

public class ProfilUserActivity extends AppCompatActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    private static final int GALLERY_REQUEST_CODE = 1 ;
    private static final int CAMERA_REQUEST_CODE = 2;
    String user_id, myid, kenal, dikenal;

    ImageView imgPoto, imgIcon;
    TextView tvNamaTampilan, tvEmail, tvJCerita, tvJTerkenal, tvJKenalan;
    EditText etNamaTampilan, etEmail;
    ImageButton imbUbahPoto, imbUbahIcon;
    Button btnEdit, btnKenalan;

    String mode,buat;
    private String photoFilePath,iconFilePath;
    private Toolbar mTopToolbar;

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpg");

    public static final int PICK_IMAGE_CODE = 100;
    public static final int DS_PHOTO_EDITOR_REQUEST_CODE = 200;

    private static final int REQUEST_EXTERNAL_STORAGE_CODE = 1000;

    public static final String OUTPUT_PHOTO_DIRECTORY = "ds_photo_editor_sample";

    ResizeImage rim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profil);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent i = getIntent();
        user_id = i.getStringExtra("user_id");
        myid = i.getStringExtra("myid");

        mode = "normal";
        buat = null;


        prepareView();


        getProfilUser(user_id,myid);

        photoFilePath="tidak_berubah";
        iconFilePath="tidak_berubah";
    }

    public void prepareView(){
        mTopToolbar = (Toolbar) findViewById(R.id.profil_toolbar2);
        setSupportActionBar(mTopToolbar);

        imgPoto = (ImageView) findViewById(R.id.imgPotoPengguna);
        imgIcon = (ImageView) findViewById(R.id.imgIconPeta);

        tvNamaTampilan = (TextView) findViewById(R.id.tvNamaPengguna);
        tvEmail = (TextView) findViewById(R.id.tvEmailPengguna);

        etNamaTampilan = (EditText) findViewById(R.id.edtNamaPengguna);
        etEmail = (EditText) findViewById(R.id.edtEmailPengguna);
        tvJCerita = (TextView) findViewById(R.id.tvJCerita);
        tvJTerkenal = (TextView) findViewById(R.id.tvJPenyimak);
        tvJKenalan = (TextView) findViewById(R.id.tvJDisimak);

        btnKenalan = (Button) findViewById(R.id.btnKenalan);
        btnKenalan.setOnClickListener(this);


        if(!((GlobalClass) getApplication()).getStringPref("user_id").equals(user_id)){
            mTopToolbar.setVisibility(View.GONE);
            btnKenalan.setVisibility(View.VISIBLE);
        } else{
            mTopToolbar.setVisibility(View.VISIBLE);
            btnKenalan.setVisibility(View.GONE);
        }



//        imbUbahPoto = (ImageButton) findViewById(R.id.imbUbahPoto);
//        imbUbahPoto.setOnClickListener(this);
//        imbUbahIcon = (ImageButton) findViewById(R.id.imbUbahIcon);
//        imbUbahIcon.setOnClickListener(this);

//        btnEdit = (Button) findViewById(R.id.btnEditProfil);
//        btnEdit.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_save) {
            //Log.d("SImpan","profil");

            updateProfilUser(etNamaTampilan.getText().toString(),etEmail.getText().toString(),photoFilePath,iconFilePath);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getProfilUser(String user_id, String myid) {
        OkHttpGetHandler okget = new OkHttpGetHandler();
        try {
            JSONObject jobj = okget.getUserProfil(user_id, myid);
            String name = "";
            if (jobj.getString("display_name").equals("null")) {
                tvNamaTampilan.setText(jobj.getString("name"));
                etNamaTampilan.setText(jobj.getString("name"));
            } else {
                tvNamaTampilan.setText(jobj.getString("display_name"));
                etNamaTampilan.setText(jobj.getString("display_name"));
            }

            tvJCerita.setText(jobj.getString("jcerita"));
            tvJTerkenal.setText(jobj.getString("jterkenal"));
            tvJKenalan.setText(jobj.getString("jkenalan"));

            kenal = jobj.getString("kenalanku");
            dikenal = jobj.getString("mengenalku");

            if(kenal.equals("0")) {
                if(dikenal.equals("0")) {
                    btnKenalan.setText("KENALAN");
                }else{
                    btnKenalan.setText("SALAMAN");
                }
            }else{
                btnKenalan.setText("GA KENAL");
            }

            tvEmail.setText(jobj.getString("email"));
            etEmail.setText(jobj.getString("email"));
            String photo = jobj.getString("photo");
            String icon = jobj.getString("icon");
            if (!photo.equals("") || !photo.equals("null")){
                Picasso.get()
                        .load(photo)
                        .transform(new CircleTransform())
                        .into(imgPoto);
            }
            if (!icon.equals("") || !icon.equals("null")) {
                Picasso.get()
                        .load(icon)
                        .into(imgIcon);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnEditProfil: {
                if (mode.equals("normal")) {
                    mode = "edit";
                    Log.d("tombol",mode);
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            tvNamaTampilan.setVisibility(View.GONE);
                            etNamaTampilan.setVisibility(View.VISIBLE);

                            tvEmail.setVisibility(View.GONE);
                            etEmail.setVisibility(View.VISIBLE);

                            imbUbahPoto.setVisibility(View.VISIBLE);
                            imbUbahIcon.setVisibility(View.VISIBLE);
                        }
                    });

                }else if (mode.equals("edit")) {
                    mode = "normal";
                    Log.d("tombol",mode);
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            tvNamaTampilan.setVisibility(View.VISIBLE);
                            etNamaTampilan.setVisibility(View.GONE);

                            tvEmail.setVisibility(View.VISIBLE);
                            etEmail.setVisibility(View.GONE);

                            imbUbahPoto.setVisibility(View.GONE);
                            imbUbahIcon.setVisibility(View.GONE);
                        }
                    });
                }
            }
            case R.id.imbUbahPoto: {
                buat = "photo";
                selectImage();
                break;
            }
            case R.id.imbUbahIcon: {
                buat = "icon";
                selectImage();
                break;
            }

            case R.id.btnKenalan: {
                OkHttpPostHandler okpost = new OkHttpPostHandler();
                if(kenal.equals("0")){
                    String kenalid = okpost.kenalan(myid,user_id);
                    kenal = kenalid;
                    btnKenalan.setText("GA KENAL");
                }else{
                    okpost.kenalan_rem(kenal);
                    kenal = "0";
                    if(dikenal.equals("0")) {
                        btnKenalan.setText("KENALAN");
                    }else{
                        btnKenalan.setText("SALAMAN");
                    }
                }
                break;
            }
        }
    }

    private void selectImage() {
        final CharSequence[] options = { "Ambil Foto", "Pilih dari Gallery","Batal" };
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfilUserActivity.this);
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
        if (EasyPermissions.hasPermissions(ProfilUserActivity.this, perms)) {
            //Create an Intent with action as ACTION_PICK
            Intent intent = new Intent(Intent.ACTION_PICK);
            // Sets the type as image/*. This ensures only components of type image are selected
            intent.setType("image/*");
            //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
            String[] mimeTypes = {"image/jpeg", "image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            // Launching the Intent
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        }else{
            EasyPermissions.requestPermissions(ProfilUserActivity.this, "Meminta Ijin Akses", GALLERY_REQUEST_CODE, perms);
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
        if(buat.equals("photo")){
            photoFilePath = image.getAbsolutePath();
        }else if(buat.equals("icon")){
            iconFilePath = image.getAbsolutePath();
        }

        return image;
    }

    private void captureFromCamera() {
        String[] perms = { "android.permission.CAMERA","android.permission.WRITE_EXTERNAL_STORAGE"};
        if (EasyPermissions.hasPermissions(ProfilUserActivity.this, perms)) {

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
            EasyPermissions.requestPermissions(ProfilUserActivity.this, "Meminta Ijin Akses", CAMERA_REQUEST_CODE, perms);
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
                    cursor.close();

                    // Set the Image in ImageView after decoding the String

                    if (buat.equals("icon")) {
                        iconFilePath = imgDecodableString;
                        File f = new File(iconFilePath);
                        dsPhotoEdit(selectedImage);
                    } else if (buat.equals("photo")) {
                        photoFilePath = imgDecodableString;
                        File f = new File(photoFilePath);
                        dsPhotoEdit(selectedImage);
                    }
                    Log.d("potopat", photoFilePath);
                    break;
                case CAMERA_REQUEST_CODE:
                    if (buat.equals("icon")) {
                        iconFilePath = iconFilePath;
                        File f = new File(iconFilePath);
                        Uri takenIcon = Uri.fromFile(f);
                        Picasso.get()
                                .load(f)
                                .transform(new CircleTransform())
                                .into(imgPoto);
                        dsPhotoEdit(takenIcon);
                    } else if (buat.equals("photo")) {
                        photoFilePath = photoFilePath;
                        File f = new File(photoFilePath);
                        Uri takenPhoto = Uri.fromFile(f);
                        Picasso.get()
                                .load(f)
                                .transform(new CircleTransform())
                                .into(imgPoto);
                        dsPhotoEdit(takenPhoto);
                    }
                    break;
                case DS_PHOTO_EDITOR_REQUEST_CODE:
                    Uri outputUri = data.getData();
                    String outputPath = outputUri.getPath();

                    if (buat.equals("icon")) {
                        iconFilePath = outputPath;
                        String fileName = iconFilePath.substring(iconFilePath.lastIndexOf("/") + 1);
                        rim = new ResizeImage();
                        iconFilePath = rim.resizeAndCompressImageBeforeSend(this, iconFilePath, fileName);
                        File f = new File(iconFilePath);
                        Picasso.get()
                                .load(f)
                                .into(imgIcon);
                    } else if (buat.equals("photo")) {
                        photoFilePath = outputPath;
                        String fileName = photoFilePath.substring(photoFilePath.lastIndexOf("/") + 1);
                        rim = new ResizeImage();
                        photoFilePath = rim.resizeAndCompressImageBeforeSend(this, photoFilePath, fileName);
                        File f = new File(photoFilePath);
                        Picasso.get()
                                .load(f)
                                .transform(new CircleTransform())
                                .into(imgPoto);
                        // Toast.makeText(this, "Photo saved in " + OUTPUT_PHOTO_DIRECTORY + " folder.", Toast.LENGTH_LONG).show();
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + requestCode);
            }
        }
    }

    public void dsPhotoEdit(Uri uri){
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
    }

    public void updateProfilUser(String display_name, String email, String photoFilePath, String iconFilePath){
        OkHttpClient client = new OkHttpClient();

        String URL = CLOUD_SERVER + "/api/update_profil_user";

//        FormBody.Builder builder = new FormBody.Builder();
//        builder.add("id", grup_id);
        String photoFileName=photoFilePath.substring(photoFilePath.lastIndexOf("/")+1);
        String iconFileName=iconFilePath.substring(iconFilePath.lastIndexOf("/")+1);

        RequestBody builder = null;
        if(!photoFilePath.equals("tidak_berubah") & !iconFilePath.equals("tidak_berubah")) {
            Log.d("Updet", "update 2 2 nya");
            builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("id", user_id)
                    .addFormDataPart("display_name", display_name)
                    .addFormDataPart("email", email)
                    .addFormDataPart("photo", photoFileName,
                            RequestBody.create(MEDIA_TYPE_PNG, new File(photoFilePath)))
                    .addFormDataPart("icon", iconFileName,
                            RequestBody.create(MEDIA_TYPE_PNG, new File(iconFilePath)))
                    .build();
        }

        if(photoFilePath.equals("tidak_berubah") & !iconFileName.equals("tidak_berubah")) {
            Log.d("Updet", "update icon");
            builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("id", user_id)
                    .addFormDataPart("display_name", display_name)
                    .addFormDataPart("email", email)
                    .addFormDataPart("icon", iconFileName,
                            RequestBody.create(MEDIA_TYPE_PNG, new File(iconFilePath)))
                    .build();
        }

        if(!photoFilePath.equals("tidak_berubah") & iconFilePath.equals("tidak_berubah")) {
            Log.d("Updet", "update poto");
            builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("id", user_id)
                    .addFormDataPart("display_name", display_name)
                    .addFormDataPart("email", email)
                    .addFormDataPart("photo", photoFileName,
                            RequestBody.create(MEDIA_TYPE_PNG, new File(photoFilePath)))
                    .build();
        }

        if(photoFilePath.equals("tidak_berubah") & iconFilePath.equals("tidak_berubah")) {
            Log.d("Updet", "text aja");
            builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("id", user_id)
                    .addFormDataPart("display_name", display_name)
                    .addFormDataPart("email", email)
                    .build();
        }

        RequestBody formBody = builder;

        Request request = new Request.Builder()
                .url(URL)
                .addHeader("X-Authorization-Token",Utility.md5(Constants.SECRET_KEY+ Utility.unixTime() + System.getProperty("http.agent")))
                .addHeader("X-Authorization-Time",Utility.unixTime())
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String jsondata = response.body().string();
            Log.d("Updet", jsondata);
            // System.out.println(response.body().string());
            try {
                JSONObject jobj = new JSONObject(jsondata);
                Integer success = jobj.getInt("api_status");
                if(success==1){
                    JSONObject jdata = new JSONObject(jobj.getString("data"));
                    ((GlobalClass) getApplication()).saveStringPref("nama_tampilan",jdata.getString("display_name"));
                    ((GlobalClass) getApplication()).saveStringPref("foto",CLOUD_SERVER + "/" + jdata.getString("photo"));
                    ((GlobalClass) getApplication()).saveStringPref("icon",CLOUD_SERVER + "/" + jdata.getString("icon"));
                    finish();
                }else{
                    Toast.makeText(ProfilUserActivity.this, "Update gagal, Silahkan Coba Lagi.", Toast.LENGTH_LONG).show();
                }
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
    public void onRationaleAccepted(int requestCode) {

    }

    @Override
    public void onRationaleDenied(int requestCode) {

    }
}
