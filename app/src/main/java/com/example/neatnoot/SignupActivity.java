package com.example.neatnoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private EditText signupFirstName, signupLastName, signupUsername, signupEmailAddress, signupDateOfBirth,
            signupMobile, signupPassword, signupPasswordConfirm;
    private ProgressBar progressBar;
    private RadioGroup radioGroupSignupGender;
    private RadioButton radioGroupSignupGenderSelected;
    private DatePickerDialog picker;
    private static final String TAG = "SignupActivity";

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
        signupUsername = findViewById(R.id.signup_username);
        signupEmailAddress = findViewById(R.id.signup_email_address);
        signupDateOfBirth = findViewById(R.id.signup_dob);
        signupMobile = findViewById(R.id.signup_mobile);
        signupPassword = findViewById(R.id.signup_password);
        signupPasswordConfirm = findViewById(R.id.signup_password_confirm);

        //RadioButton for Gender
        radioGroupSignupGender = findViewById(R.id.group_register_gender);
        radioGroupSignupGender.clearCheck();

        //DatePicker on editText
        signupDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //DatePicker
                picker = new DatePickerDialog(SignupActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        signupDateOfBirth.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        Button buttonSignUp = findViewById(R.id.signup_btn);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedGenderId = radioGroupSignupGender.getCheckedRadioButtonId();
                radioGroupSignupGenderSelected = findViewById(selectedGenderId);

                String textFirstName = signupFirstName.getText().toString();
                String textLastName = signupLastName.getText().toString();
                String textUsername = signupUsername.getText().toString();
                String textEmailAddress = signupEmailAddress.getText().toString();
                String textDateOfBirth = signupDateOfBirth.getText().toString();
                String textMobile = signupMobile.getText().toString();
                String textPassword = signupPassword.getText().toString();
                String textPasswordConfirm = signupPasswordConfirm.getText().toString();
                String textGender;
                boolean hasError = false;

                //Validate mobile no
                String mobileRegex = "[0][8][0-9]{11}";
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(textMobile);

                if (TextUtils.isEmpty(textFirstName)) {
                    Toast.makeText(SignupActivity.this, "Please enter your first name", Toast.LENGTH_SHORT).show();
                    signupFirstName.setError("This field is required");
                    signupFirstName.requestFocus();
                    hasError = true;
                }

                if (TextUtils.isEmpty(textLastName)) {
                    Toast.makeText(SignupActivity.this, "Please enter your last name", Toast.LENGTH_SHORT).show();
                    signupLastName.setError("This field is required");
                    signupLastName.requestFocus();
                    hasError = true;
                }

                if (TextUtils.isEmpty(textUsername)) {
                    Toast.makeText(SignupActivity.this, "Please enter your username", Toast.LENGTH_SHORT).show();
                    signupUsername.setError("This field is required");
                    signupUsername.requestFocus();
                    hasError = true;
                }

                if (TextUtils.isEmpty(textEmailAddress)) {
                    Toast.makeText(SignupActivity.this, "Please enter your email address", Toast.LENGTH_SHORT).show();
                    signupEmailAddress.setError("This field is required");
                    signupEmailAddress.requestFocus();
                    hasError = true;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmailAddress).matches()) {
                    Toast.makeText(SignupActivity.this, "Please check your email address", Toast.LENGTH_SHORT).show();
                    signupEmailAddress.setError("Invalid email address");
                    signupEmailAddress.requestFocus();
                    hasError = true;
                }

                if (TextUtils.isEmpty(textDateOfBirth)) {
                    Toast.makeText(SignupActivity.this, "Please enter your date of birth", Toast.LENGTH_SHORT).show();
                    signupDateOfBirth.setError("This field is required");
                    signupDateOfBirth.requestFocus();
                    hasError = true;
                }

                if (radioGroupSignupGender.getCheckedRadioButtonId()==-1) {
                    Toast.makeText(SignupActivity.this, "Please select your gender", Toast.LENGTH_SHORT).show();
                    radioGroupSignupGenderSelected.setError("This field is required");
                    radioGroupSignupGenderSelected.requestFocus();
                    hasError = true;
                }

                if (TextUtils.isEmpty(textMobile)) {
                    Toast.makeText(SignupActivity.this, "Please enter your mobile number", Toast.LENGTH_SHORT).show();
                    signupMobile.setError("This field is required");
                    signupMobile.requestFocus();
                    hasError = true;
                } else if (!mobileMatcher.find()) {
                    Toast.makeText(SignupActivity.this, "Please check your mobile number", Toast.LENGTH_SHORT).show();
                    signupMobile.setError("Invalid mobile number");
                    signupMobile.requestFocus();
                    hasError = true;
                }

                if (TextUtils.isEmpty(textPassword)) {
                    Toast.makeText(SignupActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    signupPassword.setError("This field is required");
                    signupPassword.requestFocus();
                    hasError = true;
                } else if (textPassword.length() < 8) {
                    Toast.makeText(SignupActivity.this, "Password should be at least 8 digits", Toast.LENGTH_SHORT).show();
                    signupPassword.setError("Password too weak");
                    signupPassword.requestFocus();
                    hasError = true;
                }

                if (TextUtils.isEmpty(textPasswordConfirm)) {
                    Toast.makeText(SignupActivity.this, "Please re-enter your password", Toast.LENGTH_SHORT).show();
                    signupPasswordConfirm.setError("This field is required");
                    signupPasswordConfirm.requestFocus();
                    hasError = true;
                }

                if (!textPassword.equals(textPasswordConfirm)) {
                    Toast.makeText(SignupActivity.this, "Please check your password", Toast.LENGTH_SHORT).show();
                    signupFirstName.setError("Password is not the same");
                    signupFirstName.requestFocus();
                    signupPasswordConfirm.clearComposingText();
                    hasError = true;
                }

                if (!hasError) {
                    textGender = radioGroupSignupGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFirstName, textLastName, textUsername, textEmailAddress, textDateOfBirth, textGender, textMobile, textPassword);
                }
            }
        });

    }

    private void registerUser(String textFirstName, String textLastName, String textUsername, String textEmailAddress, String textDateOfBirth, String textGender, String textMobile, String textPassword) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        //Create User Profile
        auth.createUserWithEmailAndPassword(textEmailAddress, textPassword).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    //Update User Display Name
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFirstName + textLastName).build();
                    firebaseUser.updateProfile(profileChangeRequest);

                    //Insert User Data into the Firebase Realtime Database.
                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textUsername, textDateOfBirth, textGender, textMobile);

                    //Extracting User Reference from Database for "Registered Users"
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                //Send Verification Email
                                firebaseUser.sendEmailVerification();
                                Toast.makeText(SignupActivity.this, "Signed up successfully. Please verify your email", Toast.LENGTH_LONG).show();

                                //Open User Profile after successful registration
                                Intent intent = new Intent(SignupActivity.this, UserProfileActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish(); //To close SignupActivity

                            } else {
                                Toast.makeText(SignupActivity.this, "Sign up failed. Please try again", Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e){
                        signupPassword.setError("Your password is too weak. Use a combination of captial letters, numbers, and special characters");
                        signupPassword.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e){
                        signupEmailAddress.setError("Email already registered");
                        signupEmailAddress.requestFocus();
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());{
                            Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }
}