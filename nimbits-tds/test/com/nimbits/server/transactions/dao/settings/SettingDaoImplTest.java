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

package com.nimbits.server.transactions.dao.settings;

import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.server.*;
import com.nimbits.server.cron.*;
import static org.junit.Assert.*;
import org.junit.*;

import java.io.*;
import java.util.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 3/30/12
 * Time: 8:09 PM
 */
public class SettingDaoImplTest extends NimbitsServletTest {

    @Test
    public void getSettingsTest() throws IOException, InterruptedException, NimbitsException {

        SystemMaint systemMaint = new SystemMaint();

        systemMaint.doGet(req, resp);
        Thread.sleep(2000);
        Map<SettingType, String> settings = settingsDAO.getSettings();
        assertTrue(settings.size() > 0);

    }
}
