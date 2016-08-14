package org.fablabsantiago.smartcities.app.appmobile;


import android.database.Cursor;

public class Ruta {
    /* Fields
     *
     */
    private int    id;
    private int    destid;
    private String name;         // será también el nombre del archivo gtx con los puntos
    private int    numPos;       // # de alertas negativas
    private int    numNeg;       // # de alertas positivas
    private String hora;
    private String fecha;
    private int    duration;     // en segundos
    private int    distancia;    // en metros

    /* Constructors
     *
     */
    public Ruta(Cursor cursor) {
        id        = cursor.getInt(0);
        destid    = cursor.getInt(1);
        name      = cursor.getString(2);
        numPos    = cursor.getInt(3);
        numNeg    = cursor.getInt(4);
        hora      = cursor.getString(5);
        fecha     = cursor.getString(6);
        duration  = cursor.getInt(7);
        distancia = cursor.getInt(8);
    }


    /* Method
     *
     */
    public int getId() {
        return id;
    }
    public int getDestId() {
        return destid;
    }
    public String getName() {
        return name;
    }
    public int getNumPos() {
        return numPos;
    }
    public int getNumNeg() {
        return numNeg;
    }
    public String getHora() {
        return hora;
    }
    public String getFecha() {
        return fecha;
    }
    public int getDuration() {
        return duration;
    }
    public int getDistancia() {
        return distancia;
    }
}
