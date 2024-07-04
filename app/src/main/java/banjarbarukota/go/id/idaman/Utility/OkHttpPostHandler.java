package banjarbarukota.go.id.idaman.Utility;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static banjarbarukota.go.id.idaman.Utility.Constants.CLOUD_SERVER;

/**
 * Created by Wahyu on 1/25/2018.
 */

public class OkHttpPostHandler {

    private static MediaType MEDIA_TYPE = MediaType.parse("image/jpg");

    long unixTime = System.currentTimeMillis() / 1000L;


    public String postLokasi(String tags, String nama_lokasi, String deskripsi,String lat, String lon, String jalan, String nomor, String kota, String kecamatan, String kelurahan, String kodepos, String telepon, String default_icon){
        String resp = null;
        String id = null;
        //String url = Constants.CLOUD_SERVER + "/api/tambah_lokasi";
        String url = Constants.CLOUD_SERVER + "/api/add_lokasi_temp";

        OkHttpClient client = new OkHttpClient();

        RequestBody builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("tags", tags)
                .addFormDataPart("nama", nama_lokasi)
                .addFormDataPart("deskripsi", deskripsi)
                .addFormDataPart("lat", lat)
                .addFormDataPart("lon", lon)
                .addFormDataPart("jalan", jalan)
                .addFormDataPart("nomor", nomor)
                .addFormDataPart("regency_id", kota)
                .addFormDataPart("district_id", kecamatan)
                .addFormDataPart("village_id", kelurahan)
                .addFormDataPart("kodepos", kodepos)
                .addFormDataPart("telepon", telepon)
                .addFormDataPart("default_icon", default_icon)
                .build();

        // code request code here


        RequestBody formBody = builder;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Authorization-Token",md5(Constants.SECRET_KEY+String.valueOf(unixTime)+ System.getProperty("http.agent")))
                .addHeader("X-Authorization-Time",String.valueOf(unixTime))
                .post(formBody)
                .build();

       // Log.d("buil", request.toString());

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String jsondata = response.body().string();
            // System.out.println(response.body().string());
            try {
                JSONObject jobj = new JSONObject(jsondata);
                Integer success = jobj.getInt("api_status");
                if(success==1){
                        resp = jobj.toString();
                        JSONObject jid= new JSONObject(jobj.getString("id"));
                        id = jid.toString();

//                    JSONObject jdata = new JSONObject(jobj.getString("data"));
//                    ((GlobalClass) getApplication()).saveStringPref("nama_tampilan",jdata.getString("display_name"));
//                    ((GlobalClass) getApplication()).saveStringPref("foto",CLOUD_SERVER + "/" + jdata.getString("photo"));
//                    finish();
                }else{
                    id=null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            Log.d("scan", e.toString());
            e.printStackTrace();
        }

        return id;

    }

    public String nuloc(String tags, String nama_lokasi, String deskripsi,String lat, String lon, String jalan, String nomor, String kota, String kecamatan, String kelurahan, String kodepos, String telepon, String website, String default_icon, String created_by){
        String resp = null;
        String id = null;
        String url = Constants.CLOUD_SERVER + "/api/add_lokasi_temp";

        OkHttpClient client = new OkHttpClient();

        RequestBody builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("tags", tags)
                .addFormDataPart("nama", nama_lokasi)
                .addFormDataPart("deskripsi", deskripsi)
                .addFormDataPart("lat", lat)
                .addFormDataPart("lon", lon)
                .addFormDataPart("jalan", jalan)
                .addFormDataPart("nomor", nomor)
                .addFormDataPart("regency_id", kota)
                .addFormDataPart("district_id", kecamatan)
                .addFormDataPart("village_id", kelurahan)
                .addFormDataPart("kode_pos", kodepos)
                .addFormDataPart("telepon", telepon)
                .addFormDataPart("website", website)
                .addFormDataPart("default_icon", default_icon)
                .addFormDataPart("created_by", created_by)
                .build();

        // code request code here


        RequestBody formBody = builder;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Authorization-Token",md5(Constants.SECRET_KEY+String.valueOf(unixTime)+ System.getProperty("http.agent")))
                .addHeader("X-Authorization-Time",String.valueOf(unixTime))
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String jsondata = response.body().string();
            // System.out.println(response.body().string());
            try {
                JSONObject jobj = new JSONObject(jsondata);
                Integer success = jobj.getInt("api_status");
                if(success==1){
                    resp = jobj.toString();
                    id = jobj.getString("id");
                }else{
                    id=null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            Log.d("scan", e.toString());
            e.printStackTrace();
        }

        return id;
    }

    public String sukai(String user_id, String nama_table,String key_id, String master_reaksi_id){
        String resp = null;
        String id = null;
        String url = Constants.CLOUD_SERVER + "/api/sukai";

        OkHttpClient client = new OkHttpClient();

        RequestBody builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id", user_id)
                .addFormDataPart("nama_table", nama_table)
                .addFormDataPart("key_id", key_id)
                .addFormDataPart("master_reaksi_id", master_reaksi_id)
                .build();

        // code request code here


        RequestBody formBody = builder;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Authorization-Token",md5(Constants.SECRET_KEY+String.valueOf(unixTime)+ System.getProperty("http.agent")))
                .addHeader("X-Authorization-Time",String.valueOf(unixTime))
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String jsondata = response.body().string();
            // System.out.println(response.body().string());
            try {
                JSONObject jobj = new JSONObject(jsondata);
                Integer success = jobj.getInt("api_status");
                if(success==1){
                    resp = jobj.toString();
                    id = jobj.getString("id");
                }else{
                    id=null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            Log.d("scan", e.toString());
            e.printStackTrace();
        }

        return id;
    }

    public void sukai_rem(String id){
        String resp = null;
        String url = Constants.CLOUD_SERVER + "/api/sukai_rem";

        OkHttpClient client = new OkHttpClient();

        RequestBody builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", id)
                .build();

        // code request code here


        RequestBody formBody = builder;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Authorization-Token",md5(Constants.SECRET_KEY+String.valueOf(unixTime)+ System.getProperty("http.agent")))
                .addHeader("X-Authorization-Time",String.valueOf(unixTime))
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String jsondata = response.body().string();
            // System.out.println(response.body().string());
            try {
                JSONObject jobj = new JSONObject(jsondata);
                Integer success = jobj.getInt("api_status");
                if(success==1){
                    resp = jobj.toString();
                }else{
                    resp=null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            Log.d("err", e.toString());
            e.printStackTrace();
        }
    }

    public static String getMimeType(String url) {
        File file = new File(url);
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        return mimeType;
    }
    
    public String uploadMedia(String nama_table, String id_ortu, String filePath,String fileName, String caption, String user_id){
        String res = null;
        
        MEDIA_TYPE = MediaType.parse(getMimeType(filePath));
        
        OkHttpClient client = new OkHttpClient();

        String URL = CLOUD_SERVER + "/api/tambah_media";

//        FormBody.Builder builder = new FormBody.Builder();
//        builder.add("id", grup_id);

        RequestBody builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("created_by", user_id)
                .addFormDataPart("parent", nama_table)
                .addFormDataPart("id_ortu", id_ortu)
                .addFormDataPart("caption", caption)
                .addFormDataPart("jenis", getMimeType(fileName))
                .addFormDataPart("file", fileName,
                        RequestBody.create(MEDIA_TYPE, new File(filePath)))
                .build();

        RequestBody formBody = builder;

        Request request = new Request.Builder()
                .url(URL)
                .addHeader("X-Authorization-Token",md5(Constants.SECRET_KEY+String.valueOf(unixTime)+ System.getProperty("http.agent")))
                .addHeader("X-Authorization-Time",String.valueOf(unixTime))
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            //Log.d("Siapkk Login", response.body().string());
            String jsondata = response.body().string();

            try {
                JSONObject jobj = new JSONObject(jsondata);
                res = jobj.toString();
                Integer success = jobj.getInt("api_status");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            Log.d("scan", e.toString());
            e.printStackTrace();
        }

        return res;
    }

    public String kenalan(String user_id, String kenalan_id){
        String resp = null;
        String id = null;
        String url = Constants.CLOUD_SERVER + "/api/add_kenalan";

        OkHttpClient client = new OkHttpClient();

        RequestBody builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id", user_id)
                .addFormDataPart("target_id", kenalan_id)
                .build();

        // code request code here


        RequestBody formBody = builder;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Authorization-Token",md5(Constants.SECRET_KEY+String.valueOf(unixTime)+ System.getProperty("http.agent")))
                .addHeader("X-Authorization-Time",String.valueOf(unixTime))
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String jsondata = response.body().string();
            // System.out.println(response.body().string());
            try {
                JSONObject jobj = new JSONObject(jsondata);
                Integer success = jobj.getInt("api_status");
                if(success==1){
                    resp = jobj.toString();
                    id = jobj.getString("id");
                }else{
                    id=null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            Log.d("scan", e.toString());
            e.printStackTrace();
        }

        return id;
    }

    public void kenalan_rem(String id){
        String resp = null;
        String url = Constants.CLOUD_SERVER + "/api/kenalan_rem";

        OkHttpClient client = new OkHttpClient();

        RequestBody builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", id)
                .build();

        // code request code here


        RequestBody formBody = builder;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Authorization-Token",md5(Constants.SECRET_KEY+String.valueOf(unixTime)+ System.getProperty("http.agent")))
                .addHeader("X-Authorization-Time",String.valueOf(unixTime))
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String jsondata = response.body().string();
            Log.d("kenalan",jsondata);
            try {
                JSONObject jobj = new JSONObject(jsondata);
                Integer success = jobj.getInt("api_status");
                if(success==1){
                    resp = jobj.toString();
                }else{
                    resp=null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            Log.d("err", e.toString());
            e.printStackTrace();
        }
    }

    public String addPost(String user_id, String judul, String tags, String utama, String isi, String mode_id){
        String resp = null;
        String id = null;
        String url = Constants.CLOUD_SERVER + "/api/add_berita";

        OkHttpClient client = new OkHttpClient();

        RequestBody builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("created_by", user_id)
                .addFormDataPart("judul", judul)
                .addFormDataPart("tags", tags)
                .addFormDataPart("gambar_utama", utama)
                .addFormDataPart("isi", isi)
                .addFormDataPart("mode_id", mode_id)
                .build();

        // code request code here


        RequestBody formBody = builder;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Authorization-Token",md5(Constants.SECRET_KEY+String.valueOf(unixTime)+ System.getProperty("http.agent")))
                .addHeader("X-Authorization-Time",String.valueOf(unixTime))
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String jsondata = response.body().string();
        //    Log.d("postres",response.body().string());
            try {
                JSONObject jobj = new JSONObject(jsondata);
                Integer success = jobj.getInt("api_status");
                if(success==1){
                    resp = jobj.toString();
                    id = jobj.getString("id");
                }else{
                    id=null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {

            e.printStackTrace();
        }

        return id;
    }

    public Integer updateGambarUtama(String post_id, String utama){
        String resp = null;
        Integer success = 0;
        String url = Constants.CLOUD_SERVER + "/api/update_berita";

        OkHttpClient client = new OkHttpClient();

        RequestBody builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", post_id)
                .addFormDataPart("gambar_utama", utama)
                .build();

        // code request code here


        RequestBody formBody = builder;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Authorization-Token",md5(Constants.SECRET_KEY+String.valueOf(unixTime)+ System.getProperty("http.agent")))
                .addHeader("X-Authorization-Time",String.valueOf(unixTime))
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String jsondata = response.body().string();
            // System.out.println(response.body().string());
            try {
                JSONObject jobj = new JSONObject(jsondata);
                success = jobj.getInt("api_status");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            Log.d("scan", e.toString());
            e.printStackTrace();
        }
        return success;
    }

    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


}
