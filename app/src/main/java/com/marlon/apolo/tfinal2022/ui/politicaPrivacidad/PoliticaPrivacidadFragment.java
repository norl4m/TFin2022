package com.marlon.apolo.tfinal2022.ui.politicaPrivacidad;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.marlon.apolo.tfinal2022.R;

public class PoliticaPrivacidadFragment extends Fragment {


    public static PoliticaPrivacidadFragment newInstance() {
        return new PoliticaPrivacidadFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.politica_privacidad_fragment, container, false);
        ProgressBar progressBar = root.findViewById(R.id.progressBar);
        WebView myWebView = root.findViewById(R.id.webview);
        myWebView.loadUrl("https://authwitouthauth.herokuapp.com/politica-privacidad");
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (progressBar.isShown()) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
    }

}