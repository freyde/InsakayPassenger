package com.example.myqrgenerator;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebStorage;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLOutput;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
//import android.app.AlertDialog;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class GenerateFragment extends Fragment implements OnItemSelectedListener {
    private View view;

    //Variables on Spinners
    private Spinner operatorSpinner, routeSpinner, originSpinner, destinationSpinner, amountSpinner;
    private ArrayList<String> operatorList, routeList, landmarksList,OriginList,DestinationList,amountList;
    private ArrayAdapter<String> operatorAdapter;
    private ArrayAdapter<String> routeAdapter;
    private ArrayAdapter<String> landmarksAdapter;
    private ArrayAdapter amountAdapter;
    private HashMap<String, String> operatorHash, routeHash, landmarksHash, landmarksCov;
    private String UID, routeID, operator, route, origin, destination, qrInfo,amounting, fare;
    private TextView fareView;

    //Variables on nav
    private static final String TAG = "a";
    private View mView;
    private Button generate;
    private ImageView qr_code;
    private BottomNavigationView mainNav;
    private FrameLayout mainFrame;
    private GenerateFragment generateFragment;
    private MapActivityFragment mapActivityFragment;


    MainActivity mainActivity = new MainActivity();

    public GenerateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Inflate the layout for this fragment

            mView = inflater.inflate(R.layout.fragment_home, container, false);

            operatorList = new ArrayList<String>();
            operatorHash = new HashMap<String, String>();
            routeList = new ArrayList<String>();
            amountList = new ArrayList<String>();
            routeHash = new HashMap<String, String>();
            landmarksList = new ArrayList<String>();
            landmarksHash = new HashMap<String, String>();
            landmarksCov = new HashMap<String, String>();



            operatorSpinner = (Spinner) mView.findViewById(R.id.operator_Spinner);
            routeSpinner = (Spinner) mView.findViewById(R.id.route_Spinner);
            originSpinner = (Spinner) mView.findViewById(R.id.origin_Spinner);
            destinationSpinner = (Spinner) mView.findViewById(R.id.destination_Spinner);
            amountSpinner=(Spinner)mView.findViewById(R.id.amount_Spinner);
            fareView = (TextView) mView.findViewById(R.id.fare);
            generate = (Button) mView.findViewById(R.id.generate);


            routeList.add("Select Route");
            landmarksList.add("Select Landmark");


            amountAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.bayad));
            amountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            amountSpinner.setAdapter(amountAdapter);



            operatorAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item, operatorList);
            updateOperators();
            operatorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            operatorSpinner.setAdapter(operatorAdapter);

            routeAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item, routeList);
            routeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            routeSpinner.setAdapter(routeAdapter);

            landmarksAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item, landmarksList);
            landmarksAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            originSpinner.setAdapter(landmarksAdapter);

            destinationSpinner.setAdapter(landmarksAdapter);


            operatorSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String operator = parent.getItemAtPosition(position).toString();
                    Toast.makeText(getContext(), operator, Toast.LENGTH_SHORT).show();
                    UID = operatorHash.get(operator);

                    routeList.clear();
                    routeList.add("Select Route");
                    FirebaseDatabase.getInstance().getReference("users/" + UID + "/routes")
                            .addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        String name = snapshot.child("routeName").getValue().toString();
                                        String id = snapshot.child("routeID").getValue().toString();
                                        routeList.add(name);
                                        routeHash.put(name, id);
                                    }
                                    routeAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            routeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override

                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                  String route = parent.getItemAtPosition(position).toString();

                    routeID = routeHash.get(route);
                    landmarksList.clear();
                    landmarksList.add("Select Landmark");
                    FirebaseDatabase.getInstance().getReference("users/" + UID + "/landmarks/" + routeID)
                            .addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        String name = snapshot.child("landmarkName").getValue().toString();
                                        String id = snapshot.child("landmarkID").getValue().toString();
                                        String cov = snapshot.child("coverage").getValue().toString();
                                        landmarksList.add(name);
                                        landmarksHash.put(name, id);
                                        landmarksCov.put(name, cov);
                                    }
                                    landmarksAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            originSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String route = routeSpinner.getSelectedItem().toString();
                    String destination = destinationSpinner.getSelectedItem().toString();
                    String origin = parent.getItemAtPosition(position).toString();
                    if(!origin.equals("Select Landmark") && !destination.equals("Select Landmark")) {
                        routeID = routeHash.get(route);
                        String originCov = landmarksCov.get(origin);
                        final String destinationCov = landmarksCov.get(destination);
                        System.out.println(UID +" "+routeID);
                        FirebaseDatabase.getInstance().getReference("users/"+ UID +"/fares/"+ routeID +"/matrix/"+ originCov)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        System.out.println(dataSnapshot.child(destinationCov).getValue());
                                        fare = dataSnapshot.child(destinationCov).getValue().toString();
                                        fareView.setText(fare);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            destinationSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String route = routeSpinner.getSelectedItem().toString();
                    String origin = originSpinner.getSelectedItem().toString();
                    String destination = parent.getItemAtPosition(position).toString();
                    if(!origin.equals("Select Landmark") && !destination.equals("Select Landmark")) {
                        routeID = routeHash.get(route);
                        String originCov = landmarksCov.get(origin);
                        final String destinationCov = landmarksCov.get(destination);
                        System.out.println(UID +" "+routeID);
                        FirebaseDatabase.getInstance().getReference("users/"+ UID +"/fares/"+ routeID +"/matrix/"+ originCov)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        System.out.println(dataSnapshot.child(destinationCov).getValue());
                                        fare = dataSnapshot.child(destinationCov).getValue().toString();
                                        fareView.setText(fare);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        generate.setOnClickListener(new View.OnClickListener() {

            Calendar calendar = Calendar.getInstance();

            String currentDate = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime()).replace('/', '.');

            @Override
            public void onClick(View v) {
                    operator = operatorSpinner.getSelectedItem().toString();
                    route = routeSpinner.getSelectedItem().toString();
                    origin = originSpinner.getSelectedItem().toString();
                    destination = destinationSpinner.getSelectedItem().toString();
                    amounting =amountSpinner.getSelectedItem().toString();

                  //  String change = amounting-fare;
                 //   amounting = amountAdapter.getSelectedItem().toString

                    qrInfo = "INSAKAY."+ operator +"."+route +"."+ origin +"."+ destination +"."+amounting+ "."+currentDate;




                if (qrInfo != null && !qrInfo.isEmpty()) {

                    if (!operator.equals("Select Operator") && !route.equals("Select Route") && !origin.equals("Select Landmark") && !destination.equals("Select Landmark")) {
                        try {
                            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                            BitMatrix bitMatrix = multiFormatWriter.encode(qrInfo, BarcodeFormat.QR_CODE, 1000, 1000);
                            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                            qr_code.setImageBitmap(bitmap);
                            // BitmapDrawable drawable = (BitmapDrawable) qr_code.getDrawable();
                           // MainActivity.requestPermission();
                            SaveImage(bitmap, qrInfo);

                        } catch (WriterException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

            return mView;

    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setContentView(R.layout.activity_main);


        qr_code = mView.findViewById(R.id.qrcode);
        mainNav = (BottomNavigationView) mView.findViewById(R.id.main_nav);
        mainFrame = (FrameLayout) mView.findViewById(R.id.main_frame);
        generateFragment = new GenerateFragment();
        mapActivityFragment = new MapActivityFragment();
    }

    private void setContentView(int activity_main) {
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        final String text1 = parent.getItemAtPosition(position).toString();
//        Toast.makeText(parent.getContext(), text1, Toast.LENGTH_SHORT).show();
        System.out.println(parent.getAdapter().getItem(position));

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
    private void updateOperators() {
        operatorList.add("Select Operator");
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String uid = "";
                        String name = "";
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            uid = snapshot.getKey();
                            name = dataSnapshot.child(uid + "/info/shortName").getValue().toString();
//                            System.out.println(name);
                            operatorList.add(name);
                            operatorHash.put(name, uid);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });
        operatorAdapter.notifyDataSetChanged();
    }
    private void SaveImage(Bitmap bitmap, String qrInfo ) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");

       // Random generator = new Random();
        //int n = 10000;
       // n = generator.nextInt(n);
        String fname = qrInfo +".jpg";
        File file = new File (myDir, fname);
        if (!file.exists()){
        }
        if (file.exists ())
            file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
//    public void createDir ()
//    {
//
//        File file =new File(Environment.getExternalStorageDirectory()+"/MyQRgenerator");
//        boolean success =true;
//        if (!file.exists()){
//            Toast.makeText(getApplicaitonContext()),"Directory does not exist,create it"
//
//        }
//
//
//
//
//    }


}








