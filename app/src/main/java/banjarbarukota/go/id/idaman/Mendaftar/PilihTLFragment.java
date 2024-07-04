package banjarbarukota.go.id.idaman.Mendaftar;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import banjarbarukota.go.id.idaman.R;

/**
 * Created by Wahyu on 5/7/2018.
 */

public class PilihTLFragment extends Fragment {
    // Store instance variables
    private static String title;
    private int page;
    private static Activity thus;
    private static TextView tvTanya;


    // newInstance constructor for creating fragment with arguments
    public static PilihTLFragment newInstance(int page, String title) {
        PilihTLFragment fragmentTL = new PilihTLFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentTL.setArguments(args);
        return fragmentTL;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");

        thus = getActivity();
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pilih2, container, false);
        tvTanya = (TextView) view.findViewById(R.id.tvTanya);
        tvTanya.setText(DaftarActivity.NAMA + ", " + title);
        return view;
    }


    public static void loadListTL(){

        tvTanya.setText(DaftarActivity.NAMA + ", " + title);
        ArrayAdapter<String> itemsAdapter2 =
                new ArrayAdapter<String>(thus, android.R.layout.simple_list_item_1, DaftarActivity.TL_ITEMS);

        final ListView listView2 = (ListView) thus.findViewById(R.id.lvItems2);
        listView2.setAdapter(itemsAdapter2);

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                DaftarActivity.TEMPAT_LAHIR = listView2.getItemAtPosition(position).toString();
                DaftarActivity.gotoPosition(3);
            }
        });
    }
}