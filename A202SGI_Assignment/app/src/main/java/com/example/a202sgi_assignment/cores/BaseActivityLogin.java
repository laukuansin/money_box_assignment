package com.example.a202sgi_assignment.cores;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.a202sgi_assignment.R;
import com.example.a202sgi_assignment.hanlders.SessionHandler;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.core.AuthTokenProvider;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public abstract class BaseActivityLogin extends AppCompatActivity {
    private SessionHandler _sessionHandler;
    private AppController _appController;
    private AccessTokenTracker _accessTokenTracker;

    protected abstract @LayoutRes
    int ContentView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _sessionHandler = AppController.getInstance().getSessionHandler();
        _appController = (AppController) getApplicationContext();


        setContentView(ContentView());

        if (_appController.getSessionHandler().isLoginWithFacebook()) {
            FacebookSdk.sdkInitialize(getApplicationContext());
            _accessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                    if (currentAccessToken == null) {
                        if (_appController.getSessionHandler().isLoginWithFacebook())
                            LoginManager.getInstance().logOut();

                        _appController.getSessionHandler().clearLoginSession();
                        _appController.getSessionHandler().checkAuthorization();
                    } else {
                        if (oldAccessToken != currentAccessToken) {
                            _appController.getSessionHandler().setFacebookSession(currentAccessToken.getUserId(), currentAccessToken.getToken());
                        }
                    }
                }
            };
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        _sessionHandler = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        return super.onCreateOptionsMenu(menu);
    }

    protected void hideSoftKeyboard() {
        if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
    }

    public SessionHandler getSessionHandler() {
        return _sessionHandler;
    }

    public Snackbar initSnackbar(@IdRes int contentId, String message, int duration) {
        Snackbar snackbar = Snackbar.make(findViewById(contentId), message, duration);

        View snackbarView = snackbar.getView();

        int snackbarTextId = R.id.snackbar_text;
        TextView textView = (TextView) snackbarView.findViewById(snackbarTextId);
        textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white_1000));

        return snackbar;
    }

    public Snackbar initSnackbar(@IdRes int contentId, @StringRes int messageId, int duration) {
        return initSnackbar(contentId, getResources().getText(messageId).toString(), duration);
    }

}
