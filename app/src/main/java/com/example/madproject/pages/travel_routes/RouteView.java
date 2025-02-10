package com.example.madproject.pages.travel_routes;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.madproject.R;

public class RouteView extends Fragment {

    private WebView webView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_routeview, container, false);
        assert getArguments() != null;
        String lat1 = getArguments().getString("lat1");
        String lon1 = getArguments().getString("lon1");
        String name1 = getArguments().getString("name1");
        String lat2 = getArguments().getString("lat2");
        String lon2 = getArguments().getString("lon2");
        String name2 = getArguments().getString("name2");
        Log.d(name1.toString(), lat1.toString() + ", " + lon1.toString());
        Log.d(name2.toString(), lat2.toString() + ", " + lon2.toString());


        WebView webView = rootView.findViewById(R.id.webview);

        // Enable JavaScript (if needed)
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Prevent opening links in an external browser
        webView.setWebViewClient(new WebViewClient());

        // Load the HTML content
        String htmlContent = "<html><body>" +
                "<img src=\"https://www.onemap.gov.sg/web-assets/images/logo/om_logo.png\" " +
                "style=\"height:20px;width:20px;\"/> " +
                "<a href=\"https://www.onemap.gov.sg/\" target=\"_blank\">OneMap</a> &copy; contributors | " +
                "<a href=\"https://www.sla.gov.sg/\" target=\"_blank\">Singapore Land Authority</a>" +
                "</body></html>";

        webView.loadData(htmlContent, "text/html", "UTF-8");

        return rootView;
    }
}