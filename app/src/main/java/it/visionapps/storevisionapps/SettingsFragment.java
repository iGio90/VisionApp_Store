package it.visionapps.storevisionapps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import it.visionapps.storevisionapps.adapters.AppAdapter;
import it.visionapps.storevisionapps.adapters.AppModel;
import it.visionapps.storevisionapps.widgets.SuperRecyclerView;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by iGio90 on 03/01/15.
 */
public class SettingsFragment extends Fragment {

    private MainStoreActivity mActivity;

    private TextView mAccount;
    private FrameLayout mLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (MainStoreActivity) getActivity();
        final SettingsProvider mProvider = App.getInstance().getProvider();

        final View root = inflater.inflate(R.layout.fragment_settings, null);

        mAccount = (TextView) root.findViewById(R.id.account);
        mAccount.setText(mProvider.getPref(SettingsProvider.USER_LOGGED_IN_EMAIL, ""));

        mLogout = (FrameLayout) root.findViewById(R.id.logout);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog mMaterialDialog = new MaterialDialog(mActivity)
                        .setTitle(getString(R.string.logout))
                        .setMessage(getString(R.string.confirm_logout));
                mMaterialDialog.setPositiveButton(getString(android.R.string.ok), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProvider.putPref(SettingsProvider.USER_LOGGED_IN_STATE, false);
                        mProvider.putPref(SettingsProvider.USER_LOGGED_IN_EMAIL, "");
                        mProvider.putPref(SettingsProvider.USER_LOGGED_IN_PASSWORD, "");

                        Intent i = new Intent(mActivity, StartActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }
                });
                mMaterialDialog.setNegativeButton(getString(android.R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });

                mMaterialDialog.show();
            }
        });

        return root;
    }
}