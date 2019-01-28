package com.example.mahesh.alarmlocator;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import com.example.mahesh.alarmlocator.adapter.PlaceArrayAdapter;
import com.example.mahesh.alarmlocator.model.MapAdressModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener,ResultCallback<LocationSettingsResult> {

    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(new LatLng(32.6393, -117.004304),
            new LatLng(44.901184, -67.32254));
    ProgressBar progressBar;
    Bitmap icon;
    Toolbar toolbar;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private AutoCompleteTextView addresstext;
    private String myaddress;
    private Marker marker;
    private LocationRequest locationRequest;
    private MapAdressModel mapAdressModel;
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e("TAG", "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }

            // Selecting the first object buffer.
            final Place place = places.get(0);
            getAddress(place.getLatLng());
            // getplacedetail(place);
            CharSequence attributions = places.getAttributions();
            myaddress = place.getAddress().toString();
            addresstext.setText(Html.fromHtml(place.getAddress() + ""));
            if (myaddress != null) {
                if (mapAdressModel == null)
                    mapAdressModel = new MapAdressModel();
                mapAdressModel.setAddress(myaddress);
                mapAdressModel.setLatitude("" + place.getLatLng().latitude);
                mapAdressModel.setZip("");
                mapAdressModel.setLongitude("" + place.getLatLng().longitude);
                mMap.clear();
                addresstext.dismissDropDown();
                marker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).icon(BitmapDescriptorFactory.fromBitmap(icon)));

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
                //mapAdressModel.setZip();
            }
//            boolean status = Contains(place.getLatLng());
//            if (!status) {
//                address.setText(null);
//                Toast.makeText(getActivity(), "Current address is not valid", Toast.LENGTH_SHORT).show();
//            }


        }
    };
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i("TAG", "Selected: " + item.description);
            //Myutils.hideKeyBord(MapsActivity.this);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Log.i("TAG", "Fetching details for ID: " + item.placeId);

        }
    };
    private int REQUEST_CHECK_SETTINGS=100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        toolbar=findViewById(R.id.toolbar);
        progressBar=findViewById(R.id.progress_bar);
        icon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher_round);
        if (getIntent().hasExtra("address_model")) {
            mapAdressModel = getIntent().getParcelableExtra("address_model");
        } else {
            mapAdressModel = new MapAdressModel();
        }
        buildGoogleApiClient();
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        mGoogleApiClient.connect();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        addresstext = (AutoCompleteTextView) findViewById(R.id.address);
        addresstext.setOnItemClickListener(mAutocompleteClickListener);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.done_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done: {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelable("address_model", mapAdressModel);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                //this.overridePendingTransition(R.anim.no_animation, R.anim.enter_from_right);
                finish();
            }
            break;
            case android.R.id.home: {
                onBackPressed();
            }
            default:
                return super.onOptionsItemSelected(item);

        }
        return false;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API).build();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        } else
            mMap.setMyLocationEnabled(true);

        // Add a marker in Sydney and move the camera

        if (mapAdressModel != null && mapAdressModel.getLatitude() != null && mapAdressModel.getLongitude() != null) {
            LatLng latLng = new LatLng(Double.valueOf(mapAdressModel.getLatitude()), Double.valueOf(mapAdressModel.getLongitude()));
            //marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(icon)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }


        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                LatLng mCenterLatLong = cameraPosition.target;
                mMap.clear();
                // marker = mMap.addMarker(new MarkerOptions().position(mCenterLatLong).icon(BitmapDescriptorFactory.fromBitmap(icon)));
                getAddress(mCenterLatLong);
                Log.v("position", "" + mCenterLatLong.latitude + mCenterLatLong.longitude);
            }
        });


    }

    private void getAddress(LatLng mCenterLatLong) {
        if (ConnectionUtils.isInternetConnected(this)) {
            if (mCenterLatLong != null) {
                String apiRequest = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + mCenterLatLong.latitude + "," + mCenterLatLong.longitude + "&ka&sensor=false";
                StartGetAdressApi(apiRequest, mCenterLatLong);
            }
        } else {
            // ShowSnackbar(getString(R.string.connection_error));
        }
    }

    private void getplacedetail(Place place) {
        if (ConnectionUtils.isInternetConnected(this)) {
            if (place != null) {
                String apiRequest = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + place.getId() + "&key=" + getResources().getString(R.string.google_maps_key);
                StartGetAdressApi(apiRequest, place.getLatLng());
            }
        } else {
            // ShowSnackbar(getString(R.string.connection_error));
        }
    }

    public void StartGetAdressApi(String Url, final LatLng mCenterLatLong) {
        progressBar.setVisibility(View.VISIBLE);
        Call<ResponseBody> call2 = RestApi.getInstance().GetAdressApi(Url);
        call2.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    // Log.e("Response is", "" + response.code());
                    if (response.isSuccessful()) {
                        // txt.setText(response.body().string());
                        Log.e("Response is", "" + response);
                        mapAdressModel.setLatitude("" + mCenterLatLong.latitude);
                        mapAdressModel.setLongitude("" + mCenterLatLong.longitude);
                        getCityAddress(new JSONObject(response.body().string()));
                        // System.out.println(response.body().string());
                    } else {

                    }

                } catch (Exception e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("failure is", "" + "");
            }
        });
    }

    void getCityAddress(JSONObject result) {
        Log.v("adressss", "" + result);
        if (result.has("results")) {
            try {
                JSONArray array = result.getJSONArray("results");
                String address = "";
                if (array.length() > 0) {
                    JSONObject place = array.getJSONObject(0);
                    JSONArray components = place.getJSONArray("address_components");
                    for (int i = 0; i < components.length(); i++) {
                        JSONObject component = components.getJSONObject(i);
                        JSONArray types = component.getJSONArray("types");

                        for (int j = 0; j < types.length(); j++) {
                            if (types.getString(j).equals("locality")) {
                                address = address + place.getString("formatted_address");
                                addresstext.setText(address);
                                mapAdressModel.setAddress(address);
                            } else if (types.getString(j).equals("postal_code")) {
                                mapAdressModel.setZip(component.getString("short_name"));

                            }
                        }
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
                progressBar.setVisibility(View.GONE);
            }
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                .addLocationRequest(locationRequest);
//        builder.setAlwaysShow(true);
//        PendingResult<LocationSettingsResult> result =
//                LocationServices.SettingsApi.checkLocationSettings(
//                        mGoogleApiClient,
//                        builder.build()
//                );

       // result.setResultCallback(this);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                null, null);
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        addresstext.setAdapter(mPlaceArrayAdapter);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }


    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:

                // NO need to show the dialog;

                break;

            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                //  Location settings are not satisfied. Show the user a dialog

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);

                } catch (IntentSender.SendIntentException e) {

                    //failed to show
                }
                break;

            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are unavailable so not possible to show any dialog now
                break;
        }
    }
}
