package com.technoidtintin.justdoit.MainUIFragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.technoidtintin.justdoit.Activity.AddTaskActivity;
import com.technoidtintin.justdoit.Activity.ScrollingActivity;
import com.technoidtintin.justdoit.Constants.Constants;
import com.technoidtintin.justdoit.Model.UserDetails;
import com.technoidtintin.justdoit.R;
import com.technoidtintin.justdoit.SignUpLogIn.MainActivity;
import com.technoidtintin.justdoit.ViewAnimation;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private StorageReference userImageRef;
    private FirebaseFirestore fireStoredb;

    private String uId;
    private String emailId;
    private String mUserName = "Default";
    private boolean isRotate = false;
    private boolean isNewUser;
    private Bitmap userBitmap;
    private String accountCreated;

    private Toolbar toolbar;
    private TextView mUserTv;
    private FloatingActionButton fab;
    private AlertDialog loadingDialog;
    private ImageView userImage;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        toolbar = view.findViewById(R.id.home_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("");
        //Initializing userName Tv and collapsingToolbarLayout
        mUserTv = view.findViewById(R.id.home_user_name_tv);
        CollapsingToolbarLayout collapsingToolbarLayout = view.findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitleEnabled(true);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.BLACK);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.CollapsingToolbarLayoutExpandedTextStyle);
        collapsingToolbarLayout.setTitle("Just Do It");

        //Initializing Fab button
        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRotate = ViewAnimation.rotateFab(view, !isRotate);
                Intent intent = new Intent(getActivity(), AddTaskActivity.class);
                intent.putExtra(Constants.USER_EMAIL,emailId);
                startActivity(intent);

            }
        });

        //Initializing User Image
        userImage = view.findViewById(R.id.user_image_view);
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseUser != null) {
                    firebaseAuth.signOut();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });

        //Getting isUpdated values
        ScrollingActivity scrollingActivity = (ScrollingActivity) getActivity();
        isNewUser = scrollingActivity.isFirstLogIn();

        //Initializing Firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                //Get Current User
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {

                    mUserName = firebaseUser.getDisplayName();
                    uId = firebaseUser.getUid();
                    emailId = firebaseUser.getEmail();
                    String imageUri = String.valueOf(firebaseUser.getPhotoUrl());
                    if (isNewUser) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale.getDefault());
                        Date date = new Date();
                        accountCreated = simpleDateFormat.format(date);
                        Log.e(TAG,"date: " + accountCreated);

                        loadingDialog = createLoadingDialog(getContext());
                        loadingDialog.show();
                        uploadDetails(new UserDetails(mUserName, emailId,accountCreated));
                    } else {
                        Log.e(TAG, "User data is already uploaded");
                    }

                    mUserTv.setText(mUserName);
                    if (!imageUri.isEmpty()) {
                        Picasso.get().load(firebaseUser.getPhotoUrl())
                                .error(getResources().getDrawable(R.drawable.profile_icon))
                                .placeholder(getResources().getDrawable(R.drawable.profile_icon)).into(userImage);
                    }
                } else {
                    Log.e(TAG, "user is null");
                }
            }
        };

        //Initializing FireBase Storage
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        userImageRef = storageReference.child("ProfileImages");

        //Initializing FireBaseFireStore
        fireStoredb = FirebaseFirestore.getInstance();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    //Upload Images to FireBase Storage
    private void uploadImage(Bitmap bitmap) {

        String imgeName = mUserName.trim();
        StorageReference imageRef = userImageRef.child(imgeName);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                loadingDialog.dismiss();
                Log.e(TAG, "Image Successfully uploaded to FireBase: " + taskSnapshot.getUploadSessionUri());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                loadingDialog.dismiss();
                Log.e(TAG, "Uploading failed to Firebase: " + e.getMessage());
            }
        });
    }

    private void uploadDetails(final UserDetails userDetails) {

        fireStoredb.collection("User_details")
                .document(userDetails.getUserEmail())
                .set(userDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                loadingDialog.dismiss();
                Log.e(TAG, "Data added to fire store ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingDialog.dismiss();
                Log.e(TAG, "Error uploading data, " + e.getMessage());
            }
        });
    }

    //Create Loading dialog
    private AlertDialog createLoadingDialog(Context context) {
        View layout = getLayoutInflater().inflate(R.layout.loading_dialog_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(layout);
        return builder.create();
    }
}
