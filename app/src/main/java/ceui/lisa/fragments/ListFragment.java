package ceui.lisa.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.FalsifyFooter;
import com.scwang.smartrefresh.layout.header.FalsifyHeader;

import java.util.ArrayList;
import java.util.List;

import ceui.lisa.R;
import ceui.lisa.adapters.BaseAdapter;
import ceui.lisa.core.BaseCtrl;
import ceui.lisa.utils.Common;
import ceui.lisa.utils.DensityUtil;
import ceui.lisa.view.LinearItemDecoration;
import ceui.lisa.viewmodel.BaseModel;
import jp.wasabeef.recyclerview.animators.BaseItemAnimator;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

public abstract class ListFragment<Layout extends ViewDataBinding, Item,
        ItemLayout extends ViewDataBinding> extends BaseFragment<Layout> {

    public static final long animateDuration = 400L;
    public static final int PAGE_SIZE = 20;
    protected RecyclerView mRecyclerView;
    protected RefreshLayout mRefreshLayout;
    protected ImageView noData;
    protected BaseAdapter<Item, ItemLayout> mAdapter;
    protected List<Item> allItems = new ArrayList<>();
    protected BaseModel<Item> mModel;
    protected String nextUrl;
    protected Toolbar mToolbar;
    protected BaseCtrl mBaseCtrl;

    @Override
    public void initLayout() {
        mLayoutID = R.layout.fragment_base_list;
    }

    public abstract BaseAdapter<Item, ItemLayout> adapter();

    public abstract BaseCtrl present();

    @Override
    void initData() {
        //为recyclerView设置Adapter



    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mModel = (BaseModel<Item>) new ViewModelProvider(this).get(BaseModel.class);

        mAdapter = adapter();
        if (mAdapter != null) {
            mRecyclerView.setAdapter(mAdapter);
        }

        //进页面主动刷新
        if (autoRefresh() && !mModel.isLoaded()) {
            mRefreshLayout.autoRefresh();
        }
    }

    @Override
    public void initView(View view) {

        mToolbar = view.findViewById(R.id.toolbar);
        if (mToolbar != null) {
            initToolbar(mToolbar);
        }

        mRecyclerView = view.findViewById(R.id.recyclerView);
        initRecyclerView();

        mRecyclerView.setItemAnimator(animation());

        mRefreshLayout = view.findViewById(R.id.refreshLayout);
        noData = view.findViewById(R.id.no_data);
        noData.setOnClickListener(v -> {
            noData.setVisibility(View.INVISIBLE);
            mRefreshLayout.autoRefresh();
        });
        mBaseCtrl = present();
        mRefreshLayout.setRefreshHeader(mBaseCtrl.enableRefresh() ?
                mBaseCtrl.getHeader(mContext) : new FalsifyHeader(mContext));
        mRefreshLayout.setRefreshFooter(mBaseCtrl.hasNext() ?
                mBaseCtrl.getFooter(mContext) : new FalsifyFooter(mContext));
    }

    /**
     * 指定是否显示Toolbar
     *
     * @return default true
     */
    public boolean showToolbar() {
        return true;
    }

    /**
     * 指定Toolbar title
     *
     * @return title
     */
    public String getToolbarTitle() {
        return "";
    }

    /**
     * 默认 LinearLayoutManager
     */
    public void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new LinearItemDecoration(DensityUtil.dp2px(12.0f)));
    }

    /**
     * 决定刚进入页面是否直接刷新，一般都是直接刷新，但是FragmentHotTag，不要直接刷新
     *
     * @return default true
     */
    public boolean autoRefresh() {
        return true;
    }

    public void initToolbar(Toolbar toolbar) {
        if (showToolbar()) {
            toolbar.setVisibility(View.VISIBLE);
        } else {
            toolbar.setVisibility(View.GONE);
        }
        toolbar.setTitle(getToolbarTitle());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
    }

    public void onFirstLoaded(List<Item> items) {
        if (mModel != null) {
            mModel.load(items, getClass());
        }
    }

    public void onNextLoaded(List<Item> items) {
        if (mModel != null) {
            mModel.load(items, getClass());
        }
    }

    public void clear() {
        if (mAdapter != null) {
            mAdapter.clear();
            if (mRefreshLayout != null) {
                mRefreshLayout.autoRefresh();
            }
        }
    }

    public boolean isVertical() {
        return true;
    }

    public BaseItemAnimator animation() {
        if (mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            //do nothing
            return null;
        } else {
            //设置item动画
            BaseItemAnimator baseItemAnimator = new LandingAnimator();
            baseItemAnimator.setAddDuration(animateDuration);
            baseItemAnimator.setRemoveDuration(animateDuration);
            baseItemAnimator.setMoveDuration(animateDuration);
            baseItemAnimator.setChangeDuration(animateDuration);
            return baseItemAnimator;
        }
    }
}
