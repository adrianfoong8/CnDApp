package com.example.adrian.cndapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class MainActivity extends AppCompatActivity {

    private ImageButton btnProfile, btnAboutUs, btnAddDetail, btnDonate;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef, mRef2;
    private List<Model> list;
    private RecyclerView mRecyclerView;
    private TextView txtTotalDonation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("Data");
        mRef2 = mFirebaseDatabase.getReference("Total");

        txtTotalDonation = (TextView) findViewById(R.id.totalDonationTv);
        mRef2.child("Donation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String totalDonation = dataSnapshot.getValue().toString();
                txtTotalDonation.setText(totalDonation);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        btnProfile = (ImageButton) findViewById(R.id.btn_profile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });

        btnAboutUs = (ImageButton) findViewById(R.id.btn_about_us);
        btnAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
            }
        });

        btnAddDetail = (ImageButton) findViewById(R.id.btn_add_detail);
        btnAddDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddDetailActivity.class));
            }
        });

        btnDonate = (ImageButton) findViewById(R.id.btn_donate);
        btnDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PayPalActivity.class));
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
    }

    private void showDeleteDialog(final String currentTitle, final String currentImage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Delete?");
        builder.setMessage("Are you sure to delete " + currentTitle + "?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Query mQuery = mRef.orderByChild("title").equalTo(currentTitle);
                mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(MainActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                StorageReference mPictureRef = getInstance().getReferenceFromUrl(currentImage);
                mPictureRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Image deleted successfully", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);

        FirebaseRecyclerAdapter<Model, ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Model, ViewHolder>(
                Model.class, R.layout.row, ViewHolder.class, mRef) {
            @Override
            protected void populateViewHolder(ViewHolder viewHolder, Model model, int position) {
                viewHolder.setDetails(getApplicationContext(), model.getTitle(), model.getDescription(), model.getImage());
            }

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String mTitle = getItem(position).getTitle();
                        String mDescription = getItem(position).getDescription();
                        String mPhone = getItem(position).getPhone();
                        String mEmail = getItem(position).getEmail();
                        String mImage = getItem(position).getImage();
                        String mContactPerson = getItem(position).getContactPerson();
                        String mFacebook = getItem(position).getFacebook();
                        String mWebsite = getItem(position).getWebsite();
                        String mFax = getItem(position).getFax();
                        String mAddress = getItem(position).getAddress();

                        Intent intent = new Intent(view.getContext(), MoreDetailActivity.class);
                        intent.putExtra("image", mImage);
                        intent.putExtra("title", mTitle);
                        intent.putExtra("description", mDescription);
                        intent.putExtra("phone", mPhone);
                        intent.putExtra("email", mEmail);
                        intent.putExtra("contactPerson", mContactPerson);
                        intent.putExtra("facebook", mFacebook);
                        intent.putExtra("website", mWebsite);
                        intent.putExtra("fax", mFax);
                        intent.putExtra("address", mAddress);
                        startActivity(intent);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        final String cTitle = getItem(position).getTitle();
                        final String cDescription = getItem(position).getDescription();
                        final String cPhone = getItem(position).getPhone();
                        final String cEmail = getItem(position).getEmail();
                        final String cImage = getItem(position).getImage();
                        final String cContactPerson = getItem(position).getContactPerson();
                        final String cFacebook = getItem(position).getFacebook();
                        final String cWebsite = getItem(position).getWebsite();
                        final String cFax = getItem(position).getFax();
                        final String cAddress = getItem(position).getAddress();

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        String[] options = {"Update", "Delete"};
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    Intent intent = new Intent(MainActivity.this, AddDetailActivity.class);
                                    intent.putExtra("cImage", cImage);
                                    intent.putExtra("cTitle", cTitle);
                                    intent.putExtra("cDescription", cDescription);
                                    intent.putExtra("cPhone", cPhone);
                                    intent.putExtra("cEmail", cEmail);
                                    intent.putExtra("cContactPerson", cContactPerson);
                                    intent.putExtra("cFacebook", cFacebook);
                                    intent.putExtra("cWebsite", cWebsite);
                                    intent.putExtra("cFax", cFax);
                                    intent.putExtra("cAddress", cAddress);
                                    startActivity(intent);
                                }
                                if (which == 1) {
                                    showDeleteDialog(cTitle, cImage);
                                }
                            }
                        });
                        builder.create().show();
                    }
                });
                return viewHolder;
            }
        };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}