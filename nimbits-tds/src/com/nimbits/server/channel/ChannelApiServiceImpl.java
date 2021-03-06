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

package com.nimbits.server.channel;

import com.google.appengine.api.channel.*;
import com.google.gwt.http.client.*;
import com.google.gwt.user.server.rpc.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.model.point.*;
import com.nimbits.client.service.channel.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 1/1/12
 * Time: 3:37 PM
 */
public class ChannelApiServiceImpl extends RemoteServiceServlet implements ChannelApiService, RequestCallback {


    @Override
    public void onResponseReceived(Request request, Response response) {

    }

    @Override
    public void onError(Request request, Throwable throwable) {

    }

    @Override
    public String openChannel(Point point) {
        ChannelService channelService = ChannelServiceFactory.getChannelService();
        return channelService.createChannel(point.getKey());

    }

    @Override
    public void notifyPointUpdated(Point point) {
        ChannelService channelService = ChannelServiceFactory.getChannelService();

        channelService.sendMessage(new ChannelMessage(point.getKey(), Action.update.name()));
    }

}
