package it.visionapps.storevisionapps;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import it.visionapps.storevisionapps.widgets.SuperRecyclerView;

/**
 * Created by iGio90 on 03/01/15.
 */
public class StoreFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SuperRecyclerView mAppList;
    private GridLayoutManager mAppLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_store, null);

        mAppList = (SuperRecyclerView) root.findViewById(R.id.list);
        mAppLayoutManager = new GridLayoutManager(getActivity(), 1);
        mAppList.setLayoutManager(mAppLayoutManager);

        mAppList.setRefreshListener(this);
        mAppList.setRefreshingColorResources(android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_red_light);

        fetchApps();
        return root;
    }

    @Override
    public void onRefresh() {
        fetchApps();
    }

    private void fetchApps() {
        Api.parseApi(Api.APP_LIST, new ApiHandler() {
            @Override
            public void onSuccess(JSONObject object) {
                Log.e("bam", object.toString());
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
}