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

package com.nimbits.server.api.impl;

import com.nimbits.client.common.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.common.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.server.api.*;
import com.nimbits.server.entity.*;
import com.nimbits.server.feed.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.logging.*;
import com.nimbits.server.orm.*;
import com.nimbits.server.value.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;



public class ValueServletImpl extends ApiServlet {



    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {

        try {
            processPost(req, resp);
        } catch (NimbitsException e) {
            LogHelper.logException(this.getClass(), e);
        }

    }
    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {

        try {
            processGet(req, resp);
        } catch (NimbitsException e) {
             LogHelper.logException(this.getClass(), e);
        }

    }

    protected static void processPost(final HttpServletRequest req, final HttpServletResponse resp) throws NimbitsException, IOException {
        doInit(req, resp, ExportType.plain);

        if (user != null && ! user.isRestricted()) {

            final EntityName pointName = CommonFactoryLocator.getInstance().createName(getParam(Parameters.point), EntityType.point);
            final Entity point = EntityServiceFactory.getInstance().getEntityByName(user, pointName, PointEntity.class.getName()).get(0);

            if (point != null) {


                final Value v;

                if (Utils.isEmptyString(getParam(Parameters.json))) {
                    final double latitude = getDoubleFromParam(getParam(Parameters.lat));
                    final double longitude = getDoubleFromParam(getParam(Parameters.lng));
                    final double value = getDoubleFromParam(getParam(Parameters.value));
                    final Date timestamp = getParam(Parameters.timestamp) != null ? new Date(Long.parseLong(getParam(Parameters.timestamp))) : new Date();
                    v = ValueModelFactory.createValueModel(latitude, longitude, value, timestamp, getParam(Parameters.note), getParam(Parameters.json));
                } else {
                    final Value vx = GsonFactory.getInstance().fromJson(getParam(Parameters.json), ValueModel.class);

                    v = ValueModelFactory.createValueModel(vx.getLatitude(), vx.getLongitude(), vx.getDoubleValue(), vx.getTimestamp(),
                            vx.getNote(), vx.getData(), AlertType.OK);
                }

                final Value result = RecordedValueServiceFactory.getInstance().recordValue(user, point, v, false);
                final PrintWriter out = resp.getWriter();
                final String j = GsonFactory.getInstance().toJson(result);
                out.print(j);

            }
            else {
                FeedServiceFactory.getInstance().postToFeed(user, new NimbitsException(UserMessages.ERROR_POINT_NOT_FOUND));
            }

        }

    }

    public static void processGet(final HttpServletRequest req, final HttpServletResponse resp) throws NimbitsException, IOException {
        doInit(req, resp, ExportType.plain);
        final PrintWriter out = resp.getWriter();
        Value nv = null;
        final String format = getParam(Parameters.format)==null ? Words.WORD_DOUBLE : getParam(Parameters.format);

        if (format.equals(Parameters.json.getText()) && !Utils.isEmptyString(getParam(Parameters.json))) {
            nv = GsonFactory.getInstance().fromJson(getParam(Parameters.json), ValueModel.class);
        } else if (format.equals(Words.WORD_DOUBLE) && !Utils.isEmptyString(getParam(Parameters.value))) {
            nv = ValueModelFactory.createValueModel(
                    getParam(Parameters.value),
                    getParam(Parameters.note),
                    getParam(Parameters.lat),
                    getParam(Parameters.lng),
                    getParam(Parameters.json));
        }
        out.println(processRequest(getParam(Parameters.point), getParam(Parameters.uuid), format, nv, user));
        out.close();


    }

    private static double getDoubleFromParam(final String valueStr) {
        double retVal;
        try {
            retVal = valueStr != null ? Double.valueOf(valueStr) : 0;
        } catch (NumberFormatException e) {
            retVal = 0;
        }
        return retVal;
    }

    protected static String processRequest(
            final String pointNameParam,
            final String uuid,
            final String format,
            final Value nv,
            final User u) throws NimbitsException {

        final List<Entity> result;
        if (!Utils.isEmptyString(uuid)) {
            result = EntityServiceFactory.getInstance().getEntityByKey(uuid, PointEntity.class.getName());
        }
        else if (!Utils.isEmptyString(pointNameParam)) {
            final EntityName pointName = CommonFactoryLocator.getInstance().createName(pointNameParam, EntityType.point);
            LogHelper.log(ValueServletImpl.class, "Getting point "  + pointNameParam);
            result = EntityServiceFactory.getInstance().getEntityByName(u, pointName,PointEntity.class.getName());
        }
        else {
            throw new NimbitsException(UserMessages.ERROR_POINT_NOT_FOUND);
        }

        if (result.isEmpty()) {
            throw new NimbitsException(UserMessages.ERROR_POINT_NOT_FOUND);
        }
        else {

            final Entity p = result.get(0);
            if ((u == null || u.isRestricted()) && !p.getProtectionLevel().equals(ProtectionLevel.everyone)) {
                throw new NimbitsException(UserMessages.RESPONSE_PROTECTED_POINT);
            }
            final Value value;
            if (nv != null && u != null && !u.isRestricted()) {
                // record the value, but not if this is a public
                // request
                final Value newValue = ValueModelFactory.createValueModel(
                        nv.getLatitude(), nv.getLongitude(), nv.getDoubleValue(),
                        nv.getTimestamp(), nv.getNote(), nv.getData());


                value = RecordedValueServiceFactory.getInstance().recordValue(u, p, newValue, false);
            } else {
                value = RecordedValueServiceFactory.getInstance().getCurrentValue(p);
            }
            return value != null ? format.equals(Parameters.json.getText()) ? GsonFactory.getInstance().toJson(value) : String.valueOf(value.getDoubleValue()) : "";

        }

    }


}
