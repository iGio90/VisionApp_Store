package it.visionapps.storevisionapps;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;
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
public class MyAppFragment extends Fragment implements AppAdapter.OnAppClicked {

    private SuperRecyclerView mAppList;
    private GridLayoutManager mAppLayoutManager;
    private AppAdapter mAdapter;
    private MainStoreActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (MainStoreActivity) getActivity();

        final View root = inflater.inflate(R.layout.fragment_store, null);

        mAppList = (SuperRecyclerView) root.findViewById(R.id.list);
        mAppList.setEmptyView(R.layout.no_app_empty);
        mAppLayoutManager = new GridLayoutManager(getActivity(), 1);
        mAppList.setLayoutManager(mAppLayoutManager);

        mAdapter = new AppAdapter(mActivity, this);
        mAppList.setAdapter(mAdapter);

        fetchApps();

        return root;
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
                        if (!Utils.isAppInstalled(mActivity, model.getString("app_packagename"))) {
                            models.add(new AppModel(
                                    model.getString("app_name"),
                                    model.getString("icon_url")
                            ));
                        }
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
        if (mActivity != null) {
            mActivity.setFragment(mActivity.mStoreFragment, getString(R.string.app_name), true);
            mActivity.setCurrentApp(appModel.getTitle());
        }
    }
}