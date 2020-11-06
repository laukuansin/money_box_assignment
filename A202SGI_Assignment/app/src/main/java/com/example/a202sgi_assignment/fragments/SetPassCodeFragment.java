package com.example.a202sgi_assignment.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.a202sgi_assignment.R;
import com.example.a202sgi_assignment.cores.AppController;
import com.example.a202sgi_assignment.cores.BaseFragment;
import com.hanks.passcodeview.PasscodeView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SetPassCodeFragment extends BaseFragment {
    private PasscodeView _passCodeView;
    private String passCode;
    public SetPassCodeFragment() {
    }
    public static SetPassCodeFragment newInstance() {
        SetPassCodeFragment fragment = new SetPassCodeFragment();

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
        View view=inflater.inflate(R.layout.fragment_set_passcode, container, false);
        _passCodeView=view.findViewById(R.id.passcodeView);
        _passCodeView.setListener(new PasscodeView.PasscodeViewListener() {
            @Override
            public void onFail() {

            }
            @Override
            public void onSuccess(String number) {
                passCode=number;
                AppController.getInstance().getSessionHandler().setPassCode(passCode);
                getActivity().finish();
            }
        });
        return view;

    }
}
