package com.example.madproject.pages.settings;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.madproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Alerts extends AppCompatActivity {
    private TextView alert;
    private FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_alerts);

        alert = findViewById(R.id.alerts);
        db = FirebaseFirestore.getInstance();

        getAnnouncements();
    }



    void getAnnouncements() {
        db.collection("announcements")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            StringBuilder announcements = new StringBuilder();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String message = document.getString("message");
                                announcements.append("🔹 ").append(message).append("\n\n");
                                Log.d("Firestore", document.getId() + " => " + document.getData());
                            }
                            alert.setText(announcements.toString());
                        } else {
                            Log.w("Firestore", "Error getting documents.", task.getException());
                            alert.setText("Failed to load announcements.");
                        }
                    }
                });
    }

}
