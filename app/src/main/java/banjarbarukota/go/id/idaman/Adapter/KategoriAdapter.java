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



public class KategoriAdapter extends RecyclerView.Adapter<KategoriAdapter.KategoriViewHolder>  {

    private final Context context;
    private List<Kategori> kategoriList;

    public class KategoriViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener  {


            public TextView idKategori, ortuKategori;
            public ImageView imageOrtu;

        private List<AnakKategori> anakKategoriList = new ArrayList<>();
        private RecyclerView anakKategoriRecyclerView;
        private AnakKategoriAdapter akAdapter;


        public KategoriViewHolder(View view) {
            super(view);

            idKategori = (TextView) view.findViewById(R.id.idKategori);
            ortuKategori = (TextView) view.findViewById(R.id.ortuKategori);
            imageOrtu = (ImageView) view.findViewById(R.id.imageOrtu);
            anakKategoriRecyclerView = (RecyclerView) view.findViewById(R.id.anak_recycler_view);

            akAdapter = new AnakKategoriAdapter(anakKategoriList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context.getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
            anakKategoriRecyclerView.setLayoutManager(mLayoutManager);

            anakKategoriRecyclerView.setAdapter(akAdapter);

        }


        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                Kategori kategori= kategoriList.get(position);
                // We can access the data within the views
                Toast.makeText(context.getApplicationContext(), kategori.getTextOrtu(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public KategoriAdapter(List<Kategori> kategoriList, Context context) {
        this.kategoriList = kategoriList;
        this.context=context;
    }


    @Override
    public KategoriViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.kategori_list_row, parent, false);

        return new KategoriViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final KategoriViewHolder holder, int position) {
        Kategori kategori= kategoriList.get(position);
        holder.idKategori.setText(kategori.getIdKategori());
        holder.ortuKategori.setText(kategori.getTextOrtu());

        if(kategori.getIcon().equals("null")) {
            holder.imageOrtu.setVisibility(View.GONE);
            holder.ortuKategori.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            holder.ortuKategori.setAllCaps(true);
            //Picasso.get().load(R.mipmap.bg).into(holder.imageOrtu);
        }else {
            holder.imageOrtu.setVisibility(View.VISIBLE);
            holder.ortuKategori.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            Picasso.get()
                    .load( kategori.getIcon())
                    .into(holder.imageOrtu);

        }

//        if(kategori.getDataAnak().size()==0){
//            holder.imageOrtu.setVisibility(View.VISIBLE);
//        }else{
//            holder.imageOrtu.setVisibility(View.GONE);
//        }

        holder.anakKategoriList.clear();

        final ArrayList<ContentValues> anak = kategori.getDataAnak();

        Log.d("anaknya",anak.toString());

        for (int posisi = 0;posisi < anak.size(); posisi++) {
            ContentValues baris = anak.get(posisi);
            final AnakKategori anakKategori = new AnakKategori(baris.get("id").toString(), baris.get("nama").toString(), baris.get("icon").toString(),baris.get("show_text").toString());
            holder.anakKategoriList.add(anakKategori);

            holder.akAdapter.notifyDataSetChanged();
        }
        //onAnakKategori(kategori.getDataAnak());
        ItemClickSupport.addTo(holder.anakKategoriRecyclerView).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Log.d("klikanak",holder.anakKategoriList.get(position).getTextAnak());
                        MenuKategoriActivity amk = MenuKategoriActivity.getInstance();
                        amk.cariKategori(holder.anakKategoriList.get(position).getTextAnak());
                    }

                }
        );

        Log.d("kate",kategori.getTextOrtu() + "->" + kategori.getDataAnak().size());

    }

    @Override
    public int getItemCount() {
        return kategoriList.size();
    }

}
