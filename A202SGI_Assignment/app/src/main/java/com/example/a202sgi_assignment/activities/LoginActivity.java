package com.example.a202sgi_assignment.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.a202sgi_assignment.R;
import com.example.a202sgi_assignment.cores.AppController;
import com.example.a202sgi_assignment.cores.BaseActivityLogin;
import com.example.a202sgi_assignment.domains.Account;
import com.example.a202sgi_assignment.hanlders.SessionHandler;
import com.example.a202sgi_assignment.utilities.OnSingleClickListener;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import androidx.annotation.NonNull;

public class LoginActivity extends BaseActivityLogin {
    private CallbackManager callbackManager;
    private SessionHandler _handler;
    private String name = "";
    private String email = "";
    private String id = "";
    private String profilePicture = "";
    private ProgressDialog _progressDialog;
    private LoginButton facebookLoginButton;
    private Button googleLoginButton,facebookButton;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private int RC_SIGN_IN = 1;

    @Override
    protected int ContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _handler = AppController.getInstance().getSessionHandler();
        _progressDialog = new ProgressDialog(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        //facebook login
        facebookButton=(Button)findViewById(R.id.fbBtn);
        facebookButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                facebookLoginButton.performClick();
            }
        });
        facebookLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
        //google login
        googleLoginButton = (Button) findViewById(R.id.google_login_button);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleLoginButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    AccessTokenTracker _accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken == null) {
                _handler.clearLoginSession();
            } else {
                loadUserProfile(currentAccessToken);
            }
        }
    };

    public void ReloadApp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    public void loadUserProfile(final AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {

                    email = object.getString("email");
                    name = object.getString("name");
                    id = object.getString("id");
                    profilePicture = "http://graph.facebook.com/" + id + "/picture?type=normal";
                    Log.d("pic", profilePicture);
                    Account account = new Account();
                    account.setID(id);
                    account.setEmail(email);
                    account.setProfilePic(profilePicture);
                    account.setUserName(name);

                    _handler.saveAccount(account);
                    _handler.setFacebookSession(id, accessToken.getToken());
                    _handler.createLoginSession(name, email, profilePicture);
                    Toast.makeText(LoginActivity.this, "Login with Facebook success", Toast.LENGTH_SHORT).show();

                    ReloadApp();

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");
        request.setParameters(parameters);
        request.executeAsync();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            _progressDialog.setMessage(getResources().getString(R.string.progress_logging_in));
            _progressDialog.setIndeterminate(false);
            _progressDialog.setCancelable(false);
            _progressDialog.show();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            if (_progressDialog.isShowing())
                _progressDialog.dismiss();
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completeTask) {
        try {
            GoogleSignInAccount acc = completeTask.getResult(ApiException.class);
            FirebaseGoogleAuth((acc));
            Toast.makeText(this, "Login with Gmail success", Toast.LENGTH_SHORT).show();
        } catch (ApiException e) {
            FirebaseGoogleAuth((null));
        }
    }
    private void FirebaseGoogleAuth(final GoogleSignInAccount acc)
    {
        if(acc!=null)
        {
            AuthCredential authCredential= GoogleAuthProvider.getCredential(acc.getIdToken(),null);
            mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        GoogleSignInAccount user=GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                        Account account=new Account();
                        account.setUserName(user.getDisplayName());
                        account.setProfilePic(String.valueOf(user.getPhotoUrl()));
                        account.setEmail(user.getEmail());
                        account.setID(user.getId());
                        _handler.createLoginSession(user.getDisplayName(),user.getEmail(),String.valueOf(user.getPhotoUrl()));
                        _handler.saveAccount(account);
                        _handler.setGmailSession(user.getId(),user.getIdToken());
                        ReloadApp();
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "login fail", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
}
