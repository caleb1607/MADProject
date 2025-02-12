package com.example.madproject.pages.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.madproject.R;
import com.example.madproject.helper.LocalStorageDB;
import com.example.madproject.pages.misc.Login;
import com.github.chrisbanes.photoview.PhotoView;

import org.w3c.dom.Text;

public class Feedback extends Fragment {

    View rootView;
    TextView SETTINGS;
    TextView FEEDBACK;
    ImageView FEEDBACK_ICON;
    ImageView returnButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_feedback, container, false);
        SETTINGS = rootView.findViewById(R.id.SETTINGS);
        FEEDBACK = rootView.findViewById(R.id.FEEDBACK2);
        FEEDBACK_ICON = rootView.findViewById(R.id.FEEDBACK_ICON2);
        returnButton = rootView.findViewById(R.id.returnButton);
        returnButton.setOnClickListener(view -> goBack());
        // transition
        Transition transition = TransitionInflater.from(requireContext()).inflateTransition(R.transition.shared_textview);
        setSharedElementEnterTransition(transition);
        return rootView;
    }
    public void manageTheme() {
        if (ThemeManager.isDarkTheme()) {
            rootView.setBackgroundColor(getResources().getColor(R.color.mainBackground));
        } else { // light
            rootView.setBackgroundColor(getResources().getColor(R.color.LmainBackground));
        }
    }
    private void goBack() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction
                .addSharedElement(SETTINGS, "SettingsText")
                .addSharedElement(FEEDBACK, "FeedbackText")
                .addSharedElement(FEEDBACK_ICON, "FeedbackIcon");
        getParentFragmentManager().popBackStack();
    }
}