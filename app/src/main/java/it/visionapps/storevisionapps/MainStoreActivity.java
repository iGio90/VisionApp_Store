package it.visionapps.storevisionapps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Menu;
import android.view.MenuItem;

import de.madcyph3r.materialnavigationdrawer.MaterialNavigationDrawer;
import de.madcyph3r.materialnavigationdrawer.MaterialNavigationDrawerListener;
import de.madcyph3r.materialnavigationdrawer.item.MaterialHeadItem;
import de.madcyph3r.materialnavigationdrawer.menu.MaterialDevisor;
import de.madcyph3r.materialnavigationdrawer.menu.MaterialMenu;
import de.madcyph3r.materialnavigationdrawer.menu.MaterialSection;
import de.madcyph3r.materialnavigationdrawer.tools.RoundedCornersDrawable;

/**
 * Created by iGio90 on 03/01/15.
 */
public class MainStoreActivity extends MaterialNavigationDrawer implements MaterialNavigationDrawerListener {
    private MaterialNavigationDrawer mDrawer;

    private Handler mProcessHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HandlerThread ht = new HandlerThread("store_handler");
        ht.start();
        mProcessHandler = new Handler(ht.getLooper());
    }

    public Handler getProcessHandler() {
        return mProcessHandler;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mDrawer = this;
        setDrawerDPWidth(300);

        MaterialMenu mMenu = new MaterialMenu();
        MaterialSection mStore = newSection(getString(R.string.store), this.getResources().getDrawable(R.drawable.ic_launcher), new StoreFragment(), false);
        MaterialSection mMyApps = newSection(getString(R.string.my_applications), this.getResources().getDrawable(R.drawable.ic_launcher), new StoreFragment(), false);

        MaterialSection settingsSection = this.newSection("Settings", true);

        mMenu.getSections().add(mStore);
        mMenu.getSections().add(mMyApps);
        mMenu.getSections().add(new MaterialDevisor());
        mMenu.getSections().add(settingsSection);

        final Bitmap mHeadIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        final RoundedCornersDrawable drawableAppIcon = new RoundedCornersDrawable(getResources(), mHeadIcon);

        MaterialHeadItem mHeadItem = new MaterialHeadItem("", "", drawableAppIcon, getResources().getDrawable(R.drawable.ic_launcher), mMenu, 0);
        mHeadItem.setCloseDrawerOnChanged(true);
        addHeadItem(mHeadItem);

        this.setOnChangedListener(this);
    }

    @Override
    public void onBeforeChangedHeadItem(MaterialHeadItem newHeadItem) {

    }

    @Override
    public void onAfterChangedHeadItem(MaterialHeadItem newHeadItem) {

    }
}
