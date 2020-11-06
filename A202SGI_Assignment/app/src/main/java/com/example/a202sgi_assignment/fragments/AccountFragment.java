package com.example.a202sgi_assignment.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;


import com.example.a202sgi_assignment.R;
import com.example.a202sgi_assignment.activities.MainActivity;
import com.example.a202sgi_assignment.activities.SetPassCodeActivity;
import com.example.a202sgi_assignment.cores.AppController;
import com.example.a202sgi_assignment.cores.BaseFragment;
import com.example.a202sgi_assignment.domains.Account;
import com.example.a202sgi_assignment.utilities.CircleImageView;
import com.example.a202sgi_assignment.utilities.OnSingleClickListener;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;


public class AccountFragment extends BaseFragment {
    private TextView _userName,_email;
    private LinearLayout _shareAppLayout,_contactUsLayout,_logOutLayout,_likeUsLayout,_passCodeLayout;
    private ProfilePictureView _profilePic;
    private Account account;
    private TextView _version;
    private CardView _cardView;
    private CircleImageView _profilePicGoogle;
    private GoogleSignInClient mGoogleSignInClient;
    private Switch _switchPassCode;


    public AccountFragment() {
    }
    public static AccountFragment newInstance() {
        AccountFragment fragment = new AccountFragment();

        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_account, container, false);
        _userName=view.findViewById(R.id.name);
        _email=view.findViewById(R.id.email);
        _profilePic=view.findViewById(R.id.profile_pic_fb);
        _cardView=view.findViewById(R.id.fb_pic_layout);
        _profilePicGoogle=view.findViewById(R.id.profile_pic_google);
        account=getSessionHandler().getAccount();
        _version=view.findViewById(R.id.version);
        _likeUsLayout=view.findViewById(R.id.like_us_layout);
        _userName.setText(account.getUserName());
        _email.setText(account.getEmail());
        _shareAppLayout=view.findViewById(R.id.share_layout);
        _contactUsLayout=view.findViewById(R.id.contactus_layout);
        _logOutLayout=view.findViewById(R.id.log_out_layout);
        _switchPassCode=view.findViewById(R.id.passcode_switch);
        _passCodeLayout=view.findViewById(R.id.passcode_layout);

        PackageInfo pInfo;
        try {
            pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String version = pInfo.versionName;
            _version.setText(String.format("Version %1$s", version));
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (AppController.getInstance().getSessionHandler().isLoginWithFacebook()) {
            _cardView.setVisibility(View.VISIBLE);
            _profilePicGoogle.setVisibility(View.GONE);
            _profilePic.setProfileId(account.getID());

        }
        else if(AppController.getInstance().getSessionHandler().isLoginWithGmail())
        {
            _cardView.setVisibility(View.GONE);
            _profilePicGoogle.setVisibility(View.VISIBLE);
            Picasso.get().load(account.getProfilePic()).into(_profilePicGoogle);
        }


        _switchPassCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(_switchPassCode.isChecked())
                {
                    Intent intent=new Intent(getContext(), SetPassCodeActivity.class);
                    startActivity(intent);
                }else{
                    AppController.getInstance().getSessionHandler().clearPassCode();

                }
            }
        });
        _shareAppLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Our Application will be launch in this Month. Check it out!");
                sendIntent.setType("text/plain");
                getActivity().startActivity(sendIntent);
            }
        });
        _contactUsLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setTitle("Contact Us");
                builder.setMessage("Contact No: 014-6063407\n\nEmail: laukuansin@gmail.com");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
            }
        });
        _likeUsLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = null;
                try {
                    getActivity().getPackageManager().getPackageInfo("com.facebook.katana", 0);
                    String url = "https://www.facebook.com/Money-Box-454079405367570/";
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href="+url));
                } catch (Exception e) {
                    // no Facebook app, revert to browser
                    String url = "https://www.facebook.com/Money-Box-454079405367570/";
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent .setData(Uri.parse(url));
                }
                startActivity(intent);
            }
        });
        _logOutLayout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setTitle("Log Out");
                builder.setMessage("Are you want to log out?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AttemptLogout();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(AppController.getInstance().getSessionHandler().isSetPassCode())
        {
            _switchPassCode.setChecked(true);
        }
        else{
            _switchPassCode.setChecked(false);
        }
    }

    public void AttemptLogout() {
        if (AppController.getInstance().getSessionHandler().isLoginWithFacebook()) {
            LoginManager.getInstance().logOut();
        }
        else if(AppController.getInstance().getSessionHandler().isLoginWithGmail())
        {
            FirebaseAuth auth=FirebaseAuth.getInstance();
            signOut();
            auth.signOut();
        }

        AppController.getInstance().getSessionHandler().clearLoginSession();
        Intent intent = new Intent(getContext(), MainActivity.class);
        //  intent.putExtra("SelectedIndex", AccountSettingActivity.this._appController.getSessionHandler().getDrawerSelectedIndex());
        // Closing all the Activities
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Add new Flag to start new Activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        getActivity().startActivity(intent);
        getActivity().finish();

    }

    private void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        mGoogleSignInClient.signOut()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }

}
