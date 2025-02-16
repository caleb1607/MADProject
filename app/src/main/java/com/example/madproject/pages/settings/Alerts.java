package com.example.madproject.pages.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.madproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Alerts extends Fragment {
    View rootView;
    ImageView returnButton;
    TextView SETTINGS;
    private ListView alertsListView;
    private ArrayList<String> alertsList = new ArrayList<>();
    private ArrayAdapter<String> listAdapter;
    private EditText addalerts;
    private Button add;
    private TextView delete;
    private FirebaseFirestore db;
    private SharedPreferences emailpref;
    private ImageView ajaw;
    boolean isAdmin = false;
    int selectedItemPosition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_alerts, container, false);
        add = rootView.findViewById(R.id.AddBtn);
        add.setOnClickListener(view -> onAdd());
        delete = rootView.findViewById(R.id.DeleteBtn);
        delete.setOnClickListener(view -> onDel());
        addalerts = rootView.findViewById(R.id.Addalerts);
        returnButton = rootView.findViewById(R.id.ReturnButton6);
        returnButton.setOnClickListener(view -> goBack());
        SETTINGS = rootView.findViewById(R.id.SETTINGS2);
        alertsListView = rootView.findViewById(R.id.AlertsList);
        listAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, alertsList);
        alertsListView.setAdapter(listAdapter);
        alertsListView.setOnItemClickListener(this::onListItemClick);
        // manage theme
        manageTheme();

        db = FirebaseFirestore.getInstance();
        ajaw = rootView.findViewById(R.id.ajaw);

        checkEmail(); // Call function to check email
        getAnnouncements();
        return rootView;
    }

    public void manageTheme() {
        if (ThemeManager.isDarkTheme()) {
            rootView.setBackgroundColor(getResources().getColor(R.color.mainBackground));
            addalerts.setTextColor(getResources().getColor(R.color.nyoomBlue));
            returnButton.setImageTintList(getResources().getColorStateList(R.color.hintGray));
            SETTINGS.setTextColor(getResources().getColor(R.color.hintGray));
        } else { // light
            rootView.setBackgroundColor(getResources().getColor(R.color.LhintGray));
            addalerts.setTextColor(getResources().getColor(R.color.nyoomLightBlue));
            returnButton.setImageTintList(getResources().getColorStateList(R.color.LdarkGray));
            SETTINGS.setTextColor(getResources().getColor(R.color.LdarkGray));
        }
    }

    private void restartActivity() { // restart itself
        FragmentTransaction transaction = getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new Alerts());
        transaction.commit();
    }

    private void goBack() {
        getParentFragmentManager().popBackStack();
    }

    void getAnnouncements() {
        db.collection("announcements")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String message = document.getString("message");
                                alertsList.add(message);
                                listAdapter.notifyDataSetChanged();
                                addalerts.setText("");
                            }
                        } else {
                            Log.w("Firestore", "Error getting documents.", task.getException());
                            alertsList.add("Failed to load announcements.");
                        }
                    }
                });
    }

    private void onListItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (isAdmin) {
            for (int i = 0; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                child.setBackgroundColor(getResources().getColor(R.color.transparent));
            }
            view.setBackgroundColor(getResources().getColor(R.color.LbuttonPanel));
            delete.setVisibility(View.VISIBLE);
            selectedItemPosition = position;
        }
    }

    private void checkEmail() {
        emailpref = getContext().getSharedPreferences("Emailpref", getContext().MODE_PRIVATE);
        String userEmail = emailpref.getString("email", "default@gmail.com"); // Default if not found
        Log.d("email",userEmail);
        if (userEmail.equals("nyoom123@gmail.com")) {
            isAdmin = true;
            add.setVisibility(View.VISIBLE);
            addalerts.setVisibility(View.VISIBLE);
            ajaw.setVisibility(View.VISIBLE);
        } else {
            isAdmin = false;
            add.setVisibility(View.GONE);
            addalerts.setVisibility(View.GONE);
            ajaw.setVisibility(View.GONE);
        }
    }



    private void onAdd() {
        // 5️⃣ Get text from TextView
        String alerts = addalerts.getText().toString();

        if (alerts.isEmpty()) {
            Toast.makeText(getContext(), "Text cannot be empty!", Toast.LENGTH_SHORT).show();
            return; // Stop if the text is empty
        }

        // 6️⃣ Create a Firestore document
        Map<String, Object> announcement = new HashMap<>();
        announcement.put("message", alerts);
        announcement.put("timestamp", System.currentTimeMillis()); // Optional: Timestamp

        // 7️⃣ Add to Firestore
        db.collection("announcements")
                .add(announcement) // Auto-generates a document ID
                .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Saved successfully!", Toast.LENGTH_SHORT).show();
                        restartActivity();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to save!", Toast.LENGTH_SHORT).show());

    }



    private void onDel() {
        String announcementText = alertsList.get(selectedItemPosition);
        alertsList.remove(selectedItemPosition);
        listAdapter.notifyDataSetChanged();

        // 5️⃣ Find the document that matches the text
        db.collection("announcements")
                .whereEqualTo("message", announcementText) // Match by text
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            db.collection("announcements")
                                    .document(doc.getId()) // Get the document ID
                                    .delete()
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(getContext(), "Deleted successfully!", Toast.LENGTH_SHORT).show();
                                        restartActivity();
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(getContext(), "Failed to delete!", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Toast.makeText(getContext(), "No matching document found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to find document!", Toast.LENGTH_SHORT).show());

    }
}
