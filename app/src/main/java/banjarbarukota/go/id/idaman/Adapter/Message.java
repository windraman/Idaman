package banjarbarukota.go.id.idaman.Adapter;

import android.content.ContentValues;

import org.json.JSONObject;

public class Message {
    private String text; // message body
    private ContentValues messageData; // data of the user that sent this message
    private boolean belongsToCurrentUser; // is this message sent by us?

    public Message(String text, ContentValues messageData, boolean belongsToCurrentUser) {
        this.text = text;
        this.messageData = messageData;
        this.belongsToCurrentUser = belongsToCurrentUser;
    }

    public String getText() {
        return text;
    }

    public ContentValues getMessageData() {
        return messageData;
    }

    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }
}
