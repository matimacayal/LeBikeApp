package org.fablabsantiago.smartcities.app.appmobile.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.fablabsantiago.smartcities.app.appmobile.R;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity
{
    public static final String USER_NAME = "org.fablabsantiago.smartcities.app.appmobile.UI.LoginActivity.USER_NAME";
    public static final String USER_MAIL = "org.fablabsantiago.smartcities.app.appmobile.UI.LoginActivity.USER_MAIL";
    public static final String USER_AGE = "org.fablabsantiago.smartcities.app.appmobile.UI.LoginActivity.USER_AGE";
    public static final String USER_GENRE = "org.fablabsantiago.smartcities.app.appmobile.UI.LoginActivity.USER_GENRE";

    private String TAG = LoginActivity.class.getSimpleName();
    private Context context = this;

    SharedPreferences leBikePrefs;

    private String genero = null;
    private String user = null;

    private int timesBackPressed = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        leBikePrefs = getSharedPreferences("leBikePreferences", MODE_PRIVATE);
        /*
        String user = leBikePrefs.getString(USER_NAME, null);
        if (user != null) {
            Toast.makeText(this, "Error - usuario existente", Toast.LENGTH_LONG).show();
            finish();

        }
        */

        setContentView(R.layout.activity_login);
    }

    @Override
    public void onStart() {
        super.onStart();
        user = leBikePrefs.getString(USER_NAME, null);
        if (user != null) {
            String mail = leBikePrefs.getString(USER_MAIL, "");
            String edad = leBikePrefs.getString(USER_AGE, "");
            String gen = leBikePrefs.getString(USER_GENRE, "");
            View view = (View) new View(this);
            int id = (gen.equals("female")) ? R.id.generoButtonFemale:R.id.generoButtonMale;
            view.setId(id);

            TextView title = (TextView) findViewById(R.id.loginTitleTextView);
            EditText nombreUsuarioET = (EditText) findViewById(R.id.nombreUsuarioEditText);
            EditText mailET = (EditText) findViewById(R.id.mailEditText);
            EditText edadET = (EditText) findViewById(R.id.edadEditText);
            FrameLayout loginReadyButton = (FrameLayout) findViewById(R.id.loginReadyButton);

            title.setText("TUS DATOS");
            nombreUsuarioET.setText(user);
            mailET.setText(mail);
            edadET.setText(edad);
            onGeneroButtonClick(view);

            loginReadyButton.setVisibility(View.GONE);
        }
    }

    public void onGeneroButtonClick(View view) {
        ImageButton maleButton = (ImageButton) findViewById(R.id.generoButtonMale);
        ImageButton femaleButton = (ImageButton) findViewById(R.id.generoButtonFemale);

        if (view.getId() == R.id.generoButtonMale) {
            genero = "male";
            maleButton.setBackgroundResource(R.drawable.shape_item_dondevas_verde);
            maleButton.setImageResource(R.drawable.ic_gender_male_white_24dp);
            femaleButton.setBackgroundResource(R.drawable.shape_item_dondevas);
            femaleButton.setImageResource(R.drawable.ic_gender_female_black_24dp);
        } else {
            genero = "female";
            maleButton.setBackgroundResource(R.drawable.shape_item_dondevas);
            maleButton.setImageResource(R.drawable.ic_gender_male_black_24dp);
            femaleButton.setBackgroundResource(R.drawable.shape_item_dondevas_verde);
            femaleButton.setImageResource(R.drawable.ic_gender_female_white_24dp);
        }
    }

    public void onLoginReadyButtonClick(View view) {
        EditText nombreUsuarioET = (EditText) findViewById(R.id.nombreUsuarioEditText);
        EditText mailET = (EditText) findViewById(R.id.mailEditText);
        EditText edadET = (EditText) findViewById(R.id.edadEditText);

        final String nombreUsuario = nombreUsuarioET.getText().toString();
        final String mail = mailET.getText().toString();
        final String edad = edadET.getText().toString();

        if (nombreUsuario.length() < 4) {
            Toast.makeText(this, "Nombre de usuario menor a 4 letras", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mail.contains("@")) {
            Toast.makeText(this, "Mail inválido", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mail.indexOf("@") != mail.lastIndexOf("@")) {
            Toast.makeText(this, "Mail inválido", Toast.LENGTH_SHORT).show();
            return;
        }
        if ((Integer.valueOf(edad) > 100) || (Integer.valueOf(edad) < 0)) {
            Toast.makeText(this, "Edad inválida", Toast.LENGTH_SHORT).show();
            return;
        }
        if (genero == null) {
            Toast.makeText(this, "Seleccione género", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = "https://api.thingspeak.com/update?" +
                "api_key=ER0ILPF5X98YZB5N" + "&" +
                "field1=" + nombreUsuario + "&" +
                "field2=" + nombreUsuario + "&" +
                "field3=" + "999999999" + "&" +
                "field4=" + mail + "&" +
                "field5=" + edad + "&" +
                "field6=" + genero;

        Log.i(TAG, "url: " + url);

        // Request a string response from the provided URL.
        StringRequest obreq = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, "response: " + response.toString());

                        SharedPreferences.Editor editor = leBikePrefs.edit();
                        editor.putString(USER_NAME, nombreUsuario);
                        editor.putString(USER_MAIL, mail);
                        editor.putString(USER_AGE, edad);
                        editor.putString(USER_GENRE, genero);
                        editor.apply();

                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Volley Error:" + error.toString());
                Toast.makeText(context, "Error uploading data. ", Toast.LENGTH_SHORT).show();
            }
        });
        /*
        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        */

        requestQueue.add(obreq);
    }

    @Override
    public void onBackPressed(){
        Log.i("EnRutaActivity", "OnBackPressed - in");

        if (user != null) {
            super.onBackPressed();
        } else {
            this.finishAffinity();
        }
    }
}
