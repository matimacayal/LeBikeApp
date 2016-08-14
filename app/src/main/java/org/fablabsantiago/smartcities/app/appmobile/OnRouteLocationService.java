package org.fablabsantiago.smartcities.app.appmobile;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class OnRouteLocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{
    int mStartMode;       // indicates how to behave if the service is killed
    IBinder mBinder;      // interface for clients that bind
    boolean mAllowRebind; // indicates whether onRebind should be used

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;

    private SharedPreferences serviceLocationsSP;
    private Set<String> locationSet = new HashSet<String>();
    private String routeName;

    @Override
    public void onCreate()
    {
        Log.i("OnRouteLocationService","onCreate - in");

        // Hay que hacer otro share preferences superior con registros de las rutas realizadas. Así
        // también se da nombre a estos  nuevos shared preferences... Bah, no, na q ver, no nombre
        // nuevo a nuevos sharedpreferences, pero si a los Set<String> de cada viaje. Todos
        // contenidos en un shared preferences pero condistinto nombre, distintas variables.
        serviceLocationsSP = getSharedPreferences("SERVICE_RETRIEVED_LOCATIONS_SHARED_PREFERENCES",MODE_PRIVATE);
        // Nombre de la variable tipo: origen_destino + fecha|hora + número de viaje
        // e.g.    fablab_casa_20160523-000454_5
        // TODO: origen_destino son obtenidos del intent.
        DateFormat date = new SimpleDateFormat("yyyyMMdd-HHmmss");
        routeName = "origen_destino" + "_" + date.format(new Date()) + "_" + "n";
        Log.i("OnRouteLocationService","onCreate: routeName=" + routeName + "(not used)");

        if (mGoogleApiClient == null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // The service is starting, due to a call to startService()
        Log.i("OnRouteLocationService","onStartCommand - in");
        mGoogleApiClient.connect();
        return mStartMode;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // A client is binding to the service with bindService()
        Log.i("OnRouteLocationService","onBind - in");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        // All clients have unbound with unbindService()
        Log.i("OnRouteLocationService","onUnbind - in");
        return mAllowRebind;
    }
    @Override
    public void onRebind(Intent intent)
    {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
        Log.i("OnRouteLocationService","onRebind - in");
    }
    @Override
    public void onDestroy()
    {
        // The service is no longer used and is being destroyed
        Log.i("OnRouteLocationService","onDestroy - in");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        mGoogleApiClient.disconnect();
    }

    /* |                                                 */
    /* |  Non service methods, location services related */
    /* \/                                                */
    @Override
    public void onConnected(Bundle bundle)
    {
        Log.i("OnRouteLocationService","onConnected - in");
        createLocationRequest();
        startLocationUpdates();
    }

    protected void createLocationRequest()
    {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i)
    {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {

    }

    @Override
    public void onLocationChanged(Location location)
    {
        // TODO: Implement SQLite database for handling all information and service locations
        // 1. Handle the destinations, routes, and etcs
        // 2. Saving the data generated in the service
        // Now we're going to do it with shared preferences and Set<String>

        // No location will never be equal to another because of the "et" field.
        // et: Elapsed time from last boot
        // location.toString() = "Location[fused -33,421216,-70,574286 acc=30 et=+6m17s57ms]"
        locationSet.add(location.toString());
        SharedPreferences.Editor editor = serviceLocationsSP.edit();
        editor.putStringSet("RUTA_SERVICE", locationSet);
        editor.commit();

        Log.i("OnRouteLocationService","onLocationChanged, " + location.toString());
        Toast.makeText(this, location.toString(),Toast.LENGTH_SHORT).show();
    }
}
