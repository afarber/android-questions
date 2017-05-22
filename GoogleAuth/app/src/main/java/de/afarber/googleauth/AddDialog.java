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
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final String[] networks = {"Odnoklassniki", "Mail.ru", "Vkontakte", "Facebook"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add social network")
                .setItems(networks, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String network = networks[which];
                        Toast.makeText(getActivity(),
                                "Selected: " + network,
                                Toast.LENGTH_SHORT).show();
                        if ("Facebook".equals(network)) {
                            LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("public_profile"));
                        }
                    }
                });

        return builder.create();
    }
}
