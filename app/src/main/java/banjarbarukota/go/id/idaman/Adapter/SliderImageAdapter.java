package banjarbarukota.go.id.idaman.Adapter;

import android.content.Context;
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

public class SliderImageAdapter extends RecyclerView.Adapter<SliderImageAdapter.SliderImageViewHolder>  {
    private final Context context;
    private List<SliderImage> sliderImageList;

    public class SliderImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
            public TextView idImage, caption;
            public ImageView imageView;

            public SliderImageViewHolder(View view) {
                super(view);
                idImage = (TextView) view.findViewById(R.id.tvIdSliderImage);
                //caption = (TextView) view.findViewById(R.id.namaSliderImage);
                imageView = (ImageView) view.findViewById(R.id.imageView);
            }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                SliderImage sliderImage= sliderImageList.get(position);
                // We can access the data within the views
                Toast.makeText(context.getApplicationContext(), sliderImage.getCaption(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    public SliderImageAdapter(List<SliderImage> sliderImageList, Context context){
        this.sliderImageList = sliderImageList;
        this.context=context;
    }

    @Override
    public SliderImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.slider_image_list_row, parent, false);

        return new SliderImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SliderImageViewHolder holder, int position) {
        SliderImage sliderImage= sliderImageList.get(position);
        if(!sliderImage.getIdImage().isEmpty()){
            holder.idImage.setText(sliderImage.getIdImage());
            holder.idImage.setVisibility(View.GONE);
            holder.idImage.setText(sliderImage.getIdImage());
                Picasso.get()
                        .load("http://siapkk.banjarbarukota.go.id/" + sliderImage.getUrl())
                        .into(holder.imageView);
        }

    }

    @Override
    public int getItemCount() {
        return sliderImageList.size();
    }

}
