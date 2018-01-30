package com.owncloud.android.ui.activity;

import android.accounts.Account;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.owncloud.android.R;
import com.owncloud.android.authentication.AccountUtils;
import com.owncloud.android.lib.common.UserInfo;
import com.owncloud.android.lib.common.utils.Log_OC;
import com.owncloud.android.utils.DisplayUtils;
import com.owncloud.android.viewmodels.EditUserInfoViewModel;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class EditUserInfoActivity extends FileActivity implements View.OnClickListener {

    private static final String TAG = EditUserInfoActivity.class.getSimpleName();

    public static final String KEY_ACCOUNT = "KEY_ACCOUNT";
    public static final String KEY_ACCOUNT_INFO = "KEY_ACCOUNT_INFO";

    private UserInfo mUserInfo;

    private EditNode[] mEditNodes = new EditNode[] {
            new EditNode(R.drawable.ic_phone, R.string.user_info_phone),
            new EditNode(R.drawable.ic_map_marker, R.string.user_info_address),
            new EditNode(R.drawable.ic_web, R.string.user_info_website),
            new EditNode(R.drawable.ic_twitter, R.string.user_info_twitter)
    };

    EditUserInfoViewModel mViewModel;

    @BindView(R.id.toolbar) protected Toolbar mToolbar;
    @BindView(R.id.edit_list) protected RecyclerView mEditList;
    @BindView(R.id.avatar) protected ImageView mAvatar;
    @BindView(R.id.fab) protected FloatingActionButton mFAB;
    @BindView(R.id.fab_progress) protected FABProgressCircle mFABProgress;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_account);

        ButterKnife.bind(this);

        setAccount(AccountUtils.getCurrentOwnCloudAccount(this));
        onAccountSet(false);

        setupToolbar();
        updateActionBarTitleAndHomeButtonByString("");

        Bundle parcel = getIntent().getExtras();

        Account account = Parcels.unwrap(parcel.getParcelable(KEY_ACCOUNT));
        mUserInfo = Parcels.unwrap(parcel.getParcelable(KEY_ACCOUNT_INFO));

        if (mUserInfo != null) {
            mEditNodes[0].setValue(mUserInfo.getPhone());
            mEditNodes[1].setValue(mUserInfo.getAddress());
            mEditNodes[2].setValue(mUserInfo.getWebsite());
            mEditNodes[3].setValue(mUserInfo.getTwitter());
        }

        mViewModel = ViewModelProviders.of(this).get(EditUserInfoViewModel.class);
        mViewModel.setAccount(account);

        mViewModel.getSaveStatus().observe(this, savingStatus-> {
            mFAB.setEnabled(true);
            mFABProgress.hide();
            if (!savingStatus.succeed()) {
                Snackbar.make(mEditList, R.string.user_info_edit_failed, Snackbar.LENGTH_LONG).show();
            } else {
                finish();
            }
        });

        mAvatar.setTag(account.name);
        float avatarRadius = getResources().getDimension(R.dimen.nav_drawer_header_avatar_radius);
        DisplayUtils.setAvatar(account, this, avatarRadius, getResources(), getStorageManager(), mAvatar);

        mEditList.setAdapter(new EditInfoListAdapter());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.fab})
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.fab:
                startSavingUserInfo();
                return;
        }
    }

    private void startSavingUserInfo() {
        mFABProgress.show();
        mFAB.setEnabled(false);
        mViewModel.saveData(mUserInfo);
    }


    private class EditNode {
        private int iconDrawable;
        private @StringRes int placeholderText;
        private String fieldValue;

        private EditNode(int iconDrawable, @StringRes int placeholderText) {
            this.iconDrawable = iconDrawable;
            this.placeholderText = placeholderText;
        }

        public void setValue(String value) {
            fieldValue = value;
        }

        public String getValue() {
            return fieldValue;
        }
    }

    protected class EditInfoListAdapter extends RecyclerView.Adapter<EditInfoListAdapter.EditInfoListViewHolder> {


        @Override
        public EditInfoListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.edit_user_info_single_entry, parent, false);
            return new EditInfoListViewHolder(v);
        }

        @Override
        public void onBindViewHolder(EditInfoListViewHolder holder, int position) {
            EditNode n = mEditNodes[position];
            holder.mIcon.setImageResource(n.iconDrawable);
            holder.mTextInputLayout.setHint(getString(n.placeholderText));
            holder.mText.setText(n.getValue());
            holder.mText.setOnKeyListener((v, keyCode, event) -> {
                    switch (holder.getAdapterPosition()) {
                        case 0: // phone
                            mUserInfo.setPhone(holder.mText.getText().toString());
                            break;
                        case 1: // address
                            mUserInfo.setAddress(holder.mText.getText().toString());
                            break;
                        case 2: // website
                            mUserInfo.setWebsite(holder.mText.getText().toString());
                            break;
                        case 3: // twitter
                            mUserInfo.setTwitter(holder.mText.getText().toString());
                            break;
                        default:
                            Log_OC.w(TAG, "Incorrect text edit changed with index "
                                    + holder.getAdapterPosition() + " and value "
                                    + holder.mText.getText());
                    }
                    return false;
            });
        }

        @Override
        public int getItemCount() {
            return mEditNodes.length;
        }

        public class EditInfoListViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.text_input_layout) protected TextInputLayout mTextInputLayout;
            @BindView(R.id.icon) protected ImageView mIcon;
            @BindView(R.id.text) protected EditText mText;

            public EditInfoListViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }


}
