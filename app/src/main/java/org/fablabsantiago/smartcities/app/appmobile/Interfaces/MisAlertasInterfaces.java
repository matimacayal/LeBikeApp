package org.fablabsantiago.smartcities.app.appmobile.Interfaces;


import org.fablabsantiago.smartcities.app.appmobile.Clases.Alerta;

public class MisAlertasInterfaces
{
    public interface MisAlertasTabListener {
        void onAlertasListClick(Alerta alerta);
    }

    public interface  AlertaDialogListener {
        void onCloseClick();
        void onMostrarMapa(Alerta alerta);
        void onAgregarAlerta(Alerta alerta, String action);
        void onEliminarAlerta(int id);
    }
}
