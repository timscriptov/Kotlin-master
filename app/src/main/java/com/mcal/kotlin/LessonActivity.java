package com.mcal.kotlin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mcal.kotlin.data.Bookmarks;
import com.mcal.kotlin.data.Preferences;
import com.mcal.kotlin.model.BaseActivity;
import com.mcal.kotlin.module.Dialogs;
import com.mcal.kotlin.module.HtmlRenderer;
import com.mcal.kotlin.utils.FileReader;
import com.mcal.kotlin.utils.LessonUtils;
import com.mcal.kotlin.utils.SignatureUtils;
import com.mcal.kotlin.utils.Utils;
import com.mcal.kotlin.view.MCProgressBar;
import com.mcal.kotlin.view.NestedWebView;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

import java.util.concurrent.TimeUnit;

import static com.mcal.kotlin.data.Constants.getResPath;
import static com.mcal.kotlin.data.Preferences.isOffline;
import static com.mcal.kotlin.utils.LessonUtils.getLessonNumberByUrl;
import static com.mcal.kotlin.utils.LessonUtils.isRead;

public class LessonActivity extends BaseActivity implements OnClickListener {
    private MCProgressBar progressBar;
    private NestedWebView webView;
    private FloatingActionButton prev_lesson, next_lesson, bookmark;
    private CollapsingToolbarLayout ctl;
    private int itemPosition;

    private boolean isPremium;

    @Override
    public void onClick(View p1) {
        switch (p1.getId()) {
            case R.id.bookmark_lesson:
                int num = getLessonNumberByUrl(webView.getUrl());

                if (Bookmarks.isBookmarked(num)) {
                    Bookmarks.remove(num);
                    Snackbar.make(webView, getString(R.string.removed_from_bookmarks, num), Snackbar.LENGTH_LONG).show();
                    bookmark.setImageResource(R.drawable.star_bookmark);
                } else if (Bookmarks.add(num, ctl.getTitle().toString())) {
                    Snackbar.make(webView, getString(R.string.added_to_bookmarks, num), Snackbar.LENGTH_LONG).show();
                    bookmark.setImageResource(R.drawable.star_bookmarked);
                }
                bookmark.hide();
                bookmark.show();
                break;
            case R.id.prev_lesson:
                if (!isOffline() & !Utils.isNetworkAvailable()) {
                    Dialogs.noConnectionError(LessonActivity.this);
                    return;
                }
                new PageLoader(getResPath() + "/lesson_" + (getLessonNumberByUrl(webView.getUrl()) - 1) + ".html").execute();
                itemPosition--;
                break;
            case R.id.next_lesson:
                if (!isOffline() & !Utils.isNetworkAvailable()) {
                    Dialogs.noConnectionError(LessonActivity.this);
                    return;
                }
                new PageLoader(getResPath() + "/lesson_" + (getLessonNumberByUrl(webView.getUrl()) + 1) + ".html").execute();
                itemPosition++;
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ctl = findViewById(R.id.collapsing_toolbar);
        webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebClient());
        webView.setWebChromeClient(new ChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        progressBar = findViewById(R.id.progress_bar);
        prev_lesson = findViewById(R.id.prev_lesson);
        next_lesson = findViewById(R.id.next_lesson);
        bookmark = findViewById(R.id.bookmark_lesson);
        prev_lesson.setOnClickListener(this);
        next_lesson.setOnClickListener(this);
        bookmark.setOnClickListener(this);
        itemPosition = getIntent().getIntExtra("position", 0);

        new PageLoader(getIntent().getStringExtra("url")).execute();

        isPremium = getIntent().getBooleanExtra("isPremium", false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Preferences.setBookmark(webView.getUrl());
    }

    @Override
    public void onBackPressed() {
        if (time + 15000 < System.currentTimeMillis()) {
            final int num = getLessonNumberByUrl(webView.getUrl());

            if (!isRead(num)) {
                Snackbar.make(webView, R.string.mark_as_read, Snackbar.LENGTH_INDEFINITE).setAction(R.string.yes, p1 -> markAsRead(num)).show();
            } else super.onBackPressed();

        } else super.onBackPressed();

        time = System.currentTimeMillis();
    }

    private void markAsRead(int num) {
        if (LessonUtils.markAsRead(num)) {
            Snackbar.make(webView, getString(R.string.marked_as_read, num), Snackbar.LENGTH_LONG).show();
            setResult(RESULT_OK, new Intent().putExtra("isRead", true).putExtra("position", itemPosition));
            AsyncTask.execute(() -> {
                try {
                    TimeUnit.SECONDS.sleep(2);
                    finish();
                } catch (InterruptedException ignored) {
                }
            });
        }
    }

    private class WebClient extends WebViewClient {
        @SuppressLint("RestrictedApi")
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            prev_lesson.setVisibility(getLessonNumberByUrl(url) != 1 ? View.VISIBLE : View.GONE);
            next_lesson.setVisibility(getLessonNumberByUrl(url) != 36 ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);

            if (Bookmarks.isBookmarked(getLessonNumberByUrl(url))) {
                bookmark.setImageResource(R.drawable.star_bookmarked);
            } else bookmark.setImageResource(R.drawable.star_bookmark);

            bookmark.hide();
            bookmark.show();
        }
    }

    private class ChromeClient extends WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            ctl.setTitle(title);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class PageLoader extends AsyncTask<Void, Void, Void> {

        private String link;
        private String html;

        private PageLoader(String link) {
            this.link = link;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);

            if (isOffline()) {
                if (!isPremium || BuildConfig.DEBUG) {
                    if (SignatureUtils.verifySignatureSHA(getApplicationContext()) || BuildConfig.DEBUG) {
                        link = "file:///" + link;
                        webView.loadDataWithBaseURL(link, HtmlRenderer.renderHtml(FileReader.fromStorage(link.replace("file:///", ""))), "text/html", "UTF-8", link);
                        cancel(true);
                    } else {
                        webView.loadData(html, "text/html", "UTF-8");
                    }
                }
            }
        }

        @Override
        protected Void doInBackground(Void... strings) {
            html = HtmlRenderer.renderHtml(FileReader.fromUrl(link));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (SignatureUtils.verifySignatureSHA(getApplicationContext()) || BuildConfig.DEBUG) {
                link += "#googtrans(ru|" + Preferences.getLang() + ")";
                webView.loadDataWithBaseURL(link, html, "text/html", "UTF-8", link);
            } else {
                webView.loadData(html, "text/html", "UTF-8");
            }
        }
    }
}