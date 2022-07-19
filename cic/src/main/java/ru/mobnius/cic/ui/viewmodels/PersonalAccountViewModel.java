package ru.mobnius.cic.ui.viewmodels;

import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ru.mobnius.simple_core.utils.StringUtil;

public class PersonalAccountViewModel extends ViewModel {
    @Nullable
    public WebViewClient webViewClient;
    @NonNull
    public MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();
    public boolean redirectLoaded = false;
    @NonNull
    public String lastUrl = StringUtil.EMPTY;

    public void init() {
        loadingLiveData.setValue(false);
        if (webViewClient == null) {
            webViewClient = new WebViewClient() {
                @Override
                public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                    super.onReceivedHttpError(view, request, errorResponse);
                    loadingLiveData.setValue(true);
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    loadingLiveData.setValue(true);
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    loadingLiveData.setValue(true);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    loadingLiveData.setValue(true);
                    redirectLoaded = true;
                    lastUrl = url;
                }
            };
        }
    }
}
