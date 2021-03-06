package integration;/*
 * Copyright (c) 2011. Tonic Solutions LLC. All Rights reserved.
 *
 * This source code is distributed under GPL v3 without any warranty.
 */

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.value.Value;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by bsautner
 * User: benjamin
 * Date: 3/27/11
 * Time: 3:08 PM
 */
@SuppressWarnings("all")
public class DataLoadingTest {
    @Test
    public void loadFoo() throws InterruptedException, NimbitsException {

        for (int i = 0; i < 10; i++) {

            Random r = new Random();
            ClientHelper.client().recordValue("foo", r.nextDouble() * 100);

        }
    }


    @Test
    public void TestRecordValueWithGet() throws InterruptedException, IOException, NimbitsException {
        EntityName pointName = CommonFactoryLocator.getInstance().createName(UUID.randomUUID().toString());

        ClientHelper.client().addPoint( pointName);

        for (int i = 0; i < 10; i++) {

            ClientHelper.client().recordValueWithGet(pointName, i, new Date());
            //sleep(1000);
        }

        sleep(1000);
        List<Value> result = ClientHelper.client().getSeries(pointName, 10);
        double t = 0;
        for (Value v : result) {

            t += v.getDoubleValue();
        }
        System.out.println(t);
        assertEquals(45.0, t, 0);
        ClientHelper.client().deletePoint(pointName);
    }

    public static double roundDouble(final double d) {
        return d;
//        int ix = (int) (d * 100.0); // scale it
//        return ((double) ix) / 100.0;
    }

    @Test
    public void loadLarge1() throws InterruptedException, IOException, NimbitsException {
        final String pointName = ("large" + UUID.randomUUID().toString());

        final Point p = ClientHelper.client().addPoint(pointName);
        assertNotNull(p);
        p.setFilterValue(-1);
        ClientHelper.client().updatePoint(p);
        final Random r = new Random();
        double total = 0.0;
        double total2 = 0.0;
        long d;
        double rv;
        Value v;
        for (int i = -999; i < 0; i++) {

            d = new Date().getTime() + (i * 1000 * 60);
            rv = roundDouble(r.nextDouble() * 100);
            total += rv;
           v = ClientHelper.client().recordValue(pointName, rv, new Date(d));
            // System.out.println(i);
            assertNotNull(v);
            //System.out.println(v.getNumberValue());
        }
        Thread.sleep(2000);
        final List<Value> values = ClientHelper.client().getSeries(pointName, 999);
        //System.out.println(values.size());
        for (final Value vx : values) {
            total2 += roundDouble(vx.getDoubleValue());

        }
        assertEquals(total, total2, 0.001);


    }


}
