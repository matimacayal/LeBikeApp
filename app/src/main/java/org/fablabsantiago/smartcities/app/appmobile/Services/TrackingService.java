package org.fablabsantiago.smartcities.app.appmobile.Services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.punchthrough.bean.sdk.Bean;
import com.punchthrough.bean.sdk.BeanListener;
import com.punchthrough.bean.sdk.message.BeanError;
import com.punchthrough.bean.sdk.message.Callback;
import com.punchthrough.bean.sdk.message.ScratchBank;

import org.fablabsantiago.smartcities.app.appmobile.R;
import org.fablabsantiago.smartcities.app.appmobile.Utils.DatabaseHandler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TrackingService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{
    public static final String START_TRACK = "org.fablabsantiago.smartcities.app.appmobile.Services.TrackingService.START_TRACK";
    public static final String CONNECT_BLE = "org.fablabsantiago.smartcities.app.appmobile.Services.TrackingService.CONNECT_BLE";
    public static final String BEAN = "org.fablabsantiago.smartcities.app.appmobile.Services.TrackingService.BEAN";

    private String TAG = TrackingService.class.getSimpleName();
    private Context context = this;

    int mStartMode;       // indicates how to behave if the service is killed
    IBinder mBinder;      // interface for clients that bind
    boolean mAllowRebind; // indicates whether onRebind should be used

    private String routeName;
    private int idRuta;
    private int idDestino;
    private int seqNum;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    DatabaseHandler baseDatos;
    private Bean bean;

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate - in");

        // A inicializar cuando comience track o botonera
        routeName = null;
        bean = null;

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        baseDatos = new DatabaseHandler(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand - in");
        String action = intent.getAction();
        switch (action) {
            case START_TRACK:
                startTrack(intent);
                break;
            case CONNECT_BLE:
                connectToBean(intent);
                break;
            default:
                Log.i(TAG, "Not a valid action");
                break;
        }

        return mStartMode;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    @Override
    public void onRebind(Intent intent) {

    }

    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        Log.i(TAG,"onDestroy - in");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        mGoogleApiClient.disconnect();

        //                      numPos, numNeg, duracion, distancia
        baseDatos.endRoute(idRuta, 0, 0, 0, 0);
        Log.i(TAG, "ending route " + idRuta);
    }

    /********** Custom - Non Service - Methods  **********/
    private void startTrack(Intent intent) {
        Log.i(TAG, "startTrack - in");

        //DateFormat date = new SimpleDateFormat("HHmmss:ddMMyyyy");
        //routeName = "origen_destino" + "_" + date.format(new Date()) + "_" + "n";
        //Log.i(TAG,"startTrack - routeName=" + routeName + "(not used)");

        seqNum = 0;
        idDestino = intent.getIntExtra("destino_id", -1);
        idRuta = baseDatos.getLastRutasId() + 1;
        Log.i(TAG,"idDestino: " + String.valueOf(idDestino) + ", idRuta: " + String.valueOf(idRuta));

        String date = (new SimpleDateFormat("dd/MM/yyyy")).format(new Date());
        String hour = (new SimpleDateFormat("HH:mm:ss")).format(new Date());

        //TODO: guardar ruta con nombre correspondiente.

        //                                      nombre, hora, fecha
        baseDatos.startRoute(idRuta, idDestino, "generic_track", hour, date);

        mGoogleApiClient.connect();
    }

    private void connectToBean(Intent intent) {
        Log.i(TAG, "connectToBean - in");

        bean = intent.getParcelableExtra(BEAN);

        //TODO: verify if bean is null.
        //      Is it usefull to do this?

        BeanListener beanListener = new BeanListener()
        {
            @Override
            public void onConnected() {

                Log.i("MainActivity", "connected to '" + bean.getDevice().getName());
                bean.readTemperature(new Callback<Integer>()
                {
                    @Override
                    public void onResult(Integer temp) {
                        Toast.makeText(context, bean.getDevice().getName() + " temp = " + Integer.toString(temp) + "°C", Toast.LENGTH_SHORT).show();
                        Log.i("MainActivity", "onConnected," + bean.getDevice().getName() + "temp = " + Integer.toString(temp) + "°C");
                    }
                });
            }

            @Override
            public void onConnectionFailed() {
                Log.i("MainActivity", "BeanListener.onConnectionFailed");
            }

            @Override
            public void onDisconnected() {
                Log.i("MainActivity", "BeanListener.onDisconnected");
            }

            @Override
            public void onSerialMessageReceived(byte[] data) {
                Log.i("MainActivity", "BeanListener.onSerialMessageReceived");
                //Toast.makeText(context, "serial msg received", Toast.LENGTH_SHORT).show();
                Integer dataLength = data.length;
                if (dataLength != 1) {
                    // ¿Hay que revisar cuando el length puede ser 0?
                    Log.i("MainActivity", "BeanListener message length: " + Integer.toString(dataLength));
                    char[] dataChar = new char[dataLength];
                    //byte b;
                    for (int i = 0; i < dataLength; i++) {
                        dataChar[i] = (char) data[i];
                    }
                    String msg = new String(dataChar);
                    Log.i("MainActivity", "BeanListener message content: " + msg);

                    Toast.makeText(context, "Serial msg: " + msg, Toast.LENGTH_SHORT).show();

                } else {
                    //Pensar que hacer con lo CR LF - \r\n
                }
            }

            @Override
            public void onScratchValueChanged(ScratchBank bank, byte[] value) {
                Log.i("MainActivity", "BeanListener.onScratchValueChanged");
            }

            @Override
            public void onError(BeanError error) {
                Log.i("MainActivity", "BeanListener.onError");
            }

            @Override
            public void onReadRemoteRssi(int rssi) {
                Log.i("MainActivity", "BeanListener.onReadRemoteRssi");
            }
        };

        // Assuming you are in an Activity, use 'this' for the context
        bean.connect(context, beanListener);
    }


    /********** Location Services Methods **********/
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("EnRutaTrackingService","onConnected - in");
        createLocationRequest();
        startLocationUpdates();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        seqNum = baseDatos.getLastSeqNum() + 1;
        //                            tiempo
        baseDatos.addTrackPoint(seqNum, 0, location.getLatitude(), location.getLongitude());

        Log.i(TAG,"onLocationChanged, " + location.toString());
        Toast.makeText(this, location.toString(),Toast.LENGTH_SHORT).show();

        //TODO: implement broadcast for the EnRutaActivity to receive and plot track in real time
    }
}
