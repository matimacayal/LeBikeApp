package org.fablabsantiago.smartcities.app.appmobile;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class MisAlertasActivity extends AppCompatActivity// implements TabLayout.OnTabSelectedListener
{
    private TabLayout tabLayout;
    private ViewPager viewPager;

    DatabaseHandler baseDatos;
    List<Alerta> listaAlertas = new ArrayList<Alerta>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_misalertas);

        //Adding toolbar to the activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Initializing BaseDatos
        baseDatos = new DatabaseHandler(this);
        baseDatos.eraseAlertasTable();

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab().setText("Completas"));
        tabLayout.addTab(tabLayout.newTab().setText("Pendientes"));
        tabLayout.addTab(tabLayout.newTab().setText("Todas"));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        });

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
                tabLayout.setScrollPosition(position, positionOffset, true);
            }

            @Override
            public void onPageSelected(int position)
            {

            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if (listaAlertas.isEmpty()) {
            listaAlertas = baseDatos.getAlertas();

            if (listaAlertas.isEmpty()) {
                baseDatos.newAlerta(new Alerta(2010, false, (float) 0.0, (float) 0.0, "auto", null, null,
                                                "Cruce de autos imprudentes",
                                                "Casi salgo volando por un auto que se precipito con mi dedo chico",
                                                3010, 0, "completa"));
                baseDatos.newAlerta(new Alerta(2011, true, (float) 0.0, (float) 0.0, "vias", null, null,
                                                "Nueva ciclovia",
                                                "esta super choriflai me encanta para venir cno mis amigos de la prepa",
                                                3010, 0, "pendiente"));
                baseDatos.newAlerta(new Alerta(2012, false, (float) 0.0, (float) 0.0, "peat", null, null,
                                                "Peaton qlo",
                                                "Que wea se cree el zarpao se te cruza y na así nomá",
                                                3010, 0, "pendiente"));
            }
        }

        // TODO: Ahora cada fragment hace acceso a la base de datos y carga las alertas
        // correspondientes de manera independiente. El siguiente paso es hacer que la actividad
        // cargue la data de la BD y la entregue a los fragments que la desplegarán en listas.
        MisAlertasPagerAdapter adapter = new MisAlertasPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }
}