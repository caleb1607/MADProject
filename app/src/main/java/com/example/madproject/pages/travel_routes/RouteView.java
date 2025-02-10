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
        String htmlContent = "<html>" +
                "<head>" +
                "<link rel='stylesheet' href='https://www.onemap.gov.sg/web-assets/libs/leaflet/leaflet.css' />" +
                "<script src='https://www.onemap.gov.sg/web-assets/libs/leaflet/onemap-leaflet.js'></script>" +
                "</head>" +
                "<body>" +
                "<h1>Default Map (XYZ)</h1>" +
                "<div id='mapdiv' style='height:800px;'></div>" +
                "<script>" +
                "let sw = L.latLng(1.144, 103.535);" +
                "let ne = L.latLng(1.494, 104.502);" +
                "let bounds = L.latLngBounds(sw, ne);" +
                "let map = L.map('mapdiv', {" +
                "center: L.latLng(1.2868108, 103.8545349)," +
                "zoom: 16" +
                "});" +
                "map.setMaxBounds(bounds);" +
                "let basemap = L.tileLayer('https://www.onemap.gov.sg/maps/tiles/Default/{z}/{x}/{y}.png', {" +
                "detectRetina: true," +
                "maxZoom: 19," +
                "minZoom: 11," +
                "attribution: '<img src=\"https://www.onemap.gov.sg/web-assets/images/logo/om_logo.png\" style=\"height:20px;width:20px;\"/> &nbsp;<a href=\"https://www.onemap.gov.sg/\" target=\"_blank\" rel=\"noopener noreferrer\">OneMap</a> &copy; contributors &#124; <a href=\"https://www.sla.gov.sg/\" target=\"_blank\" rel=\"noopener noreferrer\">Singapore Land Authority</a>'" +
                "});" +
                "basemap.addTo(map);" +
                "</script>" +
                "</body>" +
                "</html>";

        webView.loadData(htmlContent, "text/html", "UTF-8");

        return rootView;
    }
}