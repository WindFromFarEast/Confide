package net.confide.push.fragment.search;

import net.confide.push.R;
import net.confide.push.activities.SearchActivity;

/**
 * 搜索界面中的群搜索Fragment
 */
public class SearchGroupFragment extends net.confide.common.app.Fragment implements SearchActivity.SearchFragment {


    public SearchGroupFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_search_group;
    }

    @Override
    public void search(String content) {

    }
}
