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
 * Keeps track of the server and the user's username.
 * Has a JMS session and a map of hash tags the user follows as keys and related JMS message producers as values.
 */
public class MiniTwitterClient implements MessageListener {

    private MiniTwitter miniTwitter;
    private Session session;
    private Map<String, MessageProducer> topicMap;
    private String userName;

    /**
     * Default constructor, creates a new client that subscribes to all the server hash tags.
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
     * Sends the given message to the specified topic.
     *
     * @param topic the topic to send the message to
     * @param contents the contents of the message to send
     * @return true on success, false otherwise
     */
    public boolean sendMessage(String topic, String contents) throws JMSException {
        MapMessage message = session.createMapMessage();
        MessageProducer producer = topicMap.get(topic);

        if (producer != null) {
            message.setString("author", userName);
            message.setString("contents", contents);
            producer.send(message);
            return true;
        }
        else {
            // TODO allow topic creation
            return false;
        }
    }

    // TODO get topics

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
