package htn.logan.runningdj;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.GridViewPager;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.WindowInsets;
import com.google.android.gms.common.*;
import com.google.android.gms.common.api.*;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.MessageApi;
//import com.google.android.gms.fitness.*;

public class WearActivity extends Activity implements MessageApi.MessageListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wear);
        final Resources res = getResources();
        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
        pager.setOnApplyWindowInsetsListener(new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                // Adjust page margins:
                //   A little extra horizontal spacing between pages looks a bit
                //   less crowded on a round display.
                final boolean round = insets.isRound();
                int rowMargin = res.getDimensionPixelOffset(R.dimen.page_row_margin);
                int colMargin = res.getDimensionPixelOffset(R.dimen.page_row_margin);
                pager.setPageMargins(rowMargin, colMargin);
                return insets;
            }
        });
        pager.setAdapter(new GridPagerAdapter(this, getFragmentManager()));
    }

    @Override
    public void onMessageReceived(MessageEvent event) {
        Log.d("message", "onMessageReceived: " + event.toString());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Change album art, song title, and pause / playing based on type of event.
            }
        });
    }

}
