/*
 * Nextcloud Android client application
 *
 * @author Bartosz Przybylski
 * Copyright (C) 2018 Bartosz Przybylski
 * Copyright (C) 2018 Nextcloud GmbH.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.owncloud.android.viewmodels;

import android.accounts.Account;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.owncloud.android.lib.common.UserInfo;
import com.owncloud.android.lib.common.operations.RemoteOperationResult;
import com.owncloud.android.lib.resources.users.SetRemoteUserInfoOperation;



public class EditUserInfoViewModel extends AndroidViewModel {

    private Account mAccount;
    private MutableLiveData<SavingStatus> mSavingStatus = new MutableLiveData<>();

    public EditUserInfoViewModel(@NonNull Application application) {
        super(application);
    }

    public Account getAccount() {
        return mAccount;
    }

    public void setAccount(Account account) {
        mAccount = account;
    }

    public void saveData(UserInfo userInfo) {
        new SaveDataTask().execute(userInfo);
    }

    public LiveData<SavingStatus> getSaveStatus() {
        return mSavingStatus;
    }

    public class SavingStatus {
        private boolean mSucceed;
        private String mMessage;

        protected SavingStatus(boolean succeed, String message) {
            this.mSucceed = succeed;
            this.mMessage = message;
        }

        public boolean succeed() {
            return mSucceed;
        }

        public String getMessage() {
            return mMessage;
        }
    }

    class SaveDataTask extends AsyncTask<UserInfo, Void, Boolean> {

        @Override
        protected Boolean doInBackground(UserInfo... userInfo) {
            UserInfo info = userInfo[0];

            boolean overallSuccess = true;

            if (info.getTwitter() != null)
                overallSuccess = singleFieldUpdate(info.getId(), "twitter", info.getTwitter());

            if (info.getAddress() != null)
                overallSuccess &= singleFieldUpdate(info.getId(), "address", info.getAddress());

            if (info.getWebsite() != null)
                overallSuccess &= singleFieldUpdate(info.getId(), "website", info.getWebsite());

            if (info.getPhone() != null)
                overallSuccess &= singleFieldUpdate(info.getId(), "phone", info.getPhone());

            return overallSuccess;
        }

        private boolean singleFieldUpdate(String userId, String fieldName, String fieldValue) {
            SetRemoteUserInfoOperation op = new SetRemoteUserInfoOperation(userId, fieldName, fieldValue);
            RemoteOperationResult res = op.execute(mAccount, getApplication().getApplicationContext());
            return res.isSuccess();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            SavingStatus status = new SavingStatus(aBoolean, "");
            mSavingStatus.postValue(status);
        }
    }

}
