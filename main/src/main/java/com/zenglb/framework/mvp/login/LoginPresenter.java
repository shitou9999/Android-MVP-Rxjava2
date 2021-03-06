package com.zenglb.framework.mvp.login;

import android.app.Activity;

import com.zenglb.framework.http.ApiService;
import com.zenglb.framework.http.param.LoginParams;
import com.zenglb.framework.http.result.LoginResult;
import com.zlb.httplib.core.BaseObserver;
import com.zlb.httplib.core.rxUtils.SwitchSchedulers;

import javax.inject.Inject;

/**
 * Login Presenter
 *
 */
public class LoginPresenter implements LoginContract.Presenter {

    ApiService apiService;

    LoginContract.View mLoginView;

    @Inject
    public LoginPresenter(ApiService apiService) {
        this.apiService=apiService;
    }

    @Override
    public void login(LoginParams loginParams) {
        apiService.goLoginByRxjavaObserver(loginParams)
                .compose(SwitchSchedulers.applySchedulers())
                .subscribe(new BaseObserver<LoginResult>(null) {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        mLoginView.loginSuccess(loginResult);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        super.onFailure(code, message);
                        mLoginView.loginFail(message);
                    }
                });
    }



    /**
     * 这下面的两行能不能 Base化解
     *
     * @param view the view associated with this presenter
     */
    @Override
    public void takeView(LoginContract.View view) {
        mLoginView=view;
    }

    @Override
    public void dropView() {

    }


    //    private final LoginActivity view;
//    private final LoginModel model;
//
//    @Inject
//    public LoginPresenter(LoginActivity view, LoginModel model) {
//        this.view = view;
//        this.model = model;
//    }
//
//    public void requestHttp() {
//        view.onGetMessage(model.returnMessage());
//    }

}
