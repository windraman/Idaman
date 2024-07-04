package banjarbarukota.go.id.idaman.Utility;


import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import androidx.constraintlayout.widget.ConstraintLayout;

import org.json.JSONObject;

import java.net.InetAddress;
import java.util.concurrent.ExecutionException;

import static banjarbarukota.go.id.idaman.Utility.Constants.CLOUD_SERVER;
import static banjarbarukota.go.id.idaman.Utility.Constants.MAIN_SERVER;

/**
 * Created by Wahyu on 1/25/2018.
 */

public class OkHttpGetHandler {

    public JSONObject getAllKategori(int aktif){
        String url = Constants.CLOUD_SERVER + "/api/all_kategori?aktif=" + aktif;
        JSONObject jobj= null;

        AsyncGetHandler okHandler = new AsyncGetHandler();
        try {
            String token = Utility.md5(Constants.SECRET_KEY+Utility.unixTime()+System.getProperty("http.agent"));
            String time = Utility.unixTime();
            jobj = okHandler.execute(url,token,time).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return jobj;

    }

    public JSONObject getKota(String province_id){
        String url = Constants.CLOUD_SERVER + "/api/list_kota?province_id=" + province_id;
        JSONObject jobj= null;

        AsyncGetHandler okHandler = new AsyncGetHandler();
        try {

            String token = Utility.md5(Constants.SECRET_KEY+Utility.unixTime()+System.getProperty("http.agent"));
            String time = Utility.unixTime();
            jobj = okHandler.execute(url,token,time).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return jobj;

    }

    public JSONObject getKecamatan(String regency_id){
        String url = Constants.CLOUD_SERVER + "/api/list_kecamatan?regency_id=" + regency_id;
        JSONObject jobj= null;

        AsyncGetHandler okHandler = new AsyncGetHandler();
        try {

            String token = Utility.md5(Constants.SECRET_KEY+Utility.unixTime()+System.getProperty("http.agent"));
            String time = Utility.unixTime();
            jobj = okHandler.execute(url,token,time).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return jobj;

    }

    public JSONObject getKelurahan(String district_id){
        String url = Constants.CLOUD_SERVER + "/api/list_kelurahan?district_id=" + district_id;
        JSONObject jobj= null;

        AsyncGetHandler okHandler = new AsyncGetHandler();
        try {

            String token = Utility.md5(Constants.SECRET_KEY+Utility.unixTime()+System.getProperty("http.agent"));
            String time = Utility.unixTime();
            jobj = okHandler.execute(url,token,time).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return jobj;

    }

    public JSONObject getListChat(String user_id){
        String url = Constants.CLOUD_SERVER + "/api/list_chat?user_id=" + user_id ;
        JSONObject jobj= null;

        AsyncGetHandler okHandler = new AsyncGetHandler();
        try {

            String token = Utility.md5(Constants.SECRET_KEY+Utility.unixTime()+System.getProperty("http.agent"));
            String time = Utility.unixTime();
            jobj = okHandler.execute(url,token,time).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return jobj;
    }

    public JSONObject getBerita(String user_id,String nik){
        String url = Constants.CLOUD_SERVER + "/api/ambil_berita?user_id=" + user_id + "&nik="+ nik;
        JSONObject jobj= null;

        AsyncGetHandler okHandler = new AsyncGetHandler();
        try {

            String token = Utility.md5(Constants.SECRET_KEY+Utility.unixTime()+System.getProperty("http.agent"));
            String time = Utility.unixTime();
            jobj = okHandler.execute(url,token,time).get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return jobj;

    }

    public JSONObject getSlider() {
        String url = MAIN_SERVER + "/api/slideshow";
        JSONObject jobj = null;

        AsyncGetHandler okHandler = new AsyncGetHandler();
        //Perform the doInBackground method, passing in our url
        try {
            String token = Utility.md5(Constants.SECRET_KEY+Utility.unixTime()+System.getProperty("http.agent"));
            String time = Utility.unixTime();
            jobj = okHandler.execute(url,token,time).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return jobj;
    }

    public JSONObject getTempatDetail(String user_id,String id) {
        String url = CLOUD_SERVER + "/api/cari_lokasi?id=" + id + "&user_id=" + user_id;
        JSONObject jobj = null;

        AsyncGetHandler okHandler = new AsyncGetHandler();
        //Perform the doInBackground method, passing in our url
        try {
            String token = Utility.md5(Constants.SECRET_KEY+Utility.unixTime()+System.getProperty("http.agent"));
            String time = Utility.unixTime();
            jobj = okHandler.execute(url,token,time).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return jobj;
    }

    public JSONObject getNotif(String username,String cms_privileges_name, String status) {
        String url = CLOUD_SERVER + "/api/ambil_notif?nik="+username + "&priv=" + cms_privileges_name+ "&status=" + status;
        JSONObject jobj = null;

        AsyncGetHandler okHandler = new AsyncGetHandler();
        //Perform the doInBackground method, passing in our url
        try {
            String token = Utility.md5(Constants.SECRET_KEY+Utility.unixTime()+System.getProperty("http.agent"));
            String time = Utility.unixTime();
            jobj = okHandler.execute(url,token,time).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return jobj;
    }

    public JSONObject getListMenu(String utama) {
        String url = CLOUD_SERVER + "/api/list_menu?utama=" + utama;
        JSONObject jobj = null;

        AsyncGetHandler okHandler = new AsyncGetHandler();
        //Perform the doInBackground method, passing in our url
        try {
            //Log.d("Muka" ," SK : " + Constants.SECRET_KEY + ", time : " +  Utility.unixTime() + ", agent : " + Constants.USER_AGENT);
            String token = Utility.md5(Constants.SECRET_KEY+Utility.unixTime()+System.getProperty("http.agent"));
            String time = Utility.unixTime();
            jobj = okHandler.execute(url,token,time).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return jobj;
    }

    public JSONObject getListKategori() {
        String url = CLOUD_SERVER + "/api/list_kategori?aktif=1";
        JSONObject jobj = null;

        AsyncGetHandler okHandler = new AsyncGetHandler();
        //Perform the doInBackground method, passing in our url
        try {
            //Log.d("Muka" ," SK : " + Constants.SECRET_KEY + ", time : " +  Utility.unixTime() + ", agent : " + Constants.USER_AGENT);
            String token = Utility.md5(Constants.SECRET_KEY+Utility.unixTime()+System.getProperty("http.agent"));
            String time = Utility.unixTime();
            jobj = okHandler.execute(url,token,time).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return jobj;
    }

    public JSONObject getTentang(String key_id,String nama_table) {
        String url = CLOUD_SERVER + "/api/komentar?key_id="+key_id+"&nama_table="+nama_table;
        JSONObject jobj = null;

        AsyncGetHandler okHandler = new AsyncGetHandler();
        //Perform the doInBackground method, passing in our url
        try {
            String token = Utility.md5(Constants.SECRET_KEY+Utility.unixTime()+System.getProperty("http.agent"));
            String time = Utility.unixTime();
            jobj = okHandler.execute(url,token,time).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return jobj;
    }

    public JSONObject getMessage(String key_id, String nama_table, String user_id){
        String url = CLOUD_SERVER + "/api/komentar?key_id="+key_id+"&nama_table="+nama_table+"&req_by="+user_id;
        JSONObject jobj = null;

        AsyncGetHandler okHandler = new AsyncGetHandler();
        //Perform the doInBackground method, passing in our url
        try {
            String token = Utility.md5(Constants.SECRET_KEY+Utility.unixTime()+System.getProperty("http.agent"));
            String time = Utility.unixTime();
            jobj = okHandler.execute(url,token,time).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return jobj;
    }

    public JSONObject getGrupDetail(String id, String username){
        String url = CLOUD_SERVER + "/api/grup_detail?id=" + id + "&username=" + username;
        JSONObject jobj = null;

        AsyncGetHandler okHandler = new AsyncGetHandler();
        //Perform the doInBackground method, passing in our url
        try {
            String token = Utility.md5(Constants.SECRET_KEY+Utility.unixTime()+System.getProperty("http.agent"));
            String time = Utility.unixTime();
            jobj = okHandler.execute(url,token,time).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return jobj;
    }

    public JSONObject getUserProfil(String user_id,String myid){
        String url = CLOUD_SERVER + "/api/ambil_profil?id=" + user_id+ "&myid=" + myid ;
        JSONObject jobj = null;

        AsyncGetHandler okHandler = new AsyncGetHandler();
        //Perform the doInBackground method, passing in our url
        try {
            String token = Utility.md5(Constants.SECRET_KEY+Utility.unixTime()+System.getProperty("http.agent"));
            String time = Utility.unixTime();
            jobj = okHandler.execute(url,token,time).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return jobj;
    }

}

