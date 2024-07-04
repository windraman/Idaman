package banjarbarukota.go.id.idaman.Adapter;

public class Anggota {
    private String idAnggota;
    private String nik;
    private String nama;
    private String gambar;

    public Anggota(String idAnggota, String nik,String nama, String gambar){
        this.idAnggota= idAnggota;
        this.nik = nik;
        this.gambar = gambar;
        this.nama = nama;
    }

    public String getIdAnggota() {
        return idAnggota;
    }

    public void setIdAnggota(String idAnggota) {
        this.idAnggota = idAnggota;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }
}
