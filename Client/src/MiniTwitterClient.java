import javax.jms.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * @author Marc Karassev
 *
 * Client application to MiniTwitter.
 */
public class MiniTwitterClient implements MessageListener {

    private MiniTwitter miniTwitter;
    private Session session;
    private Map<String, MessageProducer> topicMap;
    private String userName;

    /**
     * Default constructor, creates a new client which only subscribes to the default hash tag.
     */
    public MiniTwitterClient() throws JMSException{
        ConnectionFactory factory = new ActiveMQConnectionFactory(MiniTwitterImpl.ACTIVE_MQ_USER,
                MiniTwitterImpl.ACTIVE_MQ_PASSWORD, MiniTwitterImpl.ACTIVE_MQ_HOST);
        Connection connect = factory.createConnection(MiniTwitterImpl.ACTIVE_MQ_USER,
                MiniTwitterImpl.ACTIVE_MQ_PASSWORD);

        session = connect.createSession(false, Session.AUTO_ACKNOWLEDGE);
        topicMap = new HashMap<String, MessageProducer>();
        connectToServer();
        connect.start();
    }

    // connects to the MiniTwitterServer and initializes topic list
    private void connectToServer() {
        try {
            // TODO ask registry host
            Registry registry = LocateRegistry.getRegistry("localhost", Server.REGISTRY_PORT);
            Topic topic;
            MessageConsumer consumer;

            miniTwitter = (MiniTwitter) registry.lookup(Server.STUB_NAME);
            userName = "me";
            for (String topicName: miniTwitter.listTopics()) {
                topic = session.createTopic(topicName);
                consumer = session.createConsumer(topic);
                topicMap.put(topicName, session.createProducer(topic));
                consumer.setMessageListener(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to default topic.
     *
     * @param contents the contents of the message to send
     */
    public void sendMessage(String contents) throws JMSException {
        // TODO add topic string parameter
        MapMessage message = session.createMapMessage();

        message.setString("author", userName);
        message.setString("contents", contents);
        topicMap.get(MiniTwitterImpl.DEFAULT_TOPIC).send(message);
    }

    /**
     * Passes a message to the listener and prints it.
     *
     * @param message the message passed to the listener
     */
    @Override
    public void onMessage(Message message) {
        MapMessage mapMessage = (MapMessage) message;

        try {
            System.out.println("message received, author: " + mapMessage.getString("author") + ", contents: "
                    + mapMessage.getString("contents"));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
