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

package com.nimbits.server.calculation;

import com.nimbits.client.enums.EntityType;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.calculation.Calculation;
import com.nimbits.client.model.calculation.CalculationModelFactory;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.Entity;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueModelFactory;
import com.nimbits.server.point.PointServiceFactory;
import com.nimbits.server.value.RecordedValueServiceFactory;
import helper.NimbitsServletTest;
import org.junit.Test;

import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 4/1/12
 * Time: 7:14 PM
 */
public class CalculationServiceImplTest extends NimbitsServletTest {


    @Test
    public void testCalcs() throws NimbitsException, InterruptedException {
        //TODO pass test

        final EntityName targetName = CommonFactoryLocator.getInstance().createName("TARGET" + UUID.randomUUID().toString(), EntityType.point);
        final EntityName triggerName = CommonFactoryLocator.getInstance().createName("TRIGGER" + UUID.randomUUID().toString(), EntityType.point);

        final EntityName yName = CommonFactoryLocator.getInstance().createName(UUID.randomUUID().toString(), EntityType.point);
        final EntityName zName = CommonFactoryLocator.getInstance().createName(UUID.randomUUID().toString(), EntityType.point);


        final EntityName cName = CommonFactoryLocator.getInstance().createName(UUID.randomUUID().toString(), EntityType.calculation);
        // Category c = ClientHelper.client().addCategory(cName);
        // assertNotNull(c);
        final Point trigger = PointServiceFactory.getInstance().addPoint(triggerName);
        final Point target = PointServiceFactory.getInstance().addPoint(targetName);

        final Point y =PointServiceFactory.getInstance().addPoint( yName);
        final Point z = PointServiceFactory.getInstance().addPoint(zName);

        assertNotNull(y);
        assertNotNull(z);
        assertNotNull(trigger);
        assertNotNull(trigger.getKey());
        assertNotNull(target);
        assertNotNull(target.getKey());
        final Random r = new Random();

        final double r1 = r.nextDouble();
        final double r2 = r.nextDouble();
        final double ry = r.nextDouble();
        final double rz = r.nextDouble();

        final Calculation calculation = CalculationModelFactory.createCalculation(trigger.getKey(), UUID.randomUUID().toString(),
                true, "x+y+z+" + r1, target.getKey(), trigger.getKey(), y.getKey(), z.getKey());

        final Entity ce = CalculationServiceFactory.getInstance().addUpdateCalculation(null, cName, calculation);
        assertNotNull(ce);

        final Calculation c = CalculationServiceFactory.getInstance().getCalculation(ce);
        assertNotNull(c);

        PointServiceFactory.getInstance().updatePoint(trigger);

        RecordedValueServiceFactory.getInstance().recordValue(user,yName, ValueModelFactory.createValueModel(ry));

        RecordedValueServiceFactory.getInstance().recordValue(user, zName, ValueModelFactory.createValueModel(rz));
        Thread.sleep(100);
        final Value vt = RecordedValueServiceFactory.getInstance().recordValue(user, triggerName,  ValueModelFactory.createValueModel(r2));

        assertEquals(vt.getDoubleValue(), r2, 0.0001);



        final Value vy =RecordedValueServiceFactory.getInstance().getCurrentValue(y);// ClientHelper.client().getCurrentRecordedValue(yName);
        assertEquals(vy.getDoubleValue(), ry, 0.0001);

        final Value vz = RecordedValueServiceFactory.getInstance().getCurrentValue(z);
        assertEquals(vz.getDoubleValue(), rz, 0.0001);

        Thread.sleep(100);
        final Value endResult =RecordedValueServiceFactory.getInstance().getCurrentValue(target);
        assertNotNull(endResult);
        assertEquals(r1 + r2 + ry + rz, endResult.getDoubleValue(), 0.001);

    }
}