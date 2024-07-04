package banjarbarukota.go.id.idaman.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import banjarbarukota.go.id.idaman.R;


public class AnakMenuAdapter extends RecyclerView.Adapter<AnakMenuAdapter.AnakMenuViewHolder>  {
    private List<AnakMenu> anakMenuList;

    public class AnakMenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
            public TextView idKategoriAnak, anakKategori;
            public ImageView imageAnak;

            public AnakMenuViewHolder(View view) {
                super(view);
                idKategoriAnak = (TextView) view.findViewById(R.id.tvIdKategoriAnak);
                anakKategori = (TextView) view.findViewById(R.id.anakKategori);
                imageAnak = (ImageView) view.findViewById(R.id.imageAnak);
            }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                AnakMenu anakKategori= anakMenuList.get(position);
                // We can access the data within the views
               // Toast.makeText(Conte, anakKategori.getTextAnak(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    public AnakMenuAdapter(List<AnakMenu> anakMenuList) {
        this.anakMenuList = anakMenuList;
    }

    @Override
    public AnakMenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.anak_kategori_list_row, parent, false);

        return new AnakMenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AnakMenuViewHolder holder, int position) {
        AnakMenu anakKategori= anakMenuList.get(position);
        if(!anakKategori.getIdMenuAnak().isEmpty()){
            Log.d("anak :",String.valueOf(anakKategori.isShowText()));
            if(!anakKategori.isShowText()){
                holder.anakKategori.setVisibility(View.GONE);
            }else{
                holder.anakKategori.setVisibility(View.VISIBLE);
            }
            holder.idKategoriAnak.setText(anakKategori.getIdMenuAnak());
            holder.idKategoriAnak.setVisibility(View.GONE);
            holder.anakKategori.setText(anakKategori.getTextAnak());
            if(anakKategori.getGambar().equals(null)) {
                Picasso.get().load(R.mipmap.bg).into(holder.imageAnak);
            }else {
                Picasso.get()
                        .load("http://siapkk.banjarbarukota.go.id/" + anakKategori.getGambar())
                        .into(holder.imageAnak);
            }
        }

    }

    @Override
    public int getItemCount() {
        return anakMenuList.size();
    }

}
