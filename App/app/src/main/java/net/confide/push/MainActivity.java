package net.confide.push;

import android.widget.TextView;
import net.confide.common.app.Activity;
import butterknife.BindView;

public class MainActivity extends Activity {

    @BindView(R.id.txt_test)
    TextView mTestText;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTestText.setText("Hello!");
    }
}
