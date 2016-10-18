package org.fablabsantiago.smartcities.app.appmobile;


import android.database.Cursor;
import android.util.Log;

public class Destino
{
    /* Fields
	 *
	 */
    private String nombre;
    private String direccion;
    private int id;
    private Double lat;
    private Double lon;

    /* Constructors
	 *
	 */
    public Destino(String nom, String dir, int iDe) {
        new Destino(nom, dir, iDe, null, null);
    }
    public Destino(String nom, String dir, int iDe, Double la, Double lo) {
        nombre    = nom;
        direccion = dir;
        id        = iDe;
        lat       = la;
        lon       = lo;
    }
    public Destino(Cursor cursor) {
        try {
            nombre    = cursor.getString(0);
            direccion = cursor.getString(1);
            id        = cursor.getInt(2);
            lat       = cursor.getDouble(3);
            lon       = cursor.getDouble(4);
        } catch(Exception e) {
            Log.e("MainActivity", "sqlite destination cursor pase error: " + e.toString());
            throw e;
        }
    }
    /* Methods
	 *
	 */
    public String getName()
    {
        return nombre;
    }
    public String getDirection()
    {
        return direccion;
    }
    public int getId()
    {
        return id;
    }
    public Double getLatitude() {
        return lat;
    }
    public Double getLongitude() {
        return lon;
    }
}
