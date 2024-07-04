package banjarbarukota.go.id.idaman.Adapter;

public class ListChat {
    private String id, title, name, picture, descrption, namaTable;

    public ListChat(String id, String title,String picture, String descrption, String namaTable){
        this.id = id;
        this.title = title;
        this.picture = picture;
        this.descrption = descrption;
        this.namaTable = namaTable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescrption() {
        return descrption;
    }

    public void setDescrption(String descrption) {
        this.descrption = descrption;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getNamaTable() {
        return namaTable;
    }

    public void setNamaTable(String namaTable) {
        this.namaTable = namaTable;
    }
}
