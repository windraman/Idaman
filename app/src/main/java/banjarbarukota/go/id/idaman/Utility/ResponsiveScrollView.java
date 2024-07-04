package banjarbarukota.go.id.idaman.Utility;

/**
 * Created by Wahyu on 1/25/2018.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class ResponsiveScrollView
        extends ScrollView {
    private int initialPosition;
    private int newCheck = 100;
    private OnScrollStoppedListener onScrollStoppedListener;
    private Runnable scrollerTask = new Runnable() {
        public void run() {
            int i = ResponsiveScrollView.this.getScrollY();
            View localView = ResponsiveScrollView.this.getChildAt(ResponsiveScrollView.this.getChildCount() - 1);
            int j = localView.getBottom();
            int k = ResponsiveScrollView.this.getHeight();
            int m = ResponsiveScrollView.this.getScrollY();
            int n = localView.getTop();
            if ((ResponsiveScrollView.this.initialPosition - i == 0) && (j - (k + m + n) == 0) && (ResponsiveScrollView.this.onScrollStoppedListener != null)) {
                ResponsiveScrollView.this.onScrollStoppedListener.onScrollStopped();
            }
        }
    };

    public ResponsiveScrollView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public void setOnScrollStoppedListener(OnScrollStoppedListener paramOnScrollStoppedListener) {
        this.onScrollStoppedListener = paramOnScrollStoppedListener;
    }

    public void startScrollerTask() {
        this.initialPosition = getScrollY();
        postDelayed(this.scrollerTask, this.newCheck);
    }

    public static abstract interface OnScrollStoppedListener {
        public abstract void onScrollStopped();
    }
}
