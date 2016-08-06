package com.example.bobby.hotseat.UI;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bobby.hotseat.Data.Friend;
import com.example.bobby.hotseat.Data.Sponse;
import com.example.bobby.hotseat.Data.Strings;
import com.example.bobby.hotseat.R;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Created by bobby on 6/6/16.
 */

public class InboxFragment extends Fragment {


    private static final String TAG = InboxFragment.class.getSimpleName();

    private RecyclerView mInboxRecyclerView;
    TextView emptyTextView;

    protected String mCurrentUser;

    Firebase mRef = new Firebase("https://hot-seat-28ddb.firebaseio.com/users/"
            + MainActivity.currentUser.getIdToken()
            + "/sponses");// TODO Don't use public static currentUser

    private DatabaseReference mDatabase;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Firebase.setAndroidContext(getActivity());

        // mInboxRecyclerView = (ListView) mInboxRecyclerView.findViewById(R.id.friendsListView);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        mInboxRecyclerView = (RecyclerView) rootView.findViewById(R.id.inboxRecycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mInboxRecyclerView.setLayoutManager(layoutManager);
        mInboxRecyclerView.setHasFixedSize(true);

        emptyTextView = (TextView) rootView.findViewById(R.id.emptyInbox);




        final FirebaseRecyclerAdapter<Sponse, InboxViewHolder> adapter =
                new FirebaseRecyclerAdapter<Sponse, InboxViewHolder>(
                        Sponse.class,
                        //R.layout.recycler_list_item,
                        android.R.layout.two_line_list_item,
                        InboxViewHolder.class,
                        mRef)
                {
/*
                    @Override
                    public void onBindViewHolder(InboxViewHolder inboxViewHolder, int position, List<Object> payloads) {
                        super.onBindViewHolder(inboxViewHolder, position, payloads);
                    }
                    */

                    @Override
                    protected void populateViewHolder(InboxViewHolder inboxViewHolder, Sponse sponse, int i) {

                        Log.d(TAG, "In Populate View Holder.");
                        ((TextView) InboxViewHolder.mText).setText((CharSequence) sponse.getDisplayName());

                    }
                };

        Log.d(TAG, "Adapter created");
        mInboxRecyclerView.setAdapter(adapter);
        Log.d(TAG, "Adapter set");

        Log.d(TAG, "ITEM COUNT" + adapter.getItemCount());

        emptyTextView.setVisibility(View.INVISIBLE); // TODO MAKE THIS WORK

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

    }



    @Override
    public void onStart() {
        super.onStart();
    }

    public static class InboxViewHolder
            extends RecyclerView.ViewHolder implements View.OnClickListener {
        static TextView mText;


        public InboxViewHolder(View v) {
            super(v);
            mText = (TextView) v.findViewById(android.R.id.text1);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Log.d(TAG, "CLICKED");
        }
    }

}