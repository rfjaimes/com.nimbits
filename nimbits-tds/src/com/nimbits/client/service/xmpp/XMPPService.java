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

package com.nimbits.client.service.xmpp;

import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.email.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.model.user.*;
import com.nimbits.client.model.xmpp.*;

import java.util.*;

@RemoteServiceRelativePath("xmpp")
public interface XMPPService extends RemoteService {
    void sendInvite() throws NimbitsException;

    void sendMessage(final String msgBody, final EmailAddress email);

    List<XmppResource> getPointXmppResources(final User user, final Point point) throws NimbitsException;

    Entity createXmppResource(final Entity targetPointEntity, final EntityName resourceName) throws NimbitsException;

    void sendMessage(final List<XmppResource> resources, final String message, final EmailAddress email) throws NimbitsException;

    void deleteResource(final User u, final Entity entity);
}
