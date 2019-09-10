package com.mcal.kotlin;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.BillingProcessor.IBillingHandler;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.material.navigation.NavigationView;
import com.mcal.kotlin.adapters.ListAdapter;
import com.mcal.kotlin.data.ListMode;
import com.mcal.kotlin.data.NightMode;
import com.mcal.kotlin.data.Preferences;
import com.mcal.kotlin.model.BaseActivity;
import com.mcal.kotlin.module.Ads;
import com.mcal.kotlin.module.AppUpdater;
import com.mcal.kotlin.module.Dialogs;
import com.mcal.kotlin.module.ListParser;
import com.mcal.kotlin.utils.Utils;
import com.mcal.kotlin.view.BookmarksFragment;
import com.mcal.kotlin.view.MainView;

import es.dmoral.toasty.Toasty;

import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_USER_CANCELED;
import static com.mcal.kotlin.data.Constants.IS_PREMIUM;
import static com.mcal.kotlin.data.Constants.LK;
import static com.mcal.kotlin.data.Constants.PREMIUM;
import static com.mcal.kotlin.data.Preferences.isOffline;

public class MainActivity extends BaseActivity implements MainView, SearchView.OnQueryTextListener, IBillingHandler, NavigationView.OnNavigationItemSelectedListener {

    private LinearLayout adLayout;
    private BillingProcessor billing;
    private ListAdapter listAdapter;
    private Ads ads;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private boolean isAdsBlocked = false;

    @Override
    public boolean onQueryTextSubmit(String p1) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String p1) {
        listAdapter.getFilter().filter(p1);
        return false;
    }

    @Override
    public void openLesson(String url, int position) {
        if (!isOffline() & !Utils.isNetworkAvailable()) {
            Dialogs.noConnectionError(this);
            return;
        }
        startActivityForResult(new Intent(this, LessonActivity.class)
                .putExtra("url", url)
                .putExtra("position", position), REQUEST_CODE_IS_READ);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        adLayout = findViewById(R.id.adLayout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        RecyclerView lessons = (RecyclerView) getLayoutInflater().inflate(R.layout.recycler_view, null);

        if (ListMode.getCurrentMode().equals(ListMode.Mode.GRID)) {
            lessons.setLayoutManager(new GridLayoutManager(this, 3));
            lessons.setAdapter(listAdapter = new ListParser(this).getListAdapter());
            ((LinearLayout) findViewById(R.id.listContainer)).addView(lessons);
        } else {
            lessons.setLayoutManager(new LinearLayoutManager(this));
            lessons.setAdapter(listAdapter = new ListParser(this).getListAdapter());
            ((LinearLayout) findViewById(R.id.listContainer)).addView(lessons);
        }

        ads = new Ads();
        billing = new BillingProcessor(this, LK, this);
        if (savedInstanceState == null)
            drawerLayout.openDrawer(GravityCompat.START);
        new AppUpdater(this).execute();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.continue_lesson).setVisible(Preferences.getBookmark() != null);
        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        SearchView sv = (SearchView) menu.findItem(R.id.search).getActionView();
        sv.setOnQueryTextListener(this);
        ((MenuBuilder) menu).setOptionalIconsVisible(true);

        if (NightMode.getCurrentMode() == NightMode.Mode.DAY)
            menu.findItem(R.id.day_night).setIcon(R.drawable.ic_night);

        menu.findItem(R.id.search).setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                menu.findItem(R.id.continue_lesson).setVisible(false);
                menu.findItem(R.id.day_night).setVisible(false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                menu.findItem(R.id.continue_lesson).setVisible(true);
                menu.findItem(R.id.day_night).setVisible(true);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.day_night:
                if (NightMode.getCurrentMode() == NightMode.Mode.DAY) {
                    NightMode.setMode(NightMode.Mode.NIGHT);
                    Preferences.setNightMode(true);
                } else {
                    NightMode.setMode(NightMode.Mode.DAY);
                    Preferences.setNightMode(false);
                }
                getDelegate().applyDayNight();
                break;
            case R.id.continue_lesson:
                if (isOffline() || Utils.isNetworkAvailable())
                    resumeLesson();
                else Dialogs.noConnectionError(this);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (time + 2000 > System.currentTimeMillis()) super.onBackPressed();
        else {
            Toasty.info(this, getString(R.string.press_back_once_more)).show();
            time = System.currentTimeMillis();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        billing.handleActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IS_READ) {

            if (resultCode == RESULT_OK) {
                int position = data.getIntExtra("position", 0);

                listAdapter.notifyItemChanged(position);
            }

            if (!Preferences.isRated()) Dialogs.rate(this);
            else if (!billing.isPurchased(PREMIUM)) {
                ads.showInsAd();
            }
        }
    }

    private void resumeLesson() {
        startActivityForResult(new Intent(this, LessonActivity.class).
                putExtra("url", Preferences.getBookmark()), REQUEST_CODE_IS_READ);
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        Toasty.success(this, getString(R.string.p_a)).show();// premium_activated
        navigationView.getMenu().findItem(R.id.b_p).setVisible(false);
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        if (errorCode == BILLING_RESPONSE_RESULT_USER_CANCELED) {
            Toasty.error(this, getString(R.string.purchase_canceled)).show();
            if (isAdsBlocked) System.exit(0);
        }
    }

    @Override
    public void onBillingInitialized() {
        if (!billing.isPurchased(PREMIUM)) {
            adLayout.addView(ads.getBanner(this));
            ads.loadInterstitial(this);
            navigationView.getMenu().findItem(R.id.b_p).setVisible(true);//buy_premium
            if (!billing.isPurchased(PREMIUM) & !ads.isAdsLoading()) {
                isAdsBlocked = true;
                adsBlocked();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.bookmarks:
                new BookmarksFragment().show(getSupportFragmentManager(), null);
                break;
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.settings:
                startActivityForResult(new Intent(this, SettingsActivity.class).putExtra(IS_PREMIUM, billing.isPurchased(PREMIUM)), REQUEST_CODE_SETTINGS);
                break;
            case R.id.exit:
                finish();
                break;
            case R.id.b_p://buy_premium
                billing.purchase(this, PREMIUM);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void adsBlocked() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.ads_blocked)
                .setPositiveButton(R.string.buy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        billing.purchase(MainActivity.this, PREMIUM);
                    }
                })
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                })
                .setCancelable(false)
                .create().show();
    }
}
