package com.fervenzagames.apparbitraje.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fervenzagames.apparbitraje.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetalleCombateDialog extends AppCompatDialogFragment {

    private TextView mNombreRojo;
    private TextView mNombreAzul;
    private CircleImageView mFotoRojo;
    private CircleImageView mFotoAzul;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.combate_dialog, null);

        builder.setView(view)
        .setTitle("Detalle Combate")
        .setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        mNombreRojo = view.findViewById(R.id.combate_dialog_nombreRojo);
        mNombreAzul = view.findViewById(R.id.combate_dialog_nombreAzul);
        mFotoRojo = view.findViewById(R.id.combate_dialog_fotoRojo);
        mFotoAzul = view.findViewById(R.id.combate_dialog_fotoAzul);

        return builder.create();
    }
}
