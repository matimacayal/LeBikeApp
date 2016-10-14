package org.fablabsantiago.smartcities.app.appmobile;


public class MisAlertasInterfaces
{
    public interface MisAlertasTabListener {
        void onAlertasListClick(Alerta alerta);
    }

    public interface  AlertaDialogListener {
        void onCloseClick();
        void onMostrarMapa();
        void onAgregarAlerta(Alerta alerta, String action);
        void onEliminarAlerta(int id);
    }
}
