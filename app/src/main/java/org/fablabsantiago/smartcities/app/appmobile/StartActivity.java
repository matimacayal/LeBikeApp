package org.fablabsantiago.smartcities.app.appmobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class StartActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public void startLeBike(View view)
    {
        Log.i("MainActivity","startLeBike entered");
        Intent leBikeIntent = new Intent(this, LeBikeActivity.class);
        startActivity(leBikeIntent);
    }

    public void startLePatrimonios(View view)
    {
        Log.i("MainActivity","startLePatrimonios entered");
        Intent lePatrimoniosIntent = new Intent(this, LePatrimoniosActivity.class);
        startActivity(lePatrimoniosIntent);
    }
}
