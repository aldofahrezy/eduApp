package com.example.neatnoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText emailAddressInput, passwordInput;
    private Button loginBtn;
    private TextView signupBtn;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailAddressInput = (EditText) findViewById(R.id.email_address_input);
        passwordInput = (EditText) findViewById(R.id.password_input);
        progressBar = findViewById(R.id.progress_bar);
        loginBtn = (Button) findViewById(R.id.login_btn);
        signupBtn = (TextView) findViewById(R.id.signup_page_btn);
        authProfile = FirebaseAuth.getInstance();

        //Show or Hide password
        ImageView showHidePassword = findViewById(R.id.show_hide_password);
        showHidePassword.setImageResource(R.drawable.visibility_icon);
        showHidePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordInput.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showHidePassword.setImageResource(R.drawable.visibility_icon);
                } else {
                    passwordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showHidePassword.setImageResource(R.drawable.visibility_off_icon);
                }
            }
        });

        //Login Button
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textEmailAddress = emailAddressInput.getText().toString();
                String textPassword = passwordInput.getText().toString();
                boolean hasError = false;

                if (TextUtils.isEmpty(textEmailAddress)){
                    emailAddressInput.setError("This field is required");
                    emailAddressInput.requestFocus();
                    hasError = true;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmailAddress).matches()){
                    emailAddressInput.setError("Invalid Email Address");
                    emailAddressInput.requestFocus();
                    hasError = true;
                }

                if (TextUtils.isEmpty(textPassword)){
                    passwordInput.setError("This field is required");
                    passwordInput.requestFocus();
                    hasError = true;
                }

                if (!hasError){
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmailAddress, textPassword);
                }
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             Intent intent = new Intent(MainActivity.this, SignupActivity.class);
             startActivity(intent);
            }
        });

    }

    private void loginUser(String emailAddress, String password) {
        authProfile.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    //Get instance of current user
                    FirebaseUser firebaseUser = authProfile.getCurrentUser();

                    //Check if email is verified
                    if (firebaseUser.isEmailVerified()){
                        Toast.makeText(MainActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
                        finish();

                    } else {
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut();
                        showAlertDialog();
                    }

                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        emailAddressInput.setError("User does not exist or is no longer valid. Please sign up again");
                        emailAddressInput.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(MainActivity.this, "Incorrect Email or Password", Toast.LENGTH_SHORT).show();
                        passwordInput.setText("");
                    } catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showAlertDialog() {
        //Setup Alert Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

    //Check if user is logged in
    @Override
    protected void onStart() {
        super.onStart();
        if (authProfile.getCurrentUser() != null) {
            Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
            finish();
        }
    }
}
