package banjarbarukota.go.id.idaman.Adapter;

import android.content.ContentValues;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class Berita {
    private String id, judul, isi, nama_tampilan, poto_user, status, namaTable, user_id, ilike, myid, mode_id;
    Integer jpenyuka, jkomen;
    JSONArray media, lastpenyuka;
    Context context;

    public Berita(Context context,String user_id, String id, String judul, String isi, String nama_tampilan, String poto_user, String status, String namaTable, String ilike, JSONArray media, Integer jpenyuka, JSONArray lastpenyuka, String myid,String mode_id, Integer jkomen){
        this.context = context;
        this.id = id;
        this.user_id = user_id;
        this.judul = judul;
        this.isi = isi;
        this.nama_tampilan = nama_tampilan;
        this.poto_user = poto_user;
        this.status = status;
        this.namaTable = namaTable;
        this.ilike = ilike;
        this.media = media;
        this.jpenyuka = jpenyuka;
        this.lastpenyuka = lastpenyuka;
        this.myid = myid;
        this.mode_id = mode_id;
        this.jkomen = jkomen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getIsi() {
        return isi;
    }

    public void setIsi(String isi) {
        this.isi = isi;
    }

    public String getNama_tampilan() {
        return nama_tampilan;
    }

    public String getPoto_user() {
        return poto_user;
    }

    public void setPoto_user(String poto_user) {
        this.poto_user = poto_user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNamaTable() {
        return namaTable;
    }

    public void setNamaTable(String namaTable) {
        this.namaTable = namaTable;
    }

    public String  getIlike() {
        return ilike;
    }

    public void setIlike(String ilike) {
        this.ilike = ilike;
    }

    public JSONArray getMedia() {
        return media;
    }

    public void setMedia(JSONArray media) {
        this.media = media;
    }

    public JSONArray getLastPenyuka(){
        return lastpenyuka;
    }

    public void setLastPenyuka(JSONArray jPenyuka){
        this.lastpenyuka = jPenyuka;
    }

    public Integer getJPenyuka() {
        return jpenyuka;
    }

    public void setJPenyuka(Integer jpenyuka){
        this.jpenyuka = jpenyuka;
    }

    public Integer getJKomen() {
        return jkomen;
    }

    public void setJKomen(Integer jkomen){
        this.jkomen = jkomen;
    }

    public Context getContext(){
        return context;
    }

    public void setContext(Context context){
        this.context = context;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMyid() {
        return myid;
    }

    public void setMyid(String myid) {
        this.myid = myid;
    }

    public String getMode_id() {
        return mode_id;
    }

    public void setMode_id(String mode_id) {
        this.mode_id = mode_id;
    }

}
