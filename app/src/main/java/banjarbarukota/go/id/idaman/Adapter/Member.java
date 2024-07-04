package banjarbarukota.go.id.idaman.Adapter;

import android.content.ContentValues;

public class Member {
    private String text; // message body
    private ContentValues memberData; // data of the user that sent this message
    private boolean belongsToCurrentUser; // is this message sent by us?

    public Member(String text, ContentValues memberData, boolean belongsToCurrentUser) {
        this.text = text;
        this.memberData = memberData;
        this.belongsToCurrentUser = belongsToCurrentUser;
    }

    public String getText() {
        return text;
    }

    public ContentValues getMemberData() {
        return memberData;
    }

    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }

}
