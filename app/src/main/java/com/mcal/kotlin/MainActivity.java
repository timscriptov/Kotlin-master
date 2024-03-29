package com.mcal.kotlin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.BillingProcessor.IBillingHandler;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputLayout;
import com.mcal.kotlin.adapters.ListAdapter;
import com.mcal.kotlin.data.ListMode;
import com.mcal.kotlin.data.NightMode;
import com.mcal.kotlin.data.Preferences;
import com.mcal.kotlin.model.BaseActivity;
import com.mcal.kotlin.module.AppUpdater;
import com.mcal.kotlin.module.Dialogs;
import com.mcal.kotlin.module.ListParser;
import com.mcal.kotlin.utils.Utils;
import com.mcal.kotlin.view.BookmarksFragment;
import com.mcal.kotlin.view.MainView;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import ru.svolf.melissa.MainMenuAdapter;
import ru.svolf.melissa.MainMenuItem;
import ru.svolf.melissa.MainMenuItems;
import ru.svolf.melissa.sheet.SweetContentDialog;

import static com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_USER_CANCELED;
import static com.mcal.kotlin.data.Constants.LK;
import static com.mcal.kotlin.data.Constants.PREMIUM;
import static com.mcal.kotlin.data.Preferences.isOffline;

public class MainActivity extends BaseActivity implements MainView, SearchView.OnQueryTextListener, IBillingHandler {
    private BillingProcessor billing;
    private ListAdapter listAdapter;
    private BottomSheetBehavior sheetBehavior;
    private boolean isPremium;
    private SearchView sv;

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

        sheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomView));
        //adLayout = findViewById(R.id.adLayout);
        sv = findViewById(R.id.search_bar);

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

        setupSearchView();
        setupBottomSheet();

        billing = new BillingProcessor(this, LK, this);
        if (savedInstanceState == null)
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        new AppUpdater(this).execute();

        isPremium = getIntent().getBooleanExtra("isPremium", false);
        /*if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Settings.ACTION_MANAGE_OVERLAY_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Settings.ACTION_MANAGE_OVERLAY_PERMISSION}, 1);
            }
        }*/
    }

    @Override
    public void onBackPressed() {
        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            if (time + 2000 > System.currentTimeMillis()) super.onBackPressed();
            else {
                Toasty.info(this, getString(R.string.press_back_once_more)).show();
                time = System.currentTimeMillis();
            }
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
        }
    }

    private void resumeLesson() {
        startActivityForResult(new Intent(this, LessonActivity.class).
                putExtra("url", Preferences.getBookmark()), REQUEST_CODE_IS_READ);
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        Toasty.success(this, getString(R.string.p_a)).show();// premium_activated
        // FIXME: Рефрешнуть адаптер
        setupBottomSheet();
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        if (errorCode == BILLING_RESPONSE_RESULT_USER_CANCELED) {
            Toasty.error(this, getString(R.string.purchase_canceled)).show();
        }
    }

    @Override
    public void onBillingInitialized() {
        if (!billing.isPurchased(PREMIUM)) {
            // FIXME: Рефрешнуть адаптер
            setupBottomSheet();
        }
    }

    private void setupBottomSheet() {
        TextView caption = findViewById(R.id.caption);
        RecyclerView recycler = findViewById(R.id.list);
        if (recycler.getAdapter() != null) {
            recycler.setAdapter(null);
        }
        ArrayList<MainMenuItem> menuItems = new ArrayList<>();

        caption.setText(R.string.caption_lessons);
        if (Preferences.getBookmark() != null) {
            menuItems.add(new MainMenuItem(R.drawable.bookmark, "#fad805", getString(R.string.continue_lesson), MainMenuItems.CONTINUE));
        }
        menuItems.add(new MainMenuItem(R.drawable.star_bookmark, "#fdd835", getString(R.string.bookmarks), MainMenuItems.BOOKMARKS));
        menuItems.add(new MainMenuItem(R.drawable.settings, "#546e7a", getString(R.string.settings), MainMenuItems.SETTINGS));

        if (isPremium = true) {
            menuItems.add(new MainMenuItem(R.drawable.cash_multiple, "#43a047", getString(R.string.p), MainMenuItems.PREMIUM));
        } else {
            Log.e("", "");
        }
        menuItems.add(new MainMenuItem(R.drawable.information, "#3949ab", getString(R.string.about), MainMenuItems.ABOUT));
        menuItems.add(new MainMenuItem(R.drawable.exit, "#e53935", getString(R.string.exit), MainMenuItems.EXIT));

        MainMenuAdapter adapter = new MainMenuAdapter(menuItems);
        adapter.setItemClickListener((menuItem, position) -> {
            switch (menuItem.getAction()) {
                case MainMenuItems.BOOKMARKS: {
                    new BookmarksFragment().show(getSupportFragmentManager(), null);
                    break;
                }
                case MainMenuItems.ABOUT: {
                    showAboutSheet();
                    break;
                }
                case MainMenuItems.SETTINGS: {
                    startActivityForResult(new Intent(MainActivity.this, com.mcal.kotlin.SettingsActivity.class).putExtra("isPremium", billing.isPurchased(PREMIUM)), REQUEST_CODE_SETTINGS);
                    break;
                }
                case MainMenuItems.EXIT: {
                    finish();
                    break;
                }
                case MainMenuItems.PREMIUM: {
                    billing.purchase(MainActivity.this, PREMIUM);
                    break;
                }
                case MainMenuItems.CONTINUE: {
                    if (isOffline() || Utils.isNetworkAvailable())
                        resumeLesson();
                    else Dialogs.noConnectionError(this);
                    break;
                }
            }
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });
        recycler.setAdapter(adapter);
    }

    private void setupSearchView() {
        sv.setOnQueryTextListener(this);

        findViewById(R.id.button_night).setOnClickListener(view -> {
            if (NightMode.getCurrentMode() == NightMode.Mode.DAY) {
                NightMode.setMode(NightMode.Mode.NIGHT);
                Preferences.setNightMode(true);
            } else {
                NightMode.setMode(NightMode.Mode.DAY);
                Preferences.setNightMode(false);
            }
            getDelegate().applyDayNight();
        });
    }

    public void showAboutSheet() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(40, 0, 40, 0);
        ll.setLayoutParams(layoutParams);
        final TextInputLayout til0 = new TextInputLayout(this);
        final AppCompatTextView message = new AppCompatTextView(this);
        message.setGravity(1);
        message.setText(R.string.copyright);
        til0.addView(message);
        ll.addView(til0);

        final SweetContentDialog dialog = new SweetContentDialog(this);
        dialog.setTitle(this.getString(R.string.app_name) + " v." + BuildConfig.VERSION_NAME);
        dialog.setView(ll);
        dialog.addAction(R.drawable.star, this.getString(R.string.rate), view -> {
            Dialogs.rate(this);
            dialog.cancel();
        });
        dialog.addAction(R.drawable.google_play, this.getString(R.string.more_apps), view -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:РРІР°РЅ РўРёРјР°С€РєРѕРІ")));
            dialog.cancel();
        });
        dialog.show();
    }
}