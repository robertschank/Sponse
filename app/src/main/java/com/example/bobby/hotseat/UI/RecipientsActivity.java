package com.example.bobby.hotseat.UI;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.bobby.hotseat.Data.Friend;
import com.example.bobby.hotseat.Data.Strings;
import com.example.bobby.hotseat.R;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class RecipientsActivity extends AppCompatActivity {

    private static final String TAG = RecipientsActivity.class.getSimpleName();

    protected String mCurrentUser;
    public static List<Friend> mFriendList = new ArrayList<>(); // TODO static?
    public static List<Friend> selectedFriendsList = new ArrayList<>();

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    /*private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };*/

    FirebaseAuth mAuth;
    Firebase mRef;
    private DatabaseReference mDatabase;

    RecyclerView mFriendsRecyclerView;
    Button mSendButton;

    static int purple;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    // Create a storage reference from our app
    StorageReference storageRef = storage.getReferenceFromUrl("gs://hot-seat-28ddb.appspot.com");
    StorageReference imagesRef = storageRef.child("images");

    Uri mMediaUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients);

        Intent intent = getIntent();
        mMediaUri = intent.getData();

        mFriendsRecyclerView = (RecyclerView) findViewById(R.id.friendsRecycler);
        mFriendsRecyclerView.setHasFixedSize(true);
        mFriendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFileToStorage();
            }
        });

        Log.d(TAG, "mMediaUri.getLastPathSegment: " + mMediaUri.getLastPathSegment());

        purple = ContextCompat.getColor(this, R.color.colorAccentPurple);

        int i = checkCallingPermission(String.valueOf(REQUEST_EXTERNAL_STORAGE));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mRef = new Firebase("https://hot-seat-28ddb.firebaseio.com/users/"+ MainActivity.currentUser.getIdToken() +"/friendsHash");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Firebase friendsRef = mRef.child(Strings.KEY_USERS)
                .child(MainActivity.currentUser.getIdToken())
                .child(Strings.KEY_FRIENDSHASH); // TODO Don't use public static currentUser

        final FirebaseRecyclerAdapter<String, FriendViewHolder> adapter =
                new FirebaseRecyclerAdapter<String, FriendViewHolder>(
                        String.class,
                        R.layout.recycler_list_item,
                        //android.R.layout.two_line_list_item,
                        FriendViewHolder.class,
                        mRef.orderByValue())
                 {

                    @Override
                    public void onBindViewHolder(FriendViewHolder holder, int position, List<Object> payloads) {
                        super.onBindViewHolder(holder, position, payloads);
                    }

                    @Override
                    protected void populateViewHolder(FriendViewHolder friendViewHolder, String name, int i) {
                        String key = this.getRef(i).getKey();
                        Log.d(TAG, "S1 = " + key);
                        ((TextView) friendViewHolder.mText).setText(name.toString());
                        Log.d(TAG, "VALUE = " + name);
                        Log.d(TAG, "KEY = " + mRef.child(name.toString()));
                        Log.d(TAG, "INT = " + i);
                        Friend friend = new Friend(key, name, null);
                        mFriendList.add(friend);
                    }
                };

        Log.d(TAG, "Adapter created");
        mFriendsRecyclerView.setAdapter(adapter);
        Log.d(TAG, "Adapter set");
/*
        FirebaseListAdapter<String> adapter =
                new FirebaseListAdapter<String>(this, String.class,
                        //android.R.layout.simple_list_item_checked,
                        R.layout.friend_list_item,
                        friendsRef.orderByValue()) {
                    int i = 0;
                    @Override
                    protected void populateView(View view, String s, int i) {
                        ((TextView) view.findViewById(android.R.id.text1)).setText(s);
                    }
                };

        Log.d(TAG, "ITEMS" + adapter.getCount());

        if (adapter != null) {
            mFriendsRecyclerView.setAdapter(adapter);
            mFriendsRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Log.d(TAG, "ITEMS" + parent.getCount());

                    mSendButton.setVisibility(View.VISIBLE);
                }
            });
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.error_message)
                    .setTitle(R.string.error_title)
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }*/
    }

    public static class FriendViewHolder
                    extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mText;


        public FriendViewHolder(View v) {
            super(v);
            mText = (TextView) v.findViewById(android.R.id.text1);
            v.setOnClickListener(this);
        }
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Friend selectedFriend = mFriendList.get(position);
            boolean selected = selectedFriend.isSelected();
            if (!selected) {
                mText.setBackgroundColor(purple);
                selectedFriendsList.add(selectedFriend);
                mFriendList.get(position).setSelected(true);
            } else {
                mText.setBackgroundColor(Color.TRANSPARENT);
                selectedFriendsList.remove(selectedFriend);
                mFriendList.get(position).setSelected(false);
            }
            for (Friend f:selectedFriendsList) {
                Log.d(TAG, "CLICKED IN FRIENDVIEWHOLDER, " + f.getDisplayName().toString());
            }
        }
    }

    private void uploadFileToStorage() {

        // File or Blob
        //Uri file = Uri.fromFile(new File("path/to/mountains.jpg"));
        Uri file = mMediaUri;
        String lastPathSeg = mMediaUri.getLastPathSegment();
        String contentType = "Unknown Type";
        String contentPath = "Unknown Type";

        if (lastPathSeg.contains(".mp4")) {
            contentType = "video/mp4";
            contentPath = "video";
        } else if (lastPathSeg.contains(".jpg")) {
            contentType = "image/jpeg";
            contentPath  = "image";
        } else {
            Log.d(TAG, "Error finding content type.");
        }

        // Create the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType(contentType)
                .build();

        // Upload file and metadata to the path 'images/mountains.jpg'
        //UploadTask uploadTask = storageRef.child("images/"+file.getLastPathSegment()).putFile(file, metadata);
        UploadTask uploadTask = storageRef.child(contentPath).child(lastPathSeg).putFile(file, metadata);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                System.out.println("Upload is " + progress + "% done");
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Upload is paused");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
            }
        });

    }

}