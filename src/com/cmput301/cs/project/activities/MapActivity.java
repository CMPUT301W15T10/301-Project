package com.cmput301.cs.project.activities;

/**
 * Map activity returns a result with a destination packaged in the intent as KEY_DESTINATION
 *
 * It allows a user to select a place on the map or enter the place name in a text box
 */

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.adapters.PlaceAutocompleteAdapter;
import com.cmput301.cs.project.controllers.SettingsController;
import com.cmput301.cs.project.models.Destination;
import com.cmput301.cs.project.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

// Apr 3, 2015 https://github.com/googlesamples/android-play-places/blob/master/PlaceComplete/Application/src/main/java/com/example/google/playservices/placecomplete/MainActivity.java
// Mar 31, 2015 http://developer.android.com/google/auth/api-client.html#Starting
public class MapActivity extends Activity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMapClickListener, GoogleMap.OnCameraChangeListener, GoogleMap.OnMyLocationChangeListener {
    public static final String KEY_DESTINATION = "key_destination";

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    private static final String STATE_RESOLVING_ERROR = "resolving_error";

    // fallback default bounds; will get updated with "my location"
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));
    private static final float ZOOM_PERCENTAGE = 0.8f;  // 80% zoom level
    private AutoCompleteTextView mAddressSearch;

    private Destination mOriginalDestination;

    private GoogleApiClient mGoogleApiClient;
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    private GoogleMap mGoogleMap;
    private PlaceAutocompleteAdapter mAdapter;
    private Destination mHome;

    private final Destination.Builder mBuilder = new Destination.Builder();

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a PlaceAutocomplete object from which we
             read the place ID.
              */
            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);
            final LatLng latLng = place.getLatLng();
            updateWithNameAndLatLng(place.getName(), latLng);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        setContentView(R.layout.map_activity);
        setResult(RESULT_CANCELED);

        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

        buildGoogleApiClient();

        final MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mAdapter = new PlaceAutocompleteAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_GREATER_SYDNEY, null);

        mAddressSearch = (AutoCompleteTextView) findViewById(R.id.address_search);
        mAddressSearch.setAdapter(mAdapter);
        mAddressSearch.setOnItemClickListener(mAutocompleteClickListener);
        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddressSearch.setText("");
            }
        });

        mOriginalDestination = getIntent().getParcelableExtra(KEY_DESTINATION);

        mHome = SettingsController.get(this).loadHomeAsDestination();
        final LatLng location = mHome.getLocation();
        final String name = mHome.getName();
        if (location != null) {
            final View view = findViewById(R.id.home_btn);
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    animateMapTo(location);
                    if (name == null) {
                        setSearchTextWithoutAdapter(getString(R.string.home));
                        updateLatLng(location);
                    } else {
                        updateWithNameAndLatLng(name, location);
                    }
                }
            });
        }
    }

    private void updateWithNameAndLatLng(CharSequence name, LatLng latLng) {
        if (latLng == null) return;
        updateLatLng(latLng);
        if (latLng.equals(mHome.getLocation())) {
            setSearchTextWithoutAdapter(getString(R.string.formated_home, name));
        } else {
            setSearchTextWithoutAdapter(name);
        }
        setResult(RESULT_OK, new Intent()
                .putExtra(KEY_DESTINATION, mBuilder
                        .name(name.toString())
//                        .location(latLng)  already set to the builder by updateLatLng(LatLng)
                        .build()));
    }

    private void updateLatLng(LatLng latLng) {
        if (latLng == null) return;
        mGoogleMap.clear();
        mGoogleMap.addMarker(new MarkerOptions().position(latLng));
        animateMapTo(latLng);
        setResult(RESULT_OK, new Intent()
                .putExtra(KEY_DESTINATION, mBuilder
                        .location(latLng)
                        .build()));
    }

    private void setSearchTextWithoutAdapter(CharSequence charSequence) {
        //noinspection ConstantConditions incorrect NonNull annotation
        mAddressSearch.setAdapter(null);
        mAddressSearch.setText(charSequence);
        mAddressSearch.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .build();
    }

    private void setupActionBar() {
        Utils.setupDiscardDoneBar(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setResult is called in updateWithNameAndLatLng
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;
        map.setMyLocationEnabled(true);
        map.setOnMapClickListener(this);
        map.setOnCameraChangeListener(this);
        if (mOriginalDestination != null) {
            final String name = mOriginalDestination.getName();
            final LatLng location = mOriginalDestination.getLocation();
            updateWithNameAndLatLng(name, location);
        } else {
            map.setOnMyLocationChangeListener(this);
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        updateWithNameAndLatLng(getText(R.string.custom_location), latLng);
    }

    @Override
    public void onMyLocationChange(Location l) {
        mGoogleMap.setOnMyLocationChangeListener(null);
        final LatLng latLng = new LatLng(l.getLatitude(), l.getLongitude());
        updateWithNameAndLatLng(getText(R.string.custom_location), latLng);
    }

    private void animateMapTo(LatLng latLng) {
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                latLng, mGoogleMap.getMaxZoomLevel() * ZOOM_PERCENTAGE));
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        mAdapter.setBounds(mGoogleMap.getProjection().getVisibleRegion().latLngBounds);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Successfully connected to the API client. Pass it to the adapter to enable API access.
        mAdapter.setGoogleApiClient(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // Connection to the API client has been suspended. Disable API access in the client.
        mAdapter.setGoogleApiClient(null);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
        mAdapter.setGoogleApiClient(null);
    }

    // The rest of this code is all about building the error dialog


    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode, this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((MapActivity) getActivity()).onDialogDismissed();
        }
    }
}
