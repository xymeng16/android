/**
 *   Nextcloud Android client application
 *
 *   Copyright (C) 2018 Edvard Holst
 *
 *   This program is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU AFFERO GENERAL PUBLIC LICENSE
 *   License as published by the Free Software Foundation; either
 *   version 3 of the License, or any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU AFFERO GENERAL PUBLIC LICENSE for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.owncloud.android.ui.activities.data.files;

import com.owncloud.android.datamodel.OCFile;
import com.owncloud.android.ui.activity.BaseActivity;

/**
 * Defines an interface to the Files service API. All {{@link OCFile}} remote data requests
 * should be piped through this interface.
 */
public interface FilesServiceApi {

    interface FilesServiceCallback<T> {
        void onLoaded(OCFile ocFile);
        void onError(String error);
    }

    void readRemoteFile(String fileUrl, BaseActivity activity, boolean isSharingSupported,
                        FilesServiceApi.FilesServiceCallback<OCFile> callback);
}
