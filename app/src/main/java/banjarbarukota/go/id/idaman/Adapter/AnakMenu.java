package banjarbarukota.go.id.idaman.Adapter;

public class AnakMenu {
    private String idMenuAnak;
    private String textAnak;
    private String gambar;
    private boolean showText;

    public AnakMenu(String idKategoriAnak, String textAnak, String gambar, boolean showText){
        this.idMenuAnak= idKategoriAnak;
        this.textAnak = textAnak;
        this.gambar = gambar;
        this.showText = showText;
    }

    public String getIdMenuAnak(){
        return idMenuAnak;
    }

    public void setIdMenuAnak(String idMenuAnak) {
        this.idMenuAnak= idMenuAnak;
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

    public boolean isShowText() {
        return showText;
    }

    public void setShowText(boolean showText) {
        this.showText = showText;
    }
}
