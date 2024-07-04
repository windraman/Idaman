package banjarbarukota.go.id.idaman.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import banjarbarukota.go.id.idaman.R;
import banjarbarukota.go.id.idaman.Utility.CircleTransform;

public class ListChatAdapter extends RecyclerView.Adapter<ListChatAdapter.ListChatViewHolder>  {

    private List<ListChat> listChatList;

    public class ListChatViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener  {


        public TextView id, title, description;
        public ImageView picture;
            


        public ListChatViewHolder(View view) {
            super(view);

            id = (TextView) view.findViewById(R.id.tvIdListChat);
            title = (TextView) view.findViewById(R.id.tvTitle);
            description = (TextView) view.findViewById(R.id.tvDescription);
            picture = (ImageView) view.findViewById(R.id.imgIcon);

        }


        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
//                ListChat listChat = new List<ListChat>();
//                listChat = listChat.get(position);
//                // We can access the data within the views
//                Toast.makeText(getApplicationContext(), listChat.getTextOrtu(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public ListChatAdapter(List<ListChat> listChat) {
        this.listChatList = listChat;
    }


    @Override
    public ListChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_chat_item, parent, false);

        return new ListChatViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ListChatViewHolder holder, int position) {
        ListChat listChat= listChatList.get(position);
        holder.id.setText(listChat.getId());
        holder.title.setText(listChat.getTitle());
        holder.description.setText(listChat.getDescrption());

        if(listChat.getPicture().equals("null")) {
            holder.picture.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load( R.drawable.ic_group_black_24dp)
                    .error(R.drawable.ic_group_black_24dp)
                    .transform(new CircleTransform())
                    .into(holder.picture);
        }else {
            holder.picture.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load( listChat.getPicture())
                    .error(R.drawable.ic_group_black_24dp)
                    .transform(new CircleTransform())
                    .into(holder.picture);

        }

//        if(listChat.getDataAnak().size()==0){
//            holder.imageOrtu.setVisibility(View.VISIBLE);
//        }else{
//            holder.imageOrtu.setVisibility(View.GONE);
//        }


    }

    @Override
    public int getItemCount() {
        return listChatList.size();
    }

}
