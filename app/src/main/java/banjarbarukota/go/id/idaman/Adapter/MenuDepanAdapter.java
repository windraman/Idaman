package banjarbarukota.go.id.idaman.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import banjarbarukota.go.id.idaman.MenuKategoriActivity;
import banjarbarukota.go.id.idaman.R;
import banjarbarukota.go.id.idaman.Utility.ItemClickSupport;

import static banjarbarukota.go.id.idaman.Utility.Constants.CLOUD_SERVER;
import static banjarbarukota.go.id.idaman.Utility.Constants.STORAGE_SERVER;

public class MenuDepanAdapter extends RecyclerView.Adapter<MenuDepanAdapter.MenuDepanViewHolder>  {

    private final Context context;
    private List<MenuDepan> menuDepanList;

    public class MenuDepanViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener  {

            public TextView idKategori, ortuKategori;
            public ImageView imageOrtu, imgBatch;

        private List<AnakKategori> anakKategoriList = new ArrayList<>();
        private RecyclerView anakMenuRecyclerVIew;
        private AnakKategoriAdapter akAdapter;


        public MenuDepanViewHolder(View view) {
            super(view);

            idKategori = (TextView) view.findViewById(R.id.tvIdMenu);
            ortuKategori = (TextView) view.findViewById(R.id.tvNamaMenu);
            imageOrtu = (ImageView) view.findViewById(R.id.imgIconMenu);
            imgBatch = (ImageView) view.findViewById(R.id.imgBatch);
            //anakMenuRecyclerVIew = (RecyclerView) view.findViewById(R.id.anak_recycler_view);

//            akAdapter = new AnakKategoriAdapter(anakKategoriList);
//            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
//            anakMenuRecyclerVIew.setLayoutManager(mLayoutManager);
//
//            anakMenuRecyclerVIew.setAdapter(akAdapter);

        }


        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                MenuDepan menuDepan= menuDepanList.get(position);
                // We can access the data within the views
                Toast.makeText(context.getApplicationContext(), menuDepan.getTextOrtu(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public MenuDepanAdapter(List<MenuDepan> menuDepanList, Context context) {
        this.menuDepanList = menuDepanList;
        this.context=context;
    }


    @Override
    public MenuDepanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_depan_list_row, parent, false);

        return new MenuDepanViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MenuDepanViewHolder holder, int position) {
        MenuDepan menuDepan= menuDepanList.get(position);

        holder.idKategori.setText(menuDepan.getIdMenu());
        holder.idKategori.setVisibility(View.GONE);
        holder.ortuKategori.setText(menuDepan.getTextOrtu());

       // Log.d("mmenudep",menuDepan.getTextOrtu() + "-" + menuDepan.getShowText());
        if(menuDepan.getShowText().equals("1")) {
            holder.ortuKategori.setVisibility(View.VISIBLE);
        }else{
            holder.ortuKategori.setVisibility(View.GONE);
        }

        if(menuDepan.getIcon().equals("null")) {
            holder.imageOrtu.setVisibility(View.GONE);
            holder.ortuKategori.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            //holder.ortuKategori.setAllCaps(true);
            //Picasso.get().load(R.mipmap.bg).into(holder.imageOrtu);
        }else {
            holder.imageOrtu.setVisibility(View.VISIBLE);
            //holder.ortuKategori.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            Picasso.get()
                    .load( STORAGE_SERVER +  menuDepan.getIcon())
                    .into(holder.imageOrtu);

        }

//        for (int posisi = 0;posisi < anak.size(); posisi++) {
//            ContentValues baris = anak.get(posisi);
//            final AnakKategori anakKategori = new AnakKategori(baris.get("id").toString(), baris.get("nama").toString(), baris.get("icon").toString(),baris.get("show_text").toString());
//            holder.anakKategoriList.add(anakKategori);
//
//            holder.akAdapter.notifyDataSetChanged();
//        }

        if(menuDepan.getDataAnak().size()!=0){
            holder.imgBatch.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load( STORAGE_SERVER + menuDepan.getDataAnak().get(0).getAsString("icon"))
                    .into(holder.imgBatch);
        }else{
            holder.imageOrtu.setVisibility(View.GONE);
        }

//        holder.anakKategoriList.clear();
//
//        final ArrayList<ContentValues> anak = menuDepan.getDataAnak();
//
//
//
//        for (int posisi = 0;posisi < anak.size(); posisi++) {
//            ContentValues baris = anak.get(posisi);
//            final AnakKategori anakKategori = new AnakKategori(baris.get("id").toString(), baris.get("nama").toString(), baris.get("icon").toString(),baris.get("show_text").toString());
//            holder.anakKategoriList.add(anakKategori);
//
//            holder.akAdapter.notifyDataSetChanged();
//        }
//        //onAnakKategori(kategori.getDataAnak());
//        ItemClickSupport.addTo(holder.anakMenuRecyclerVIew).setOnItemClickListener(
//                new ItemClickSupport.OnItemClickListener() {
//                    @Override
//                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
//                        Log.d("klikanak",holder.anakKategoriList.get(position).getTextAnak());
//                        MenuKategoriActivity amk = MenuKategoriActivity.getInstance();
//                        amk.cariKategori(holder.anakKategoriList.get(position).getTextAnak());
//                    }
//
//                }
//        );
//
//        Log.d("kate",menuDepan.getTextOrtu() + "->" + menuDepan.getDataAnak().size());

    }

    @Override
    public int getItemCount() {
        return menuDepanList.size();
    }

}
