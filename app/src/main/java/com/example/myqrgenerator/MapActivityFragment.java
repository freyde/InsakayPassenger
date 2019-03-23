package com.example.myqrgenerator;


import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import static android.support.constraint.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapActivityFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private String UID, routeID, operator, route, origin, destination, qrInfo;
    private GoogleMap mGoogleMap;
    private MapView mMapView;
    private View mView;
    private ArrayList<String> conductorList;
    private HashMap<String, Double> latitude, longitude;
    private Context context;


    public MapActivityFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment

        mView =  inflater.inflate(R.layout.fragment_notification, container, false);
        mMapView = (MapView) mView.findViewById(R.id.map);
        conductorList = new ArrayList<String>();
        latitude = new HashMap<String, Double>();
        longitude = new HashMap<String, Double>();
        context = getContext();
        updateConductorLocations();
        return mView;

    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateConductorLocations();

        if(mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            updateConductorLocations();
        }

    }
    @Override
    public void onResume() {
        super.onResume();

        if(mMapView != null) {

            updateConductorLocations();
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(context);
        mGoogleMap = googleMap;

        try{
            boolean success =googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(getActivity(),R.raw.custommap));


            if(!success){
                Log.e("MapsActivity","Style parsing failed");
            }
        }catch (Resources.NotFoundException e){
            Log.e(TAG,"can't find style error",e);
        }





        mGoogleMap.clear();
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        System.out.println("Map Updated");

        if(conductorList != null) {

            for(String conductor : conductorList) {
                googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude.get(conductor), longitude.get(conductor))).title(conductor));
                CameraPosition test = CameraPosition.builder().target(new LatLng(latitude.get(conductor), longitude.get(conductor))).zoom(16).bearing(0).tilt(45).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(test));
            }
        }



    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //        LatLng latLng = new LatLng(14.195862, 120.878176);

    public void updateConductorLocations() {
//        conductorList.clear();
//        latitude.clear();
//        longitude.clear();
        FirebaseDatabase.getInstance().getReference("onOperation")
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String conductor = snapshot.getKey();
                            Double lat = Double.parseDouble(snapshot.child("lat").getValue().toString());
                            Double longi = Double.parseDouble(snapshot.child("long").getValue().toString());
                            conductorList.add(conductor);
                            latitude.put(conductor, lat);
                            longitude.put(conductor, longi);
                            System.out.println("conductor: "+ conductor);
                            System.out.println(latitude);

                        }
                        mMapView.getMapAsync(MapActivityFragment.this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}




