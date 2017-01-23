package org.fablabsantiago.smartcities.app.appmobile.UI.Fragments;


import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fablabsantiago.smartcities.app.appmobile.Clases.Alerta;
import org.fablabsantiago.smartcities.app.appmobile.Interfaces.MisAlertasInterfaces.AlertaDialogListener;
import org.fablabsantiago.smartcities.app.appmobile.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AlertaEditDialog extends DialogFragment
{
    Alerta alerta;
    Bundle alertaInfo;

    TextView fecha;
    TextView hora;
    ImageView positiva;
    ImageView negativa;
    EditText titulo;
    EditText descripcion;
    ImageView ciclovia;
    ImageView vias;
    ImageView espacios;
    ImageView mantencion;
    ImageView automoviles;
    ImageView otros;
    ImageView peaton;
    List<ImageView> tiposAlerta;


    private AlertaDialogListener alertaDialogListener;

    public AlertaEditDialog () {
    }

    public AlertaEditDialog newInstance(Alerta alerta) {
        AlertaEditDialog alertaEditDialog = new AlertaEditDialog();
        Bundle bundle = alerta.toBundle();
        alertaEditDialog.setArguments(bundle);
        return alertaEditDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Si se está editando una alerta existente, esta viene dentro de los argumentos, y se pasa a Alerta
        // Si no, se inicializa una alerta null.
        alertaInfo = getArguments();
        if (alertaInfo != null) {
            String action = alertaInfo.getString("NEW_ALERTA_ACTION");
            if (action.equals("EDIT_ALERTA")) {
                alertaInfo.remove("NEW_ALERTA_ACTION");
                alerta = new Alerta(alertaInfo);
            } else if (action.equals("NEW_ALERTA_FROM_MAP")) {
                alerta = null;
            }
        }

        return inflater.inflate(R.layout.fragmentdialog_edit_alerta, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button cerrar = (Button) view.findViewById(R.id.edit_alerta_dialog_close_button);
        fecha = (TextView) view.findViewById(R.id.edit_alerta_dialog_fecha);
        hora = (TextView) view.findViewById(R.id.edit_alerta_dialog_hora);
        positiva = (ImageView) view.findViewById(R.id.edit_alerta_dialog_alerta_positiva);
        negativa = (ImageView) view.findViewById(R.id.edit_alerta_dialog_alerta_negativa);
        titulo = (EditText) view.findViewById(R.id.edit_alerta_dialog_titulo);
        descripcion = (EditText) view.findViewById(R.id.edit_alerta_dialog_descripcion);
        ciclovia = (ImageView) view.findViewById(R.id.edit_alerta_dialog_ciclovias);
        vias = (ImageView) view.findViewById(R.id.edit_alerta_dialog_vias);
        espacios = (ImageView) view.findViewById(R.id.edit_alerta_dialog_espacios);
        mantencion = (ImageView) view.findViewById(R.id.edit_alerta_dialog_mantencion);
        automoviles = (ImageView) view.findViewById(R.id.edit_alerta_dialog_automoviles);
        otros = (ImageView) view.findViewById(R.id.edit_alerta_dialog_otros);
        peaton = (ImageView) view.findViewById(R.id.edit_alerta_dialog_peaton);
        LinearLayout mostrarMapa = (LinearLayout) view.findViewById(R.id.edit_alerta_dialog_mostrar_napa);
        ImageView camara = (ImageView) view.findViewById(R.id.edit_alerta_dialog_camera);
        TextView agregar = (TextView) view.findViewById(R.id.edit_alerta_dialog_agregar);
        TextView eliminar = (TextView) view.findViewById(R.id.edit_alerta_dialog_eliminar);

        tiposAlerta = new ArrayList<ImageView>(Arrays.asList(
                ciclovia, vias,
                espacios, mantencion,
                automoviles, otros,
                peaton));

        if (alerta != null) {
            fecha.setText(alerta.getFecha());
            hora.setText(alerta.getHora());
            if (alerta.getPosNeg()) {
                positiva.setAlpha((float) 1.0);
            } else {
                negativa.setAlpha((float) 1.0);
            }
            titulo.setText(alerta.getTitulo());
            descripcion.setText(alerta.getDescrption());
            switch (alerta.getTipoAlerta()) {
                case "cicl": ciclovia.setAlpha((float) 1.0);    break;
                case "vias": vias.setAlpha((float) 1.0);        break;
                case "vege": espacios.setAlpha((float) 1.0);    break;
                case "mant": mantencion.setAlpha((float) 1.0);  break;
                case "auto": automoviles.setAlpha((float) 1.0); break;
                case "peat": peaton.setAlpha((float) 1.0);      break;
                case "otro": otros.setAlpha((float) 1.0);       break;
                default:
                    break;
            }
        } else {
            DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat hour = new SimpleDateFormat("HH:mm:ss");
            String dateText =  date.format(new Date());
            String hourText = hour.format(new Date());
            fecha.setText(dateText);
            hora.setText(hourText);

            mostrarMapa.setEnabled(false);
            mostrarMapa.setAlpha((float) 0.3);
        }


        positiva.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                positiva.setAlpha((float) 1.0);
                negativa.setAlpha((float) 0.4);
            }
        });
        negativa.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                positiva.setAlpha((float) 0.4);
                negativa.setAlpha((float) 1.0);
            }
        });

        View.OnClickListener tipoAlertaOnClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                for (ImageView vista : tiposAlerta) {
                    vista.setAlpha((float) 0.4);
                }
                v.setAlpha((float) 1.0);
            }
        };
        for (ImageView tipo : tiposAlerta) {
            tipo.setOnClickListener(tipoAlertaOnClickListener);
        }


        cerrar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                alertaDialogListener.onCloseClick();
            }
        });

        mostrarMapa.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                alertaDialogListener.onMostrarMapa();
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                int id;
                if (alerta != null) {
                    id = alerta.getId();
                } else {
                    id = -1;
                }
                alertaDialogListener.onEliminarAlerta(id);
            }
        });

        agregar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                String hora2 = hora.getText().toString();
                String fecha2 = fecha.getText().toString();
                String titulo2 = titulo.getText().toString();
                String descripcion2 = descripcion.getText().toString();
                String tipo2 = "";
                ImageView tipoSelec;
                for (ImageView tipo : tiposAlerta) {
                    if (tipo.getAlpha() == (float) 1.0) {
                        switch (tipo.getId()) {
                            case R.id.edit_alerta_dialog_ciclovias:
                                tipo2 = "cicl";
                                break;
                            case R.id.edit_alerta_dialog_vias:
                                tipo2 = "vias";
                                break;
                            case R.id.edit_alerta_dialog_mantencion:
                                tipo2 = "mant";
                                break;
                            case R.id.edit_alerta_dialog_automoviles:
                                tipo2 = "auto";
                                break;
                            case R.id.edit_alerta_dialog_peaton:
                                tipo2 = "peat";
                                break;
                            case R.id.edit_alerta_dialog_otros:
                                tipo2 = "otro";
                                break;
                            default:
                                tipo2 = "";
                                break;
                        }
                    }
                }
                Boolean posneg2 = (positiva.getAlpha() == (float) 1.0);

                String action;
                int id2;
                double lat2;
                double lon2;
                int idRuta2;
                int ver2;
                if (alerta != null) {
                    id2 = alerta.getId();
                    lat2 = alerta.getLat();
                    lon2 = alerta.getLng();
                    idRuta2 = alerta.getIdRuta();
                    ver2 = alerta.getVersion() + 1;
                    action = "UPDATE_ALERTA";
                } else {
                    id2 = alertaInfo.getInt("NEW_ALERTA_ID");
                    lat2 = alertaInfo.getDouble("NEW_ALERTA_LAT");
                    lon2 = alertaInfo.getDouble("NEW_ALERTA_LON");
                    idRuta2 = alertaInfo.getInt("NEW_ALERTA_IDRUTA");
                    ver2 = 1;
                    action = "NEW_ALERTA";
                }

                Alerta newAlerta = new Alerta(
                        id2,
                        posneg2,
                        lat2,
                        lon2,
                        tipo2,
                        hora2,
                        fecha2,
                        titulo2,
                        descripcion2,
                        idRuta2,
                        ver2,
                        "");
                newAlerta.setEstado(newAlerta.isComplete());

                alertaDialogListener.onAgregarAlerta(newAlerta, action);
            }
        });
    }


    public void setAlertasDialogListener(AlertaDialogListener alertaDialogListener) {
        this.alertaDialogListener = alertaDialogListener;
    }
}
