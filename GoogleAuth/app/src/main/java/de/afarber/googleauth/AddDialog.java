package de.afarber.googleauth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.vk.sdk.VKSdk;

import java.util.Arrays;

import ru.ok.android.sdk.Odnoklassniki;
import ru.ok.android.sdk.util.OkAuthType;
import ru.ok.android.sdk.util.OkScope;

public class AddDialog extends DialogFragment {
    private final static String[] NETWORKS = { "Odnoklassniki", "Vkontakte", "Facebook" };

    private final static String OK_APP_ID = "1251216640";
    private final static String OK_APP_KEY = "CBAOAFJLEBABABABA";
    private final static String OK_REDIRECT_URI = "okauth://ok1251216640";

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
                        case 0:
                            Odnoklassniki.createInstance(getActivity(), OK_APP_ID, OK_APP_KEY);
                            Odnoklassniki.getInstance().requestAuthorization(getActivity(),
                                    OK_REDIRECT_URI,
                                    OkAuthType.ANY,
                                    OkScope.VALUABLE_ACCESS,
                                    OkScope.LONG_ACCESS_TOKEN
                            );
                           break;
                        case 1:
                            VKSdk.login(getActivity());
                            break;
                        case 2:
                            LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("public_profile"));
                            break;
                    }
                }
            });

        return builder.create();
    }
}
