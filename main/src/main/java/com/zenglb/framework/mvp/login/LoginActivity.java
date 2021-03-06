package com.zenglb.framework.mvp.login;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zenglb.framework.R;
import com.zenglb.framework.activity.access.RegisterActivity;
import com.zenglb.framework.base.mvp.BaseMVPActivityNEW;
import com.zenglb.framework.http.ApiService;
import com.zenglb.framework.http.HttpRetrofit;
import com.zenglb.framework.http.param.LoginParams;
import com.zenglb.framework.http.result.LoginResult;
import com.zenglb.framework.navigation.MainActivityBottomNavi;
import com.zenglb.framework.persistence.SPDao;
import com.zenglb.framework.persistence.dbmaster.DaoSession;
import com.zlb.httplib.core.SPKey;

import javax.inject.Inject;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

/**
 * Demo
 *
 *
 */
public class LoginActivity extends BaseMVPActivityNEW implements LoginContract.View {
    @Inject
    SPDao spDao;

    @Inject
    DaoSession daoSession;

//    @Inject
//    ApiService apiService;

    @Inject
    LoginPresenter loginPresenter;

    private static final String PW = "zxcv1234";  //FBI WARMING !!!!
    private boolean isFromLaunch = false;         //从哪里跳转来登录页面的

    EditText etUsername, etPassword;
    Button oauthBtn;
    FloatingActionButton fabBtn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        loginInit();
        //1,从Launcher 页面过来 2，用户主动退出 3，超时或其他页面退出（再次登录要回到那里去）
        isFromLaunch = getIntent().getBooleanExtra("isFromLaunch", false);
        if (!isFromLaunch) {
            logoutCustomComponent();
        }
    }


    /**
     * 登录的从新初始化，把数据
     */
    private void loginInit() {
        spDao.saveData(SPKey.KEY_ACCESS_TOKEN, "");
        HttpRetrofit.setToken("");
    }


    /**
     * 集成的IM 等第三方系统需要单独的退出来,因为
     */
    private void logoutCustomComponent() {
//        RongyunIM.logout();
//        Clear Oautoken,在web 页面的时候怎么退出来
    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    protected void onResume() {
        super.onResume();
        //Bind view to the presenter which will signal for the presenter to load the task.
        loginPresenter.takeView(this);
    }

    @Override
    public void onPause() {
        loginPresenter.dropView();
        super.onPause();
    }

    @Override
    protected void initViews() {
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        oauthBtn = (Button) findViewById(R.id.login_btn);
        fabBtn = (FloatingActionButton) findViewById(R.id.fab_btn);

        fabBtn.setOnClickListener(this);
        oauthBtn.setOnClickListener(this);

        etUsername.setText(spDao.getData(SPKey.KEY_LAST_ACCOUNT, "", String.class));
        etPassword.setText(PW);
        etUsername.setText("18826562075");
    }


    /**
     * Login ,普通的登录和使用Rxjava 的方式都可以
     */
    public void mvpLogin() {
        String userName = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            Toasty.error(this.getApplicationContext(), "请完整输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        //1.需要改进，能否改进为链式写法
        LoginParams loginParams = new LoginParams();
        loginParams.setClient_id("5e96eac06151d0ce2dd9554d7ee167ce");
        loginParams.setClient_secret("aCE34n89Y277n3829S7PcMN8qANF8Fh");
        loginParams.setGrant_type("password");
        loginParams.setUsername(userName);
        loginParams.setPassword(password);

        loginPresenter.login(loginParams);
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_btn:
                goRegister();
                break;
            case R.id.login_btn:
                mvpLogin();
                break;
        }
    }


    @Override
    public void loginFail(String failMsg) {
        Toasty.error(this.getApplicationContext(), "登录失败" + failMsg, Toast.LENGTH_SHORT).show();
    }


    /**
     * 登录成功
     *
     * @param loginResult
     */
    public void loginSuccess(LoginResult loginResult) {
        spDao.saveData(SPKey.KEY_ACCESS_TOKEN, "Bearer " + loginResult.getAccessToken());
        spDao.saveData(SPKey.KEY_REFRESH_TOKEN, loginResult.getRefreshToken());
        spDao.saveData(SPKey.KEY_LAST_ACCOUNT, etUsername.getText().toString().trim());
        HttpRetrofit.setToken(spDao.getData(SPKey.KEY_ACCESS_TOKEN, "", String.class));

        if (isFromLaunch) {
            Intent i2 = new Intent(LoginActivity.this, MainActivityBottomNavi.class);
            startActivity(i2);
            LoginActivity.this.finish();
        } else {//是来自Launcher启动的就跳转到主页面，否则从哪里来就到那里去
            LoginActivity.this.finish();
        }

    }

    /**
     * 跳转到注册®️
     */
    public void goRegister() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, fabBtn, fabBtn.getTransitionName());
            startActivity(new Intent(this, RegisterActivity.class), options.toBundle());

        } else {
            startActivity(new Intent(this, RegisterActivity.class));
        }
    }


    /**
     * 登录页面不允许返回，之前返回Home
     * <p>
     * App用户在其他设备上登录，原来的设备因为unOauth 弹出到登录页面
     * 竟然按返回键还能回去，假如有缓存敏感信息不是。。。
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }







//    public void gotoSecond(View view) {
//        startActivity(new Intent(this, SecondActivity.class));
//    }
//
//    public void requestHttp(View view) {
//        presenter.requestHttp();
//    }
//
//    public void onGetMessage(String message) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
//    }

}
