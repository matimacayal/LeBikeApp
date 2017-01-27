package org.fablabsantiago.smartcities.app.appmobile.UI.Fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.fablabsantiago.smartcities.app.appmobile.R;

public class DestinoEditDialog extends DialogFragment
{
    private Bundle destinoInfo;
    private String destinoName;
    private String destinoDirection;
    private int destinoId;
    private Float destinoLat;
    private Float destinoLon;

    private DialogListener dialogListener;

    public DestinoEditDialog () {

    }

    public DestinoEditDialog newInstance() {
        DestinoEditDialog fragDialog = new DestinoEditDialog();
        return fragDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        destinoInfo = getArguments();
        return inflater.inflate(R.layout.fragmentdialog_edit_destino, container, false);
    }

    //TODO: verificar que los campos sean números o letras respectivamente, tambien setear teclado especial para números.

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText nombre = (EditText) view.findViewById(R.id.edit_destino_dialog_nombre_edittext);
        final EditText direccion = (EditText) view.findViewById(R.id.edit_destino_dialog_direccion_edittext);
        final EditText latitude = (EditText) view.findViewById(R.id.edit_destino_dialog_latitude_edittext);
        final EditText longitude = (EditText) view.findViewById(R.id.edit_destino_dialog_longitude_edittext);

        if (destinoInfo != null) {
            Log.i("DestinoEditDialog", "Editando: " + destinoInfo.getString("name") + ", id:" + destinoInfo.get("id"));
            nombre.setText(destinoInfo.getString("name"));
            direccion.setText(destinoInfo.getString("direction"));
            destinoId = destinoInfo.getInt("id");
            latitude.setText(String.valueOf(destinoInfo.getDouble("latitude")));
            longitude.setText(String.valueOf(destinoInfo.getDouble("longitude")));
        } else {
            destinoId = -1;
        }

        Button close = (Button) view.findViewById(R.id.edit_destino_dialog_close_button);
        close.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Log.i("DestinoEditDialog","'close' pressed");
                dialogListener.onCloseClick();
            }
        });

        TextView guardar = (TextView) view.findViewById(R.id.edit_destino_dialog_guardar_button);
        guardar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Log.i("DestinoEditDialog","'guardar' pressed");

                String nom = nombre.getText().toString();
                String dir = direccion.getText().toString();
                String lat = latitude.getText().toString();
                String lon = longitude.getText().toString();

                if (nom.isEmpty() || dir.isEmpty() || lat.isEmpty() || lon.isEmpty()) {
                    dialogListener.showToast("There's an empty field");
                    return;
                }

                dialogListener.onGuardarClick(
                        nom,
                        dir,
                        destinoId,
                        Double.valueOf(lat),
                        Double.valueOf(lon));
            }
        });

        TextView eliminar = (TextView) view.findViewById(R.id.edit_destino_dialog_eliminar_button);
        eliminar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Log.i("DestinoEditDialog","'eliminar' pressed");
                dialogListener.onEliminarClick(destinoInfo.getInt("id"));
            }
        });
    }


    public interface DialogListener {
        void onCloseClick();
        void onEliminarClick(int id);
        void onGuardarClick(String name, String dir, int ide, Double lat, Double lon);
        void showToast(String text);
    }

    public void setDialogListener(DialogListener dialogListener) {
        this.dialogListener = dialogListener;
    }
}
