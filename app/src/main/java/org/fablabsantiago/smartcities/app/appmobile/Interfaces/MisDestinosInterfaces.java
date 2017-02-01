package org.fablabsantiago.smartcities.app.appmobile.Interfaces;


public class MisDestinosInterfaces
{
    public interface DestinoDialogListener {
        void onCloseClick();
        void onEliminarClick(int id);
        void onGuardarClick(String name, String dir, int ide, Double lat, Double lon);
        void showToast(String text);
    }
}
