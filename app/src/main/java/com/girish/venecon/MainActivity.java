package com.girish.venecon;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.android.vending.billing.IInAppBillingService;
import com.girish.venecon.utils.Constants;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

import static com.girish.venecon.Utils.mContext;
//import com.google.firebase.analytics.FirebaseAnalytics;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //private FirebaseAnalytics mFirebaseAnalytics;
    IInAppBillingService mService;

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
//            queryItems();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

       // mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mContext = getApplicationContext();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initializeInAppPurchases();
        changeToFragment(new FXFragment(), mContext.getString(R.string.foreign_exchange));
        MobileAds.initialize(this, Constants.ADMOB_APP_ID);

    }

    private void queryItems() {
        ArrayList<String> skuList = new ArrayList<String>();
//        skuList.add("premium_upgrade");
//        skuList.add("gas");
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
        try {
            Bundle skuDetails = mService.getSkuDetails(3,
                    getPackageName(), "inapp", querySkus);
            Log.i("sm", "sad");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void initializeInAppPurchases() {
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        getApplicationContext().bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_first_layout) {
            changeToFragment(new FXFragment(), mContext.getString(R.string.foreign_exchange));

        } else if (id == R.id.calculator_layout) {
            changeToFragment(new CalculatorFragment(), mContext.getString(R.string.calculator));

        } else if (id == R.id.nav_second_layout) {
            changeToFragment(new ReservesFragment(), mContext.getString(R.string.foreign_reserves));
        }
//        } else if (id == R.id.bitcoin) {
//            changeToFragment(new BitcoinFragment(), "Bitcoin");
//        }
        else if (id == R.id.m2) {
            changeToFragment(new M2Fragment(), mContext.getString(R.string.money_supply));
        }

        else if (id == R.id.oil) {
            changeToFragment(new OilFragment(), mContext.getString(R.string.oil_prices));
        }

//        else if (id == R.id.inflation) {
//            changeToFragment(new InflationFragment(), mContext.getString(R.string.inflation));
//        }

        else if (id == R.id.mw) {
            changeToFragment(new MinWageFragment(), mContext.getString(R.string.minimum_wage));
        }

        else if (id == R.id.crude_production) {
            changeToFragment(new CrudeProductionFragment(), mContext.getString(R.string.crude_production));
        }

        else if (id == R.id.us_oil) {
            changeToFragment(new USOilFragment(), mContext.getString(R.string.us_oil));
        }

        else if (id == R.id.about) {
            changeToFragment(new AboutFragment(), mContext.getString(R.string.about));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeToFragment(Fragment fragment, String title)
    {
        ActionBar toolbar = getSupportActionBar();
        if(toolbar != null) {
            toolbar.setTitle(title);
        }
        FragmentManager fragmentManager = getFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.content_frame
                        , fragment)
                .commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConn);
        }
    }
}



// Did this to include Shinobi Charts 20161010 https://www.shinobicontrols.com/docs/ShinobiControls/ShinobiChartsAndroid/1.9.0/Standard/Normal/user-guide/import-library-android-studio.html