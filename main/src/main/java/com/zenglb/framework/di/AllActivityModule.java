package com.zenglb.framework.di;

import com.zenglb.framework.activity.launch.LaunchActivity;
import com.zenglb.framework.di.component.BaseActivityComponent;
import com.zenglb.framework.di.module.DefaultActivityModule;
import com.zenglb.framework.di.module.LoginActivityModule;
import com.zenglb.framework.di.scope.ActivityScope;
import com.zenglb.framework.mvp.login.LoginActivity;
import com.zenglb.framework.mvp_more.MVPActivity;
import com.zenglb.framework.mvp_oauth.Oauth_MVP_Activity;
import com.zenglb.framework.navigation.MainActivityBottomNavi;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * 全部放在这里来统一的管理 ！
 * 新建了一个Activity 的并且需要inject 的只需要添加两行代码
 * <p>
 * 对个人而言，这样的好处在于：
 * 1.每次不再需要额外声明一个SubCompoent，再次减少模板代码
 * 2.每个Activity的Module都放在同一个AllActivitysModule中进行统一管理，每次修改只需要修改这一个类即可
 * 3.每个Activity所单独需要的依赖，依然由各自的Module进行管理和实例化，依然没有任何耦合
 */
@Module(subcomponents = {
        BaseActivityComponent.class  //1111111111 subcomponent
})
public abstract class AllActivityModule {

    // TODO: 2018/1/12 这样是很方便了，然而并不是所有的Activity 都需要依赖注入的东西，继承了BaseActivity 就要写这个很烦人啊！

    /**
     * BaseActivity <- BaseDiActivity  <- BaseMVPActivity  这样的继承关系就好了，但是3个Base 啊! 只能接受2个
     *
     * 1 BaseActivity: 不要MVP模式，也不用DI 依赖注入（少写两行代码）就用这个吧
     *
     * 2 BaseDiActivity：不要MVP，但是要依赖注入用这个（同时在这里加两行代码）
     *
     * 3 BaseMVPActivity 要MVP 要DI ，要要要 嘛
     */

    //2222222 新建了一个Activity 的并且需要inject 的只需要添加两行代码
    @ActivityScope
    @ContributesAndroidInjector(modules = LoginActivityModule.class)
    abstract LoginActivity contributeMainActivitytInjector();

    @ContributesAndroidInjector(modules = DefaultActivityModule.class)
    abstract LaunchActivity contributeLaunchActivityInjector();

    @ContributesAndroidInjector(modules = DefaultActivityModule.class)
    abstract Oauth_MVP_Activity contribute1Injector();

    @ContributesAndroidInjector(modules = DefaultActivityModule.class)
    abstract MainActivityBottomNavi contribute2Injector();

    @ContributesAndroidInjector(modules = DefaultActivityModule.class)
    abstract MVPActivity contribute3Injector();


//    Pro-tip: If your subcomponent and its builder have no other methods or supertypes than the ones mentioned in step #2, you can use @ContributesAndroidInjector to generate them for you. Instead of steps 2 and 3, add an abstract module method that returns your activity, annotate it with @ContributesAndroidInjector, and specify the modules you want to install into the subcomponent. If the subcomponent needs scopes, apply the scope annotations to the method as well.
//
//    @ActivityScope
//    @ContributesAndroidInjector(modules = { /* modules to install into the subcomponent */ })
//    abstract YourActivity contributeYourActivityInjector();


}
