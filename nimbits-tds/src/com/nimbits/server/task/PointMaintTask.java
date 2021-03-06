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
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.valueblobstore.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.logging.*;
import com.nimbits.server.user.*;
import com.nimbits.server.value.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;

public class PointMaintTask extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(PointMaintTask.class.getName());


    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {



        try {
            processPost(req, resp);

        } catch (Exception ex) {
            LogHelper.logException(this.getClass(), ex);
        }

    }


    protected static void processPost(final ServletRequest req, final ServletResponse resp) throws NimbitsException {
        final Gson gson = GsonFactory.getInstance();
        resp.setContentType(Const.CONTENT_TYPE_HTML);

        final String j = req.getParameter(Parameters.json.getText());
        final Point e = gson.fromJson(j, PointModel.class);
        final User u = UserServiceFactory.getInstance().getUserByKey(e.getOwner());
        if (e.getExpire() > 0) {
            TaskFactory.getInstance().startDeleteDataTask(
                    e,
                    true, e.getExpire());
        }
        consolidateBlobs(u, e);

    }



    public static void consolidateBlobs(final User u, final Entity e) throws NimbitsException {
        // n = UserTransactionFactory.getInstance().(p.getUserFK());
        // final Point p = PointServiceFactory.getInstance().getPointByKey(e.getKey());


        final List<ValueBlobStore> stores = RecordedValueTransactionFactory.getDaoInstance(e).getAllStores();
        if (! stores.isEmpty()) {
            log.info("Consolidating " + stores.size() + " blob stores");
            final Collection<Long> dates = new ArrayList<Long>(stores.size());
            for (final ValueBlobStore store : stores) {
                //consolidate blobs that have more than one date.
                if (dates.contains(store.getTimestamp().getTime())) {
                    RecordedValueTransactionFactory.getDaoInstance(e).consolidateDate(store.getTimestamp());
                    log.info("Consolidating " + store.getTimestamp());
                }
                else {
                    log.info("Adding first time " + store.getTimestamp());
                    dates.add(store.getTimestamp().getTime());
                }
            }



        }
    }


}

