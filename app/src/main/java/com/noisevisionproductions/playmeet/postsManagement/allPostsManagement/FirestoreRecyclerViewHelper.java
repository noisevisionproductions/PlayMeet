package com.noisevisionproductions.playmeet.postsManagement.allPostsManagement;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.paging.LoadState;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.Query;
import com.noisevisionproductions.playmeet.PostModel;
import com.noisevisionproductions.playmeet.R;

public class FirestoreRecyclerViewHelper {
    public static void setupRecyclerView(Query query, RecyclerView recyclerView, FragmentManager fragmentManager, Context context, LifecycleOwner lifecycleOwner, View view) {
        ProgressBar progressBar = view.findViewById(R.id.progressBarLayout);
        AppCompatTextView noPostFound = view.findViewById(R.id.noPostFound);
        int pageSize = 5;
        PagingConfig config = new PagingConfig(pageSize, 1, true, 10, 100);

        FirestorePagingOptions<PostModel> options = new FirestorePagingOptions.Builder<PostModel>()
                .setLifecycleOwner(lifecycleOwner)
                .setQuery(query, config, PostModel.class)
                .build();

        AdapterAllPosts adapterAllPosts = new AdapterAllPosts(options, fragmentManager, context);
        recyclerView.setAdapter(adapterAllPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);

        adapterAllPosts.addLoadStateListener(loadStates -> {
            if (loadStates.getRefresh() instanceof LoadState.NotLoading) {
                noPostFound.setVisibility(adapterAllPosts.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
            if (loadStates.getRefresh() instanceof LoadState.Loading) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
            return null;
        });
    }
}
