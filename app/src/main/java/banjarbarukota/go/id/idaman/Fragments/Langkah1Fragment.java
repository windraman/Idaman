package banjarbarukota.go.id.idaman.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

import banjarbarukota.go.id.idaman.BuildConfig;
import banjarbarukota.go.id.idaman.LokasiWizardActivity;
import banjarbarukota.go.id.idaman.R;
import pub.devrel.easypermissions.EasyPermissions;

public class Langkah1Fragment extends Fragment implements MapEventsReceiver {
    public View view;

    MapView map = null;

    MapEventsOverlay markersOverlay;

    Marker tapMarker;

    LokasiWizardActivity lokasiWizardActivity;

    GeoPoint startPoint;
    IMapController mapController;

    FloatingActionButton fbMyLoc;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.lokasi_osm, container, false);

        lokasiWizardActivity = (LokasiWizardActivity) getActivity();

        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);


        map = (MapView) view.findViewById(R.id.map);

        map.setTileSource(TileSourceFactory.MAPNIK);

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        mapController = map.getController();
        mapController.setZoom(14);


        if (lokasiWizardActivity.mylat != null){
            startPoint = new GeoPoint(Double.parseDouble(lokasiWizardActivity.mylat), Double.parseDouble(lokasiWizardActivity.mylng));
        }else{
            startPoint = new GeoPoint(-3.4419, 114.831);
        }


        mapController.animateTo(startPoint);

        markersOverlay = new MapEventsOverlay(this.getActivity(), this);

        map.getOverlays().add(0, markersOverlay);

        tapMarker = new Marker(map);

        fbMyLoc = (FloatingActionButton) view.findViewById(R.id.fbMyLoc);

        fbMyLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lokasiWizardActivity.mylat != null){
                    GeoPoint myLoc = new GeoPoint(Double.parseDouble(lokasiWizardActivity.mylat), Double.parseDouble(lokasiWizardActivity.mylng));
                    mapController.animateTo(myLoc);
                    mapController.setZoom(16);
                    singleTapConfirmedHelper(myLoc);
                }
            }
        });

//        Marker startMarker = new Marker(map);
//        startMarker.setPosition(startPoint);
//        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
//        map.getOverlays().add(startMarker);


        return view;

    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        //Toast.makeText(getActivity(), "Tap on ("+p.getLatitude()+","+p.getLongitude()+")", Toast.LENGTH_SHORT).show();
        lokasiWizardActivity.lat = String.valueOf(p.getLatitude());
        lokasiWizardActivity.lon = String.valueOf(p.getLongitude());

        addMarker(p);

        return false;
    }

    public void addMarker(GeoPoint gp){
        //markersOverlay

        map.getOverlays().remove(tapMarker);

        tapMarker.setPosition(gp);
        tapMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(tapMarker);
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        return false;
    }



}
