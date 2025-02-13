package com.example.madproject.pages.settings;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.madproject.R;
import com.example.madproject.helper.BusTimesBookmarksDB;
import com.example.madproject.helper.BusTimesRecentsDB;
import com.example.madproject.helper.LocalStorageDB;
import com.example.madproject.pages.Main;
import com.example.madproject.pages.misc.Login;
import com.example.madproject.pages.misc.Startup;
import com.github.chrisbanes.photoview.PhotoView;

public class Settings extends Fragment {

    View rootView;
    Context mainContext;
    LocalStorageDB localStorageDB;
    // views
    Button toggleThemeButton;
    Button clearCacheButton;
    Button deleteAccountButton;
    Space deleteAccountSpace;
    Button logoutButton;
    LinearLayout alertsButton;
    LinearLayout MRTMapButton;
    LinearLayout feedbackButton;
    FrameLayout mapFL;
    PhotoView MRT_MAP;
    ImageView SETTINGS_ICON;
    TextView SETTINGS;
    ImageView ALERTS_ICON;
    TextView ALERTS;
    ImageView MRTMAP_ICON;
    TextView MRTMAP;
    ImageView FEEDBACK_ICON;
    TextView FEEDBACK;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        // views setup
        toggleThemeButton = rootView.findViewById(R.id.ToggleThemeButton);
        toggleThemeButton.setOnClickListener(view -> toggleTheme());
        clearCacheButton = rootView.findViewById(R.id.ClearCacheButton);
        clearCacheButton.setOnClickListener(view -> clearCache());
        deleteAccountButton = rootView.findViewById(R.id.DeleteAccountButton);
        deleteAccountButton.setOnClickListener(view -> onDeleteAccount());
        deleteAccountSpace = rootView.findViewById(R.id.DeleteAccountSpace);
        logoutButton = rootView.findViewById(R.id.LogOutButton);
        logoutButton.setOnClickListener(view -> onLogout());
        alertsButton = rootView.findViewById(R.id.AlertsButton);
        alertsButton.setOnClickListener(view -> onAlertsView());
        MRTMapButton = rootView.findViewById(R.id.MRTMapButton);
        MRTMapButton.setOnClickListener(view -> onMRTMapView());
        feedbackButton = rootView.findViewById(R.id.FeedbackButton);
        feedbackButton.setOnClickListener(view -> onFeedbackView());
        mapFL = rootView.findViewById(R.id.MapFL);
        MRT_MAP = rootView.findViewById(R.id.MRT_MAP);
        MRT_MAP.setOnMatrixChangeListener(rect -> { // scale size based on zoom
            float scale = MRT_MAP.getScale();
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) MRT_MAP.getLayoutParams();
            if (scale > 1f) {
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                layoutParams.height = (int) (300 * getResources().getDisplayMetrics().density);
            }
            MRT_MAP.setLayoutParams(layoutParams);
        });
        SETTINGS_ICON = rootView.findViewById(R.id.SETTINGS_ICON);
        SETTINGS = rootView.findViewById(R.id.SETTINGS);
        ALERTS_ICON = rootView.findViewById(R.id.ALERTS_ICON);
        ALERTS = rootView.findViewById(R.id.ALERTS);
        MRTMAP_ICON = rootView.findViewById(R.id.MRTMAP_ICON);
        MRTMAP = rootView.findViewById(R.id.MRTMAP);
        FEEDBACK_ICON = rootView.findViewById(R.id.FEEDBACK_ICON);
        FEEDBACK = rootView.findViewById(R.id.FEEDBACK);
        localStorageDB = new LocalStorageDB(getContext());
        if (localStorageDB.getValue("LoginToken").equals("0")) {
            logoutButton.setText("Sign In");
            deleteAccountButton.setVisibility(View.GONE);
            deleteAccountSpace.setVisibility(View.GONE);
        } else { // equals("1")
            logoutButton.setText("Log Out");
            deleteAccountButton.setVisibility(View.VISIBLE);
            deleteAccountSpace.setVisibility(View.VISIBLE);
        }
        // transition
        Transition transition = TransitionInflater.from(requireContext()).inflateTransition(R.transition.shared_textview);
        setSharedElementEnterTransition(transition);
        // manage theme
        manageTheme();
        if (true) { // announcement got
            activateAlertsButton(true);
        }
        return rootView;
    }
    public void manageTheme() {
        if (ThemeManager.isDarkTheme()) {
            rootView.setBackgroundColor(getResources().getColor(R.color.mainBackground));
            toggleThemeButton.setBackgroundTintList(getResources().getColorStateList(R.color.buttonPanel));
            toggleThemeButton.setTextColor(getResources().getColor(R.color.white));
            SETTINGS_ICON.setImageTintList(getResources().getColorStateList(R.color.white));
            SETTINGS.setTextColor(getResources().getColor(R.color.white));
            alertsButton.setBackgroundTintList(getResources().getColorStateList(R.color.buttonPanel));
            MRTMapButton.setBackgroundTintList(getResources().getColorStateList(R.color.buttonPanel));
            feedbackButton.setBackgroundTintList(getResources().getColorStateList(R.color.buttonPanel));
            ALERTS_ICON.setImageTintList(getResources().getColorStateList(R.color.nyoomBlue));
            ALERTS.setTextColor(getResources().getColor(R.color.nyoomBlue));
            MRTMAP_ICON.setImageTintList(getResources().getColorStateList(R.color.nyoomGreen));
            MRTMAP.setTextColor(getResources().getColor(R.color.nyoomGreen));
            FEEDBACK_ICON.setImageTintList(getResources().getColorStateList(R.color.nyoomYellow));
            FEEDBACK.setTextColor(getResources().getColor(R.color.nyoomYellow));
            MRT_MAP.setImageResource(R.drawable.mrtdark2048);
        } else { // light
            rootView.setBackgroundColor(getResources().getColor(R.color.LhintGray));
            toggleThemeButton.setBackgroundTintList(getResources().getColorStateList(R.color.LdarkGray));
            toggleThemeButton.setTextColor(getResources().getColor(R.color.black));
            SETTINGS_ICON.setImageTintList(getResources().getColorStateList(R.color.white));
            SETTINGS.setTextColor(getResources().getColor(R.color.white));
            alertsButton.setBackgroundTintList(getResources().getColorStateList(R.color.nyoomBlue));
            MRTMapButton.setBackgroundTintList(getResources().getColorStateList(R.color.nyoomGreen));
            feedbackButton.setBackgroundTintList(getResources().getColorStateList(R.color.LnyoomYellow));
            ALERTS_ICON.setImageTintList(getResources().getColorStateList(R.color.white));
            ALERTS.setTextColor(getResources().getColor(R.color.white));
            MRTMAP_ICON.setImageTintList(getResources().getColorStateList(R.color.white));
            MRTMAP.setTextColor(getResources().getColor(R.color.white));
            FEEDBACK_ICON.setImageTintList(getResources().getColorStateList(R.color.white));
            FEEDBACK.setTextColor(getResources().getColor(R.color.white));
            MRT_MAP.setImageResource(R.drawable.mrtlight2048);
        }
        if (true) { // announcement got
            activateAlertsButton(true);
        }
    }
    private void activateAlertsButton(boolean activate) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(alertsButton, "scaleX", 1.0f, 0.9f, 1.0f, 0.9f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(alertsButton, "scaleY", 1.0f, 0.9f, 1.0f, 0.9f);
        if (activate) {
            alertsButton.setBackgroundTintList(getResources().getColorStateList(R.color.redError));
            ALERTS_ICON.setImageTintList(getResources().getColorStateList(R.color.white));
            ALERTS.setTextColor(getResources().getColor(R.color.white));
            scaleX.setDuration(750);
            scaleY.setDuration(750);
            scaleX.setRepeatCount(ObjectAnimator.INFINITE);
            scaleX.setRepeatMode(ObjectAnimator.REVERSE);
            scaleY.setRepeatCount(ObjectAnimator.INFINITE);
            scaleY.setRepeatMode(ObjectAnimator.REVERSE);
            scaleX.start();
            scaleY.start();
            return;
        }
        manageTheme(); // update to default
        scaleX.end();
        scaleY.end();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainContext = context;
    }
    private void toggleTheme() {
        if (localStorageDB.getValue("theme_preference").equals("light")) {
            localStorageDB.insertOrUpdate("theme_preference", "dark");
        } else {
            localStorageDB.insertOrUpdate("theme_preference", "light");
        }
        ThemeManager.setTheme(mainContext, !ThemeManager.isDarkTheme());
        manageTheme();
    }
    private void clearCache() {
        BusTimesBookmarksDB busTimesBookmarksDB = new BusTimesBookmarksDB(getContext());
        busTimesBookmarksDB.deleteAllBookmarks();
        BusTimesRecentsDB busTimesRecentsDB = new BusTimesRecentsDB(getContext());
        busTimesRecentsDB.deleteAllRecords();
        Toast.makeText(getContext(), "Cache Cleared", Toast.LENGTH_SHORT).show();
    }
    private void onDeleteAccount() {

    }
    private void onLogout() {
        localStorageDB.insertOrUpdate("LoginToken", "0");
        Intent logout = new Intent(mainContext, Login.class);
        startActivity(logout);
        if (mainContext instanceof Activity) {
            Activity activity = (Activity) mainContext;
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            activity.finish();
        }
    }
    private void onAlertsView() {
        FragmentTransaction transaction = getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new Alerts())
                .addToBackStack(null);
        transaction.commit();
    }
    private void onMRTMapView() {
        mapFL.setVisibility(View.VISIBLE);
        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        mapFL.startAnimation(fadeIn);
        View mapBG = rootView.findViewById(R.id.MapBG);
        mapBG.setOnClickListener(view -> mapFL.setVisibility(View.GONE));
    }
    private void onFeedbackView() {
        FragmentTransaction transaction = getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slidefade_in_right,  // Enter animation
                        R.anim.slidefade_out_left,  // Exit animation
                        R.anim.slidefade_in_left,   // Pop enter animation (when fragment is re-added)
                        R.anim.slidefade_out_right  // Pop exit animation (when fragment is removed)
                )
                .replace(R.id.fragment_container, new Feedback())
                .addSharedElement(SETTINGS, "SettingsText")
                .addSharedElement(FEEDBACK, "FeedbackText")
                .addSharedElement(FEEDBACK_ICON, "FeedbackIcon")
                .addToBackStack(null);
        transaction.commit();
    }
}