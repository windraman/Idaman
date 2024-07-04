package banjarbarukota.go.id.idaman.Adapter;

import android.content.ContentValues;

import java.util.ArrayList;

public class MenuDepan {
    private String idMenu, textOrtu, icon, showText, aksi;
    private ArrayList<ContentValues> dataAnak;

    public MenuDepan(String idMenu, String textOrtu, String icon, ArrayList<ContentValues> dataAnak, String showText, String aksi){
        this.idMenu = idMenu;
        this.textOrtu = textOrtu;
        this.dataAnak = dataAnak;
        this.icon = icon;
        this.showText = showText;
        this.aksi = aksi;
    }

    public String getIdMenu(){
        return idMenu;
    }

    public void setIdMenu(String idKategori) {
        this.idMenu = idMenu;
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

    public String getShowText() {
        return showText;
    }

    public void setShowText(String showText) {
        this.showText = showText;
    }

    public String getAksi() {
        return aksi;
    }

    public void setAksi(String aksi) {
        this.aksi = aksi;
    }
}
