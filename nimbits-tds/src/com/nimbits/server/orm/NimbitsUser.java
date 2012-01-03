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

package com.nimbits.server.orm;

import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.email.EmailAddress;
import com.nimbits.client.model.user.User;

import javax.jdo.annotations.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


//import com.google.appengine.api.users.User;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class NimbitsUser implements User {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private Boolean sendEmail;

    @Persistent
    private Date dateCreated;

    @Persistent
    private Date lastLoggedIn;

    @Persistent
    private List<Long> connections;

    @Persistent
    private Long facebookID;

    @Persistent
    private String email;

    @Persistent
    private String facebookToken;

    @Persistent
    private String host;

    @Persistent
    private String secret;

    @Persistent
    private String twitterToken;

    @Persistent
    private String twitterTokenSecret;

    @Persistent
    private String userSource;

    @Persistent
    private String uuid;

    // A user that can only work with public data
    @NotPersistent
    private boolean restricted;
    /**
     *
     */
    private static final long serialVersionUID = 1L;


    public List<Long> getConnections() {
        if (connections == null) {
            connections = new LinkedList<Long>();
        }
        return connections;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    @Override
    public Date getLastLoggedIn() {
        return this.lastLoggedIn;// == null ? new Date(this.dateCreated.getTime()) : new Date(this.lastLoggedIn.getTime());
    }

    @Override
    public void setLastLoggedIn(final Date lastLoggedIn) {
        this.lastLoggedIn = new Date(lastLoggedIn.getTime());
    }

    public void setDateCreated(final Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getUserSource() {
        return userSource;
    }

    public void setUserSource(final String userSource) {
        this.userSource = userSource;
    }

    @Persistent
    public String getTwitterTokenSecret() {
        return twitterTokenSecret;
    }

    public void setTwitterTokenSecret(final String twitterTokenSecret) {
        this.twitterTokenSecret = twitterTokenSecret;
    }

    public String getTwitterToken() {
        return twitterToken;
    }

    public void setTwitterToken(final String twitterToken) {
        this.twitterToken = twitterToken;
    }

    public String getFacebookToken() {
        return facebookToken;
    }

    public void setFacebookToken(final String facebookToken) {
        this.facebookToken = facebookToken;
    }

    public void setFacebookID(final long facebookID) {
        this.facebookID = facebookID;
    }

    public NimbitsUser() {
    }

    public NimbitsUser(final EmailAddress email, final String uuid) {
        dateCreated = new Date();
        lastLoggedIn = dateCreated;
        this.email = email.getValue();
        this.sendEmail = true;
        this.uuid = uuid;

    }

    public void setEmail(final EmailAddress email) {
        this.email = email.getValue();
        // case
    }

    public void setSendEmail(final boolean sendEmail) {
        this.sendEmail = sendEmail;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(final String secret) {
        this.secret = secret;
    }

    public boolean isRestricted() {
        return restricted;
    }

    public void setRestricted(final boolean restricted) {
        this.restricted = restricted;
    }

    public void addConnection(final long id) {
        if (!connections.contains(id)) {
            connections.add(id);
        }
    }

    public void removeConnection(final long id) {
        connections.remove(id);
    }

    public boolean getSendEmail() {
        return sendEmail != null && sendEmail;
    }

    public long getId() {
        return id == null ? 0 : id;
    }

    public EmailAddress getEmail() {
        return CommonFactoryLocator.getInstance().createEmailAddress(email);
    }

    public long getFacebookID() {
        return facebookID == null ? 0 : facebookID;
    }

//    public long getID() {
//        return id == null ? 0 : id;
//    }
}