package com.example.myqrgenerator;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SavedQRFragment extends Fragment {
    ListView gv;
    TextView tV;
    ArrayList<File> list;
    ArrayList<String> b;
    private String root;

    public SavedQRFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saved_qr, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        list = imageReader(Environment.getExternalStorageDirectory());
        gv =  view.findViewById(R.id.gridView);
        tV = view.findViewById(R.id.textView);
        root = Environment.getExternalStorageDirectory().toString().concat("/saved_images/");
        gv.setAdapter(new GridAdapter());
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getContext(),View_Image.class).putExtra("img",list.get(position).toString()));
            }
        });
    }
    class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = getLayoutInflater().inflate(R.layout.single_grid,parent, false);
            ImageView iv= convertView.findViewById(R.id.imageView);
            iv.setImageURI(Uri.parse(getItem(position).toString()));
            tV = (TextView) convertView.findViewById(R.id.textView);
            System.out.println(root);
            String[] rawName = (Uri.parse(getItem(position).toString()).toString()).split(root);
            tV.setText(rawName[1]);
            return convertView;
        }
    }

    private ArrayList<File> imageReader(File root) {
        ArrayList<File> a = new ArrayList<>();
        b= new ArrayList<>();
        File myDir = new File(root + "/saved_images");
        File[] files = myDir.listFiles();
        if(!files.equals(null)) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                } else {
                    if (files[i].getName().endsWith(".jpg")) {
                        String filename = files[i].getName();
                        a.add(files[i]);
                        b.add(filename);
                    }
                }
            }
        }
        return a;
    }
    public ArrayList <String> getname(){
        return b;
    }
}
