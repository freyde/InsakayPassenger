package com.example.myqrgenerator;



import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;

import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentTransaction;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;


public class MainActivity extends AppCompatActivity {
    private BottomNavigationView main_Nav;
    private FrameLayout mMainframe;
    private GenerateFragment generateFragment;
    private MapActivityFragment mapActivityFragment;
    private SavedQRFragment savedQRFragment;
    private Button generate,scan;
    private static final int REQ_PERMISSION=120;
    private int STORAGE_PERMISSION_CODE = 1;
    private static int SPLASH_TIMEOUT=2000;

//    private ImageView qr_code;
//    private List<String>list = new ArrayList<String>();
//    private Spinner spinner1, spinner2,spinner3, spinner4;

    private BottomNavigationView mMainReceipt;
    //  private FrameLayout mMainOp;
    //  private GenerateFragment generateFragment;
    //  private MapActivityFragment mapActivityFragment;
    //  private BottomNavigationView mainNav;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        secondDir();
        makedir();

        File folder= new File(Environment.getExternalStorageDirectory()+"/saved_images");
        boolean success =true;
        if(!folder.exists()){
            success= folder.mkdirs();
    }
        if(success){

        }else {
            System.out.println("failed tanga");
            System.out.println(folder);
        }


       mMainframe = (FrameLayout) findViewById(R.id.main_frame);
       mMainReceipt = (BottomNavigationView) findViewById(R.id.main_nav);

      // final SavedQRFragment saved_qr = new SavedQRFragment();
        generateFragment = new GenerateFragment();
        savedQRFragment = new SavedQRFragment();
        mapActivityFragment = new MapActivityFragment();
      main_Nav = (BottomNavigationView) findViewById(R.id.main_nav);
        setFragment(generateFragment);
        main_Nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {


            public boolean onNavigationItemSelected(@NonNull MenuItem Item) {

                switch (Item.getItemId()) {
                    case R.id.nav_OnOperation:
                        setFragment(mapActivityFragment);
                        return true;

                    case R.id.nav_receipt:
                        setFragment(generateFragment);
                        return true;

                    case R.id.saved_Qr:
                        setFragment(savedQRFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }
    private void requestStoragePermission2() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity
                                    .this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);

                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();

        }
        else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);

        }

    }
    private void requestStoragePermission() {

        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE))
        {
    new AlertDialog.Builder(this)
            .setTitle("Permission needed")
            .setMessage("This permission because of this and that")
            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(MainActivity
                            .this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
                }
            })
            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();

        }
else{
    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==STORAGE_PERMISSION_CODE){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){

                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();

            }
            else {
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();

    }
public void makedir(){


    if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
        //Toast.makeText(MainActivity.this,"You Have already granted this permission",Toast.LENGTH_SHORT).show();

    }else {
        requestStoragePermission();

          }

     }
    public void secondDir(){

        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
            //Toast.makeText(MainActivity.this,"You Have already granted this permission",Toast.LENGTH_SHORT).show();

        }else {
            requestStoragePermission2();

              }

           }

        }







