package com.dp.bigdata.taurus.frontend.client.scheduler;

import com.dp.bigdata.taurus.frontend.client.main.presenter.MainPresenter;
import com.mvp4g.client.Mvp4gModule;
import com.mvp4g.client.annotation.module.Loader;

@Loader( MainPresenter.class )
public interface SchedulerModule extends Mvp4gModule{

}
