package pubSubServer;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import events.AbstractEvent;
import subscribers.AbstractSubscriber;


/**
 * @author kkontog, ktsiouni, mgrigori
 *  
 */
class Channel extends AbstractChannel {

	private Set<AbstractSubscriber> subscribers = new HashSet<AbstractSubscriber>();
	private String channelTopic;
	private Queue<AbstractEvent> events = new ArrayDeque<AbstractEvent>();
	

	public Channel(String channelTopic) {
		this.channelTopic = channelTopic;
		System.out.println("Channel " + channelTopic + " created");
	}
	
	
	/* (non-Javadoc)
	 * @see pubSubServer.AbstractChannel#publishEvent(events.AbstractEvent)
	 */
	protected void publishEvent(AbstractEvent event) {
		events.add(event);
		notifySubscribers(event);

	}

	
	/* (non-Javadoc)
	 * @see pubSubServer.AbstractChannel#subscribe(subscribers.ISubscriber)
	 */
	protected void subscribe(AbstractSubscriber subscriber) {
		subscribers.add(subscriber);
	}

	
	/* (non-Javadoc)
	 * @see pubSubServer.AbstractChannel#unsubscribe(subscribers.ISubscriber)
	 */
	protected void unsubscribe(AbstractSubscriber subscriber) {
		subscribers.remove(subscriber);
	}

	
	
	/**
	 * 
	 * If you are reading this outside this class something is wrong. 
	 * This is a private method which should be called plain 'notify'
	 * unfortunately the name is not available in java. 
	 * 
	 * It iterates over all available subscribed subscribers and notifies them
	 * of the occurrence of the event. 
	 * @param event the event that's to be disseminated to the subscribers
	 */
	private void notifySubscribers(AbstractEvent event) {
		AbstractEvent currentEvent; 
		currentEvent = event;
		ChannelAccessControl control = ChannelAccessControl.getInstance();
		for(AbstractSubscriber subscriber : subscribers) {
			if(!control.checkIfBlocked(subscriber, this.channelTopic)){
				subscriber.alert(currentEvent, this.channelTopic);
			}
		}
	}

	
	/* (non-Javadoc)
	 * @see pubSubServer.AbstractChannel#getChannelTopic()
	 */
	public String getChannelTopic() {
		return channelTopic;
	}
	
	public void blockSubscriber(AbstractSubscriber subscriber){
		ChannelAccessControl control = ChannelAccessControl.getInstance();
		control.blockSubcriber(subscriber, this.channelTopic);
	}
	
	public void unblockSubscriber(AbstractSubscriber subscriber){
		ChannelAccessControl control = ChannelAccessControl.getInstance();
		control.unBlockSubscriber(subscriber, this.channelTopic);
	}
	
	public boolean isSubscriberBlocked(AbstractSubscriber subscriber){
		ChannelAccessControl control = ChannelAccessControl.getInstance();
		return(control.checkIfBlocked(subscriber, this.channelTopic));
	}

}
