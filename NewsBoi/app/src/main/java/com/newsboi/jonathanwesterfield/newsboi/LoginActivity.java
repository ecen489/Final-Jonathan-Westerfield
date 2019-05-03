package com.newsboi.jonathanwesterfield.newsboi;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity
{

    private EditText usernameField, passwdField;

    private FirebaseUser fUser;
    private FirebaseAuth mAuth;
    private Button createAcctBtn, loginBtn;
    private String email, passwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initInterfaces();

        mAuth = FirebaseAuth.getInstance();
        fUser = null;
    }

    public void initInterfaces()
    {
        this.usernameField = (EditText) findViewById(R.id.usernameField);
        this.passwdField = (EditText) findViewById(R.id.passwdField);
        this.createAcctBtn = (Button) findViewById(R.id.accountCreateBtn);
        this.loginBtn = (Button) findViewById(R.id.loginBtn);

    }

    /**
     * Creates an account based on whatever Username and Password is in the
     * textfields unless the textfields are empty
     *
     * @param view
     */
    public void createAcctClk(View view)
    {
        // test if the textfields are empty
        if (this.usernameField.getText().toString().equalsIgnoreCase("")
                || this.passwdField.getText().toString().equalsIgnoreCase(""))
            Toast.makeText(getApplicationContext(), "Enter Username and/or password", Toast.LENGTH_SHORT)
                    .show();

        email = usernameField.getText().toString();
        passwd = passwdField.getText().toString();

        // if account creation is successful, switch to the pull activity

        // Check to make sure there is a user so we can change activities
        createAccount(email, passwd);

        return;
    }

    /**
     * Actually creates the account using the provided email and password
     * @param email
     * @param password
     * @return
     */
    public void createAccount(String email, String password)
    {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Fuckups", "createUserWithEmail:success");
                            Toast.makeText(getApplicationContext(), "Account Creation Successful.", Toast.LENGTH_SHORT)
                                    .show();
                            fUser = mAuth.getCurrentUser();
                            switchToMainActivity();
                        }
                        else
                        {
                            // If sign in fails, display a message to the user.
                            Log.w("Fuckups", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Creation failed. User might already exist.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Click event for the login button
     *
     * @param view
     */
    public void loginClk(View view)
    {
        // test if the textfields are empty
        if (this.usernameField.getText().toString().equalsIgnoreCase("")
                || this.passwdField.getText().toString().equalsIgnoreCase(""))
            Toast.makeText(getApplicationContext(), "Enter Username and/or password", Toast.LENGTH_SHORT)
                    .show();

        email = usernameField.getText().toString();
        passwd = passwdField.getText().toString();

        login(email, passwd);
    }

    /**
     * Actually logs in the user using the provided email and password
     * @param email
     * @param passwd
     * @return
     */
    public void login(String email, String passwd)
    {
        mAuth.signInWithEmailAndPassword(email, passwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            // Sign in success, update UI with the signed-in user's information
                            fUser = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), "Login Successful.", Toast.LENGTH_SHORT)
                                    .show();
                            switchToMainActivity();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void switchToMainActivity()
    {
        Intent goPull = new Intent(this, MainActivity.class);
        startActivity(goPull);
    }
}
