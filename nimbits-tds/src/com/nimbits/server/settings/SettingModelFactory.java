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
 * Unless required by applicable law or agreed to in writing, software distributed under the license is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.nimbits.server.settings;

import com.nimbits.client.model.setting.Setting;
import com.nimbits.client.model.setting.SettingModel;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 5/5/11
 * Time: 3:43 PM
 */
class SettingModelFactory {

    public static Setting createSettingModel(Setting setting) {
        return new SettingModel(setting.getName(), setting.getValue());

    }

}