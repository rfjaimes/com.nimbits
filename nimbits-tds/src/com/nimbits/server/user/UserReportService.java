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

package com.nimbits.server.user;

import com.nimbits.client.common.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.user.*;
import com.nimbits.server.logging.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;

/**
 * Created by Benjamin Sautner
 * User: bsautner
 * Date: 8/17/11
 * Time: 4:21 PM
 */
public class UserReportService extends HttpServlet {

    private static final int INT = 60;

    @Override
    public void doGet(final HttpServletRequest req,final HttpServletResponse resp) throws IOException {

        String c = req.getParameter(Parameters.count.getText());
        String email = req.getParameter(Parameters.email.getText());
        final PrintWriter out = resp.getWriter();
        try {
            int count = 100;
            if (! Utils.isEmptyString(c)) {
            count = Integer.valueOf(c);
        }


        if (Utils.isEmptyString(email)) {

            final List<User> users = UserTransactionFactory.getInstance().getAllUsers("lastLoggedIn desc", count);


            out.println("<p>Total number of users: " + users.size() + "</p>");
            out.println("<table border = 1>");
            out.println("<TR>");
            out.println("<TD>" + "User" + "</TD>");
            out.println("<TD>" + "Last Logged In" + "</TD>");
            out.println("<TD>" + "Date Created" + "</TD>");
            out.println("<TD>" + "API Calls" + "</TD>");
            out.println("</TR>");

            for (final User u : users) {
                boolean returnedUser = !(u.getLastLoggedIn().getTime() - u.getDateCreated().getTime() > 1000 * INT);

                if (returnedUser) {
                    out.println("<TR  bgcolor=\"#006633\">");
                } else {
                    out.println("<TR  bgcolor=\"#CCCC99\">");
                }


                    out.println("<TD>" + u.getEmail() + "</TD>");

                out.println("<TD>" + u.getLastLoggedIn() + "</TD>");
                out.println("<TD>" + u.getDateCreated() + "</TD>");

              //  ShardedCounter counter = getOrCreateCounter(u.getEmail());
                //out.println("<TD>" +counter.getCount() + "</TD>");
                out.println("</TR>");

            }

            out.println("</table>");
        }
        else {
          //  EmailAddress emailAddress = CommonFactoryLocator.getInstance().createEmailAddress(email);
          //  out.println("<h5>" + getOrCreateCounter(emailAddress).getCount() + "</h5>");

            out.println();
        }
        } catch (NimbitsException e) {
            LogHelper.logException(this.getClass(), e);
        }

        out.close();
    }

}
