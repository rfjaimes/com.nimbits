package integration;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.nimbits.client.enums.Action;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.common.CommonFactoryLocator;
import com.nimbits.client.model.entity.EntityName;
import com.nimbits.client.model.value.Value;
import com.nimbits.client.model.value.ValueModel;
import com.nimbits.client.model.value.ValueModelFactory;
import com.nimbits.server.gson.GsonFactory;
import org.jivesoftware.smack.XMPPException;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 11/22/11
 * Time: 10:44 AM
 */
public class TestXMPP {


    @Test
    public void testObjectTransfer() throws NimbitsException, XMPPException, IOException, InterruptedException {
//        NimbitsClient client = ClientHelper.client();
//        XMPPClient x = XMPPClientFactory.getInstance(client, "nimbits-qa");
//        assertTrue(x.connect());
//        Value v = ValueModelFactory.createValueModel(1.23);
//        EntityName pointName = CommonFactoryLocator.getInstance().createName("foo");
//        x.sendValue(pointName, v);
//
//        final boolean[] done = {false};
//
//        x.addMessageReceivedListeners(new XMPPClientImpl.MessageReceivedListener() {
//
//            @Override
//            public void onMessageReceived(String message) {
//                System.out.println(message);
//                Gson gson = GsonFactory.getInstance();
//                Point point = gson.fromJson(message, PointModel.class);
//                assertTrue(message != null);
//                assertEquals(point.getValue().getValue(), 1.23, 0.0);
//                done[0] = true;
//            }
//        });
//        int waiting = 0;
//
//        while (!done[0]) {
//            Thread.sleep(100);
//            waiting++;
//            if (waiting > 100) { //10 seconds
//                done[0] = true;
//                fail();
//            }
//
//        }
    }

    @Test
    public void testPojoTransfer() throws NimbitsException, XMPPException, IOException, InterruptedException {
//        NimbitsClient client = ClientHelper.client();
//        XMPPClient x = XMPPClientFactory.getInstance(client, "nimbits-qa");
//        final Gson gson = GsonFactory.getInstance();
//        assertTrue(x.connect());
//        Robot robot = new Robot();
//        robot.setEmotion(Robot.Emotion.happy);
//        String data = gson.toJson(robot);
//
//
//        Value v = ValueModelFactory.createValueModel(0.0,
//                0.0,
//                1.34,
//                new Date(),
//                0,
//                "",
//                data);
//
//        EntityName pointName = CommonFactoryLocator.getInstance().createName("foo");
//        x.sendValue(pointName, v);
//
//        final boolean[] done = {false};
//
//        x.addMessageReceivedListeners(new XMPPClientImpl.MessageReceivedListener() {
//
//            @Override
//            public void onMessageReceived(String message) {
//                System.out.println(message);
//
//                Point point = gson.fromJson(message, PointModel.class);
//                assertTrue(message != null);
//                Value v = point.getValue();
//                try {
//                    assertEquals(v.getValue(), 1.34, 0.0);
//                } catch (Exception e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }
//                System.out.println(point.getValue().getData());
//                Robot robot = gson.fromJson(v.getData(), Robot.class);
//                assertEquals(robot.getEmotion(), Robot.Emotion.happy);
//
//                done[0] = true;
//            }
//        });
//        int waiting = 0;
//
//        while (!done[0]) {
//            Thread.sleep(100);
//            waiting++;
//            if (waiting > 100) { //10 seconds
//                done[0] = true;
//                fail();
//            }
//
//        }
    }


    @Test
    public void testGson() {
        Value v = ValueModelFactory.createValueModel(1.23);
        EntityName pointName = CommonFactoryLocator.getInstance().createName("foo");
        Collection collection = new ArrayList();
        collection.add(Action.record);
        collection.add("foo");
        collection.add(v);


        String json = GsonFactory.getInstance().toJson(collection);
        System.out.println(json);
        Gson gson = GsonFactory.getInstance();


        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(json).getAsJsonArray();
        String px = gson.fromJson(array.get(1), String.class);
        Value vx = gson.fromJson(array.get(2), ValueModel.class);
        Action action = gson.fromJson(array.get(0), Action.class);
        assertEquals(action, Action.record);
        assertEquals(px, "foo");
        assertEquals(vx.getDoubleValue(), 1.23, 0.0);


    }

}
