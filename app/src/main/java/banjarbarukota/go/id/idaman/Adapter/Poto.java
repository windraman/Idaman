package banjarbarukota.go.id.idaman.Adapter;

import android.content.ContentValues;
import android.net.Uri;

import java.util.ArrayList;

public class Poto {
    private String potoName, potoPath, potoCaption, potoId, mime;
    private Uri potoUri;

    public Poto(String potoId, String potoName, String potoPath, Uri potoUri, String mime){
        this.potoId = potoId;
        this.potoName = potoName;
        this.potoPath = potoPath;
        this.potoUri = potoUri;
        this.mime = mime;
    }

    public String getPotoPath() {
        return potoPath;
    }

    public void setPotoPath(String potoPath) {
        this.potoPath = potoPath;
    }


    public String getPotoId() {
        return potoId;
    }

    public void setPotoId(String potoId) {
        this.potoId = potoId;
    }

    public String getPotoName() {
        return potoName;
    }

    public void setPotoName(String potoName) {
        this.potoName = potoName;
    }

    public Uri getPotoUri() {
        return potoUri;
    }

    public void setPotoUri(Uri potoUri) {
        this.potoUri = potoUri;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }
}
