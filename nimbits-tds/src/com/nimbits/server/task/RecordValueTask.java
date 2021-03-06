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

package com.nimbits.server.task;

import com.google.gson.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.server.calculation.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.intelligence.*;
import com.nimbits.server.logging.*;
import com.nimbits.server.orm.*;
import com.nimbits.server.subscription.*;
import com.nimbits.server.summary.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.util.logging.*;

public class RecordValueTask extends HttpServlet {

    final Logger log = Logger.getLogger(RecordValueTask.class.getName());
    private static final long serialVersionUID = 1L;

    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) {

        processRequest(req);


    }

    protected void processRequest(ServletRequest req) {
        final Gson gson = GsonFactory.getInstance();
        final String userJson = req.getParameter(Parameters.pointUser.getText());
        final String pointJson = req.getParameter(Parameters.pointJson.getText());
        final String valueJson = req.getParameter(Parameters.valueJson.getText());
        final String loopFlagParam = req.getParameter(Parameters.loop.getText());
        final Entity entity = gson.fromJson(pointJson, EntityModel.class);
        final Value value = gson.fromJson(valueJson, ValueModel.class);
        log.info(userJson);
        log.info(pointJson);
        log.info(valueJson);

        final boolean loopFlag = Boolean.valueOf(loopFlagParam);
        final User u = gson.fromJson(userJson, UserModel.class);
        try {
            final Point point = entity instanceof Point
                    ? (Point) entity
                    : (Point) EntityTransactionFactory.getInstance(u).getEntityByKey(entity.getKey(), PointEntity.class).get(0);

            if (!loopFlag) {



                CalculationServiceFactory.getInstance().processCalculations(u, point, value);
                IntelligenceServiceFactory.getInstance().processIntelligence(u, point);
                SubscriptionServiceFactory.getInstance().processSubscriptions(u,  point, value);
                SummaryServiceFactory.getInstance().processSummaries(u, point);



            }

        } catch (NimbitsException e) {
            LogHelper.logException(this.getClass(), e);
        }
    }


}