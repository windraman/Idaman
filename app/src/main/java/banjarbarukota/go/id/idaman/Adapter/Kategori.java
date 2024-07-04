package banjarbarukota.go.id.idaman.Adapter;

import android.content.ContentValues;

import java.util.ArrayList;

public class Kategori {
    private String idKategori, textOrtu, icon;
    private ArrayList<ContentValues> dataAnak;

    public Kategori(String idKategori,String textOrtu, String icon,  ArrayList<ContentValues> dataAnak){
        this.idKategori = idKategori;
        this.textOrtu = textOrtu;
        this.dataAnak = dataAnak;
        this.icon = icon;
    }

    public String getIdKategori(){
        return idKategori;
    }

    public void setIdKategori(String idKategori) {
        this.idKategori = idKategori;
    }

    public String getIcon(){
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTextOrtu(){
        return textOrtu;
    }

    public void setTextOrtu(String textOrtu) {
        this.textOrtu = textOrtu;
    }

    public ArrayList<ContentValues> getDataAnak(){
        return dataAnak;
    }

    public void setDataAnak(ArrayList<ContentValues> dataAnak) {
        this.dataAnak = dataAnak;
    }
}
