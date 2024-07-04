package banjarbarukota.go.id.idaman.Adapter;

import android.content.ContentValues;

public class ImageItems {
    private String text; // message body
    private ContentValues imageItemsData; // data of the user that sent this message

    public ImageItems(String text, ContentValues imageItemsData) {
        this.text = text;
        this.imageItemsData = imageItemsData;
    }

    public String getText() {
        return text;
    }

    public ContentValues getImageItemsData() {
        return imageItemsData;
    }

}
