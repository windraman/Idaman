package banjarbarukota.go.id.idaman.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import banjarbarukota.go.id.idaman.R;
import banjarbarukota.go.id.idaman.Utility.CircleTransform;

public class MemberAdapter extends BaseAdapter {

    List<Member> members = new ArrayList<Member>();
    Context context;

    public MemberAdapter(Context context) {
        this.context = context;
    }

    public void add(Member members) {
        this.members.add(members);
        notifyDataSetChanged(); // to render the list we need to notify
    }

    @Override
    public int getCount() {
        return members.size();
    }

    @Override
    public Object getItem(int i) {
        return members.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    // This is the backbone of the class, it handles the creation of single ListView row (chat bubble)
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        MemberViewHolder holder = new MemberViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Member member= members.get(i);

            convertView = messageInflater.inflate(R.layout.list_member_item, null);
            //holder.avatar = (View) convertView.findViewById(R.id.avatar);
            holder.nama = (TextView) convertView.findViewById(R.id.name);
            holder.status = (TextView) convertView.findViewById(R.id.member_status);
            holder.img=(ImageView) convertView.findViewById(R.id.member_image);

            convertView.setTag(holder);
        if (member.isBelongsToCurrentUser()) {
            holder.nama.setText("Saya");
            holder.status.setText(member.getMemberData().get("status").toString());
        }else{
            if(member.getMemberData().getAsString("display_name").equals("NULL")){
                holder.nama.setText(member.getMemberData().get("name").toString());
            }else{
                holder.nama.setText(member.getMemberData().get("display_name").toString());
            }

            holder.status.setText(member.getMemberData().get("status").toString());
        }



            Picasso.get()
                .load("http://siapkk.banjarbarukota.go.id/" + member.getMemberData().get("photo").toString())
                .transform(new CircleTransform())
                .into(holder.img);


        return convertView;
    }

}

class MemberViewHolder {
    public TextView nama;
    public TextView status;
    ImageView img;
}



