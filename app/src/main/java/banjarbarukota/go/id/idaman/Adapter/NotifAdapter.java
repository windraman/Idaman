package banjarbarukota.go.id.idaman.Adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import banjarbarukota.go.id.idaman.R;
import banjarbarukota.go.id.idaman.Utility.CircleTransform;

public class NotifAdapter extends BaseAdapter {

    List<Notifikasi> notifs = new ArrayList<Notifikasi>();
    Context context;
    int [] imageId;

    public NotifAdapter(Context context) {
        this.context = context;
    }

    public void add(Notifikasi notifikasi) {
        this.notifs.add(notifikasi);
        notifyDataSetChanged(); // to render the list we need to notify
    }

    @Override
    public int getCount() {
        return notifs.size();
    }

    @Override
    public Object getItem(int i) {
        return notifs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    // This is the backbone of the class, it handles the creation of single ListView row (chat bubble)
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        NotifViewHolder holder = new NotifViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        final Notifikasi notif = notifs.get(i);

            convertView = messageInflater.inflate(R.layout.notif_item, null);
            //holder.avatar = (View) convertView.findViewById(R.id.avatar);
            holder.link = (TextView) convertView.findViewById(R.id.link);
            holder.isiNotif = (TextView) convertView.findViewById(R.id.isi_notif);
            holder.img=(ImageView) convertView.findViewById(R.id.notif_image);

            convertView.setTag(holder);

            holder.link.setText(notif.getNotifData().get("url").toString());
            holder.isiNotif.setText(notif.getText());

            holder.isiNotif.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer realPosition = (Integer) view.getTag();
                    Log.d("kliknotif",notif.getNotifData().get("url").toString());
                    String[] aksis = notif.getNotifData().get("url").toString().split(";");
                    String c = "banjarbarukota.go.id.idaman.Utility.Jumper";
                    String methodName = aksis[0];
                    Method method = null;
                    try {
                        Class<?> dc = Class.forName(c); // convert string classname to class
                        Object dog = dc.newInstance(); // invoke empty constructor
                        if(aksis.length==2) {
                            Class<?>[] paramTypes = {Context.class, String.class};
                            Method printDogMethod = dog.getClass().getMethod(methodName, paramTypes);
                            printDogMethod.invoke(dog, context, aksis[1]);
                        }else if(aksis.length==1){
                            Class<?>[] paramTypes = {Context.class};
                            Method printDogMethod = dog.getClass().getMethod(methodName, paramTypes);
                            printDogMethod.invoke(dog, context);
                        }else if(aksis.length==3){
                            Class<?>[] paramTypes = {Context.class, String.class, String.class};
                            Method printDogMethod = dog.getClass().getMethod(methodName, paramTypes);
                            printDogMethod.invoke(dog, context, aksis[1], aksis[2]);
                        }

                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            });


        if(notif.getNotifData().get("style").toString().equals("big")) {
                holder.img.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(notif.getNotifData().get("gambar").toString())
                        .into(holder.img);
            }else{
                holder.img.setVisibility(View.GONE);
            }


        return convertView;
    }

}

class NotifViewHolder {
    public TextView link;
    public TextView isiNotif;
    ImageView img;
}



