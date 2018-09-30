package com.example.flaviomassimo.carcare.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.example.flaviomassimo.carcare.R;
/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements
        OnClickListener {

    private static final String TAG = "EmailPassword";

    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button btn_sign_out;
    private Button btn_verify;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Views
        mStatusTextView = findViewById(R.id.status);
        mDetailTextView = findViewById(R.id.detail);
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);

        // Buttons
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.email_create_account_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.verify_email_button).setOnClickListener(this);
        btn_sign_out=(Button)findViewById(R.id.sign_out_button);
        btn_verify=(Button)findViewById(R.id.verify_email_button);
        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null && mAuth.getCurrentUser().isEmailVerified()){
            btn_verify.setEnabled(false);
            btn_verify.setVisibility(View.GONE);
            btn_sign_out.setEnabled(true);
            btn_sign_out.setVisibility(View.VISIBLE);

        }
        if(mAuth.getCurrentUser()!=null && !mAuth.getCurrentUser().isEmailVerified()){
            btn_verify.setEnabled(true);
            btn_verify.setVisibility(View.VISIBLE);
            btn_sign_out.setEnabled(false);
            btn_sign_out.setVisibility(View.GONE);

        }
        // [END initialize_auth]
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        SharingValues.setCurrentUser(mAuth.getCurrentUser());
        SharingValues.setCurrentUserAuth(mAuth);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }


        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            SharingValues.setCurrentUser(mAuth.getCurrentUser());
                            SharingValues.setCurrentUserAuth(mAuth);
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            btn_verify.setEnabled(true);
                            btn_verify.setVisibility(View.VISIBLE);
                            btn_sign_out.setEnabled(false);
                            btn_sign_out.setVisibility(View.GONE);
                        }
                        else { try
                        {
                            throw task.getException();
                        }
                        catch (FirebaseAuthUserCollisionException existEmail) {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Email already used.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);

                            // TODO: Take your action
                        }
                        catch (Exception e)
                        {

                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        }

                    }
                });
        // [END create_user_with_email]
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm() ) {
            Toast.makeText(LoginActivity.this, "Email not verified or wrong credentials.",
                    Toast.LENGTH_SHORT).show();
            return;
        }



        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            SharingValues.setCurrentUser(mAuth.getCurrentUser());
                            SharingValues.setCurrentUserAuth(mAuth);
                            updateUI(user);
                            if(user.isEmailVerified()){
                            Intent i = new Intent(LoginActivity.this,MainMenuActivity.class);
                            startActivity(i);
                            finish();}
                            else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Email not verified",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);}
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            mStatusTextView.setText("Authentication failed");
                        }

                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    private void signOut() {
        mAuth.signOut();
        SharingValues.setCurrentUser(null);
        SharingValues.setCurrentUserAuth(null);
        updateUI(null);
    }

    private void sendEmailVerification() {
        // Disable button
        findViewById(R.id.verify_email_button).setEnabled(false);

        // Send verification email
        // [START send_email_verification]
        SharingValues.setCurrentUser(mAuth.getCurrentUser());
        SharingValues.setCurrentUserAuth(mAuth);
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        findViewById(R.id.verify_email_button).setEnabled(true);
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                            back();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(LoginActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }
    private void back(){
        findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
        findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
        findViewById(R.id.signed_in_buttons).setVisibility(View.GONE);
    }
    private void updateUI(FirebaseUser user) {
        TextView t=(TextView) findViewById(R.id.title_text);
        if (user != null && SharingValues.getLogOut()) {
            mStatusTextView.setText(mAuth.getCurrentUser().getDisplayName());
            mDetailTextView.setText(mAuth.getCurrentUser().getEmail());
            t.setText("Email connected: ");
            findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
            findViewById(R.id.email_password_fields).setVisibility(View.GONE);
            findViewById(R.id.signed_in_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.verify_email_button).setEnabled(!user.isEmailVerified());
        } else {

            t.setText("");
            mStatusTextView.setText("Please Sign In");
            mDetailTextView.setText(null);

            findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
            findViewById(R.id.signed_in_buttons).setVisibility(View.GONE);
            SharingValues.setLogOut(false);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.email_create_account_button) {
            SharingValues.setLogOut(true);
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.email_sign_in_button) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.sign_out_button) {
            signOut();
        } else if (i == R.id.verify_email_button) {
            SharingValues.setLogOut(false);
            sendEmailVerification();

        }
    }
}