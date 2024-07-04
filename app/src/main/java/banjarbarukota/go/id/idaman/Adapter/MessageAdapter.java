package banjarbarukota.go.id.idaman.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiTextView;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import banjarbarukota.go.id.GlobalClass;
import banjarbarukota.go.id.idaman.ChatActivity;
import banjarbarukota.go.id.idaman.MediaPlayerActivity;
import banjarbarukota.go.id.idaman.PhotoViewActivity;
import banjarbarukota.go.id.idaman.R;
import banjarbarukota.go.id.idaman.Utility.CircleTransform;
import banjarbarukota.go.id.idaman.Utility.Constants;
import banjarbarukota.go.id.idaman.Utility.ItemClickSupport;
import banjarbarukota.go.id.idaman.Utility.OkHttpPostHandler;

public class MessageAdapter extends BaseAdapter {

    List<Message> messages = new ArrayList<Message>();
    Context context;
    int [] imageId;

    OkHttpPostHandler postHandler;

    public MessageAdapter(Context context) {
        this.context = context;
    }

    public void add(Message message) {
        this.messages.add(message);
        notifyDataSetChanged(); // to render the list we need to notify
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    // This is the backbone of the class, it handles the creation of single ListView row (chat bubble)
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        final Message message = messages.get(i);
        String ilike = "0";

        if (message.isBelongsToCurrentUser()) { // this message was sent by us so let's create a basic chat bubble on the right
            convertView = messageInflater.inflate(R.layout.my_message, null);
            holder.messageBody = (EmojiTextView) convertView.findViewById(R.id.message_body);
            holder.messageDate=(TextView) convertView.findViewById(R.id.tvMessageDate);
            holder.imgChat=(ImageView) convertView.findViewById(R.id.imgChat);
            holder.chatVideoView = (VideoView) convertView.findViewById(R.id.chatVideoView);
            holder.chatVideoLayout = (ConstraintLayout) convertView.findViewById(R.id.chatVideoLayout);
            holder.imbPlay = (ImageButton) convertView.findViewById(R.id.imbPlay);

            convertView.setTag(holder);
            holder.messageBody.setText(message.getText());

            String strDate = message.getMessageData().get("created_at").toString();
            holder.messageDate.setText(strDate);


            if(!message.getMessageData().get("gambar").equals("null")){
                String mime = message.getMessageData().get("jenis_gambar").toString();
                if(mime.contains("image")) {
                    holder.imgChat.setVisibility(View.VISIBLE);
                    holder.chatVideoLayout.setVisibility(View.GONE);
                    Picasso.get()
                            .load(message.getMessageData().get("gambar").toString())
                            .into(holder.imgChat);
                }else if(mime.contains("video")) {
                    holder.imgChat.setVisibility(View.GONE);
                    holder.chatVideoLayout.setVisibility(View.VISIBLE);

                    Uri video = Uri.parse(Constants.CLOUD_SERVER + "/" + message.getMessageData().get("gambar").toString());

                    holder.chatVideoView.setVideoURI(video);

                }
            }else{
                holder.imgChat.setVisibility(View.GONE);
                holder.chatVideoLayout.setVisibility(View.GONE);
            }
        } else { // this message was sent by someone else so let's create an advanced chat bubble on the left
            convertView = messageInflater.inflate(R.layout.their_message, null);
            //holder.avatar = (View) convertView.findViewById(R.id.avatar);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.messageBody = (EmojiTextView) convertView.findViewById(R.id.message_body);
            holder.img=(ImageView) convertView.findViewById(R.id.profile_image);
            holder.imgChat=(ImageView) convertView.findViewById(R.id.imgChat);
            holder.messageDate=(TextView) convertView.findViewById(R.id.tvMessageDate);
            holder.imbLike = (ImageButton) convertView.findViewById(R.id.imbMessageLike);
            holder.chatVideoView = (VideoView) convertView.findViewById(R.id.chatVideoView);
            holder.chatVideoLayout = (ConstraintLayout) convertView.findViewById(R.id.chatVideoLayout);
            holder.imbPlay = (ImageButton) convertView.findViewById(R.id.imbPlay);

            ilike = message.getMessageData().get("ilike").toString();

            if(!ilike.equals("0")){
                holder.imbLike.setImageResource(R.drawable.ic_thumb_red);
                holder.imbLike.setTag(ilike);
                // Log.d("liked", jobj0.get("ilike").toString());
            }else{
                holder.imbLike.setTag("netral");
            }

            convertView.setTag(holder);

            if(message.getMessageData().get("nama_tampilan").toString().equals("NULL")) {
                holder.name.setText(message.getMessageData().get("nama").toString());
            }else {
                holder.name.setText(message.getMessageData().get("nama_tampilan").toString());
            }
            holder.messageBody.setText(message.getText());

            Picasso.get()
                    .load(message.getMessageData().get("photo").toString())
                    .transform(new CircleTransform())
                    .into(holder.img);

           if(!message.getMessageData().get("gambar").equals("null")){
               String mime = message.getMessageData().get("jenis_gambar").toString();
               if(mime.contains("image")) {
                   holder.imgChat.setVisibility(View.VISIBLE);
                   holder.chatVideoLayout.setVisibility(View.GONE);
                   Picasso.get()
                           .load(message.getMessageData().get("gambar").toString())
                           .into(holder.imgChat);
               }else if(mime.contains("video")) {
                   holder.imgChat.setVisibility(View.GONE);
                   holder.chatVideoLayout.setVisibility(View.VISIBLE);

                   Uri video = Uri.parse(Constants.CLOUD_SERVER + "/" + message.getMessageData().get("gambar").toString());

                   holder.chatVideoView.setVideoURI(video);
               }

            }else{
               holder.imgChat.setVisibility(View.GONE);
               holder.chatVideoLayout.setVisibility(View.GONE);
           }

            String strDate = message.getMessageData().get("created_at").toString();
            holder.messageDate.setText(strDate);

            holder.imbLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    postHandler = new OkHttpPostHandler();
                    //Log.d("menyukai",message.getMessageData().get("id").toString());
                    if(holder.imbLike.getTag().equals("netral")){
                        holder.imbLike.setImageResource(R.drawable.ic_thumb_red);
                        String newid = postHandler.sukai(((GlobalClass) ((Activity) context).getApplication()).getStringPref("user_id"),"komentar",message.getMessageData().get("id").toString(),"1");
                        holder.imbLike.setTag(newid);
                    }  else{
                        postHandler.sukai_rem(holder.imbLike.getTag().toString());
                        holder.imbLike.setImageResource(R.drawable.ic_thumb);
                        holder.imbLike.setTag("netral");
                    }

                }
            });

            holder.imbPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!holder.chatVideoView.isPlaying()) {
                        holder.chatVideoView.start();
                        holder.imbPlay.setImageResource(R.drawable.ic_white_pause);
                        holder.imbPlay.setBackgroundColor(Color.TRANSPARENT);
                    } else {
                        holder.chatVideoView.pause();
                        holder.imbPlay.setImageResource(R.drawable.ic_white_play);
                        holder.imbPlay.setBackgroundColor(Color.LTGRAY);
                    }
                }
            });


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("pesan mereka",message.getMessageData().get("id").toString());
                    String photo_url = message.getMessageData().get("gambar").toString();
                    String mime = message.getMessageData().get("jenis_gambar").toString();
                    if(!photo_url.equals("null")) {
                        if(mime.contains("image")) {
                            Intent intent = new Intent(context, PhotoViewActivity.class);
                            intent.putExtra("photo_url", photo_url);
                            context.startActivity(intent);
                        }else if(mime.contains("video")) {

//                            Intent intent = new Intent(context, MediaPlayerActivity.class);
//                            intent.putExtra("url", photo_url);
//                            intent.putExtra("media_name", photo_url);
//                            context.startActivity(intent);
                        }
                    }
                }
            });

        }
        return convertView;
    }

    public String getFileMimeType(String url){
        File file = new File(url);
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String mimeType = fileNameMap.getContentTypeFor(file.getName());

        return mimeType;
    }

}

class MessageViewHolder {
    public View avatar;
    public TextView name;
    public EmojiTextView messageBody;
    public TextView messageDate;
    ImageView img;
    ImageView imgChat;
    ImageButton imbLike, imbPlay;
    RecyclerView rvLiked;
    VideoView chatVideoView;
    ConstraintLayout chatVideoLayout;
}



