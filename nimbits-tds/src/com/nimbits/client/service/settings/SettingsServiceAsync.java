/*
 * Copyright (c) 2010 Tonic Solutions LLC.
 *
 * http://www.nimbits.com
 *
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, eitherexpress or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.client.service.settings;

import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.enums.*;

import java.util.*;

public interface SettingsServiceAsync {

    void getSetting(final SettingType paramName, final AsyncCallback<String> asyncCallback);

    void getSettings(final AsyncCallback<Map<SettingType, String>> asyncCallback);

    void getBooleanSetting(final SettingType paramName, AsyncCallback<Boolean> async);

    void updateSetting(final SettingType setting, final String newValue, AsyncCallback<Void> async);

    void addSetting(final SettingType setting, final String defaultValue, AsyncCallback<Void> async);
}
