package net.confide.push.fragment.main;

import net.confide.common.app.Fragment;
import net.confide.common.widget.GalleyView;
import net.confide.push.R;

import butterknife.BindView;

/**
 * 已激活的聊天Fragment
 */
public class ActiveFragment extends Fragment {

    @BindView(R.id.galleyView)
    GalleyView mGalley;

    public ActiveFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_active;
    }

    @Override
    protected void initData() {
        super.initData();
        mGalley.setup(getLoaderManager(), new GalleyView.SelectedChangeListener() {
            @Override
            public void onSelectedCountChanged(int count) {

            }
        });
    }
}
