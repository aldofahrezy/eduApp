package com.example.neatnoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    private EditText signupFirstName, signupLastName, signupEmailAddress, signupDateOfBirth,
            signupMobile, signupPassword, signupPasswordConfirm;
    private ProgressBar progressBar;
    private RadioGroup radioGroupSignupGender;
    private RadioButton radioGroupSignupGenderSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);

/*
        getSupportActionBar().setTitle("Sign Up");
*/

        progressBar = findViewById(R.id.progress_bar);

        signupFirstName = findViewById(R.id.signup_firstname);
        signupLastName = findViewById(R.id.signup_lastname);
        signupEmailAddress = findViewById(R.id.signup_email_address);
        signupDateOfBirth = findViewById(R.id.signup_dob);
        signupMobile = findViewById(R.id.signup_mobile);
        signupPassword = findViewById(R.id.signup_password);
        signupPasswordConfirm = findViewById(R.id.signup_password_confirm);

        radioGroupSignupGender = findViewById(R.id.group_register_gender);
        radioGroupSignupGender.clearCheck();

        Button buttonSignUp = findViewById(R.id.signup_btn);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedGenderId = radioGroupSignupGender.getCheckedRadioButtonId();
                radioGroupSignupGenderSelected = findViewById(selectedGenderId);

                String firstName = signupFirstName.getText().toString();
                String lastName = signupLastName.getText().toString();
                String emailAddress = signupEmailAddress.getText().toString();
                String DateOfBirth = signupDateOfBirth.getText().toString();
                String mobile = signupMobile.getText().toString();
                String password = signupPassword.getText().toString();
                String passwordConfirm = signupPasswordConfirm.getText().toString();
                String gender;

                if (TextUtils.isEmpty(firstName)) {
                    Toast.makeText(SignupActivity.this, "Please enter your first name", Toast.LENGTH_SHORT).show();
                    signupFirstName.setError("This field is required");
                    signupFirstName.requestFocus();
                } else if (TextUtils.isEmpty(lastName)) {
                    Toast.makeText(SignupActivity.this, "Please enter your last name", Toast.LENGTH_SHORT).show();
                    signupLastName.setError("This field is required");
                    signupLastName.requestFocus();
                } else if (TextUtils.isEmpty(emailAddress)) {
                    Toast.makeText(SignupActivity.this, "Please enter your email address", Toast.LENGTH_SHORT).show();
                    signupEmailAddress.setError("This field is required");
                    signupEmailAddress.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
                    Toast.makeText(SignupActivity.this, "Please check your email address", Toast.LENGTH_SHORT).show();
                    signupEmailAddress.setError("Invalid email address");
                    signupEmailAddress.requestFocus();
                } else if (TextUtils.isEmpty(DateOfBirth)) {
                    Toast.makeText(SignupActivity.this, "Please enter your date of birth", Toast.LENGTH_SHORT).show();
                    signupDateOfBirth.setError("This field is required");
                    signupDateOfBirth.requestFocus();
                } else if (radioGroupSignupGender.getCheckedRadioButtonId()==-1) {
                    Toast.makeText(SignupActivity.this, "Please select your gender", Toast.LENGTH_SHORT).show();
                    radioGroupSignupGenderSelected.setError("This field is required");
                    radioGroupSignupGenderSelected.requestFocus();
                } else if (TextUtils.isEmpty(mobile)) {
                    Toast.makeText(SignupActivity.this, "Please enter your mobile number", Toast.LENGTH_SHORT).show();
                    signupMobile.setError("This field is required");
                    signupMobile.requestFocus();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignupActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    signupPassword.setError("This field is required");
                    signupPassword.requestFocus();
                } else if (password.length() < 8) {
                    Toast.makeText(SignupActivity.this, "Password should be at least 8 digits", Toast.LENGTH_SHORT).show();
                    signupPassword.setError("Password too weak");
                    signupPassword.requestFocus();
                } else if (TextUtils.isEmpty(passwordConfirm)) {
                    Toast.makeText(SignupActivity.this, "Please re-enter your password", Toast.LENGTH_SHORT).show();
                    signupPasswordConfirm.setError("This field is required");
                    signupPasswordConfirm.requestFocus();
                } else if (!password.equals(passwordConfirm)) {
                    Toast.makeText(SignupActivity.this, "Please check your password", Toast.LENGTH_SHORT).show();
                    signupFirstName.setError("Password is not the same");
                    signupFirstName.requestFocus();
                    signupPasswordConfirm.clearComposingText();
                } else {
                    gender = radioGroupSignupGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(firstName, lastName, emailAddress, DateOfBirth, gender, mobile, password);
                }
            }
        });

    }

    private void registerUser(String firstName, String lastName, String emailAddress, String dateOfBirth, String gender, String mobile, String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(emailAddress, password).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SignupActivity.this, "Signed up successfully", Toast.LENGTH_LONG).show();
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    firebaseUser.sendEmailVerification();

                    /*Intent intent = new Intent(SignupActivity.this, UserProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();*/
                }
            }
        });
    }
}