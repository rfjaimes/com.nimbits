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

package com.nimbits.server.email;

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointName;
import com.nimbits.client.model.user.User;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueModelFactory;
import com.nimbits.server.point.PointServiceFactory;
import com.nimbits.server.recordedvalue.RecordedValueServiceFactory;
import com.nimbits.server.user.UserTransactionFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.logging.Logger;

public class IncomingMailTask extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    //  private final Map<String, Point> points = new HashMap<String, Point>();
    private static final Logger log = Logger.getLogger(IncomingMailTask.class.getName());


    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse resp) {

        final String fromAddress = req.getParameter(Const.PARAM_FROM_ADDRESS);
        final String inContent = req.getParameter(Const.PARAM_IN_CONTENT);

        final EmailAddress internetAddress = CommonFactoryLocator.getInstance().createEmailAddress(fromAddress);
        // final UserContext context = GsonFactory.getInstance().fromJson(contextJson, UserContextImpl.class);
        final User u;
        try {
            log.info("Incoming mail post: " + internetAddress);
            u = UserTransactionFactory.getInstance().getNimbitsUser(internetAddress);
            // delete me context.addTrace(IncomingMailTask.class.getName());


            final String content = inContent.replaceAll("\n", "").replaceAll("\r", "");
            final String Data[] = content.split(";");
            log.info("Incoming mail post: " + inContent);

            if (u != null) {
                // delete me context.addTrace(u.getEmail().getValue());
                if (Data.length > 0) {
                    for (String s : Data) {
                        processLine(u, s);
                    }
                }
            } else {
                log.severe("Null user for incoming mail:" + fromAddress);

            }
        } catch (NimbitsException e) {
            log.severe(e.getMessage());
        }
        //TODO add users to list of spammers

    }

    void processLine(final User u, final String s) throws NimbitsException {
        final String emailLine[] = s.split(",");
        final PointName pointName = CommonFactoryLocator.getInstance().createPointName(emailLine[0]);
        final Point point = PointServiceFactory.getInstance().getPointByName(u, pointName);

        if (point != null) {
            sendValue(u, point, emailLine);
        }
    }

    private static void sendValue(final User u,
                                  final Point point,
                                  final String k[]
    ) throws NimbitsException {


        long timestamp;
        Double v = 0.0;
        String note;

        if (k != null && k.length > 1) {

            try {
                v = Double.valueOf(k[1].trim());
            } catch (NumberFormatException e1) {
                log.info("Invalid mail message from: " + u.getEmail() + " " + k[0] + "," + k[1]);
            }

            if (k.length == 3) {

                try {
                    String ts = k[2].trim();
                    timestamp = Long.parseLong(ts);
                } catch (NumberFormatException e) {
                    timestamp = new Date().getTime();
                    log.info("Invalid mail message from: " + u.getEmail() + " " + k[0] + "," + k[1] + "," + k[2]);
                }
            } else {
                timestamp = new Date().getTime();
            }
            if (k.length == 4) {
                note = (k[3].trim());
            } else {
                note = "";
            }
            final Value value = ValueModelFactory.createValueModel(0.0, 0.0, v, new Date(timestamp), point.getId(), note);
            RecordedValueServiceFactory.getInstance().recordValue(u, point, value, false);
        }


    }

}