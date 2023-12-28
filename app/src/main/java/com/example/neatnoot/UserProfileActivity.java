 package com.example.neatnoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.temporal.Temporal;

 public class UserProfileActivity extends AppCompatActivity {

    private TextView userProfileWelcome, userProfileFullName, userProfileEmailAddress, userProfileDateOfBirth, userProfileGender, userProfileMobile;
    private ProgressBar progressBar;
    private String fullName, emailAddress, dateOfBirth, gender, mobile;
    private ImageView profilePicture;
    private FirebaseAuth authProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userProfileWelcome = findViewById(R.id.show_welcome);
        userProfileFullName = findViewById(R.id.show_fullName);
        userProfileEmailAddress = findViewById(R.id.show_emailAddress);
        userProfileDateOfBirth = findViewById(R.id.show_dateOfBirth);
        userProfileGender = findViewById(R.id.show_gender);
        userProfileMobile = findViewById(R.id.show_mobile);
        progressBar = findViewById((R.id.progress_bar));

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null){
            Toast.makeText(UserProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
        } else {
            checkIfEmailVerified(firebaseUser);
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }


    }

     private void checkIfEmailVerified(FirebaseUser firebaseUser) {

        if (!firebaseUser.isEmailVerified()){
            showAlerDialog();
        }

     }

     private void showAlerDialog() {
         //Setup Alert Builder
         AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
         builder.setTitle("Email not verified");
         builder.setMessage("Pleaser verify your email");

         //Continue to email button
         builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 Intent intent = new Intent(Intent.ACTION_MAIN);
                 intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                 startActivity(intent);
             }
         });

         AlertDialog alertDialog = builder.create();
         alertDialog.show();
     }

     private void showUserProfile(FirebaseUser firebaseUser) {

        String userID = firebaseUser.getUid();

         DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
         referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                 if (readUserDetails != null){
                     fullName = firebaseUser.getDisplayName();
                     emailAddress = firebaseUser.getEmail();
                     dateOfBirth = readUserDetails.dateOfBirth;
                     gender = readUserDetails.gender;
                     mobile = readUserDetails.mobile;

                     userProfileWelcome.setText("Welcome" + fullName);
                     userProfileFullName.setText(fullName);
                     userProfileEmailAddress.setText(emailAddress);
                     userProfileDateOfBirth.setText(dateOfBirth);
                     userProfileGender.setText(gender);
                     userProfileMobile.setText(mobile);

                 }
                 progressBar.setVisibility(View.GONE);
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });

     }
 }