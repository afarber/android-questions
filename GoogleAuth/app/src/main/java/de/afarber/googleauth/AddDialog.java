package de.afarber.googleauth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

public class AddDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String[] networks = {"Odnoklassniki", "Mail.ru", "Vkontakte", "Facebook"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add social network")
                .setItems(networks, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(),
                                "Selected: " + networks[which],
                                Toast.LENGTH_SHORT).show();
                    }
                });

        return builder.create();
    }
}
