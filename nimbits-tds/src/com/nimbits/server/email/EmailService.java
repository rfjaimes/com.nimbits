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

package com.nimbits.server.email;

import com.nimbits.client.exception.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.value.*;

public interface EmailService {
    void sendEmail(final EmailAddress email, final String message);

    void sendEmail(final EmailAddress email, final String message, final String subject);

    void sendEmail(final EmailAddress fromEmail,
                   final EmailAddress emailAddress,
                   final String message,
                   final String subject);

    void sendAlert(final Entity entity, final Point point, final EmailAddress email, final Value value) throws NimbitsException;
}
