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
import banjarbarukota.go.id.idaman.Utility.CircleTransform;

public class AnggotaAdapter extends RecyclerView.Adapter<AnggotaAdapter.AnggotaViewHolder>  {
    private List<Anggota> anggotaList;

    public class AnggotaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
            public TextView idAnggota, anggota;
            public ImageView imageAnggota;

            public AnggotaViewHolder(View view) {
                super(view);
                idAnggota = (TextView) view.findViewById(R.id.tvIdAnggota);
                anggota = (TextView) view.findViewById(R.id.namaAnggota);
                imageAnggota = (ImageView) view.findViewById(R.id.imageAnggota);
            }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                Anggota anggota= anggotaList.get(position);
                // We can access the data within the views
            }
        }
    }


    public AnggotaAdapter(List<Anggota> anggotaList) {
        this.anggotaList = anggotaList;
    }

    @Override
    public AnggotaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.anggota_list_row, parent, false);

        return new AnggotaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AnggotaViewHolder holder, int position) {
        Anggota anggota= anggotaList.get(position);
        if(!anggota.getIdAnggota().isEmpty()){
            holder.idAnggota.setText(anggota.getIdAnggota());
            holder.idAnggota.setVisibility(View.GONE);
            holder.anggota.setText(anggota.getNama());
            if(anggota.getGambar().equals(null)) {
                Picasso.get().load(R.mipmap.bg)
                        .transform(new CircleTransform())
                        .into(holder.imageAnggota);
            }else {
                Picasso.get()
                        .load("http://siapkk.banjarbarukota.go.id/" + anggota.getGambar())
                        .transform(new CircleTransform())
                        .into(holder.imageAnggota);
            }
        }

    }

    @Override
    public int getItemCount() {
        return anggotaList.size();
    }

}
