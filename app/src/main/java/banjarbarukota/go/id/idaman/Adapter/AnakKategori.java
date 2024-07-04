package banjarbarukota.go.id.idaman.Adapter;

import android.content.ContentValues;

import java.util.ArrayList;

public class AnakKategori {
    private String idKategoriAnak;
    private String textAnak;
    private String gambar;
    private String showText;

    public AnakKategori(String idKategoriAnak, String textAnak, String gambar,String showText){
        this.idKategoriAnak= idKategoriAnak;
        this.textAnak = textAnak;
        this.gambar = gambar;
        this.showText = showText;
    }

    public String getIdKategoriAnak(){
        return idKategoriAnak;
    }

    public void setIdKategoriAnak(String idKategoriAnak) {
        this.idKategoriAnak = idKategoriAnak;
    }

    public String getTextAnak(){
        return textAnak;
    }

    public void setTextAnak(String textAnak) {
        this.textAnak= textAnak;
    }

    public String getGambar(){
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String isShowText() {
        return showText;
    }

    public void setShowText(String showText) {
        this.showText = showText;
    }
}
