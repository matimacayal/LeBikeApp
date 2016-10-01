package org.fablabsantiago.smartcities.app.appmobile;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class DestinoEditDialog extends DialogFragment
{
    public DestinoEditDialog () {

    }

    public DestinoEditDialog newInstance() {
        DestinoEditDialog fragDialog = new DestinoEditDialog();
        return fragDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_edit_destino, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button close = (Button) view.findViewById(R.id.edit_destino_dialog_close_button);
        close.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Log.i("DestinoEditDialog","'close' pressed");
                getFragmentManager().popBackStack();
            }
        });
    }
}
