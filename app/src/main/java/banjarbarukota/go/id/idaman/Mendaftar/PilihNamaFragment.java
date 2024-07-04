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

public class PilihNamaFragment extends Fragment {
    // Store instance variables
    private static String title;
    private int page;
    private static Activity thus;
    private static TextView tvTanya;


    // newInstance constructor for creating fragment with arguments
    public static PilihNamaFragment newInstance(int page, String title) {
        PilihNamaFragment fragmentNama = new PilihNamaFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentNama.setArguments(args);
        return fragmentNama;
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
        View view = inflater.inflate(R.layout.fragment_pilih, container, false);
        tvTanya = (TextView) view.findViewById(R.id.tvTanya);
        tvTanya.setText(DaftarActivity.NIK + ", " + title);
        return view;
    }

    public static void loadListNama(){
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(thus, android.R.layout.simple_list_item_1, DaftarActivity.NAMA_ITEMS);

        final ListView listView = (ListView) thus.findViewById(R.id.lvItems);
        listView.setAdapter(itemsAdapter);

        tvTanya.setText(DaftarActivity.NIK + ", " + title);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                DaftarActivity.NAMA = listView.getItemAtPosition(position).toString();
                DaftarActivity.gotoPosition(2);
            }
        });
    }
}