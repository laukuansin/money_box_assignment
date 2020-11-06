package com.example.a202sgi_assignment.hanlders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.a202sgi_assignment.activities.LoginActivity;
import com.example.a202sgi_assignment.domains.Account;
import com.google.gson.GsonBuilder;

public class SessionHandler {
    private SharedPreferences _sharedPreferences;
    private SharedPreferences.Editor _editor;
    private Context _context;

    private static final int PRIVATE_MODE = 0;
    private static final String PREFERENCE_NAME = "preferences";
    private static final String KEY_IS_LOGIN = "loggedIn";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USER_NAME = "user-name";
    private static final String KEY_PROFILE_PICTURE = "profile-pic";
    private static final String KEY_ACCOUNT = "account";

    private static final String KEY_GMAIL_ACCESS_TOKEN = "gmailAccessToken";
    private static final String KEY_GMAIL_IDENTITY = "gmail-id";

    private static final String KEY_FACEBOOK_ACCESS_TOKEN = "facebookAccessToken";
    private static final String KEY_FACEBOOK_IDENTITY = "facebook-id";

    private static final String PASS_CODE="passcode";


    public SessionHandler(Context context) {
        this._context = context;
        this._sharedPreferences = this._context.getSharedPreferences(PREFERENCE_NAME, PRIVATE_MODE);
        this._editor = this._sharedPreferences.edit();
    }

    //facebook login session
    public void setFacebookSession(String facebookId, String facebookAccessToken) {
        this._editor.putString(KEY_FACEBOOK_IDENTITY, facebookId);
        this._editor.putString(KEY_FACEBOOK_ACCESS_TOKEN, facebookAccessToken);
        this._editor.commit();
    }

    //gmail login session
    public void setGmailSession(String gmailID, String gmailAccessToken) {
        this._editor.putString(KEY_GMAIL_IDENTITY, gmailID);
        this._editor.putString(KEY_GMAIL_ACCESS_TOKEN, gmailAccessToken);
        this._editor.commit();
    }

    public String getGmailAccessToken() {
        return this._sharedPreferences.getString(KEY_GMAIL_ACCESS_TOKEN, "");
    }

    public String getGmailIdentity() {
        return this._sharedPreferences.getString(KEY_GMAIL_IDENTITY, "");
    }

    public String getFacebookAccessToken() {
        return this._sharedPreferences.getString(KEY_FACEBOOK_ACCESS_TOKEN, "");
    }
    public void setPassCode(String passcode)
    {
        this._editor.putString(PASS_CODE, passcode);
        this._editor.commit();
    }
    public String getPassCode()
    {
        return this._sharedPreferences.getString(PASS_CODE, "");

    }
    public void clearPassCode()
    {
        this._editor.remove(PASS_CODE);
        this._editor.commit();
    }
    public boolean isSetPassCode()
    {
        return !TextUtils.isEmpty(getPassCode());
    }

    public String getFacebookIdentity() {
        return this._sharedPreferences.getString(KEY_FACEBOOK_IDENTITY, "");
    }

    public boolean isLoginWithFacebook() {
        return !TextUtils.isEmpty(getFacebookIdentity()) && !TextUtils.isEmpty(getFacebookAccessToken());
    }

    public boolean isLoginWithGmail() {
        return !TextUtils.isEmpty(getGmailIdentity()) && !TextUtils.isEmpty(getGmailAccessToken());
    }

    public void checkAuthorization() {
        if (!this.isLoggedIn()) {
            Intent intent = new Intent(this._context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            this._context.startActivity(intent);
        }
    }

    public void saveAccount(Account account) {
        this._editor.putString(KEY_ACCOUNT, new GsonBuilder().create().toJson(account));
        this._editor.commit();
    }

    public Account getAccount() {
        return new GsonBuilder().create().fromJson(this._sharedPreferences.getString(KEY_ACCOUNT, ""), Account.class);
    }

    public void createLoginSession(String name, String email, String profilePic) {

        this._editor.putBoolean(KEY_IS_LOGIN, true);
        this._editor.putString(KEY_USER_NAME, name);
        this._editor.putString(KEY_PROFILE_PICTURE, profilePic);
        this._editor.putString(KEY_EMAIL, email);
        this._editor.commit();

    }

    public String getName() {
        return this._sharedPreferences.getString(KEY_USER_NAME, "");
    }

    public boolean isLoggedIn() {
        return this._sharedPreferences.getBoolean(KEY_IS_LOGIN, false);
    }


    public void clearLoginSession() {
        this._editor.clear();
        this._editor.commit();

    }


}
