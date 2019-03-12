package com.example.adrian.cndapp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class AddDetailActivity extends AppCompatActivity {

    EditText mTitleEt, mDescriptionEt, mAddressEt, mContactPersonEt, mPhoneEt, mFaxEt, mEmailEt,
            mWebsiteEt, mFacebookEt;
    ImageView mDetailIv;
    String mDetailImage;
    Button mAddBtn;

    String mStoragePath = "Images/";
    String mDatabasePath = "Data";
    Uri mFilePathUri;
    DatabaseReference mDatabaseReference;
    ProgressDialog mProgressDialog;
    int IMAGE_REQUEST_CODE = 5;
    String cImage, cTitle, cDescription, cPhone, cEmail, cContactPerson, cFacebook, cWebsite, cFax, cAddress;
    private StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_detail);

        mTitleEt = findViewById(R.id.pTitleEt);
        mDescriptionEt = findViewById(R.id.pDescriptionEt);
        mAddressEt = findViewById(R.id.pAddressEt);
        mContactPersonEt = findViewById(R.id.pContactPersonEt);
        mPhoneEt = findViewById(R.id.pPhoneEt);
        mFaxEt = findViewById(R.id.pFaxEt);
        mEmailEt = findViewById(R.id.pEmailEt);
        mWebsiteEt = findViewById(R.id.pWebsiteEt);
        mFacebookEt = findViewById(R.id.pFacebookEt);
        mDetailIv = findViewById(R.id.pImageIv);
        mAddBtn = findViewById(R.id.pAddBtn);

        Bundle intent = getIntent().getExtras();
        if (intent != null) {
            cImage = intent.getString("cImage");
            cTitle = intent.getString("cTitle");
            cDescription = intent.getString("cDescription");
            cPhone = intent.getString("cPhone");
            cEmail = intent.getString("cEmail");
            cContactPerson = intent.getString("cContactPerson");
            cFacebook = intent.getString("cFacebook");
            cWebsite = intent.getString("cWebsite");
            cFax = intent.getString("cFax");
            cAddress = intent.getString("cAddress");

            mTitleEt.setText(cTitle);
            mDescriptionEt.setText(cDescription);
            mAddressEt.setText(cAddress);
            mContactPersonEt.setText(cContactPerson);
            mPhoneEt.setText(cPhone);
            mFaxEt.setText(cFax);
            mEmailEt.setText(cEmail);
            mWebsiteEt.setText(cWebsite);
            mFacebookEt.setText(cFacebook);
            Picasso.get().load(cImage).into(mDetailIv);

            mAddBtn.setText("Update");
        }

        mDetailIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"),
                        IMAGE_REQUEST_CODE);
            }
        });

        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAddBtn.getText().equals("Add")) {
                    uploadDataToFirebase();
                } else {
                    beginUpdate();
                }
            }
        });

        mStorageReference = getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(mDatabasePath);
        mProgressDialog = new ProgressDialog(AddDetailActivity.this);
    }

    private void beginUpdate() {
        mProgressDialog.setMessage("Updating...");
        mProgressDialog.show();
        deletePreviousImage();
    }

    private void deletePreviousImage() {
        StorageReference mPictureRef = getInstance().getReferenceFromUrl(cImage);
        mPictureRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AddDetailActivity.this, "Previous image deleted.", Toast.LENGTH_SHORT).show();
                uploadNewImage();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }

    private void uploadNewImage() {
        String imageName = System.currentTimeMillis() + ".png";
        StorageReference storageReference2 = mStorageReference.child(mStoragePath + imageName);
        Bitmap bitmap = ((BitmapDrawable) mDetailIv.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = storageReference2.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddDetailActivity.this, "New image uploaded.", Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                Uri downloadUri = uriTask.getResult();
                updateDatabase(downloadUri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }

    private void updateDatabase(final String image) {
        final String title = mTitleEt.getText().toString();
        final String description = mDescriptionEt.getText().toString();
        final String address = mAddressEt.getText().toString();
        final String contactPerson = mContactPersonEt.getText().toString();
        final String phone = mPhoneEt.getText().toString();
        final String fax = mFaxEt.getText().toString();
        final String email = mEmailEt.getText().toString();
        final String website = mWebsiteEt.getText().toString();
        final String facebook = mFacebookEt.getText().toString();
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mRef = mFirebaseDatabase.getReference("Data");

        Query query = mRef.orderByChild("title").equalTo(cTitle);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ds.getRef().child("title").setValue(title);
                    ds.getRef().child("description").setValue(description);
                    ds.getRef().child("address").setValue(address);
                    ds.getRef().child("contactPerson").setValue(contactPerson);
                    ds.getRef().child("phone").setValue(phone);
                    ds.getRef().child("fax").setValue(fax);
                    ds.getRef().child("email").setValue(email);
                    ds.getRef().child("website").setValue(website);
                    ds.getRef().child("facebook").setValue(facebook);
                    ds.getRef().child("image").setValue(image);
                }
                mProgressDialog.dismiss();
                Toast.makeText(AddDetailActivity.this, "Data updated.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddDetailActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void uploadDataToFirebase() {
        if (mFilePathUri != null) {
            mProgressDialog.setTitle("Uploading");
            mProgressDialog.show();
            StorageReference storageReference2 = mStorageReference.child(mStoragePath +
                    System.currentTimeMillis() + "." + getFileExtention(mFilePathUri));

            storageReference2.putFile(mFilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            Uri downloadUri = uriTask.getResult();

                            String mDetailTitle = mTitleEt.getText().toString().trim();
                            String mDetailDescription = mDescriptionEt.getText().toString().trim();
                            String mDetailAddress = mAddressEt.getText().toString().trim();
                            String mDetailContactPerson = mContactPersonEt.getText().toString().trim();
                            String mDetailPhone = mPhoneEt.getText().toString().trim();
                            String mDetailFax = mFaxEt.getText().toString().trim();
                            String mDetailEmail = mEmailEt.getText().toString().trim();
                            String mDetailWebsite = mWebsiteEt.getText().toString().trim();
                            String mDetailFacebook = mFacebookEt.getText().toString().trim();
                            mProgressDialog.dismiss();
                            Toast.makeText(AddDetailActivity.this, "Uploaded successfully.",
                                    Toast.LENGTH_SHORT).show();

                            ImageUploadInfo imageUploadInfo = new ImageUploadInfo(mDetailTitle,
                                    mDetailDescription, mDetailAddress, mDetailContactPerson,
                                    mDetailPhone, mDetailFax, mDetailEmail, mDetailWebsite, mDetailFacebook,
                                    downloadUri.toString());

                            String imageUploadId = mDatabaseReference.push().getKey();
                            mDatabaseReference.child(imageUploadId).setValue(imageUploadInfo);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgressDialog.dismiss();
                            Toast.makeText(AddDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            mProgressDialog.setTitle("Uploading...");
                        }
                    });
        } else {
            Toast.makeText(this, "Please select image", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtention(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null &&
                data.getData() != null) {
            mFilePathUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                        mFilePathUri);
                mDetailIv.setImageBitmap(bitmap);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
