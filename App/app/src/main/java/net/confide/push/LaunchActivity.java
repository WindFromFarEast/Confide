package net.confide.push;

import net.confide.common.app.Activity;
import net.confide.push.activities.MainActivity;
import net.confide.push.fragment.assist.PermissionFragment;

public class LaunchActivity extends Activity {

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_launch;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PermissionFragment.haveAll(this, getSupportFragmentManager())) {
            MainActivity.show(this);
            finish();
        }
    }
}
