package it.visionapps.storevisionapps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalService;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import it.visionapps.storevisionapps.adapters.AppAdapter;
import it.visionapps.storevisionapps.adapters.AppModel;
import it.visionapps.storevisionapps.widgets.SuperRecyclerView;

/**
 * Created by iGio90 on 03/01/15.
 */
public class StoreFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AppAdapter.OnAppClicked {
    private SuperRecyclerView mAppList;
    private GridLayoutManager mAppLayoutManager;
    private AppAdapter mAdapter;
    private MainStoreActivity mActivity;

    // Details
    private boolean inDetails = false;
    private FrameLayout mDetailsContainer;
    private ObservableScrollView mMainScrollView;
    private TextView mTitle;
    private TextView mVersion;
    private TextView mPrice;
    private ImageView mIcon;
    private TextView mChangelog;
    private LinearLayout mScreenshots;
    private TextView mDescription;
    private FloatingActionButton mFab;

    private String mCurrentPack = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (MainStoreActivity) getActivity();

        final View root = inflater.inflate(R.layout.fragment_store, null);

        mAppList = (SuperRecyclerView) root.findViewById(R.id.list);
        mAppLayoutManager = new GridLayoutManager(getActivity(), 1);
        mAppList.setLayoutManager(mAppLayoutManager);

        mAppList.setRefreshListener(this);
        mAppList.setRefreshingColorResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);

        mAdapter = new AppAdapter(mActivity, this);
        mAppList.setAdapter(mAdapter);

        mDetailsContainer = (FrameLayout) root.findViewById(R.id.app_details_fragment);
        mMainScrollView = (ObservableScrollView) root.findViewById(R.id.details_scroll);
        mTitle = (TextView) mDetailsContainer.findViewById(R.id.title);
        mVersion = (TextView) mDetailsContainer.findViewById(R.id.version);
        mPrice = (TextView) mDetailsContainer.findViewById(R.id.price);
        mIcon = (ImageView) mDetailsContainer.findViewById(R.id.icon);
        mChangelog = (TextView) mDetailsContainer.findViewById(R.id.changelog);
        mScreenshots = (LinearLayout) mDetailsContainer.findViewById(R.id.screenshots);
        mDescription = (TextView) mDetailsContainer.findViewById(R.id.description);

        mFab = (FloatingActionButton) mDetailsContainer.findViewById(R.id.fab);
        mFab.attachToScrollView(mMainScrollView);

        fetchApps();

        return root;
    }

    @Override
    public void onRefresh() {
        mActivity.getProcessHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAppList.setRefreshing(false);
                    }
                });
            }
        }, 3 * 1000);

        fetchApps();
    }

    private void fetchApps() {
        ApiHelper.parseApi(ApiHelper.APP_LIST, new ApiHandler() {
            @Override
            public void onSuccess(JSONObject object) {
                final ArrayList<AppModel> models = new ArrayList<>();
                try {
                    JSONArray data = object.getJSONArray("data");
                    for (int i=0;i<data.length();i++) {
                        JSONObject model = data.getJSONObject(i);
                        models.add(new AppModel(
                                model.getString("app_name"),
                                model.getString("icon_url")
                        ));
                    }
                } catch (JSONException ignored) {}

                if (mActivity != null) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.set(models);
                        }
                    });
                }
            }

            @Override
            public void onError(String e) {
            }
        });
    }

    @Override
    public void onAppClicked(AppModel appModel) {
        ApiHelper.parseApi(ApiHelper.APP_DETAILS + "/" + appModel.getTitle() + "/stable", new ApiHandler() {
            @Override
            public void onSuccess(final JSONObject object) {
                if (mActivity != null) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            inDetails = true;

                            try {
                                final JSONObject data = object.getJSONObject("data");
                                final String appName = data.getString("app_name");
                                mTitle.setText(appName);
                                final String appVersion = data.getString("version_name");
                                mVersion.setText(appVersion);

                                final String price = data.getString("app_price");
                                if (price.equals("free") || price.equals("0")) {
                                    mPrice.setText("Free");
                                } else {
                                    mPrice.setText(price + "€");
                                }
                                ImageLoader.getInstance().displayImage(data.getString("icon_url"), mIcon, App.getNoFallbackOptions());
                                mChangelog.setText(data.getString("version_changelog"));
                                mDescription.setText(data.getString("app_description"));
                                final String apkUrl = data.getString("apk_url");

                                mCurrentPack = data.getString("app_packagename");
                                mFab.setImageResource(R.drawable.ic_file_download);
                                mFab.setColorNormal(getResources().getColor(R.color.green_400));
                                mFab.setColorPressed(getResources().getColor(R.color.green_600));
                                mFab.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (mPrice.getText().equals("Free")) {
                                            mActivity.downloadApp(apkUrl, appName, appVersion);
                                        } else {
                                            mActivity.buyItem(price, appName, apkUrl, appName, appVersion);
                                        }
                                    }
                                });

                                checkAppInstalled();
                                checkAppUpdate(mCurrentPack, price, apkUrl, appName, appVersion);

                                JSONArray screenshots = data.getJSONArray("screenshots_urls");
                                mScreenshots.removeAllViews();
                                for (int i=0;i<screenshots.length();i++) {
                                    final ImageView mScreen = new ImageView(mActivity);
                                    ImageLoader.getInstance().displayImage((String) screenshots.get(i), mScreen, App.getNoFallbackOptions(), new ImageLoadingListener() {
                                        @Override
                                        public void onLoadingStarted(String imageUri, View view) {

                                        }

                                        @Override
                                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                                        }

                                        @Override
                                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                            mScreenshots.addView(mScreen);
                                        }

                                        @Override
                                        public void onLoadingCancelled(String imageUri, View view) {

                                        }
                                    });
                                }

                                mDetailsContainer.setVisibility(View.VISIBLE);
                            } catch (JSONException ignored) {
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(String e) {
            }
        });
    }

    private void checkAppInstalled() {
        if (Utils.isAppInstalled(mActivity, mCurrentPack)) {
            mFab.setImageResource(R.drawable.ic_delete);
            mFab.setColorNormal(getResources().getColor(R.color.red_400));
            mFab.setColorPressed(getResources().getColor(R.color.red_600));
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri packageUri = Uri.parse("package:" + mCurrentPack);
                    Intent uninstallIntent =
                            new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
                    startActivity(uninstallIntent);
                }
            });
        }
    }

    private void checkAppUpdate(final String packName, final String version, final String price,
                                final String apkUrl, final String appName) {
        if (Utils.isAppInstalled(mActivity, mCurrentPack)) {
            try {
                PackageInfo pInfo = mActivity.getPackageManager().getPackageInfo(packName, 0);
                if (pInfo.versionName.equals(version)) {
                    mFab.setImageResource(R.drawable.ic_file_download);
                    mFab.setColorNormal(getResources().getColor(R.color.purple_400));
                    mFab.setColorPressed(getResources().getColor(R.color.purple_600));
                    mFab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mPrice.getText().equals("Free")) {
                                mActivity.downloadApp(apkUrl, appName, version);
                            } else {
                                mActivity.buyItem(price, appName, apkUrl, appName, version);
                            }
                        }
                    });
                }
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }
    }

    public void leaveDetails() {
        inDetails = false;
        mDetailsContainer.setVisibility(View.GONE);
        mCurrentPack = "";
    }

    public boolean isInDetails() {
        return inDetails;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (inDetails)
            checkAppInstalled();
    }

    public void setCurrentApp(String appName) {
        for (int i=0;i<mAdapter.getItemCount();i++) {
            if (appName.equals(mAdapter.getItem(i).getTitle())) {
                onAppClicked(mAdapter.getItem(i));
            }
        }
    }
}