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

package com.nimbits.server.transactions.dao.calculation;

import com.nimbits.*;
import com.nimbits.client.exception.*;
import com.nimbits.client.model.calculation.*;
import com.nimbits.client.model.entity.*;
import com.nimbits.client.model.user.*;
import com.nimbits.server.calculation.*;
import com.nimbits.server.orm.*;

import javax.jdo.*;
import java.util.*;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 2/18/12
 * Time: 12:24 PM
 */
@SuppressWarnings({"unchecked", "unused"})

public class CalculationDAOImpl implements CalculationTransactions {

    public CalculationDAOImpl(final User user) {

    }



    @Override
    public List<Calculation> getCalculations(final Entity entity) throws NimbitsException {


        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final Query q = pm.newQuery(CalcEntity.class);
            q.setFilter("trigger == k");
            q.declareParameters("String k");
            final Collection<Calculation> results = (Collection<Calculation>) q.execute(entity.getKey());
            return CalculationModelFactory.createCalculations(results);

        } finally {
            pm.close();
        }




    }

    @Override
    public void deleteCalculation(final Entity entity) {
        final PersistenceManager pm = PMF.get().getPersistenceManager();

        try {
            final Query q = pm.newQuery(CalcEntity.class);
            q.setFilter("trigger == k");
            q.declareParameters("String k");
            final Collection<Calculation> results = (Collection<Calculation>) q.execute(entity.getKey());
            pm.deletePersistentAll(results);

        } finally {
            pm.close();
        }
    }


}

