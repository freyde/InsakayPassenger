package com.example.myqrgenerator;

import android.app.Activity;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class View_Image extends Activity {
ImageView iv2;
TextView iv3;
SavedQRFragment savedQRFragment;
private String root;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__image);
      savedQRFragment = new SavedQRFragment();
        root = Environment.getExternalStorageDirectory().toString().concat("/saved_images/");

       // Intent i =getIntent();
//        File f = i.getExtras().getParcelable("img");

        String f=getIntent().getStringExtra("img");
        iv2 =(ImageView) findViewById(R.id.qrImage);
        iv2.setImageURI(Uri.parse(f));

        iv3=(TextView)findViewById(R.id.qrName);
        String[] rawName = f.split(root);
        iv3.setText(rawName[1]);
        savedQRFragment.getname();
        System.out.println(savedQRFragment);

    }
}

//
//public class ManualTicket extends DialogFragment {
//
//    private View view;
//    private Spinner routeSpinner, originSpinner, destinationSpinner;
//    private ArrayList<String> routeList, landmarksList;
//    private ArrayAdapter<String> routeAdapter, landmarksAdapter;
//    private HashMap<String, String> routeHash, landmarksHash;
//    private String UID, routeID, originID, destinationID;
//
//
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        LayoutInflater inflater = requireActivity().getLayoutInflater();
//
//        view = inflater.inflate(R.layout.fragment_home, null);
//
//
//
//
//
//        routeList = new ArrayList<String>();
//        landmarksList = new ArrayList<String>();
//        routeHash = new HashMap<String, String>();
//        landmarksHash = new HashMap<String, String>();
//
//
//
//        UID = SaveSharedPreference.getOpUID(getActivity().getApplicationContext());
//
//        updateRoutes();
//        System.out.println(routeList.size());
//        routeSpinner = (Spinner) view.findViewById(R.id.route_Spinner);
//        routeAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item, routeList);
//        routeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        routeSpinner.setAdapter(routeAdapter);
//
//
//        destinationSpinner = (Spinner) view.findViewById(R.id.destination_Spinner);
//        landmarksAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item, landmarksList);
//        landmarksAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        originSpinner = (Spinner) view.findViewById(R.id.origin_Spinner);
//        originSpinner.setAdapter(landmarksAdapter);
//        destinationSpinner.setAdapter(landmarksAdapter);
//
//        routeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String route = parent.getItemAtPosition(position).toString();
//                String routeID = routeHash.get(route);
//                landmarksList.clear();
//                landmarksList.add("Select Origin");
//                FirebaseDatabase.getInstance().getReference("users/" + UID + "/landmarks/" + routeID)
//                        .addListenerForSingleValueEvent(new ValueEventListener() {
//
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                    String name = snapshot.child("landmarkName").getValue().toString();
//                                    String id = snapshot.child("landmarkID").getValue().toString();
//                                    landmarksList.add(name);
//                                    landmarksHash.put(name, id);
//                                    System.out.println(snapshot);
//                                }
//                                System.out.println();
//                                landmarksAdapter.notifyDataSetChanged();
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
//
//
//                Toast.makeText(getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//
//        builder.setTitle(getString(R.string.qr_scan_manual_ticketing))
//                .setView(view)
//                .setPositiveButton(getString(R.string.proceed), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                })
//
//                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//
//        return builder.create();
//    }
//
//    private void updateRoutes() {
//        routeList.add("Select Route");
//        FirebaseDatabase.getInstance().getReference("users")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                            String name = snapshot.child("routeName").getValue().toString();
////                        String key = snapshot.getKey();
//                            String uid = snapshot.getKey();
//                            routeList.add(name);
//                            routeHash.put(name, uid);
////                        routeKeys.put(name, key);
//                            System.out.println(snapshot);
//                        }
//                        System.out.println();
//                        routeAdapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//    }
//}
//
//
