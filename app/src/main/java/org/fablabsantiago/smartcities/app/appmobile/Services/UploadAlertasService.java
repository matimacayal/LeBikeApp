package org.fablabsantiago.smartcities.app.appmobile.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.fablabsantiago.smartcities.app.appmobile.Clases.Alerta;
import org.fablabsantiago.smartcities.app.appmobile.Utils.DatabaseHandler;
import org.json.JSONObject;

import java.util.List;

public class UploadAlertasService extends Service
{
    public static final String UPLOAD_BY_TRACK = "org.fablabsantiago.smartcities.app.appmobile.Services.UploadAlertasServices.UPLOAD_BY_TRACK";

    public static final String TRACK_ID = "org.fablabsantiago.smartcities.app.appmobile.Services.UploadAlertasServices.TRACK_ID";

    private String TAG = UploadAlertasService.class.getSimpleName();
    private Context context = this;

    int mStartMode;       // indicates how to behave if the service is killed
    IBinder mBinder;      // interface for clients that bind
    boolean mAllowRebind; // indicates whether onRebind should be used

    public List<Alerta> listaAlertas;
    private RequestQueue requestQueue;
    private DatabaseHandler baseDatos;

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate - in");

        listaAlertas = null;
        requestQueue = null;

        baseDatos = new DatabaseHandler(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand - in");
        String action = intent.getAction();

        // TODO: Implement the case for singular alertas.
        switch(action) {
            case UPLOAD_BY_TRACK:
                uploadNewAlertas(intent);
                break;
            default:
                Log.i(TAG, "Not a valid action");
                break;
        }

        return mStartMode;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind - in");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind - in");
        return mAllowRebind;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.i(TAG, "onRebind - in");
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy - in");
    }

    /********** Custom - Non Service - Methods  **********/
    protected void uploadNewAlertas(Intent intent) {
        int rutaId = intent.getIntExtra(TRACK_ID, -1);
        if (rutaId == -1) {
            Log.i(TAG, "No track id specified");
            stopSelf();
            return;
        }

        listaAlertas = baseDatos.getAlertasByIdRuta(rutaId);
        if (listaAlertas.isEmpty()) {
            Log.i(TAG, "No alertas to upload");
            stopSelf();
            return;
        }

        requestQueue = Volley.newRequestQueue(this);

        final Handler h = new Handler();
        final int delay = 15000; //milliseconds

        h.postDelayed(new Runnable(){
            public void run(){
                Log.i(TAG, "Runnable:run - in");
                uploadAlerta();
                if (!listaAlertas.isEmpty()) {
                    h.postDelayed(this, delay);
                } else {
                    Log.i(TAG, "Runnable:run - no more alertas to upload");
                    stopSelf();
                }
            }
        }, 0);
    }

    protected void uploadAlerta() {
        // Se puede revisar tambien si listaAlertas != null pero por ahora esto no puede ocurrir
        if (!listaAlertas.isEmpty()) {
            Alerta alerta = listaAlertas.get(0);

            // Ojo que el servidor guarda solo variables númericas.
            // TODO: ver id's de usuarios. Hasta ahora solo hay un usuario.
            //       Aunque en realidad las tendría que generar un servidor central al iniciar la aplicación.
            //       Podemos usar algún identificador del celular. O correo ingresado.
            String user_id = "1";
            String vote = (alerta.getPosNeg()) ? "1" : "0";
            String id_ruta = Integer.toString(alerta.getIdRuta());
            String tipo_alerta = alerta.getTipoAlerta();
            String version = Integer.toString(alerta.getVersion());
            final String alerta_id = Integer.toString(alerta.getId());

            String fecha_hora = alerta.getFecha() + 'T' + alerta.getHora();
            String lat = Double.toString(alerta.getLat());
            String lon = Double.toString(alerta.getLng());

            String url = "http://api.thingspeak.com/update.json?" +
                    "api_key=HQ4OKJ1ACT9R8WPE&" +
                    "field1=" + user_id + "&" +
                    "field2=" + vote + "&" +
                    "field3=" + id_ruta + "&" +
                    "field4=" + tipo_alerta + "&" +
                    "field5=" + version + "&" +
                    "field6=" + alerta_id + "&" +
                    "created_at=" + fecha_hora + "&" +
                    "lat=" + lat + "&" +
                    "long=" + lon;

            Log.i(TAG, "(Alerta : " + alerta_id + ") http request: " + url);

            JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i(TAG, "(Alerta : " + alerta_id + ") json response: " + response.toString());
                            listaAlertas.remove(0);
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "(Alerta : " + alerta_id + ") Volley Error:" + error.toString());
                            Toast.makeText(context, "Error uploading data. ", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

            requestQueue.add(obreq);
        }
    }
}
