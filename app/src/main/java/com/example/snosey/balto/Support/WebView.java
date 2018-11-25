package com.example.snosey.balto.Support;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import com.example.snosey.balto.R;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by Snosey on 4/15/2018.
 */

public class WebView extends Activity {


    @InjectView(R.id.webView)
    android.webkit.WebView webview;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
        setContentView(R.layout.webview);
        ButterKnife.inject(this);
        setUpWebView();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setUpWebView() {
        String fullUrl = getIntent().getStringExtra("url");
        webview.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);

        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setAllowFileAccess(true);
        webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        webview.getSettings().setMediaPlaybackRequiresUserGesture(false);

        webview.setWebChromeClient(new WebChromeClient() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPermissionRequest(final PermissionRequest request) {

            }

            @Override
            public void onProgressChanged(android.webkit.WebView view, int newProgress) {
                //Make the bar disappear after URL is loaded, and changes string to Loading...
                setTitle(getString(R.string.com_facebook_loading));
                setProgress(newProgress * 100); //Make the bar disappear after URL is loaded

                // Return the app name after finish loading
                if (newProgress == 100)
                    setTitle(R.string.app_name);

            }


        });
        String appCachePath = getCacheDir().getAbsolutePath();
        webview.getSettings().setAppCachePath(appCachePath);

        webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webview.getSettings().setAppCacheEnabled(true);

        webview.getSettings().setAllowFileAccessFromFileURLs(true);
        webview.getSettings().setAllowUniversalAccessFromFileURLs(true);

        WebSettings websettings = webview.getSettings();
        websettings.setJavaScriptEnabled(true);

        webview.setHorizontalScrollBarEnabled(false);
        webview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setAllowFileAccessFromFileURLs(true);
        webview.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(android.webkit.WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (url.contains("elbalto.com")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                onBackPressed();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            @Override
            public void onPageFinished(android.webkit.WebView view, String url) {
                // TODO Auto-generated method stub

                super.onPageFinished(view, url);
            }

        });
        webview.setWebContentsDebuggingEnabled(true);
        webview.loadUrl(fullUrl);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }
}

