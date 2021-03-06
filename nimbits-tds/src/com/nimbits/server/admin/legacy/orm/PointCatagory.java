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

package com.nimbits.server.admin.legacy.orm;

import javax.jdo.annotations.*;

@Deprecated //only here for one time DTS on upgrade
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class PointCatagory   {

    public PointCatagory() {

    }

    public PointCatagory( Long userFK, String name, String description, String uuid, Integer protectionLevel) {

        this.userFK = userFK;
        this.name = name;
        this.description = description;
        this.uuid = uuid;
        this.protectionLevel = protectionLevel;
    }

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private Long userFK;

    @Persistent
    private String name;

    @Persistent
    private String description;

    @Persistent
    private String uuid;

    @Persistent
    private Integer protectionLevel;

    public Long getId() {
        return id;
    }

    public Long getUserFK() {
        return userFK;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUuid() {
        return uuid;
    }

    public Integer getProtectionLevel() {
        return protectionLevel;
    }
}
