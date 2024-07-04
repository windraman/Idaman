package banjarbarukota.go.id.idaman.Adapter;

import android.graphics.Color;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import banjarbarukota.go.id.idaman.R;

public class PotoAdapter extends RecyclerView.Adapter<PotoAdapter.PotoViewHolder>  {

    private List<Poto> potoList;

    public class PotoViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener  {

            public TextView realPotoPath, potoId;
            public EditText potoCaption;
            public ImageView imagePoto;
            public ImageButton imbHapusPoto, imbPlay;
            public VideoView videoView;
            public ConstraintLayout lVideoPreview;


        public PotoViewHolder(View view) {
            super(view);

            potoId = (TextView) view.findViewById(R.id.tvPotoId);
            potoCaption = (EditText) view.findViewById(R.id.edtCaptionPotoItem);
            realPotoPath = (TextView) view.findViewById(R.id.tvRealPotoPath);
            imagePoto = (ImageView) view.findViewById(R.id.imgItemPoto);
            imbHapusPoto = (ImageButton) view.findViewById(R.id.imbHapusItemPoto);

            imbHapusPoto.setOnClickListener(this);

            videoView = (VideoView) view.findViewById(R.id.uploadVideoView);

            lVideoPreview = (ConstraintLayout) view.findViewById(R.id.lVideoPreview);
            imbPlay = (ImageButton) view.findViewById(R.id.imbVideoPlay);
            imbPlay.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                Poto poto= potoList.get(position);
                if(view.equals(imbHapusPoto)){
                    removeAt(position);
                }
                if(view.equals(imbPlay)){
                    if (!videoView.isPlaying()) {
                        videoView.start();
                        imbPlay.setImageResource(R.drawable.ic_white_pause);
                        imbPlay.setBackgroundColor(Color.TRANSPARENT);
                    } else {
                        videoView.pause();
                        imbPlay.setImageResource(R.drawable.ic_white_play);
                        imbPlay.setBackgroundColor(Color.LTGRAY);
                    }
                }
                // We can access the data within the views
                //Toast.makeText(getApplicationContext(), poto.getTextOrtu(), Toast.LENGTH_SHORT).show();
            }
        }

        public void removeAt(int position) {
            potoList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, potoList.size());
        }
    }

    public PotoAdapter(List<Poto> potoList) {
        this.potoList= potoList;
    }


    @Override
    public PotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.poto_list_item, parent, false);

        return new PotoViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final PotoViewHolder holder, int position) {
        Poto poto= potoList.get(position);
        if(poto.getPotoId().equals("null")){
            holder.potoId.setText(String.valueOf(position));
        }else{
            holder.potoId.setText(poto.getPotoId());
        }
        //holder.potoCaption.setText(poto.getPotoCaption());
        holder.realPotoPath.setText(poto.getPotoPath());
        holder.realPotoPath.setTag(poto.getPotoName());

        holder.imbHapusPoto.setImageResource(R.drawable.ic_delete_forever_black_24dp);

        if(!poto.getPotoPath().equals("null")) {
            if(poto.getMime().contains("image")) {
                String filePath = poto.getPotoPath();
                File f = new File(filePath);
                Picasso.get()
                        .load(f)
                        .into(holder.imagePoto);
                holder.imagePoto.setVisibility(View.VISIBLE);
                holder.lVideoPreview.setVisibility(View.INVISIBLE);
            }else if(poto.getMime().contains("video")) {
                String filePath = poto.getPotoPath();

                holder.videoView.setVideoPath(poto.getPotoPath());

                holder.imagePoto.setVisibility(View.INVISIBLE);
                holder.lVideoPreview.setVisibility(View.VISIBLE);
            }
        }

        holder.videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return potoList.size();
    }


}
