package com.example.flaviomassimo.carcare.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.flaviomassimo.carcare.R;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;

public class ActivitySignIn extends AppCompatActivity implements View.OnClickListener{
    private Button mButton;
    FirebaseUser user;
    private DatabaseReference mRef;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient=GoogleSignIn.getClient(this,gso);
        SharingValues.setGoogleSignInClient(mGoogleSignInClient);
        setContentView(R.layout.activity_sign_in);
        mButton=(Button) findViewById(R.id.login_register);
        findViewById(R.id.sign_in_google).setOnClickListener(this);
        findViewById(R.id.login_register).setOnClickListener(this);}


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.login_register){
            Intent intent = new Intent(ActivitySignIn.this,LoginActivity.class);
            startActivity(intent);
            finish();

        }
        if(i==R.id.sign_in_google){
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 9);
        }
    }
    public void onStart(){
        super.onStart();
        account = GoogleSignIn.getLastSignedInAccount(this);
        user=FirebaseAuth.getInstance().getCurrentUser();
        if(account!=null && user!=null){
            Intent intent=new Intent(ActivitySignIn.this,MainMenuActivity.class);
            startActivity(intent);
            finish();
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 9) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
            user=FirebaseAuth.getInstance().getCurrentUser();
            System.out.println("----Account mAuth iniziale "+FirebaseAuth.getInstance());
            if(account!=null && user!=null){
                    Intent intent=new Intent(ActivitySignIn.this,MainMenuActivity.class);
                    startActivity(intent);
                    finish();}

            else{
                System.out.println("-------------nell'handler del google sign in------- ");
                if(user==null){
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        user = FirebaseAuth.getInstance().getCurrentUser();
                                        System.out.println("------New User-----"+user);
                                        Intent intent=new Intent(ActivitySignIn.this,MainMenuActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }}

                });
                }
            }
        } catch (ApiException e) {
            System.out.println("signInResult:failed code=" +e.getStatusCode());
        }
    }
}
