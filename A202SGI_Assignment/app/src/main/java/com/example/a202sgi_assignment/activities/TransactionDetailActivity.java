package com.example.a202sgi_assignment.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.a202sgi_assignment.R;
import com.example.a202sgi_assignment.adapters.ImageAdapter;
import com.example.a202sgi_assignment.cores.BaseActivity;
import com.example.a202sgi_assignment.fragments.TransactionDetailFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class TransactionDetailActivity extends BaseActivity  implements ImageAdapter.OnActionListener {
    public static final String TRANSACTION_ID="transaction-id";
    private Fragment _fragment;
    private String transactionID;

    @Override
    protected int ContentView() {
        return R.layout.activity_logged_template;
    }

    @Override
    protected void AttemptSave() {
        ((TransactionDetailFragment)_fragment).updateTransaction();
        hideSoftKeyboard();
    }

    @Override
    protected void AttemptDelete() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Delete Transaction");
        builder.setMessage("Transaction will be deleted. This action cannot be undone.");
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((TransactionDetailFragment)_fragment).removeTransaction();
                finish();
                dialogInterface.cancel();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
        hideSoftKeyboard();
    }

    @Override
    protected void AttemptSearch() {

    }

    @Override
    protected void AttemptAdd() {

    }

    @Override
    protected void AttemptFilter() {

    }

    @Override
    protected void AttemptRefresh() {

    }

    @Override
    protected int MenuResource() {
        return R.menu.share_save_delete;
    }

    @Override
    protected boolean DisableActionMenu() {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_share:
                ((TransactionDetailFragment)_fragment).screenShot();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            if(bundle.containsKey(TRANSACTION_ID))
            {
                transactionID=bundle.getString(TRANSACTION_ID);
            }
        }
        if (savedInstanceState == null ) {
            _fragment = TransactionDetailFragment.newInstance(transactionID);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.frame_container, _fragment).commit();

        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("Discard changes of this transaction?");
        builder.setPositiveButton("DISCARD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                dialogInterface.cancel();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
        hideSoftKeyboard();
    }

    @Override
    public void removeImage(int position) {
        ((TransactionDetailFragment)_fragment).removeImage(position);

    }
}
