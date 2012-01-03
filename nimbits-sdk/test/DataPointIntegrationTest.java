/*
 * Copyright (c) 2011. Tonic Solutions, LLC. All Rights Reservered. This Code is distributed under GPL V3 without any warrenty.
 */

import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.category.Category;
import com.nimbits.client.model.category.CategoryName;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.value.Value;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Benjamin Sautner
 * User: benjamin
 * Date: 3/14/11
 * Time: 7:28 PM
 */
public class DataPointIntegrationTest extends TestCase {


    private CategoryName cat;


    @Before
    public void setUp() throws Exception {
        cat = CommonFactoryLocator.getInstance().createCategoryName(UUID.randomUUID().toString());

        Category c = ClientHelper.client().addCategory(cat);

        Assert.assertTrue(c.getId() > 0);
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("tear down waiting for tasks to finish");
        Thread.sleep(1000);
        // ClientHelper.client().deleteCategory(cat);
        System.out.println("tear down complete");
    }

    @Test
    public void testNoCompression() throws Exception {
        Point p = new PointModel();

        p.setName(CommonFactoryLocator.getInstance().createPointName("test" + UUID.randomUUID().toString()));
        p.setExpire(1);
        p.setCompression(0);
        Point r = ClientHelper.client().addPoint(p, cat);

        double x = testCompression(p);
        ClientHelper.client().deletePoint(p.getName());

        Assert.assertEquals(345.0, x);

        //   gClient.DeletePoint(p.getValue());
    }

    @Test
    public void testCompressionSeparatePostsNoDate() throws Exception {
        Point p = new PointModel();
        p.setName(CommonFactoryLocator.getInstance().createPointName("test" + UUID.randomUUID().toString()));
        p.setCompression(0.1);
        ClientHelper.client().addPoint(p, cat);

        double rx = 0.0;

        try {

            for (int i = 0; i < 40; i++) {
                StringBuilder b = new StringBuilder();
                rx += 0.1;

                b.append("&p1=").append(URLEncoder.encode(p.getName().getValue(), Const.CONST_ENCODING)).append("&v1=").append(rx);
                // System.out.println( b.toString());
                ClientHelper.client().recordBatch(b.toString());
                System.out.println(rx);
                Thread.sleep(1000);
            }

            Thread.sleep(2000);
            List<Value> v = ClientHelper.client().getSeries(p.getName(), 10);
            double retVal = 0.0;
            for (Value x : v) {
                retVal += x.getNumberValue();
                System.out.println(x.getNumberValue());
            }

            DecimalFormat twoDForm = new DecimalFormat("#.##");
            retVal = Double.valueOf(twoDForm.format(retVal));
            ClientHelper.client().deletePoint(p.getName());

            assertEquals(30.0, retVal, 0.0);

        } catch (IOException e) {

        } catch (InterruptedException e) {

        }
    }

    @Test
    public void testCompressionSeperateAlternatingValuesPostsNoDate() throws Exception {

        Point p = new PointModel();

        p.setName(CommonFactoryLocator.getInstance().createPointName("test" + UUID.randomUUID().toString()));
        p.setCompression(0.1);
        Point result = ClientHelper.client().addPoint(p, cat);
        assertNotNull(result);

        double rx = 0.0;

        try {


            for (int i = 0; i < 40; i++) {
                StringBuilder b = new StringBuilder();
                if (rx == 0.0) {
                    rx = 1.0;
                } else {
                    rx = 0.0;
                }

                b.append("&p2=").append(URLEncoder.encode(p.getName().getValue(), Const.CONST_ENCODING)).append("&v2=").append(rx);
                // System.out.println( b.toString());
                System.out.println(ClientHelper.client().recordBatch(b.toString()));
                Thread.sleep(100);

            }
            Thread.sleep(3000);
            List<Value> v = ClientHelper.client().getSeries(p.getName(), 40);
            double retVal = 0.0;
            for (Value x : v) {
                retVal += x.getNumberValue();

            }

            DecimalFormat twoDForm = new DecimalFormat("#.##");
            retVal = Double.valueOf(twoDForm.format(retVal));
            ClientHelper.client().deletePoint(p.getName());
            Assert.assertEquals(20.0, retVal);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void testCompression() throws NimbitsException {
        Point p = new PointModel();

        p.setName(CommonFactoryLocator.getInstance().createPointName("test" + UUID.randomUUID().toString()));
        p.setCompression(2.0);
        ClientHelper.client().addPoint(p, cat);

        double x = testCompression(p);

        Assert.assertEquals(255.0, x);
        ClientHelper.client().deletePoint(p.getName());
    }

    @Test
    public void testChangeCompression() throws NimbitsException {
        Point p = new PointModel();

        p.setName(CommonFactoryLocator.getInstance().createPointName("test" + UUID.randomUUID().toString()));
        p.setCompression(0.0);
        ClientHelper.client().addPoint(p, cat);


        Point px = ClientHelper.client().getPoint(p.getName());

        Assert.assertNotNull(px);
        px.setCompression(2.0);
        ClientHelper.client().updatePoint(px);


        Point px2 = ClientHelper.client().getPoint(p.getName());
        Assert.assertEquals(2.0, px2.getCompression());
        Assert.assertEquals(px.getId(), px2.getId());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        //double x2 = testCompression(p);
        ClientHelper.client().deletePoint(p.getName());
        // Assert.assertEquals(255.0, x2);


        // gClient.DeletePoint(p.getValue());
    }

    @Test
    public void TestZeroCompressionWithBatch() throws NimbitsException {
        Point p = new PointModel();

        p.setName(CommonFactoryLocator.getInstance().createPointName("test" + UUID.randomUUID().toString()));
        p.setCompression(0.0);
        ClientHelper.client().addPoint(p, cat);
        System.out.println("Starting batch compression integration test compression = " + p.getCompression());

        StringBuilder b = new StringBuilder();

        try {

            for (int i = 0; i < 40; i++) {
                b.append("&p").append(i).append("=").append(URLEncoder.encode(p.getName().getValue(), Const.CONST_ENCODING)).append("&v").append(i).append("=").append(i).append("&t").append(i).append("=").append(new Date().getTime());

                Thread.sleep(100);

            }
            System.out.println(b.toString());
            System.out.println(ClientHelper.client().recordBatch(b.toString()));

            double retVal = 0.0;

            Thread.sleep(1000);
            List<Value> v = ClientHelper.client().getSeries(p.getName(), 10);
            for (Value x : v) {
                retVal += x.getNumberValue();
                System.out.println(x.getNumberValue() + "  " + x.getTimestamp());
            }
            Assert.assertEquals(345.0, retVal);
            ClientHelper.client().deletePoint(p.getName());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        //				String ba = "&email=" + email + "&secret="  + secret +
        //				"&p1=" + URLEncoder.encode("b1", Const.CONST_ENCODING)+ "&v1=" + r.nextDouble() +
        //				"&p2=" + URLEncoder.encode("b2", Const.CONST_ENCODING)+ "&v2=" + r.nextDouble() +
        //				"&p3=" + URLEncoder.encode("b3", Const.CONST_ENCODING)+ "&v3=" + r.nextDouble() +
        //				"&p4=" + URLEncoder.encode("b4", Const.CONST_ENCODING)+ "&v4=" + r.nextDouble() +
        //				"&p5=" + URLEncoder.encode("b5", Const.CONST_ENCODING)+ "&v5=" + r.nextDouble() +
        //				"&p6=" + URLEncoder.encode("b6", Const.CONST_ENCODING)+ "&v6=" + r.nextDouble() +
        //				"&p7=" + URLEncoder.encode("b7", Const.CONST_ENCODING)+ "&v7=" + r.nextDouble();
        //
        //
    }

    @Test
    public void TestCompressionWithBatch() throws NimbitsException {
        Point p = new PointModel();

        p.setName(CommonFactoryLocator.getInstance().createPointName("test" + UUID.randomUUID().toString()));
        p.setCompression(2.0);
        ClientHelper.client().addPoint(p, cat);
        StringBuilder b = new StringBuilder();

        try {

            for (int i = 0; i < 40; i++) {
                b.append("&p").append(i).append("=").append(URLEncoder.encode(p.getName().getValue(), Const.CONST_ENCODING)).append("&v").append(i).append("=").append(i).append("&t").append(i).append("=").append(new Date().getTime());

                Thread.sleep(1000);

            }
            //  System.out.println(b.toString());
            System.out.println(ClientHelper.client().recordBatch(b.toString()));
            double retVal = 0.0;

            Thread.sleep(1000);
            List<Value> v = ClientHelper.client().getSeries(p.getName(), 10);
            for (Value x : v) {
                retVal += x.getNumberValue();
                System.out.println(x.getNumberValue() + "  " + x.getTimestamp());

            }
            Assert.assertEquals(255.0, retVal);
            ClientHelper.client().deletePoint(p.getName());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

//    @Test
//    public void loadRandomDataIntoB1UsingKey() throws InterruptedException, IOException {
//       NimbitsUser nx = new NimbitsUser(email, key);
//
//       NimbitsClient c =  NimbitsClientFactory.getInstance(nx, hostURL);
//       Random r = new Random();
//
//
//        for (int i = 0; i < 10; i++)
//        {
//         Value v = new Value();
//            v.setValue(r.nextDouble()*10);
//            v.setTimestamp(new Date());
//            System.out.println(c.recordValue("b1",v));
//
//
//                Thread.sleep(1000);
//
//
//        }
//    }

    @Test
    public void TestCompressionWithBatchWithMissingPoints() throws NimbitsException {
        Point p = new PointModel();

        p.setName(CommonFactoryLocator.getInstance().createPointName("test" + UUID.randomUUID().toString()));

        p.setCompression(2.0);
        ClientHelper.client().addPoint(p, cat);
        StringBuilder b = new StringBuilder();

        try {

            for (int i = 0; i < 40; i++) {
                b.append("&p");
                b.append(i);
                b.append("=");
                b.append(URLEncoder.encode(p.getName().getValue(), Const.CONST_ENCODING));
                b.append("&v");
                b.append(i);
                b.append("=");
                b.append(i);


            }
            //   b.append("&p41=32423fsdfsdf&v41=324fsdsd");
            System.out.println(b.toString());
            System.out.println(ClientHelper.client().recordBatch(b.toString()));
            double retVal = 0.0;

            Thread.sleep(1000);
            List<Value> v = ClientHelper.client().getSeries(p.getName(), 10);
            for (Value x : v) {
                retVal += x.getNumberValue();
                System.out.println(x.getNumberValue() + "  " + x.getTimestamp());
            }
            Assert.assertEquals(216.0, retVal);
            ClientHelper.client().deletePoint(p.getName());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }


    private double testCompression(Point p) throws NimbitsException {
        System.out.println("Starting compression integration test compression = " + p.getCompression() + "  " + p.getId());

        double retVal = 0.0;

        try {
            for (int i = 0; i < 40; i++) {
                Thread.sleep(10);
                Value v = ClientHelper.client().recordValue(p.getName(), i, new Date());

            }
            // Thread.sleep(2000);
            List<Value> v = ClientHelper.client().getSeries(p.getName(), 10);
            for (Value x : v) {
                retVal += x.getNumberValue();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.out.println("End compression integration test " + retVal);

        return retVal;


    }
}