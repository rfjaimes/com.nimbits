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

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.*;
import com.google.gson.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.value.*;
import com.nimbits.server.gson.*;
import com.nimbits.server.user.*;

import javax.servlet.http.*;
import java.util.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 10/7/11
 * Time: 2:12 PM
 */
public class TaskFactoryImpl implements TaskFactory {
    private final Gson gson = GsonFactory.getInstance();

    @Override
    public void startDeleteDataTask(final Point point,
                                    final boolean onlyExpired,
                                    final int exp) {


        final Queue queue = QueueFactory.getQueue(Const.QUEUE_DELETE_DATA);
        if (onlyExpired) {
            queue.add(TaskOptions.Builder.withUrl(Const.PATH_DELETE_DATA_TASK)
                    .param(Const.Params.PARAM_POINT_ID,  point.getUUID())
                    .param(Const.Params.PARAM_EXP, Long.toString(exp))

            );
        } else {
            queue.add(TaskOptions.Builder.withUrl(Const.PATH_DELETE_DATA_TASK)
                    .param(Const.Params.PARAM_POINT_ID, (point.getUUID()))
            );
        }


    }
    @Override
    public void startSummaryTask(final Entity entity) {
        final Queue queue = QueueFactory.getQueue(Const.QUEUE_DELETE_SUMMARY);
        String json = GsonFactory.getInstance().toJson(entity);
        queue.add(TaskOptions.Builder.withUrl(Const.PATH_SUMMARY_TASK)
                .param(Const.Params.PARAM_JSON, json)
        );
    }


    @Override
    public void startProcessBatchTask(final HttpServletRequest req, final HttpServletResponse resp) throws NimbitsException {


        final com.google.appengine.api.taskqueue.Queue queue = QueueFactory.getQueue(Const.QUEUE_PROCESS_BATCH);
        final User u = UserServiceFactory.getServerInstance().getHttpRequestUser(req);

        final String userJson = gson.toJson(u);

        final TaskOptions options = TaskOptions.Builder.withUrl(Const.PATH_TASK_PROCESS_BATCH);
        final Enumeration enumeration = req.getParameterNames();
        final Map m = req.getParameterMap();

        while (enumeration.hasMoreElements()) {
            final String param = enumeration.nextElement().toString();
            final String value = ((String[]) m.get(param))[0];
            options.param(param, value);
        }

        options.param(Const.Params.PARAM_JSON_USER, userJson);

        queue.add(options);


    }

    @Override
    public void startRecordValueTask(final User u, final Point point, final Value value, final boolean loopFlag) {
        if (Double.valueOf(value.getNumberValue()).isInfinite()) {
            return;
        }
        final Queue queue = QueueFactory.getQueue(Const.QUEUE_RECORD_VALUE);

        final String userJson = gson.toJson(u);
        final String pointJson = gson.toJson(point);
        final String valueJson = gson.toJson(value);

        queue.add(TaskOptions.Builder.withUrl(Const.PATH_TASK_RECORD_VALUE)
                .param(Const.Params.PARAM_JSON_USER, userJson)
                .param(Const.Params.PARAM_JSON_POINT, pointJson)
                .param(Const.Params.PARAM_JSON_VALUE, valueJson)
                .param(Const.PARAM_LOOP, String.valueOf(loopFlag))

        );
    }

    @Override
    public void startIncomingMailTask(final String fromAddress, final String inContent) {

        final Queue queue = QueueFactory.getQueue(Const.QUEUE_INCOMING_MAIL);
        queue.add(TaskOptions.Builder.withUrl(Const.PATH_INCOMING_MAIL_QUEUE)
                .param(Const.Params.PARAM_FROM_ADDRESS, fromAddress)
                .param(Const.IN_CONTENT, inContent));


    }

    @Override
    public void startPointMaintTask(final Point point) {
        final String json = gson.toJson(point);

        final Queue queue = QueueFactory.getQueue(Const.TASK_POINT_MAINT);

        queue.add(TaskOptions.Builder.withUrl(Const.PATH_POINT_MAINT_TASK)
                .param(Const.Params.PARAM_POINT, json));

    }



//    @Override
//    public void startEntityMaintTask(final User user, ) {
//        final String json = gson.toJson(user);
//
//        final Queue queue = QueueFactory.getQueue(Const.TASK_CATEGORY_MAINT);
//
//        queue.add(TaskOptions.Builder.withUrl(Const.PATH_CATEGORY_MAINT_TASK)
//                .param(Const.PARAM_USER, json));
//
//    }

    @Override
    public void startUpgradeTask(final Action action,final  Entity entity) {


        final Queue queue = QueueFactory.getQueue(Const.TASK_UPGRADE);
        String json = "";
        if (entity != null) {
            json = GsonFactory.getInstance().toJson(entity);
        }
        queue.add(TaskOptions.Builder.withUrl(Const.PATH_UPGRADE_TASK)
        .param(Const.Params.PARAM_JSON, json)
        .param(Const.Params.PARAM_ACTION, action.getCode()));

    }

    @Override
    public void startMoveCachedValuesToStoreTask(final Point point) {
        final String json = gson.toJson(point);

        final Queue queue = QueueFactory.getQueue(Const.TASK_MOVE);

        queue.add(TaskOptions.Builder.withUrl(Const.PATH_MOVE_TASK)
                .param(Const.Params.PARAM_POINT, json));
    }


}
