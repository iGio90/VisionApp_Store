package it.visionapps.storevisionapps.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import it.visionapps.storevisionapps.App;
import it.visionapps.storevisionapps.R;

/**
 * Created by iGio90 on 03/01/15.
 */
public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppHolder> {

    private Activity mContext;
    private final ArrayList<AppModel> mItems = new ArrayList<>();

    public AppAdapter(Activity context) {
        mContext = context;
    }

    public void set(ArrayList<AppModel> items) {
        synchronized (mItems) {
            mItems.clear();
            mItems.addAll(items);
            notifyDataSetChanged();
        }
    }

    public AppModel getItem(int position) {
        return mItems.get(position);
    }

    public static class AppHolder extends RecyclerView.ViewHolder {
        View view;
        TextView mTitle;
        ImageView mIcon;

        public AppHolder(View itemView, int viewType) {
            super(itemView);

            view = itemView;

            switch (viewType) {
                default:
                    mTitle = (TextView) view.findViewById(R.id.title);
                    mIcon = (ImageView) view.findViewById(R.id.icon);
                    break;
            }
        }
    }

    @Override
    public AppHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_app, parent, false);
                break;
        }

        return new AppHolder(v, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onBindViewHolder(final AppHolder holder, final int position) {
        if (holder.view.getId() == R.id.row_news_category) {
            final AppModel item = getItem(position);

            holder.mTitle.setText(item.getTitle());
            ImageLoader.getInstance().displayImage(item.getIconUrl(), holder.mIcon, App.getNoFallbackOptions());
        }
    }
}
