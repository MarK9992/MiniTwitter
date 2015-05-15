import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.rmi.RemoteException;
import java.util.*;

/**
 * @author Marc Karassev
 *
 * Implements MiniTwitter's remote interface.
 * Keeps track of hashtags list.
 * Creates the default hashtag.
 */
public class MiniTwitterImpl implements MiniTwitter {

    public static final String DEFAULT_TOPIC = "#HelloWorld", ACTIVE_MQ_USER = "user", ACTIVE_MQ_PASSWORD = "password",
        ACTIVE_MQ_HOST = "tcp://localhost:61616";

    private Set<String> topics;

    /**
     * Default constructor, constructs a new MiniTwitterServer with a default hash tag.
     */
    public MiniTwitterImpl() {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(ACTIVE_MQ_USER, ACTIVE_MQ_PASSWORD,
                    ACTIVE_MQ_HOST);
            Connection connect = factory.createConnection(ACTIVE_MQ_USER, ACTIVE_MQ_PASSWORD);
            Session session = connect.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic defaultTopic = session.createTopic(DEFAULT_TOPIC);

            topics = new HashSet<String>();
            topics.add(defaultTopic.getTopicName());
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
        return topics;
    }
}
