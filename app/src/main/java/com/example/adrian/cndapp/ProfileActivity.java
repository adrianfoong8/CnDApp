package com.example.adrian.cndapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private Button btnChangeEmail, btnChangePassword, btnSendPasswordResetEmail, btnDeleteUser, btnBack,
            changeEmail, changePassword, sendEmail, delete, btnLogout;
    private EditText oldEmail, newEmail, oldPassword, newPassword;
    private TextInputLayout oldEmailLayout, newEmailLayout, oldPasswordLayout, newPasswordLayout;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        btnChangeEmail = (Button) findViewById(R.id.btn_change_email);
        btnChangePassword = (Button) findViewById(R.id.btn_change_password);
        btnSendPasswordResetEmail = (Button) findViewById(R.id.btn_send_password_reset_email);
        btnDeleteUser = (Button) findViewById(R.id.btn_delete_user);
        btnBack = (Button) findViewById(R.id.btn_back);
        changeEmail = (Button) findViewById(R.id.change_email);
        changePassword = (Button) findViewById(R.id.change_password);
        sendEmail = (Button) findViewById(R.id.send);
        delete = (Button) findViewById(R.id.delete);
        btnLogout = (Button) findViewById(R.id.logout);

        oldEmail = (EditText) findViewById(R.id.old_email);
        newEmail = (EditText) findViewById(R.id.new_email);
        oldPassword = (EditText) findViewById(R.id.old_password);
        newPassword = (EditText) findViewById(R.id.new_password);

        oldEmailLayout = (TextInputLayout) findViewById(R.id.old_email_layout);
        newEmailLayout = (TextInputLayout) findViewById(R.id.new_email_layout);
        oldPasswordLayout = (TextInputLayout) findViewById(R.id.old_password_layout);
        newPasswordLayout = (TextInputLayout) findViewById(R.id.new_password_layout);

        oldEmail.setVisibility(View.GONE);
        newEmail.setVisibility(View.GONE);
        oldPassword.setVisibility(View.GONE);
        newPassword.setVisibility(View.GONE);
        changeEmail.setVisibility(View.GONE);
        changePassword.setVisibility(View.GONE);
        sendEmail.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);
        btnBack.setVisibility(View.GONE);
        oldEmailLayout.setVisibility(View.GONE);
        newEmailLayout.setVisibility(View.GONE);
        oldPasswordLayout.setVisibility(View.GONE);
        newPasswordLayout.setVisibility(View.GONE);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail.setVisibility(View.GONE);
                newEmail.setVisibility(View.GONE);
                oldPassword.setVisibility(View.GONE);
                newPassword.setVisibility(View.GONE);
                changeEmail.setVisibility(View.GONE);
                changePassword.setVisibility(View.GONE);
                sendEmail.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
                btnBack.setVisibility(View.GONE);
                btnChangeEmail.setVisibility(View.VISIBLE);
                btnChangePassword.setVisibility(View.VISIBLE);
                btnSendPasswordResetEmail.setVisibility(View.VISIBLE);
                btnDeleteUser.setVisibility(View.VISIBLE);
                btnLogout.setVisibility(View.VISIBLE);
                oldEmailLayout.setVisibility(View.GONE);
                newEmailLayout.setVisibility(View.GONE);
                oldPasswordLayout.setVisibility(View.GONE);
                newPasswordLayout.setVisibility(View.GONE);
            }
        });

        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail.setVisibility(View.GONE);
                newEmail.setVisibility(View.VISIBLE);
                oldPassword.setVisibility(View.GONE);
                newPassword.setVisibility(View.GONE);
                changeEmail.setVisibility(View.VISIBLE);
                changePassword.setVisibility(View.GONE);
                sendEmail.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
                btnBack.setVisibility(View.VISIBLE);
                btnChangeEmail.setVisibility(View.GONE);
                btnChangePassword.setVisibility(View.GONE);
                btnSendPasswordResetEmail.setVisibility(View.GONE);
                btnDeleteUser.setVisibility(View.GONE);
                btnLogout.setVisibility(View.GONE);
                oldEmailLayout.setVisibility(View.GONE);
                newEmailLayout.setVisibility(View.VISIBLE);
                oldPasswordLayout.setVisibility(View.GONE);
                newPasswordLayout.setVisibility(View.GONE);
            }
        });

        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (user != null && !newEmail.getText().toString().trim().equals("")) {
                    user.updateEmail(newEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ProfileActivity.this, "Email address is updated. Please sign in with new email id!", Toast.LENGTH_LONG).show();
                                        logout();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(ProfileActivity.this, "Failed to update email!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                } else if (newEmail.getText().toString().trim().equals("")) {
                    newEmail.setError("Enter email");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail.setVisibility(View.GONE);
                newEmail.setVisibility(View.GONE);
                oldPassword.setVisibility(View.GONE);
                newPassword.setVisibility(View.VISIBLE);
                changeEmail.setVisibility(View.GONE);
                changePassword.setVisibility(View.VISIBLE);
                sendEmail.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
                btnBack.setVisibility(View.VISIBLE);
                btnChangeEmail.setVisibility(View.GONE);
                btnChangePassword.setVisibility(View.GONE);
                btnSendPasswordResetEmail.setVisibility(View.GONE);
                btnDeleteUser.setVisibility(View.GONE);
                btnLogout.setVisibility(View.GONE);
                oldEmailLayout.setVisibility(View.GONE);
                newEmailLayout.setVisibility(View.GONE);
                oldPasswordLayout.setVisibility(View.GONE);
                newPasswordLayout.setVisibility(View.VISIBLE);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (user != null && !newPassword.getText().toString().trim().equals("")) {
                    if (newPassword.getText().toString().trim().length() < 6) {
                        newPassword.setError("Password too short, enter minimum 6 characters");
                        progressBar.setVisibility(View.GONE);
                    } else {
                        user.updatePassword(newPassword.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ProfileActivity.this, "Password is updated, sign in with new oldPassword!", Toast.LENGTH_SHORT).show();
                                            logout();
                                            progressBar.setVisibility(View.GONE);
                                        } else {
                                            Toast.makeText(ProfileActivity.this, "Failed to update oldPassword!", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                    }
                } else if (newPassword.getText().toString().trim().equals("")) {
                    newPassword.setError("Enter new password");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        btnSendPasswordResetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail.setVisibility(View.VISIBLE);
                newEmail.setVisibility(View.GONE);
                oldPassword.setVisibility(View.GONE);
                newPassword.setVisibility(View.GONE);
                changeEmail.setVisibility(View.GONE);
                changePassword.setVisibility(View.GONE);
                sendEmail.setVisibility(View.VISIBLE);
                delete.setVisibility(View.GONE);
                btnBack.setVisibility(View.VISIBLE);
                btnChangeEmail.setVisibility(View.GONE);
                btnChangePassword.setVisibility(View.GONE);
                btnSendPasswordResetEmail.setVisibility(View.GONE);
                btnDeleteUser.setVisibility(View.GONE);
                btnLogout.setVisibility(View.GONE);
                oldEmailLayout.setVisibility(View.VISIBLE);
                newEmailLayout.setVisibility(View.GONE);
                oldPasswordLayout.setVisibility(View.GONE);
                newPasswordLayout.setVisibility(View.GONE);
            }
        });

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (!oldEmail.getText().toString().trim().equals("")) {
                    auth.sendPasswordResetEmail(oldEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ProfileActivity.this, "Reset oldPassword email is sent!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(ProfileActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                } else {
                    oldEmail.setError("Enter email");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        btnDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Delete User");
                builder.setMessage("User account for " + user.getEmail() + " will be deleted.");
                builder.setIcon(R.drawable.ic_action_warning);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(ProfileActivity.this);
                        builder2.setTitle("ARE YOU SURE?");
                        builder2.setMessage("You will no longer be able to login with " + user.getEmail());
                        builder2.setIcon(R.drawable.ic_action_warning);
                        builder2.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder2.setNegativeButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressBar.setVisibility(View.VISIBLE);
                                if (user != null) {
                                    user.delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(ProfileActivity.this, "Your profile is deleted!", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                                                        finish();
                                                        progressBar.setVisibility(View.GONE);
                                                    } else {
                                                        Toast.makeText(ProfileActivity.this, "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                                                        progressBar.setVisibility(View.GONE);
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                        AlertDialog alertDialog = builder2.create();
                        alertDialog.show();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Logout?");
                builder.setMessage("Are you sure you want to logout?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    //sign out method
    public void logout() {
        auth.signOut();
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}
