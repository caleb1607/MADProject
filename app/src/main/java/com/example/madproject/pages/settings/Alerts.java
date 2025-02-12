package com.example.madproject.pages.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
        delete = findViewById(R.id.Delete);
        addalerts = findViewById(R.id.Addalerts);
        db = FirebaseFirestore.getInstance();

        getAnnouncements();
        checkEmail(); // Call function to check email
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
}
