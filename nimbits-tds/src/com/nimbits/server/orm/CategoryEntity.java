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

package com.nimbits.server.orm;

import com.nimbits.client.exception.*;
import com.nimbits.client.model.category.*;
import com.nimbits.client.model.entity.*;

import javax.jdo.annotations.*;
import java.util.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/8/12
 * Time: 10:53 AM
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "false")
public class CategoryEntity extends EntityStore implements Category {

    private static final long serialVersionUID = -4488132572071199717L;
    @Persistent
    private final Date createDate;

    public CategoryEntity(final Entity entity) throws NimbitsException {
        super(entity);
        createDate = new Date();
    }


}
