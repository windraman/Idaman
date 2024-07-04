package banjarbarukota.go.id.idaman.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import banjarbarukota.go.id.idaman.Adapter.Poto;
import banjarbarukota.go.id.idaman.Adapter.PotoAdapter;
import banjarbarukota.go.id.idaman.LokasiWizardActivity;
import banjarbarukota.go.id.idaman.R;
import banjarbarukota.go.id.idaman.Utility.ResizeImage;
import banjarbarukota.go.id.idaman.VideoTrimmerActivity;
import pub.devrel.easypermissions.EasyPermissions;

public class Langkah3Fragment extends Fragment implements EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks{
    private static final int GALLERY_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    public View view;

    private static final int REQUEST_TAKE_GALLERY_VIDEO = 100;
    private static final int VIDEO_TRIM = 101;

    public static RecyclerView potoRecyclerView;
    private PotoAdapter potoAdapter;
    ImageButton imbTambahPoto;
    private String photoFilePath;

    ResizeImage rim;

    public LokasiWizardActivity lokasiWizardActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.lokasi_upload_poto_list, container, false);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        lokasiWizardActivity = (LokasiWizardActivity) getActivity();

        imbTambahPoto = (ImageButton) view.findViewById(R.id.imbTambahGambar);

        potoRecyclerView = (RecyclerView) view.findViewById(R.id.rvPoto);

        potoAdapter = new PotoAdapter(lokasiWizardActivity.potoList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        potoRecyclerView.setLayoutManager(mLayoutManager);
        potoRecyclerView.setItemAnimator(new DefaultItemAnimator());
        potoRecyclerView.setAdapter(potoAdapter);

        potoRecyclerView.addItemDecoration(new DividerItemDecoration(potoRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

        imbTambahPoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();

            }
        });

        return view;
    }

    private void tambahPoto(String potoId, String potoPath,String realPotoPath, String mime, Boolean trimmed){
        //resize image
        String fileName=realPotoPath.substring(realPotoPath.lastIndexOf("/")+1);
        Poto poto = null;
        if(mime.contains("image")) {
            rim = new ResizeImage();
            String editedPhotoFilePath = rim.resizeAndCompressImageBeforeSend(getActivity(), realPotoPath, fileName);
            //Log.d("editedPath", editedPhotoFilePath);
            Uri editedUri = Uri.fromFile(new File(editedPhotoFilePath));
            String editedFileName = editedPhotoFilePath.substring(editedPhotoFilePath.lastIndexOf("/") + 1);

            Log.d("editedFileName", editedFileName);
            Log.d("editedUri", editedUri.getPath());

            poto = new Poto(potoId, editedFileName, editedPhotoFilePath,editedUri, mime);

            lokasiWizardActivity.potoList.add(poto);
            potoAdapter.notifyDataSetChanged();

            potoRecyclerView.scrollToPosition(lokasiWizardActivity.potoList.size() - 1);
        }else if(mime.contains("video")) {
            if(!trimmed) {
                File file = new File(realPotoPath);
                if (file.exists()) {
                    startActivityForResult(new Intent(getActivity(),
                                    VideoTrimmerActivity.class).putExtra("EXTRA_PATH", realPotoPath),
                            VIDEO_TRIM);
                    getActivity().overridePendingTransition(0, 0);
                } else {
                    Toast.makeText(getActivity(), "Please select proper video", Toast.LENGTH_LONG);
                }
            }else{
                Uri editedUri = Uri.fromFile(new File(realPotoPath));
                String editedFileName = realPotoPath.substring(realPotoPath.lastIndexOf("/") + 1);

                poto = new Poto(potoId, editedFileName, realPotoPath,editedUri, mime);

                lokasiWizardActivity.potoList.add(poto);
                potoAdapter.notifyDataSetChanged();

                potoRecyclerView.scrollToPosition(lokasiWizardActivity.potoList.size() - 1);
            }

        }


    }

    private void selectImage() {
        final CharSequence[] options = { "Ambil Foto", "Pilih dari Gallery","Batal" };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
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
            EasyPermissions.requestPermissions(getActivity(), "Meminta Ijin Akses", GALLERY_REQUEST_CODE, perms);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = null;
        File image = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "IDAMAN_" + timeStamp + "_";
            File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        }else{
            Date date = new Date(String.valueOf(new Date()));
            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getContext().getApplicationContext());
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

    private void captureFromCamera() {
        String[] perms = { "android.permission.CAMERA","android.permission.WRITE_EXTERNAL_STORAGE"};
        if (EasyPermissions.hasPermissions(getActivity().getApplicationContext(), perms)) {
            Log.d("camera","action");
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
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
            EasyPermissions.requestPermissions(getActivity(), "Meminta Ijin Akses", CAMERA_REQUEST_CODE, perms);
        }
    }


    public void onActivityResult(int requestCode,int resultCode,Intent data){
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    //data.getData return the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    Log.d("selctedImage", selectedImage.toString());
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    // Get the cursor
                    Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();
                    //Get the column index of MediaStore.Images.Media.DATA
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    //Gets the String value in the column
                    String imgDecodableString = cursor.getString(columnIndex);
                    String mime = getFileMimeType(imgDecodableString);
                    if(mime.contains("image")) {
                        tambahPoto("null", photoFilePath, imgDecodableString, mime, true);
                    }else if(mime.contains("video")) {
                        tambahPoto("null", photoFilePath, imgDecodableString, mime, false);
                    }
                    cursor.close();

                    break;
                case CAMERA_REQUEST_CODE:
                     tambahPoto("null",photoFilePath,photoFilePath, getFileMimeType(photoFilePath), true);

                    break;
                case  VIDEO_TRIM:
                    if (data != null) {
                        String videoPath = data.getExtras().getString("INTENT_VIDEO_FILE");

                        tambahPoto("null",videoPath,videoPath, getFileMimeType(videoPath),true);
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
