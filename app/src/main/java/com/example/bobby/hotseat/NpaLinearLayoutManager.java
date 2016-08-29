package com.example.bobby.hotseat;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * Created by bobby on 8/29/16.
 */

public class NpaLinearLayoutManager extends LinearLayoutManager {



    /**
     * Disable predictive animations. There is a bug in RecyclerView which causes views that
     * are being reloaded to pull invalid ViewHolders from the internal recycler stack if the
     * adapter size has decreased since the ViewHolder was recycled.
     */
    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }

    public NpaLinearLayoutManager(Context context) {
        super(context);
    }

/*
        public NpaLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        public NpaLinearLayoutManager(Context context, int spanCount) {
            super(context, spanCount);
        }

        public NpaLinearLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
            super(context, spanCount, orientation, reverseLayout);
        }
        */
}