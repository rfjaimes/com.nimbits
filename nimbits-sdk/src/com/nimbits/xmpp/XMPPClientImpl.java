package com.nimbits.xmpp;


import com.nimbits.client.NimbitsClient;
import com.nimbits.client.enums.Action;
import com.nimbits.client.exception.NimbitsException;
import com.nimbits.client.model.Const;
import com.nimbits.client.model.point.Point;
import com.nimbits.client.model.point.PointModel;
import com.nimbits.client.model.point.PointModelFactory;
import com.nimbits.client.model.point.PointName;
import com.nimbits.client.model.value.Value;
import com.nimbits.server.gson.GsonFactory;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromContainsFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;


/**
 * Created by Benjamin Sautner
 * User: BSautner
 * Date: 11/22/11
 * Time: 11:09 AM
 */
public class XMPPClientImpl extends Observable implements XMPPClient {
    private NimbitsClient client;
    private String appId;
    private Connection connection;

    private final List<MessageReceivedListener> messageReceivedListeners = new ArrayList<MessageReceivedListener>();

    // Category Click Handlers
    public interface MessageReceivedListener {
        void onMessageReceived(Point point);
    }

    public void addMessageReceivedListeners(final MessageReceivedListener listener) {
        messageReceivedListeners.add(listener);
    }

    void notifyMessageReceivedListener(Point point) {
        for (MessageReceivedListener messageReceivedListener : messageReceivedListeners) {
            messageReceivedListener.onMessageReceived(point);
        }
    }

    public void disconnect() {
        connection.disconnect();
    }

    public XMPPClientImpl(NimbitsClient client, String appId) {
        this.client = client;
        this.appId = appId;
    }

    public void sendMessage(final String outgoingMessage) {


        //  Connection.DEBUG_ENABLED = true;
        ChatManager chatmanager = connection.getChatManager();

        Chat newChat = chatmanager.createChat(appId + "@" + Const.DEFAULT_APPSPOT, new MessageListener() {
            public void processMessage(Chat chat, Message message) {
//                System.out.println("Received message: " + message);
            }
        });

        try {

            newChat.sendMessage(outgoingMessage);
        } catch (XMPPException e) {
            System.out.println("Error Delivering block");
        }
    }

    @Override
    public boolean connect() throws NimbitsException {
        final boolean connected;

        if (client.getGoogleUser() == null) {
            throw new NimbitsException("You must authenticate with Google Account to use the XMPP Listener");
        } else {
            connection = new XMPPConnection(Const.DEFAULT_XMPP_SERVER);
            try {
                connection.connect();

                connection.login(client.getGoogleUser().getGoogleEmailAddress().getValue()
                        , client.getGoogleUser().getGooglePassword());
                connected = connection.isConnected();

                if (connected) {
                    ChatManager chatmanager = connection.getChatManager();

                    PacketFilter filter = new AndFilter(new PacketTypeFilter(Message.class),
                            new FromContainsFilter(appId + "@" + Const.DEFAULT_APPSPOT));
                    PacketCollector myCollector = connection.createPacketCollector(filter);
                    PacketListener myListener = new PacketListener() {
                        public void processPacket(Packet packet) {
                            String text = getContent(packet.toXML());
                            Point point = GsonFactory.getInstance().fromJson(text, PointModel.class);
                            notifyMessageReceivedListener(point);
                            //  System.out.println(packet.toXML());
                        }
                    };

                    connection.addPacketListener(myListener, filter);

                    chatmanager.addChatListener(new ChatManagerListener() {
                        @Override
                        public void chatCreated(Chat chat, boolean createdLocally) {

                        }
                    });
                } else {
                    throw new NimbitsException("Could not connect to the XMPP Listener");
                }
            } catch (XMPPException e) {
                throw new NimbitsException(e.getMessage());
            }

        }
        return connected;

    }

    String getContent(String responseXML) {
        DocumentBuilder db = null;
        String retVal = null;
        try {
            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(responseXML));
            Document doc = db.parse(is);
            NodeList nodes = doc.getElementsByTagName("body");
            Node node = nodes.item(0);
            retVal = node.getTextContent();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SAXException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return retVal;
    }

    public void sendValue(PointName pointName, Value value) {
        Collection collection = new ArrayList();
        Point point = PointModelFactory.createPointModel(0, 0);
        point.setName(pointName);
        point.setValue(value);
        collection.add(Action.record);
        collection.add(point);
        String gson = GsonFactory.getInstance().toJson(collection);
        sendMessage(gson);
    }

}
