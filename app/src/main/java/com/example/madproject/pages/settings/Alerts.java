package com.example.madproject.pages.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.madproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Alerts extends AppCompatActivity {
    private TextView alert;
    private EditText addalerts;
    private Button add,delete;
    private FirebaseFirestore db;
    private SharedPreferences emailpref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_alerts);

        alert = findViewById(R.id.alerts);
        add = findViewById(R.id.Add);
        add.setOnClickListener(view -> onAdd());
        delete = findViewById(R.id.Delete);
        delete.setOnClickListener(view -> onDel());
        addalerts = findViewById(R.id.Addalerts);
        db = FirebaseFirestore.getInstance();

        getAnnouncements();
        checkEmail(); // Call function to check email
    }



    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
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



    private void checkEmail() {
        emailpref = getSharedPreferences("Emailpref", MODE_PRIVATE);
        String userEmail = emailpref.getString("email", "default@gmail.com"); // Default if not found
        Log.d("email",userEmail);
        if (userEmail.equals("nyoom123@gmail.com")) {
            add.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            addalerts.setVisibility(View.VISIBLE);
        } else {
            add.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            addalerts.setVisibility(View.GONE);
        }
    }



    private void onAdd() {
        // 5️⃣ Get text from TextView
        String alerts = addalerts.getText().toString();

        if (alerts.isEmpty()) {
            Toast.makeText(this, "Text cannot be empty!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(this, "Saved successfully!", Toast.LENGTH_SHORT).show();
                        restartActivity();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to save!", Toast.LENGTH_SHORT).show());

    }



    private void onDel() {
        // 4️⃣ Get text from TextView
        String announcementText = addalerts.getText().toString();

        if (announcementText.isEmpty()) {
            Toast.makeText(this, "No text to delete!", Toast.LENGTH_SHORT).show();
            return;
        }

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
                                        Toast.makeText(this, "Deleted successfully!", Toast.LENGTH_SHORT).show();
                                        restartActivity();
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, "Failed to delete!", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Toast.makeText(this, "No matching document found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to find document!", Toast.LENGTH_SHORT).show());

    }
}
