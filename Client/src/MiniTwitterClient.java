import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.rmi.RemoteException;
import java.util.*;

/**
 * @author Marc Karassev, Quentin Cornevin
 *
 * Client application to MiniTwitter.
 * Keeps track of the server and the user's username.
 * Has a JMS session and a map of hash tags the user follows as keys and related JMS message producers as values.
 * Also keeps received messages in a timeline and the number of unread messages.
 */
public class MiniTwitterClient implements MessageListener {

    // keys in map messages
    public static final String TOPIC_KEY = "topic", AUTHOR_KEY = "author", CONTENTS_KEY = "contents", DATE_KEY = "DATE",
            NEW_TOPIC_KEY = "new topic";

    private MiniTwitter miniTwitter;
    private Session session;
    private Map<String, MessageProducer> topicMap;
    private String userName;
    private LinkedList<MapMessage> timeLine;
    private int unreadMessages;

    /**
     * Creates a new client that subscribes to all the given hash tags.
     *
     * @param miniTwitter the server
     * @param topics the list of hash tags to subscribe to
     * @param userName the user's name
     * @throws JMSException
     */
    public MiniTwitterClient(MiniTwitter miniTwitter, Set<String> topics, String userName) throws JMSException {
        this.timeLine = new LinkedList<MapMessage>();
        this.unreadMessages = 0;
        ConnectionFactory factory = new ActiveMQConnectionFactory(MiniTwitterImpl.ACTIVE_MQ_USER,
                MiniTwitterImpl.ACTIVE_MQ_PASSWORD, MiniTwitterImpl.ACTIVE_MQ_HOST);
        Connection connect = factory.createConnection(MiniTwitterImpl.ACTIVE_MQ_USER,
                MiniTwitterImpl.ACTIVE_MQ_PASSWORD);
        Topic topic;
        MessageConsumer consumer;

        connect.setClientID(userName);
        session = connect.createSession(false, Session.AUTO_ACKNOWLEDGE);
        topicMap = new HashMap<String, MessageProducer>();
        this.miniTwitter = miniTwitter;
        this.userName = userName;
        for (String topicName: topics) {
            topic = session.createTopic(topicName);
            consumer = session.createDurableSubscriber(topic, userName + topicName);
            topicMap.put(topicName, session.createProducer(topic));
            consumer.setMessageListener(this);
        }
        connect.start();
    }

    /**
     * Sends the given message to the specified topic. Creates a new topic if the given one does not exist.
     *
     * @param topicName the topic to send the message to
     * @param contents the contents of the message to send
     * @throws JMSException
     */
    public void sendMessage(String topicName, String contents) throws JMSException {
        MapMessage message = session.createMapMessage();
        MessageProducer producer = topicMap.get(topicName);

        message.setString(TOPIC_KEY, topicName);
        message.setString(AUTHOR_KEY, userName);
        message.setString(DATE_KEY, Calendar.getInstance().getTime().toString());
        message.setString(CONTENTS_KEY, contents);
        if (producer == null) {
            Topic topic = session.createTopic(topicName);
            MessageConsumer consumer = session.createDurableSubscriber(topic, userName + topicName);

            producer = session.createProducer(topic);
            topicMap.put(topicName, producer);
            consumer.setMessageListener(this);
            try {
                miniTwitter.addSubscription(userName, topicName);
                if (!miniTwitter.listTopics().contains(topicName)) {
                    MapMessage newTopicMessage = session.createMapMessage();

                    // TODO duplication du code de création de messages
                    newTopicMessage.setString(TOPIC_KEY, MiniTwitterImpl.NEW_TOPICS_TOPIC);
                    newTopicMessage.setString(AUTHOR_KEY, userName);
                    newTopicMessage.setString(DATE_KEY, Calendar.getInstance().getTime().toString());
                    newTopicMessage.setString(CONTENTS_KEY, "new topic: " + topicName);
                    newTopicMessage.setString(NEW_TOPIC_KEY, topicName);
                    topicMap.get(MiniTwitterImpl.NEW_TOPICS_TOPIC).send(newTopicMessage);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        producer.send(message);
    }

    /**
     * Subscribes to the given topic.
     *
     * @param topicName the name of the topic to subscribe to
     * @return true on success, false otherwise
     * @throws JMSException
     */
    public boolean subscribeToTopic(String topicName) throws JMSException {
        for (String subscribedTopic: topicMap.keySet()) {
            if (subscribedTopic.equals(topicName)) {
                return true;
            }
        }
        try {
            for (String listedTopic: miniTwitter.listTopics()) {
                if (listedTopic.equals(topicName)) {
                    Topic topic = session.createTopic(topicName);

                    session.createDurableSubscriber(topic, userName + topicName).setMessageListener(this);
                    try {
                        miniTwitter.addSubscription(userName, topicName);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    topicMap.put(topicName, session.createProducer(topic));
                    return true;
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Returns the hash tags set the user follows except the one dedicated to new ones.
     *
     * @return a set of strings containing hash tags
     */
    public Set<String> getTopics() {
        Set<String> topics = new HashSet<String>(topicMap.keySet());

        topics.remove(MiniTwitterImpl.NEW_TOPICS_TOPIC);
        return topics;
    }

    /**
     * Passes a message to the listener and adds it to the time line. If there is more than 300 tweet in the time line,
     * the older one is deleted.
     *
     * @param message the message passed to the listener
     */
    @Override
    public void onMessage(Message message) {
        MapMessage mapMessage = (MapMessage) message;
        this.unreadMessages++;
        System.out.println("The number of unread tweets is: " + unreadMessages);
        if(timeLine.size() < 300) {
            timeLine.add(mapMessage);
        } else {
            // TODO une structure de donnée FIFO de taille fixe effaçant automatiquement les plus anciennens valeurs si nécessaire serait plus pertinante
            timeLine.removeFirst();
            timeLine.add(mapMessage);
        }
    }

    /**
     * This method prints the time line with all the last 300 tweets.
     */
    public void readTimeLine() throws JMSException {
        // TODO move output to Client class
        System.out.println("\n -----------------");
        if(unreadMessages > 0) {
            for (int i = 0; i < timeLine.size(); i++) {
                MapMessage mapMessage = timeLine.get(i);
                String tweet = "tweet received, topic: " + mapMessage.getString(TOPIC_KEY) + ", author: "
                        + mapMessage.getString(AUTHOR_KEY) + ", date: " + mapMessage.getString(DATE_KEY)
                        + ", contents: " + mapMessage.getString(CONTENTS_KEY);

                System.out.println(i + 1 + " - " + tweet);
            }
            System.out.println("-----------------");
            unreadMessages = 0;
        } else {
            System.out.println("Sorry there is no unread tweets!");
        }
    }

    /**
     * Returns the time line containing the last 300 tweets received.
     *
     * @return the value of the timeLine attribute
     */
    public LinkedList<MapMessage> getTimeLine() {
        return timeLine;
    }
}
