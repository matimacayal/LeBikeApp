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

    /* Constructors
	 *
	 */
    public Destino(String nom, String dir, int iDe) {
        nombre    = nom;
        direccion = dir;
        id        = iDe;
    }
    public Destino(Cursor cursor) {
        try {
            nombre    = cursor.getString(0);
            direccion = cursor.getString(1);
            id        = cursor.getInt(2);
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
}
