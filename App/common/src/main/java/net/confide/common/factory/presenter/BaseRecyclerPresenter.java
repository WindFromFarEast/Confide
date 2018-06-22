package net.confide.common.factory.presenter;

import android.support.v7.util.DiffUtil;

import net.confide.common.widget.recycler.RecyclerAdapter;
import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.List;

/**
 * 对RecyclerView进行的一个简单的Presenter封装
 * @param <ViewModel>
 * @param <View>
 */
public class BaseRecyclerPresenter<ViewModel, View extends BaseContract.RecyclerView> extends BasePresenter<View> {

    public BaseRecyclerPresenter(View mView) {
        super(mView);
    }

    /**
     * 刷新RecyclerView列表的新数据(全局刷新)
     * @param dataList 新数据
     */
    protected void refreshData(final List<ViewModel> dataList) {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                View view = getView();
                if (view == null) {
                    return;
                }
                //更新数据并刷新界面
                RecyclerAdapter<ViewModel> adapter = view.getRecyclerAdapter();
                adapter.replace(dataList);//更新数据
                view.onAdapterDataChange();//刷新界面
            }
        }) ;
    }

    /**
     * 刷新界面(局部刷新),保证执行方法在主线程
     * @param diffResult 差异结果集合
     * @param dataList 新数据
     */
    protected void refreshData(final DiffUtil.DiffResult diffResult, final List<ViewModel> dataList) {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                refreshDataOnUiThread(diffResult, dataList);
            }
        });
    }

    /**
     * 具体在主线程执行的局部刷新方法
     * @param diffResult
     * @param dataList
     */
    private void refreshDataOnUiThread(final DiffUtil.DiffResult diffResult, final List<ViewModel> dataList) {
        View view = getView();
        if (view == null) {
            return;
        }
        RecyclerAdapter<ViewModel> adapter = view.getRecyclerAdapter();
        //改变数据集合,但不通知界面刷新
        adapter.getItems().clear();
        adapter.getItems().addAll(dataList);
        //通知界面刷新占位布局
        view.onAdapterDataChange();
        //进行增量更新
        diffResult.dispatchUpdatesTo(adapter);
    }
}
