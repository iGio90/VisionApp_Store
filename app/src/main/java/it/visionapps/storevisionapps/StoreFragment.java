package it.visionapps.storevisionapps;

import android.graphics.Bitmap;
import android.os.Bundle;
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
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private LinearLayout mDetailsContainer;
    private TextView mTitle;
    private TextView mVersion;
    private ImageView mIcon;
    private TextView mChangelog;
    private LinearLayout mScreenshots;

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

        mDetailsContainer = (LinearLayout) root.findViewById(R.id.app_details_fragment);
        mTitle = (TextView) mDetailsContainer.findViewById(R.id.title);
        mVersion = (TextView) mDetailsContainer.findViewById(R.id.version);
        mIcon = (ImageView) mDetailsContainer.findViewById(R.id.icon);
        mChangelog = (TextView) mDetailsContainer.findViewById(R.id.changelog);
        mScreenshots = (LinearLayout) mDetailsContainer.findViewById(R.id.screenshots);

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
                                JSONObject data = object.getJSONObject("data");
                                mTitle.setText(data.getString("app_name"));
                                mVersion.setText(data.getString("version_name"));
                                ImageLoader.getInstance().displayImage(data.getString("icon_url"), mIcon, App.getNoFallbackOptions());
                                mChangelog.setText(data.getString("version_changelog"));
                                mDetailsContainer.setVisibility(View.VISIBLE);

                                JSONArray screenshots = data.getJSONArray("screenshots_urls");
                                Log.e("bam", "size :" + screenshots.length());
                                for (int i=0;i<screenshots.length();i++) {
                                    Log.e("bam", (String) screenshots.get(i));
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
                            } catch (JSONException ignored) {
                                Log.e("bam", ignored.toString());
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

    public void leaveDetails() {
        inDetails = false;
        mDetailsContainer.setVisibility(View.GONE);
    }

    public boolean isInDetails() {
        return inDetails;
    }
}