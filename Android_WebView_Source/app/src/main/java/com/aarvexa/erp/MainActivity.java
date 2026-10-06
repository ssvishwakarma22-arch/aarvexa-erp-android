package com.aarvexa.erp;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.SafeBrowsingResponse;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final Set<String> ALLOWED_HOSTS = Set.of(
            "aarvexaimf.com",
            "www.aarvexaimf.com"
    );

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);
        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setSavePassword(false);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            settings.setSafeBrowsingEnabled(true);
        }

        webView.setWebViewClient(new WebViewClient() {
            private boolean isAllowed(@NonNull Uri uri) {
                String scheme = uri.getScheme();
                String host = uri.getHost();
                return "https".equalsIgnoreCase(scheme)
                        && host != null
                        && ALLOWED_HOSTS.contains(host.toLowerCase());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return !isAllowed(request.getUrl());
            }

            @Override
            @SuppressWarnings("deprecation")
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                try {
                    return !isAllowed(Uri.parse(url));
                } catch (Exception ignored) {
                    return true;
                }
            }

            @Override
            public void onSafeBrowsingHit(
                    WebView view,
                    WebResourceRequest request,
                    int threatType,
                    SafeBrowsingResponse callback) {
                callback.backToSafety(true);
            }
        });

        webView.loadUrl(BuildConfig.ERP_URL);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.setWebViewClient(null);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
