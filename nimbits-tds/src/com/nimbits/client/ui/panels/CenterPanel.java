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

package com.nimbits.client.ui.panels;

import com.extjs.gxt.ui.client.*;
import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.util.*;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.layout.*;
import com.google.gwt.core.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.*;
import com.nimbits.client.constants.*;
import com.nimbits.client.enums.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.value.*;
import com.nimbits.client.service.subscription.*;
import com.nimbits.client.service.twitter.*;
import com.nimbits.client.service.xmpp.*;
import com.nimbits.client.ui.controls.*;
import com.nimbits.client.ui.helper.*;

import java.util.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 7/23/11
 * Time: 10:37 AM
 */
public class CenterPanel extends NavigationEventProvider {
    private static final String DEFAULT_CHART_NAME = "Chart";
    private static final int HEIGHT = 450;
    // private final Map<String, Entity> entities = new HashMap<String, Entity>();
    // private final Map<String, AnnotatedTimeLinePanel> lines = new HashMap<String, AnnotatedTimeLinePanel>();

    private NavigationPanel navigationPanel;
    private Map<SettingType, String> settings;
    private LoginInfo loginInfo;
    private LayoutContainer chartContainer;
    private int chartHeight;
    HBoxLayoutData flex = new HBoxLayoutData(new Margins(0, 5, 0, 0));
    Action action;

    public CenterPanel(LoginInfo info, Map<SettingType, String> settings, Action action) {
        this.loginInfo = info;
        this.settings = settings;
        this.action = action;

    }

    @Override
    protected void onRender(final Element target, final int index) {
        super.onRender(target, index);
        try {
            loadLayout();
        } catch (NimbitsException e) {
            FeedbackHelper.showError(e);
        }

    }

    NavigationPanel createNavigationPanel() {
        final NavigationPanel navTree = new NavigationPanel(loginInfo.getUser(), settings);


        navTree.addEntityClickedListeners(new EntityClickedListener() {

            @Override
            public void onEntityClicked(final TreeModel c)  {
                addEntity(c);

            }

        });

        navTree.addEntityDeletedListeners(new EntityDeletedListener() {

            @Override
            public void onEntityDeleted(final Entity c)  {
                notifyEntityDeletedListener(c);

            }

        });

        navTree.addValueEnteredListeners(new ValueEnteredListener() {
            @Override
            public void onValueEntered(TreeModel model, Value value) {
                AnnotatedTimeLinePanel p;
              for (int i = 0; i < chartContainer.getItemCount(); i++) {
                  p = (AnnotatedTimeLinePanel) chartContainer.getItem(i);
                  try {
                      p.addValue(model, value);
                  } catch (NimbitsException e) {
                      FeedbackHelper.showError(e);
                  }
              }
            }
        });
        return navTree;

    }

    private void loadLayout() throws NimbitsException {

        final ContentPanel panel = new ContentPanel( );
        chartHeight  = Double.valueOf(Window.getClientHeight() * .9 / 2).intValue();
        int navHeight  = Double.valueOf(Window.getClientHeight() / 2).intValue()-50;
        MainMenuBar toolBar = initToolbar(loginInfo, settings);
        panel.setTopComponent(toolBar);
        panel.setLayout(new RowLayout(Style.Orientation.VERTICAL));
        panel.setHeaderVisible(false);

        panel.setFrame(false);
        panel.setBodyBorder(true);
        panel.setCollapsible(true);

        navigationPanel = createNavigationPanel();
        navigationPanel.setLayout(new FillLayout());
        navigationPanel.setHeight(navHeight);
        panel.add(navigationPanel, new RowData(1, .5, new Margins(0)));


        HBoxLayout layout = new HBoxLayout();
        layout.setPadding(new Padding(0));
        layout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.MIDDLE);
        chartContainer = new LayoutContainer();
        chartContainer.setLayout(layout);
        flex.setFlex(1);
        addChart();
        // addChart();



        if (action != Action.android) {
            ContentPanel chartPanel = new ContentPanel();
            chartPanel.setHeight(HEIGHT);
            chartPanel.setFrame(true);
            chartPanel.setHeaderVisible(false);
            chartPanel.add(chartContainer);
            panel.add(chartPanel, new RowData(1, 5, new Margins(0)));

        }


        add(panel, new FlowData(0));
        //chartPanel.layout();
        layout(true);

    }

    private MainMenuBar initToolbar(final LoginInfo loginInfo, Map<SettingType, String> settings) throws NimbitsException {
        MainMenuBar toolBar = new MainMenuBar(loginInfo, settings);
        toolBar.addEntityModifiedListeners(new MainMenuBar.EntityModifiedListener() {
            @Override
            public void onEntityModified(TreeModel model, Action action) throws NimbitsException {
                switch (action) {
                    case update: case create:
                        navigationPanel.addUpdateTreeModel(model, false);
                        break;
                    case refresh:
                        navigationPanel.getUserEntities(true);
                }
            }
        } );
        toolBar.addActionListeners(new MainMenuBar.ActionListener() {
            @Override
            public void onAction(Action action) throws NimbitsException {
                switch (action) {
                    case expand:
                        navigationPanel.toggleExpansion();
                        break;
                    case logout:
                        final String logoutUrl = loginInfo != null ? loginInfo.getLogoutUrl() : Path.PATH_NIMBITS_HOME;
                        Window.Location.replace(logoutUrl);
                        break;
                    case xmpp:
                        sendXMPPInvite();
                        break;
                    case facebook:
                        Window.Location.replace(Path.PATH_FACEBOOK_APP);
                        break;
                    case twitter:
                        twitterAuthorise();
                        break;
                    case addChart:
                        addChart();
                        break;
                    case save:
                        navigationPanel.saveAll();
                        break;
                }
            }
        });
        return toolBar;
    }

    private void addChart() {
        final AnnotatedTimeLinePanel line = new AnnotatedTimeLinePanel(true,  DEFAULT_CHART_NAME + (chartContainer.getItemCount() + 1));
        line.setHeight(chartHeight);
        line.setSelected(true);
        line.addListener(Events.OnClick, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent baseEvent) {
                AnnotatedTimeLinePanel p;
                for (int i = 0; i < chartContainer.getItemCount(); i ++) {
                    p = (AnnotatedTimeLinePanel) chartContainer.getItem(i);
                    line.setSelected(p.getName().equals(line.getName()));
                }
            }
        });
        line.addChartRemovedClickedListeners(new AnnotatedTimeLinePanel.ChartRemovedListener() {
            @Override
            public void onChartRemovedClicked() {
                for (int i = 0; i < chartContainer.getItemCount(); i ++) {
                    AnnotatedTimeLinePanel p = (AnnotatedTimeLinePanel) chartContainer.getItem(i);
                    if (p.getName().equals(line.getName())) {
                        chartContainer.remove(p);
                        break;
                    }
                }
//                for (int i = 0; i < chartContainer.getItemCount(); i ++) {
//                    AnnotatedTimeLinePanel p = (AnnotatedTimeLinePanel) chartContainer.getItem(i);
//                }
                chartContainer.layout(true);
            }
        });
        chartContainer.add(line, flex);
        layout(true);
    }

    private void sendXMPPInvite() {
        XMPPServiceAsync IMService = GWT.create(XMPPService.class);
        IMService.sendInvite(new XMPPInviteAsyncCallback());
    }

    private void twitterAuthorise() throws NimbitsException {
        TwitterServiceAsync twitterService = GWT.create(TwitterService.class);
        twitterService.twitterAuthorise(loginInfo.getEmailAddress(), new TwitterAuthoriseAsyncCallback());
    }

    public void addEntity(final TreeModel model) {
        try {
        switch (model.getEntityType()) {
            case user:
                break;
            case point: case category:
                chartEntity(model);
                break;
            case file:
                EntityOpenHelper.showEntity(model.getBaseEntity());
                break;
            case subscription:
                displaySubscription(model.getBaseEntity());
                break;
            case userConnection:
                break;
            case feed:
                notifyEntityClickedListener(model);
                break;
        }
        } catch (NimbitsException ex) {
            FeedbackHelper.showError(ex);
        }

    }


    private void displaySubscription(final Entity entity) {
        SubscriptionServiceAsync service = GWT.create(SubscriptionService.class);
        service.getSubscribedEntity(entity, new GetSubscribedEntityAsyncCallback());
    }
   //chart
    private void chartEntity(final TreeModel model) {
        for (int i = 0; i < chartContainer.getItemCount(); i++) {
            AnnotatedTimeLinePanel p = (AnnotatedTimeLinePanel) chartContainer.getItem(i);
            if (p.isSelected()) {
                p.addEntityModel(model);
            }
        }
    }

    private static class TwitterAuthoriseAsyncCallback implements AsyncCallback<String> {

        @Override
        public void onFailure(Throwable caught) {
            GWT.log(caught.getMessage(), caught);
        }

        @Override
        public void onSuccess(String result) {
            //	Window.alert(result);
            Window.Location.replace(result);
        }

    }

    private static class XMPPInviteAsyncCallback implements AsyncCallback<Void> {

        @Override
        public void onFailure(Throwable caught) {


        }

        @Override
        public void onSuccess(Void result) {
            Window.alert("Please check your instant messaging client for an invite to chat from nimbits1.appspot.com. You must accept the invitation in order for Nimbits to IM you.");

        }

    }


    private class GetSubscribedEntityAsyncCallback implements AsyncCallback<Entity> {

        @Override
        public void onFailure(Throwable caught) {
            GWT.log(caught.getMessage(), caught);
        }

        @Override
        public void onSuccess(Entity result) {
            try {
                addEntity( new GxtModel(result));
            } catch (NimbitsException e) {
                FeedbackHelper.showError(e);
            }
        }
    }
}
