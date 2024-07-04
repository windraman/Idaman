package banjarbarukota.go.id.idaman.Adapter;

import android.content.ContentValues;

public class Notifikasi {
    private String text; // message body
    private ContentValues notifData; // data of the user that sent this message

    public Notifikasi(String text, ContentValues notifData) {
        this.text = text;
        this.notifData = notifData;
    }

    public String getText() {
        return text;
    }

    public ContentValues getNotifData() {
        return notifData;
    }

}
