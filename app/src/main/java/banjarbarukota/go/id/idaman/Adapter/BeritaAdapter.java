package banjarbarukota.go.id.idaman.Adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import banjarbarukota.go.id.GlobalClass;
import banjarbarukota.go.id.idaman.Main2Activity;
import banjarbarukota.go.id.idaman.ProfilUserActivity;
import banjarbarukota.go.id.idaman.R;
import banjarbarukota.go.id.idaman.Utility.CircleTransform;
import banjarbarukota.go.id.idaman.Utility.Jumper;
import banjarbarukota.go.id.idaman.Utility.OkHttpPostHandler;

import static banjarbarukota.go.id.idaman.Utility.Constants.CLOUD_SERVER;
import static banjarbarukota.go.id.idaman.Utility.Constants.STREAM_SERVER;

public class BeritaAdapter extends RecyclerView.Adapter<BeritaAdapter.BeritaViewHolder>  {

    private List<Berita> beritaList;

    public class BeritaViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener  {


        public TextView id, isi, nama_tampilan,status, liked, comet;
        String Judul,namaTable, mode_id;
        public ImageView poto_user;
        public ArrayList<ContentValues> media;
        ImageButton imbLike, imbChat, imbShare;
        ArrayList<String> images = new ArrayList<>();
        ViewPagerAdapter adapter;
        Context context;
        LinearLayout sliderDotspanel;
        ViewPager vpGambar;
        ImageView[] dots;
        int dotscount;


        public BeritaViewHolder(View view) {
            super(view);

            id = (TextView) view.findViewById(R.id.tvIdBerita);
            isi = (TextView) view.findViewById(R.id.tvIsiBerita);
            nama_tampilan = (TextView) view.findViewById(R.id.tvUsernameRibbon);
            status = (TextView) view.findViewById(R.id.tvStatusRibbon);
            poto_user = (ImageView) view.findViewById(R.id.imgUserRibbon);
            vpGambar = (ViewPager) view.findViewById(R.id.vpGambarBerita);
            liked = (TextView) view.findViewById(R.id.tvSuka);
            imbLike = (ImageButton) view.findViewById(R.id.imbLike);
            imbChat = (ImageButton) view.findViewById(R.id.imbKomentar);
            imbShare = (ImageButton) view.findViewById(R.id.imbShare);
            comet = (TextView) view.findViewById(R.id.tvJKomen);
            mode_id = "1";
            dots = null;
            dotscount = 0;
            sliderDotspanel = (LinearLayout) view.findViewById(R.id.SliderDots);
            adapter = null;

        }


        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
//                Berita berita = new List<Berita>();
//                berita = berita.get(position);
//                // We can access the data within the views
//                Toast.makeText(getApplicationContext(), berita.getTextOrtu(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public BeritaAdapter(List<Berita> berita) {
        this.beritaList = berita;
    }


    @Override
    public BeritaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.berita_item, parent, false);

        return new BeritaViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final BeritaViewHolder holder, int position) {
        Berita berita= beritaList.get(position);
        holder.id.setText(berita.getId());
        holder.isi.setText(berita.getIsi());
        holder.nama_tampilan.setText(berita.getNama_tampilan());
        holder.status.setText(berita.getStatus());
        holder.mode_id = berita.getMode_id();

        if(berita.getPoto_user().equals("null")) {
            holder.poto_user.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load( R.drawable.ic_baseline_people_24)
                    .error(R.drawable.ic_baseline_people_24)
                    .transform(new CircleTransform())
                    .into(holder.poto_user);
        }else {
            holder.poto_user.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load( berita.getPoto_user())
                    .error(R.drawable.ic_group_black_24dp)
                    .transform(new CircleTransform())
                    .into(holder.poto_user);

        }

        holder.poto_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Jumper jumper = new Jumper();
                jumper.openProfil(berita.getContext(),berita.getUser_id(),berita.getMyid());
            }
        });


        ArrayList<String> images = new ArrayList<>();
        holder.adapter = new ViewPagerAdapter(berita.getContext(), images);
        holder.vpGambar.setAdapter(holder.adapter);


        for (int j = 0; j < berita.getMedia().length(); j++) {
            try {
                JSONObject jMedia = berita.getMedia().getJSONObject(j);
                Log.d("media", jMedia.toString());
                String val;
                if(!berita.getMode_id().equals("7")) {
                    val = CLOUD_SERVER + "/" + jMedia.getString("file");
                }else{
                    val = jMedia.getString("file");
                }
                images.add(val);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        holder.adapter.notifyDataSetChanged();

        holder.dotscount = holder.adapter.getCount();
        holder.dots = new ImageView[holder.dotscount];

        if(holder.dotscount>0) {
            holder.sliderDotspanel.removeAllViews();
            for(int i = 0; i < holder.dotscount; i++){

                holder.dots[i] = new ImageView(berita.getContext());
                holder.dots[i].setImageDrawable(ContextCompat.getDrawable(berita.getContext(), R.drawable.noactive_dot));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                params.setMargins(8, 0, 8, 0);

                holder.sliderDotspanel.addView(holder.dots[i], params);

            }

            holder.sliderDotspanel.setVisibility(View.VISIBLE);
            holder.dots[0].setImageDrawable(ContextCompat.getDrawable(berita.getContext(), R.drawable.active_dot));
            holder.vpGambar.setVisibility(View.VISIBLE);
        }else{
            holder.vpGambar.setVisibility(View.GONE);
        }

        if(holder.dotscount==1){
            holder.sliderDotspanel.setVisibility(View.GONE);
        }

        holder.vpGambar.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for(int i = 0; i< holder.dotscount; i++){
                    holder.dots[i].setImageDrawable(ContextCompat.getDrawable(berita.getContext(), R.drawable.noactive_dot));
                }

                holder.dots[position].setImageDrawable(ContextCompat.getDrawable(berita.getContext(), R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Log.d("suka", berita.getIlike());

        if(berita.getIlike().equals("0")) {
            holder.imbLike.setImageResource(R.drawable.ic_thumb);
            holder.imbLike.setTag("netral");
        }else{
            holder.imbLike.setImageResource(R.drawable.ic_thumb_red);
            holder.imbLike.setTag(berita.getIlike());
        }

        holder.imbLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OkHttpPostHandler postHandler = new OkHttpPostHandler();
                if(holder.imbLike.getTag().equals("netral")){
                    holder.imbLike.setImageResource(R.drawable.ic_thumb_red);
                    String newid = postHandler.sukai(berita.getUser_id(),"informasi",berita.getId(),"1");
                    holder.imbLike.setTag(newid);
                }  else{
                    postHandler.sukai_rem(holder.imbLike.getTag().toString());
                    holder.imbLike.setImageResource(R.drawable.ic_thumb);
                    holder.imbLike.setTag("netral");
                }
            }
        });

        holder.imbChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Jumper jump = new Jumper();
                jump.openChat(berita.getContext(),berita.getId(),berita.getNamaTable());
            }
        });

        holder.liked.setText(String.valueOf(berita.getJPenyuka()));
        holder.comet.setText(String.valueOf(berita.getJKomen()));

    }


    @Override
    public int getItemCount() {
        return beritaList.size();
    }

}
