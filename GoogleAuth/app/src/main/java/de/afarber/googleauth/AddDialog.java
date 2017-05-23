package de.afarber.googleauth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.facebook.login.LoginManager;

import java.util.Arrays;

public class AddDialog extends DialogFragment {
    private final static String[] NETWORKS = {"Odnoklassniki", "Mail.ru", "Vkontakte", "Facebook"};

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add social network")
            .setItems(NETWORKS, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int selectedIndex) {
                    String network = NETWORKS[selectedIndex];
                    Toast.makeText(getActivity(),
                            "Selected: " + network,
                            Toast.LENGTH_SHORT).show();
                    switch (selectedIndex) {
                        // Odnoklassniki
                        case 0:
                            break;
                        // Mail.ru
                        case 1:
                            break;
                        // Vkontakte
                        case 2:
                            break;
                        //Facebook
                        case 3:
                            LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("public_profile"));
                            break;
                    }
                }
            });

        return builder.create();
    }
}
