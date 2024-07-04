package banjarbarukota.go.id.idaman.Adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import banjarbarukota.go.id.idaman.R;

public class CustomSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {
    Context context;
    List<CustomSpinner> spinItems = new ArrayList<CustomSpinner>();

    public CustomSpinnerAdapter(Context context) {
        this.context = context;
    }

    public void add(CustomSpinner spinItem) {
        this.spinItems.add(spinItem);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return spinItems.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public String getSpinnerItemId(int i) {
        return spinItems.get(i).getItemId();
    }


    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        CustomSpinnerViewHolder holder = new CustomSpinnerViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        CustomSpinner spinItem = spinItems.get(i);

        convertView = messageInflater.inflate(R.layout.custom_spinner_item_main, null);
        //holder.avatar = (View) convertView.findViewById(R.id.avatar);
        holder.itemId = (TextView) convertView.findViewById(R.id.tvSpinnerId);
        holder.name = (TextView) convertView.findViewById(R.id.tvSpinnerName);
        holder.imageIcon=(ImageView) convertView.findViewById(R.id.imgSpinner);

        convertView.setTag(holder);

        holder.itemId.setText(spinItem.getItemId());
        holder.name.setText(spinItem.getName());


        if(!spinItem.getImageIcon().equals("null")){
            holder.imageIcon.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(spinItem.getImageIcon())
                    .into(holder.imageIcon);

        }else {
            holder.imageIcon.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int i, View convertView, ViewGroup viewGroup) {
        CustomSpinnerViewHolder holder = new CustomSpinnerViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        CustomSpinner spinItem = spinItems.get(i);

        convertView = messageInflater.inflate(R.layout.custom_spinner_item_main, null);
        //holder.avatar = (View) convertView.findViewById(R.id.avatar);
        holder.itemId = (TextView) convertView.findViewById(R.id.tvSpinnerId);
        holder.name = (TextView) convertView.findViewById(R.id.tvSpinnerName);
        holder.imageIcon=(ImageView) convertView.findViewById(R.id.imgSpinner);

        convertView.setTag(holder);

        holder.itemId.setText(spinItem.getItemId());
        holder.name.setText(spinItem.getName());


        if(!spinItem.getImageIcon().equals("null")){
            holder.imageIcon.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(spinItem.getImageIcon())
                    .into(holder.imageIcon);

        }else {
            holder.imageIcon.setVisibility(View.GONE);
        }

        return convertView;
    }

    private View initialSelection(boolean dropdown) {
        // Just an example using a simple TextView. Create whatever default view
        // to suit your needs, inflating a separate layout if it's cleaner.
        TextView view = new TextView(context);
        view.setText("Pilih");
        int spacing = 5;
        view.setPadding(0, spacing, 0, spacing);

        if (dropdown) { // Hidden when the dropdown is opened
            view.setHeight(0);
        }

        return view;
    }

}

class CustomSpinnerViewHolder {
    public TextView itemId;
    public TextView name;
    ImageView imageIcon;
}