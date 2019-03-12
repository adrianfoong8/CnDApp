package com.example.adrian.cndapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MoreDetailActivity extends AppCompatActivity {

    TextView mTitleTv, mDescriptionTv, mPhoneTv, mEmailTv, mFacebookTv, mWebsiteTv, mFaxTv, mAddressTv, mContactPersonTv;
    ImageView mImageIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_detail);

        mTitleTv = findViewById(R.id.titleTv);
        mDescriptionTv = findViewById(R.id.descriptionTv);
        mPhoneTv = findViewById(R.id.phoneTv);
        mEmailTv = findViewById(R.id.emailTv);
        mImageIv = findViewById(R.id.imageView);
        mAddressTv = findViewById(R.id.addressTv);
        mFacebookTv = findViewById(R.id.facebookTv);
        mFaxTv = findViewById(R.id.faxTv);
        mWebsiteTv = findViewById(R.id.websiteTv);
        mContactPersonTv = findViewById(R.id.contactPersonTv);

        String image = getIntent().getStringExtra("image");
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String phone = getIntent().getStringExtra("phone");
        String email = getIntent().getStringExtra("email");
        String address = getIntent().getStringExtra("address");
        String facebook = getIntent().getStringExtra("facebook");
        String fax = getIntent().getStringExtra("fax");
        String website = getIntent().getStringExtra("website");
        String contactPerson = getIntent().getStringExtra("contactPerson");

        mTitleTv.setText(title);
        mDescriptionTv.setText(description);
        mPhoneTv.setText(phone);
        mEmailTv.setText(email);
        Picasso.get().load(image).into(mImageIv);
        mAddressTv.setText(address);
        mFacebookTv.setText(facebook);
        mFaxTv.setText(fax);
        mWebsiteTv.setText(website);
        mContactPersonTv.setText(contactPerson);
    }
}
