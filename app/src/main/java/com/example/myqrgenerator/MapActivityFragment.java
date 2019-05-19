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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

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
    private Spinner operatorSpinner;
    private ArrayAdapter<String> operatorAdapter;
    private ArrayList<String> conductorList, operatorList;
    private HashMap<String, Double> latitude, longitude;
    private HashMap<String, String> operators, plates, times;
    private Context context;
    private long diff;
    private Boolean browsing = false;


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
        operatorSpinner = (Spinner) mView.findViewById(R.id.operatorSpinner);
        operatorList = new ArrayList<String>();
        conductorList = new ArrayList<String>();
        latitude = new HashMap<String, Double>();
        longitude = new HashMap<String, Double>();
        operators = new HashMap<String, String>();
        plates = new HashMap<String, String>();
        times = new HashMap<String, String>();
        context = getContext();

        operatorList.add("Select Operator");
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String opID = "";
                        String name = "";

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            name = snapshot.child("info/shortName").getValue().toString();
                            opID = snapshot.child("info/operatorID").getValue().toString();
                            operators.put(name, opID);
                            operatorList.add(name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        operatorAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, operatorList);
        operatorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        operatorSpinner.setAdapter(operatorAdapter);

        operatorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mMapView.getMapAsync(MapActivityFragment.this);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        if(!browsing) {
            MapsInitializer.initialize(context);
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mGoogleMap = googleMap;
        }


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

        System.out.println("Map Updated");

        if(conductorList != null && operatorSpinner.getSelectedItem() != "Select Operator") {
            String selectedOp = operatorSpinner.getSelectedItem().toString();
            Double latTotal = 0D, lngTotal = 0D, lat = 0D, lng = 0D;
            int c = 0;
            for(String conductor : conductorList) {
                if(conductor.startsWith(operators.get(selectedOp))) {
                    String time = "";
                    SimpleDateFormat timeFormat = new SimpleDateFormat("MM_dd_yy_HH:mm");
                    String curTime = timeFormat.format(new Date());
                    time = times.get(conductor);
                    Date dCurTime, dTime;
                    try {
                        dCurTime = timeFormat.parse(curTime);
                        dTime = timeFormat.parse(time);
                        diff = dCurTime.getTime() - dTime.getTime();
                        System.out.println(TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if(TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS) < 15) {
                        lat = latitude.get(conductor);
                        lng = longitude.get(conductor);
                        googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(plates.get(conductor)));
                        latTotal += lat;
                        lngTotal += lng;
                        c++;
                    }
                }
            }
            CameraPosition test;
            if(!browsing) {
                test = CameraPosition.builder().target(new LatLng(latTotal / c, lngTotal / c)).zoom(11).bearing(0).tilt(45).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(test));
                browsing = true;
            }
        } else {
            browsing = false;
            CameraPosition test = CameraPosition.builder().target(new LatLng(12.867031, 121.766552)).zoom(5).bearing(0).tilt(45).build();
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(test));
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
                            String plateNo = snapshot.child("busPlate").getValue().toString();
                            String time = snapshot.child("time").getValue().toString();
                            conductorList.add(conductor);
                            latitude.put(conductor, lat);
                            longitude.put(conductor, longi);
                            plates.put(conductor, plateNo);
                            times.put(conductor, time);
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




