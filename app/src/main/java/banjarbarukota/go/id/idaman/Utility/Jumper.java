package banjarbarukota.go.id.idaman.Utility;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.dsphotoeditor.sdk.ui.stickerview.StickerView;

import banjarbarukota.go.id.GlobalClass;
import banjarbarukota.go.id.idaman.BeritaActivity;
import banjarbarukota.go.id.idaman.ChatActivity;
import banjarbarukota.go.id.idaman.GrupDetailActivity;
//import banjarbarukota.go.id.idaman.Jitsi.BatamuanActivity;
//import banjarbarukota.go.id.idaman.Jitsi.ViconActivity;
import banjarbarukota.go.id.idaman.LokasiWizardActivity;
import banjarbarukota.go.id.idaman.Main2Activity;
import banjarbarukota.go.id.idaman.ProfilUserActivity;
import banjarbarukota.go.id.idaman.RtmpActivity;
import banjarbarukota.go.id.idaman.TempatDetailActivity;
import banjarbarukota.go.id.idaman.WebViewActivity;

import static banjarbarukota.go.id.idaman.Utility.Constants.CLOUD_SERVER;

public class Jumper {;
    public void openUrl(Context context, String openurl) {
        if(openurl.equals("http://siapkk.banjarbarukota.go.id/admin/lokasi/add,nyala") || openurl.equals("http://siapkk.banjarbarukota.go.id/admin/")){
            if(((GlobalClass) context).getStringPref("login_pakai").equals("nik")){
                Intent localIntent = new Intent(context, WebViewActivity.class);
                localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                String params[] = openurl.split(",");
                if(params.length>1) {
                    localIntent.putExtra("openurl", params[0]);
                    localIntent.putExtra("startTracker", params[1]);
                }else{
                    localIntent.putExtra("openurl", openurl);
                }
                context.startActivity(localIntent);
            }else{
                Toast.makeText(context, "Silahkan Logout dan Login kembali menggunakan NIK anda.", Toast.LENGTH_LONG).show();
            }

        }else{
            Intent localIntent = new Intent(context, WebViewActivity.class);
            localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            String params[] = openurl.split(",");
            if(params.length>1) {
                localIntent.putExtra("openurl", params[0]);
                localIntent.putExtra("startTracker", params[1]);
            }else{
                localIntent.putExtra("openurl", openurl);
            }
            context.startActivity(localIntent);
        }

    }

    public void openBrowser(Context context, String url){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
    }

    public void openNewLokasi(Context context){
        if(((GlobalClass) context).getStringPref("login_pakai").equals("nik")){
            Intent launchIntent = new Intent(context, LokasiWizardActivity.class);
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launchIntent);
        }else{
            Toast.makeText(context, "Silahkan Logout dan Login kembali menggunakan NIK anda.", Toast.LENGTH_LONG).show();
        }

    }

    public void openOtherApp(Context context,String paket)
    {
        Log.d("other",paket);
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(paket);
        if (launchIntent != null) {
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launchIntent);//null pointer check in case package name was not found
        }else{
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+paket));
            browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(browserIntent);
        }
    }

    public void openGrup(Context context,String grup_id)
    {
        if(((GlobalClass) context).getStringPref("login_pakai").equals("nik")){
            Intent localIntent = new Intent(context, GrupDetailActivity.class);
            localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            localIntent.putExtra("grup_id", grup_id);

            context.startActivity(localIntent);
        }else{
            Toast.makeText(context, "Silahkan Logout dan Login kembali menggunakan NIK anda.", Toast.LENGTH_LONG).show();
        }

    }

    public void openChat(Context context, String key_id,String nama_table)
    {
        Intent chatIntent = new Intent(context, ChatActivity.class);
        chatIntent.putExtra("key_id", key_id);
        chatIntent.putExtra("nama_table", nama_table);
        chatIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(chatIntent);
    }

    public void openLokasi(Context context,String id)
    {
        Intent localIntent = new Intent(context, TempatDetailActivity.class);
        localIntent.putExtra("id", id);
        //localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(localIntent);
    }

    public void openLiveStreaming(Context context,String pwd)
    {
        Intent liveStreaming = new Intent(context, RtmpActivity.class);
        liveStreaming.putExtra("pwd", pwd);
        liveStreaming.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(liveStreaming);
    }

//    public void openVicon(Context context,String room)
//    {
//        Intent localIntent = new Intent(context, ViconActivity.class);
//        localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(localIntent);
//    }

//    public void openRoom(Context context, String room)
//    {
//        // add call notif here
//        Intent localIntent = new Intent(context, BatamuanActivity.class);
//        //localIntent.putExtra("room", room);
//        localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(localIntent);
//    }

    public void openBakawan(Context context)
    {
        Intent localIntent = new Intent(context, BeritaActivity.class);
        localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(localIntent);
    }

    public void openProfil(Context context, String user_id, String myid)
    {
        Intent localIntent = new Intent(context, ProfilUserActivity.class);
        localIntent.putExtra("user_id", user_id);
        localIntent.putExtra("myid", myid);
        localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(localIntent);
    }

}
