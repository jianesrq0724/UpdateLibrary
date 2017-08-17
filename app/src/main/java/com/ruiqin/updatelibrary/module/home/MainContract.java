package com.ruiqin.updatelibrary.module.home;

import com.ruiqin.updatelibrary.base.BaseModel;
import com.ruiqin.updatelibrary.base.BasePresenter;
import com.ruiqin.updatelibrary.base.BaseView;
import com.ruiqin.updatelibrary.module.home.adapter.MainRecyclerAdapter;
import com.ruiqin.updatelibrary.module.home.bean.MainRecyclerData;

import java.util.List;

/**
 * Created by ruiqin.shen
 * 类说明：
 */

public interface MainContract {
    interface Model extends BaseModel {
        List<MainRecyclerData> initData();
    }

    interface View extends BaseView {
        void setRecyclerAdapterSuccess(MainRecyclerAdapter mainRecyclerAdapter);
    }

    abstract class Presenter extends BasePresenter<View, Model> {
        abstract void setAdapter();
    }
}
