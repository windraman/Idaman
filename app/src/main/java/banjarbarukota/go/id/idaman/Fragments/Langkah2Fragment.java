package banjarbarukota.go.id.idaman.Fragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import banjarbarukota.go.id.idaman.Adapter.CustomSpinner;
import banjarbarukota.go.id.idaman.Adapter.CustomSpinnerAdapter;
import banjarbarukota.go.id.idaman.LokasiWizardActivity;
import banjarbarukota.go.id.idaman.R;
import banjarbarukota.go.id.idaman.Utility.MultiSelectionSpinner;
import banjarbarukota.go.id.idaman.Utility.OkHttpGetHandler;

public class Langkah2Fragment extends Fragment implements View.OnFocusChangeListener, AdapterView.OnItemSelectedListener{
    public View view;
    String[] kotas, kecamatans, kelurahans;
    List<String> listKategori, listKota, listKecamatan, listKelurahan, listCustomSpinner;
    MultiSelectionSpinner multiSpinTags;
    Spinner spinKota, spinKecamatan, spinKelurahan, spinDefaultIcon;

    CustomSpinnerAdapter kotaAdapter, kecamatanAdapter, kelurahanAdapter, iconAdapter;

    EditText edtNamaLokasi, edtJalan, edtNomor, edtKodePos, edtTelepon, edtWebSite, edtDeskripsi;

    OkHttpGetHandler okhan;

    LokasiWizardActivity lokasiWizardActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.lokasi_detail2_fragment, container, false);
        lokasiWizardActivity = (LokasiWizardActivity) getActivity();

        okhan = new OkHttpGetHandler();

        multiSpinTags = (MultiSelectionSpinner) view.findViewById(R.id.multiSpinTags);
        getTags();
        //multiSpinTags.setOnItemSelectedListener(this);
        multiSpinTags.setOnFocusChangeListener(this);

        edtNamaLokasi = (EditText) view.findViewById(R.id.edtNamaLokasi);
        edtNamaLokasi.setOnFocusChangeListener(this);
        edtJalan = (EditText) view.findViewById(R.id.edtJalan);
        edtJalan.setOnFocusChangeListener(this);
        edtKodePos = (EditText) view.findViewById(R.id.edtKodePos);
        edtKodePos.setOnFocusChangeListener(this);
        edtNomor = (EditText) view.findViewById(R.id.edtNomor);
        edtNomor.setOnFocusChangeListener(this);
        edtTelepon = (EditText) view.findViewById(R.id.edtTelepon);
        edtTelepon.setOnFocusChangeListener(this);
        edtWebSite = (EditText) view.findViewById(R.id.edtWebsite);
        edtWebSite.setOnFocusChangeListener(this);
        edtDeskripsi = (EditText) view.findViewById(R.id.edtDeskripsi);
        edtDeskripsi.setOnFocusChangeListener(this);

        //assign spinner kota
        kotas = new String[]{};
        listKota = new ArrayList<>(Arrays.asList(kotas));
        spinKota = view.findViewById(R.id.spinnerKota);
        kotaAdapter = new CustomSpinnerAdapter(getActivity().getApplicationContext());
        loadListKota();
        spinKota.setAdapter(kotaAdapter);
        spinKota.setOnItemSelectedListener(this);


        //assign spinner kecamatan
        kecamatans = new String[]{};
        listKecamatan = new ArrayList<>(Arrays.asList(kecamatans));
        spinKecamatan = (Spinner) view.findViewById(R.id.spinnerKecamatan);
        kecamatanAdapter = new CustomSpinnerAdapter(getActivity().getApplicationContext());
        loadListKecamatan();
        spinKecamatan.setAdapter(kecamatanAdapter);
        spinKecamatan.setOnItemSelectedListener(this);

        //assign spinner kelurahan
        kelurahans = new String[]{};
        listKelurahan = new ArrayList<>(Arrays.asList(kelurahans));
        spinKelurahan = (Spinner) view.findViewById(R.id.spinnerKelurahan);
        spinKelurahan.setOnItemSelectedListener(this);


        spinDefaultIcon = view.findViewById(R.id.spinnerDefaultIcon);
        iconAdapter = new CustomSpinnerAdapter(getActivity().getApplicationContext());
        loadListDefaultIcon();
        spinDefaultIcon.setAdapter(iconAdapter);
        spinDefaultIcon.setOnItemSelectedListener(this);


        multiSpinTags.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                lokasiWizardActivity.tags = multiSpinTags.getSelectedItem().toString();
                if (hasFocus) {

                }
                if (!hasFocus){

                }
            }
        });

        spinKecamatan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                lokasiWizardActivity.district_id = ((TextView)adapterView.getSelectedView().findViewById(R.id.tvSpinnerId)).getText().toString();
                loadListKelurahan(((TextView)adapterView.getSelectedView().findViewById(R.id.tvSpinnerId)).getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinKelurahan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                lokasiWizardActivity.village_id = ((TextView)adapterView.getSelectedView().findViewById(R.id.tvSpinnerId)).getText().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinDefaultIcon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                lokasiWizardActivity.default_icon = ((TextView)adapterView.getSelectedView().findViewById(R.id.tvSpinnerId)).getText().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }

    public void getTags() {
        listKategori = new ArrayList<String>();
        listKategori.add("");

        JSONObject jkategori = okhan.getAllKategori(1);

        JSONArray dataArray = null;
        try {
            dataArray = (JSONArray) jkategori.getJSONArray("data");

            if (dataArray.length() > 0) {
                for (int d = 0; d < dataArray.length(); d++) {
                    JSONObject jdata = dataArray.getJSONObject(d);
                    listKategori.add(jdata.getString("nama"));
                }
            }
            multiSpinTags.setItems(listKategori);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadListKota() {
        JSONObject jkota = okhan.getKota("63");

        JSONArray dataArray = null;
        try {
            dataArray = (JSONArray) jkota.getJSONArray("data");
            if (dataArray.length() > 0) {
                for (int d = 0; d < dataArray.length(); d++) {
                    JSONObject jdata = dataArray.getJSONObject(d);
                    final CustomSpinner customSpinner= new CustomSpinner(jdata.getString("id"), jdata.getString("name"), "null");
                    kotaAdapter.add(customSpinner);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadListKecamatan() {
        JSONObject jkecamatan = okhan.getKecamatan("6372");

        JSONArray dataArray = null;
        try {
            dataArray = (JSONArray) jkecamatan.getJSONArray("data");
            if (dataArray.length() > 0) {
                for (int d = 0; d < dataArray.length(); d++) {
                    JSONObject jdata = dataArray.getJSONObject(d);
                    final CustomSpinner customSpinner= new CustomSpinner(jdata.getString("id"), jdata.getString("name"), "null");

                    kecamatanAdapter.add(customSpinner);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadListKelurahan(String district_id) {
        JSONObject jkelurahan = okhan.getKelurahan(district_id);

        JSONArray dataArray = null;
        try {
            dataArray = (JSONArray) jkelurahan.getJSONArray("data");
            if (dataArray.length() > 0) {
                //spinKelurahan.setAdapter(null);
                kelurahanAdapter = new CustomSpinnerAdapter(getActivity().getApplicationContext());
                for (int d = 0; d < dataArray.length(); d++) {
                    JSONObject jdata = dataArray.getJSONObject(d);
                    final CustomSpinner customSpinner= new CustomSpinner(jdata.getString("id"), jdata.getString("name"), "null");
                    Log.d("kelurahans :",jdata.getString("name"));
                    kelurahanAdapter.add(customSpinner);
                }
                spinKelurahan.setAdapter(kelurahanAdapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadListDefaultIcon() {
        JSONObject jkategori = okhan.getAllKategori(1);

        JSONArray dataArray = null;
        try {
            dataArray = (JSONArray) jkategori.getJSONArray("data");

            ArrayList<ContentValues> data;
            ContentValues values;
            if (dataArray.length() > 0) {
                data = new ArrayList<ContentValues>();
                for (int j = 0; j < dataArray.length(); j++) {
                    JSONObject jdata = dataArray.getJSONObject(j);
                    final CustomSpinner customSpinner= new CustomSpinner(jdata.getString("id"), jdata.getString("nama"), jdata.getString("icon"));

                    iconAdapter.add(customSpinner);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        lokasiWizardActivity.tags = multiSpinTags.getSelectedItem().toString();
        lokasiWizardActivity.nama = edtNamaLokasi.getText().toString();
        lokasiWizardActivity.jalan = edtJalan.getText().toString();
        lokasiWizardActivity.nomor = edtNomor.getText().toString();
        lokasiWizardActivity.kode_pos = edtKodePos.getText().toString();
        lokasiWizardActivity.telepon = edtTelepon.getText().toString();
        lokasiWizardActivity.website = edtWebSite.getText().toString();
        lokasiWizardActivity.deskripsi = edtDeskripsi.getText().toString();
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
