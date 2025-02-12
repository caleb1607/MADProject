package com.example.madproject.pages.settings;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.madproject.R;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Feedback extends Fragment {

    View rootView;
    Context context;
    TextView SETTINGS;
    TextView FEEDBACK;
    ImageView FEEDBACK_ICON;
    TextView SUBTITLE1;
    TextView SUBTITLE2;
    ImageView returnButton;
    EditText feedbackTextBox;
    Button submitButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_feedback, container, false);
        context = getContext();
        SETTINGS = rootView.findViewById(R.id.SETTINGS);
        FEEDBACK = rootView.findViewById(R.id.FEEDBACK2);
        FEEDBACK_ICON = rootView.findViewById(R.id.FEEDBACK_ICON2);
        SUBTITLE1 = rootView.findViewById(R.id.SUBTITLE1);
        SUBTITLE2 = rootView.findViewById(R.id.SUBTITLE2);
        returnButton = rootView.findViewById(R.id.ReturnButton5);
        returnButton.setOnClickListener(view -> goBack());
        feedbackTextBox = rootView.findViewById(R.id.FeedbackTextBox);
        submitButton = rootView.findViewById(R.id.SubmitButton);
        submitButton.setOnClickListener(view -> {
            onSubmit(generateMSG());
            goBack();
            Toast.makeText(getContext(), "Thanks for your time!", Toast.LENGTH_SHORT).show();
        });
        // transition
        Transition transition = TransitionInflater.from(requireContext()).inflateTransition(R.transition.shared_textview);
        setSharedElementEnterTransition(transition);
        // manage theme
        manageTheme();
        return rootView;
    }
    public void manageTheme() {
        if (ThemeManager.isDarkTheme()) {
            rootView.setBackgroundColor(getResources().getColor(R.color.mainBackground));
            SETTINGS.setTextColor(getResources().getColor(R.color.hintGray));
            returnButton.setImageTintList(getResources().getColorStateList(R.color.hintGray));
            FEEDBACK_ICON.setImageTintList(getResources().getColorStateList(R.color.nyoomYellow));
            FEEDBACK.setTextColor(getResources().getColorStateList(R.color.nyoomYellow));
            feedbackTextBox.setBackgroundTintList(getResources().getColorStateList(R.color.backgroundPanel));
            feedbackTextBox.setHintTextColor(getResources().getColor(R.color.hintGray));
        } else { // light
            rootView.setBackgroundColor(getResources().getColor(R.color.LmainBackground));
            SETTINGS.setTextColor(getResources().getColor(R.color.LhintGray));
            returnButton.setImageTintList(getResources().getColorStateList(R.color.LhintGray));
            FEEDBACK_ICON.setImageTintList(getResources().getColorStateList(R.color.LnyoomYellow));
            FEEDBACK.setTextColor(getResources().getColorStateList(R.color.LnyoomYellow));
            feedbackTextBox.setBackgroundTintList(getResources().getColorStateList(R.color.LbackgroundPanel));
            feedbackTextBox.setHintTextColor(getResources().getColor(R.color.LhintGray));
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

    private static void onSubmit(String message) {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                try {
                    String messageText = URLEncoder.encode(params[0], "UTF-8");  // Encode message to handle special characters

                    // Prepare the URL with the correct token and chat_id
                    String urlString = "https://api.telegram.org/bot8140352720:AAHV0zueK4aU__qcRr_6sdyPyFmuUfUXTF0/sendMessage" +
                            "?chat_id=" + "5896827510" +
                            "&text=" + messageText;

                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);

                    // Send the request
                    OutputStream os = connection.getOutputStream();
                    os.write(new byte[0]); // Empty data to trigger the POST request

                    connection.getResponseCode(); // Get the response

                } catch (Exception ignored) {}
                return null;
            }
        }.execute(message);
    }
    private String generateMSG() {
        String message = "From: email\nMessage:\n";
        message += feedbackTextBox.getText().toString();
        return message;
    }
}
