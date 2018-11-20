package com.polinema.location;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;

import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements DapatkanAlamatTask.onTaskSelesai {

    private Button mLocationButton;
    private Button btnlokasi;
    private TextView mLocationTextView;
    private ImageView mAndroidImageView;
    private AnimatorSet mRotateAnim;
    private Location mLastLocation;
    private boolean mTrackingLocation;
    private static final int  REQUEST_LOCATION_PERMISSION=1;
    private static final int REQUEST_PICK_PLACE = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    private PlaceDetectionClient mPlaceDetectionClient;
    private String mLastPlaceName;

    private static String NAME ="" ;//static variabel untuk digunakan sebagai instance save agar saat berubah rotasi tidak hilang data sebelumnya
    private static String ADDRESS = "";//static variabel untuk digunakan sebagai instance save agar saat berubah rotasi tidak hilang data sebelumnya
    private static int IMG=-1;//static variabel untuk digunakan sebagai instance save agar saat berubah rotasi tidak hilang data sebelumnya
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) { //function digunakan agar data dari alamat sebelumnya disimpan ke dalaman Save Instance State
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("placeName",NAME);
        savedInstanceState.putString("placeAddress",ADDRESS);
        savedInstanceState.putInt("placeImage",IMG);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) { // function digunakn saat merestore data yang ada dalam Instancestate kedalam object yandg ditentukan
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.getString("placeName")=="")
        {
            mLocationTextView.setText("Tekan Button dibawah ini untuk mendapatkan lokasi anda");
        }
        else
        {
            mLocationTextView.setText(getString(R.string.alamat_text,savedInstanceState.getString("placeName"),savedInstanceState.getString("placeAddress"), System.currentTimeMillis()));
            mAndroidImageView.setImageResource(savedInstanceState.getInt("placeImage"));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationButton=(Button) findViewById(R.id.button_location);
        mLocationTextView =(TextView) findViewById(R.id.textView_location);
        mAndroidImageView = (ImageView) findViewById(R.id.imageView_android);
        btnlokasi =(Button) findViewById(R.id.lokasi);

        mRotateAnim = (AnimatorSet) AnimatorInflater.loadAnimator(this,R.animator.rotate);
        mRotateAnim.setTarget(mAndroidImageView);

        mLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mTrackingLocation)
                {
                    mulaiTrackingLokasi();
                }
                else
                {
                    stopTrackingLokasi();
                }
                mLocationTextView.setText(getString(R.string.alamat_text,
                        "sedang mencari nama tempat",
                        "sedang mencari alamat",
                        System.currentTimeMillis()));
                mTrackingLocation = true;
                mLocationButton.setText("Stop tracking lokasi");
            }
        });
        btnlokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();// untuk mengekesekusi placepicker
                try{
                    startActivityForResult(builder.build(MainActivity.this), REQUEST_PICK_PLACE);
                }catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e){
                    e.printStackTrace();
                }
            }
        });


        mLocationCallback = new LocationCallback()
        {
            public void onLocationResult(LocationResult locationResult)
            {
                if (mTrackingLocation)
                {
                    new DapatkanAlamatTask(MainActivity.this,
                            MainActivity.this).execute(locationResult.getLastLocation());
                }
            }
        };

        mPlaceDetectionClient = Places.getPlaceDetectionClient(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }else{
//            Log.d("GETPERMISI","getLocation:permissions granded");
            mFusedLocationClient.getLastLocation().addOnSuccessListener(
                    new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location !=null){
                                /*mLastLocation= location;
                                mLocationTextView.setText(
                                        getString(R.string.location_text,
                                                mLastLocation.getLatitude(),
                                                mLastLocation.getLongitude(),
                                                mLastLocation.getTime()));*/
                                new DapatkanAlamatTask(MainActivity.this,
                                        MainActivity.this).execute(location);
                            }else {
                                mLocationTextView.setText("lokasi tidak tersedia");
                            }
                        }
                    }
            );
        }
        mLocationTextView.setText(getString(R.string.alamat_text,
                "sedang mencari alamat", System.currentTimeMillis()));
    }

    private void mulaiTrackingLokasi()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_PERMISSION);
        }
        else
        {
            mFusedLocationClient.requestLocationUpdates(getLocationRequest(),mLocationCallback,null);
            //mLocationTextView.setText(getString(R.string.alamat_text,"Sedang mencari alamat",System.currentTimeMillis()));
            mTrackingLocation=true;
            mLocationButton.setText("Stop Traking Lokasi !");
            mRotateAnim.start();
        }

    }

    private void stopTrackingLokasi()
    {
        if (mTrackingLocation)
        {
            mTrackingLocation=false;
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            mLocationButton.setText("Go Tracking Lokasi !");
            mLocationTextView.setText("Tracking sedang dihetikan");
            mRotateAnim.end();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            // mendapatkan object dr placepicker
            Place place = PlacePicker.getPlace(this,data );
            setTipeLokasi(place);
            mLocationTextView.setText(
                    getString(R.string.alamat_text,
                            place.getName(),
                            place.getAddress(),
                            System.currentTimeMillis())
            );

            NAME = place.getName().toString(); // masukan data data tersebut kedalam statid variabel untuk di saveinstance agar tidak hilang
            ADDRESS = place.getAddress().toString();// masukan data data tersebut kedalam statid variabel untuk di saveinstance agar tidak hilang
            IMG = setTipeLokasi(place);// masukan data data tersebut kedalam statid variabel untuk di saveinstance agar tidak hilang
            mAndroidImageView.setImageResource(IMG);
        } else{
            //mLocationTextView.setText("belum pilih lokasi bebs");
            mLocationTextView.setText("belum pilih lokasi ");

        }
    }

    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[]permissions, @NonNull int[] grantResults){
        switch (requestCode){
            case REQUEST_LOCATION_PERMISSION:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getLocation();
                }else {
                    Toast.makeText(this," permissions bapaknya gak dapet masbro, mesake",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

/*    @Override
    public void onTaskCompleted(String result) {
        if (mTrackingLocation)
        {
            mLocationTextView.setText(getString(R.string.alamat_text,result,System.currentTimeMillis()));
        }

    }*/

    @Override
    public void onTaskCompleted(final String result) throws SecurityException {
        if (mTrackingLocation) {
            Task<PlaceLikelihoodBufferResponse> placeResult =
                    mPlaceDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener(
                    new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                            if(task.isSuccessful()){
                                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                                float maxLikelihood = 0;
                                Place currentPlace = null;
                                //cek tempat yg dihasilkan adalah tmpt yng plg mendekati
                                for (PlaceLikelihood placeLikelihood : likelyPlaces){
                                    if(maxLikelihood < placeLikelihood.getLikelihood()){
                                        maxLikelihood = placeLikelihood.getLikelihood();
                                        currentPlace = placeLikelihood.getPlace();
                                    }
                                }

                                if (currentPlace !=null) {
                                    mLocationTextView.setText(
                                            getString(R.string.alamat_text,
                                                    currentPlace.getName(),
                                                    result, System.currentTimeMillis())
                                    );
                                    setTipeLokasi(currentPlace);

//                                    NAME = placeLikelihood.getPlace().getName().toString();// masukan data data tersebut kedalam statid variabel untuk di saveinstance agar tidak hilang
//                                    ADDRESS = placeLikelihood.getPlace().getAddress().toString();// masukan data data tersebut kedalam statid variabel untuk di saveinstance agar tidak hilang
//                                    IMG = setTipeLokasi(placeLikelihood.getPlace());// masukan data data tersebut kedalam statid variabel untuk di saveinstance agar tidak hilang
//                                    mAndroidImageView.setImageResource(IMG);
                                }
                                    likelyPlaces.release();
                                } else {
                                    mLocationTextView.setText(
                                            getString(R.string.alamat_text,
                                                    "nama lokasi tidak ditemukan!",
                                                    result,
                                                    System.currentTimeMillis())
                                    );
                                }
                            }

                    });
            mPlaceDetectionClient.getCurrentPlace(null);
            // menampilkan alamat
            // mLocationTextView.setText(getString(R.string.alamat_text, result, System.currentTimeMillis()));
        }
    }


    private LocationRequest getLocationRequest()
    {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

//    private void setTipeLokasi(Place currentPlace){
      private int setTipeLokasi(Place currentPlace){
        int drawableID = -1;
        for(Integer placeType : currentPlace.getPlaceTypes()){
            switch (placeType){
                case Place.TYPE_UNIVERSITY:
                    drawableID = R.drawable.school;
                    break;
                case Place.TYPE_CAFE:
                    drawableID = R.drawable.coffee;
                    break;
                case Place.TYPE_SHOPPING_MALL:
                    drawableID = R.drawable.shopping;
                    break;
                case Place.TYPE_BANK:
                    drawableID = R.drawable.bank;
                    break;
                case Place.TYPE_FOOD:
                    drawableID = R.drawable.fork;
                    break;
                case Place.TYPE_MOSQUE:
                    drawableID = R.drawable.mosque;
                    break;
                case Place.TYPE_MOVIE_THEATER:
                    drawableID = R.drawable.movie;
                    break;
            }
        }
        if (drawableID < 0){
            drawableID = R.drawable.unknown;
        }
        mAndroidImageView.setImageResource(drawableID);
          return drawableID;
    }
}

