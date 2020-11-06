package com.example.a202sgi_assignment.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.a202sgi_assignment.R;
import com.example.a202sgi_assignment.activities.MainActivity;
import com.example.a202sgi_assignment.cores.AppController;
import com.example.a202sgi_assignment.cores.BaseFragment;
import com.hanks.passcodeview.PasscodeView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PassCodeFragment extends BaseFragment {
    private PasscodeView _passCodeView;

    public PassCodeFragment() {
    }
    public static PassCodeFragment newInstance() {
        PassCodeFragment fragment = new PassCodeFragment();

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
        View view=inflater.inflate(R.layout.fragment_passcode, container, false);
        _passCodeView=view.findViewById(R.id.passcodeView);

        _passCodeView
                .setLocalPasscode(AppController.getInstance().getSessionHandler().getPassCode())
                .setListener(new PasscodeView.PasscodeViewListener() {
            @Override
            public void onFail() {
                Toast.makeText(getContext(), "Password is wrong", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(String number) {
                Toast.makeText(getContext(), "Login success", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getContext(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;

    }
}
