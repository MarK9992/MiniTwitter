import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.rmi.RemoteException;
import java.util.*;

/**
 * @author Marc Karassev
 *
 * Implements MiniTwitter's remote interface.
 * Keeps track of hashtags list.
 * Creates the default hashtag and the dedicated to new hashtags one.
 */
public class MiniTwitterImpl implements MiniTwitter, MessageListener {

    public static final String DEFAULT_TOPIC = "#HelloWorld", NEW_TOPICS_TOPIC = "#NewTopics", ACTIVE_MQ_USER = "user",
            ACTIVE_MQ_PASSWORD = "password", ACTIVE_MQ_HOST = "tcp://localhost:61616";

    private Set<String> topics;

    /**
     * Default constructor, constructs a new MiniTwitterServer with a default hash tag and the one dedicated to new
     * ones.
     */
    public MiniTwitterImpl() {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(ACTIVE_MQ_USER, ACTIVE_MQ_PASSWORD,
                    ACTIVE_MQ_HOST);
            Connection connect = factory.createConnection(ACTIVE_MQ_USER, ACTIVE_MQ_PASSWORD);
            Session session = connect.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer newTopicsConsumer = session.createConsumer(session.createTopic(NEW_TOPICS_TOPIC));

            session.createTopic(DEFAULT_TOPIC);
            topics = new HashSet<String>();
            topics.add(DEFAULT_TOPIC);
            topics.add(NEW_TOPICS_TOPIC);
            newTopicsConsumer.setMessageListener(this);
            connect.start();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lists all hash tags.
     *
     * @return a set of string representing hash tags
     * @throws RemoteException
     */
    @Override
    public Set<String> listTopics() throws RemoteException {
        // TODO sécuriser NEW_TOPICS_TOPIC
        return topics;
    }

    /**
     * Passes a message to the listener and gets the new topic from it.
     *
     * @param message the message passed to the listener containing the new topic information
     */
    @Override
    public void onMessage(Message message) {
        MapMessage mapMessage = (MapMessage) message;

        try {
            topics.add(mapMessage.getString(MiniTwitterClient.NEW_TOPIC_KEY));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
