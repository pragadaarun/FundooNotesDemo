package com.example.fundoonotes.UI.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fundoonotes.Firebase.Model.FirebaseUserModel;
import com.example.fundoonotes.UI.Activity.LoginRegisterActivity;
import com.example.fundoonotes.DashBoard.Activity.HomeActivity;
import com.example.fundoonotes.R;
import com.example.fundoonotes.UI.Activity.SharedPreferenceHelper;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginFragment extends Fragment {

    private EditText emailText, passwordText;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "LoginFragment";
    private final int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;
    SharedPreferenceHelper sharedPreferenceHelper;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login,
                container, false);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        emailText = (EditText) getView().findViewById(R.id.emailText);
        passwordText = (EditText) getView().findViewById(R.id.passwordText);
        Button loginButton = (Button) getView().findViewById(R.id.loginButton);
        TextView signUpText = (TextView) getView().findViewById(R.id.signUpText);
        TextView forgotPassword = (TextView) getView().findViewById(R.id.forgotPassword);
        SignInButton googleSignIn = getView().findViewById(R.id.googleSignIN);
        sharedPreferenceHelper = new SharedPreferenceHelper(getContext());
        GoogleSignInOptions gsi = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gsi);
        loginButton.setOnClickListener(this::logInAction);
        signUpText.setOnClickListener(v -> {
            ((LoginRegisterActivity) getActivity()).navigateToRegister();
        });

        forgotPassword.setOnClickListener(this::resetPassword);
        googleSignIn.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn
                    .getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this::googleSignInAction);
    }

    private void resetPassword(View v) {
        EditText resetMail = new EditText(v.getContext());
        final AlertDialog.Builder passwordResetDialog = new AlertDialog
                .Builder(v.getContext());
        passwordResetDialog.setTitle("Reset Password");
        passwordResetDialog.setMessage("Enter Your Registered Mail");
        passwordResetDialog.setView(resetMail);

        passwordResetDialog.setPositiveButton("Reset",
                (dialog, which) -> {
                    String mail = resetMail.getText().toString();
                    mAuth.sendPasswordResetEmail(mail)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getContext(),
                                            "Reset Link Sent To Your Email",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(),
                                         "Error! Reset Link Not Sent ",
                                            Toast.LENGTH_SHORT).show();
                        }
                    });
                }).setNegativeButton("Cancel", (dialog, which) -> {
            // close the dialog
        });
        passwordResetDialog.create().show();
    }

    private void logInAction(View v) {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (TextUtils.isEmpty(email) || !email.contains("@")) {
            emailText.setError("Requires Email Address");
            Toast.makeText(getContext(), "Please Enter valid Email Address",
                    Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(password)) {
            passwordText.setError("Requires password");
            Toast.makeText(getContext(), "Please Enter valid Password",
                    Toast.LENGTH_SHORT).show();
        }else if (password.length() < 8) {
            passwordText.setError("Enter minimum Eight Characters");
            Toast.makeText(getContext(), "Password should contain at least 8 Characters",
                    Toast.LENGTH_LONG).show();
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                    task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Login Successful!!",
                                    Toast.LENGTH_LONG).show();
                            sharedPreferenceHelper.setIsLoggedIn(true);
                            getActivity().finish();
                            Intent intent = new Intent(getContext(),
                                    HomeActivity.class);
                            startActivity(intent);
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                passwordText.setError("Password is Wrong");
                                passwordText.requestFocus();
                            } catch (FirebaseAuthUserCollisionException e) {
                                emailText.setError("Email Address is already Active in Another Device");
                                emailText.requestFocus();
                            } catch (FirebaseAuthInvalidUserException e) {
                                emailText.setError("Email Address is Not Registered");
                                emailText.requestFocus();
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                    });
        }
    }

    private void googleSignInAction(Task<AuthResult> task) {
        if (task.isSuccessful()) {
            GoogleSignInAccount account = GoogleSignIn
                    .getLastSignedInAccount(getContext());
            if (account != null) {
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                String userName = firebaseUser.getDisplayName();
                String email = firebaseUser.getEmail();
                FirebaseUserModel model = new FirebaseUserModel(userName, email);
                Log.e(TAG, "googleSignInAction: " + userName + " " + email);
                firebaseFirestore.collection("users")
                        .document(firebaseUser.getUid()).set(model.asMap());
                Toast.makeText(getContext(), "Signed in using" + email, Toast.LENGTH_SHORT).show();
            }
            // Sign in success, update UI with the signed-in user's information
            Log.d(TAG, "signInWithCredential:success");
            sharedPreferenceHelper.setIsLoggedIn(true);
            FirebaseUser user = mAuth.getCurrentUser();
            Intent intent = new Intent(getContext(), HomeActivity.class);
            startActivity(intent);
            getActivity().finish();
        } else {
            // If sign in fails, display a message to the user.
            Log.w(TAG, "signInWithCredential:failure", task.getException());
        }
    }
}