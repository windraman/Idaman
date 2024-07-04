package banjarbarukota.go.id.idaman.Adapter;

public class SliderImage {
    private String idImage;
    private String url;
    private String caption;

    public SliderImage(String idImage, String url, String caption){
        this.idImage= idImage;
        this.url = url;
        this.caption = caption;
    }

    public String getIdImage() {
        return idImage;
    }

    public void setIdImage(String idImage) {
        this.idImage = idImage;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
