package pubSubServer;

import java.util.List;

import events.AbstractEvent;
import events.EventType;
import publishers.AbstractPublisher;


/**
 * @author kkontog, ktsiouni, mgrigori
 * MUST IMPLEMENT the Singleton design pattern
 * Class providing an interface for {@link AbstractPublisher} objects to cover their publishing needs 
 */
public class ChannelEventDispatcher {

	private ChannelPoolManager cpManager;
	private static ChannelEventDispatcher instance = null;	//set the initial instances of ChannelAccessControl objects to null
	
	public static ChannelEventDispatcher getInstance() {	//get instance method checks if there has already been an instance created
		if(instance == null) {				//if not then it creates an instance
			instance = new ChannelEventDispatcher();
		}
		return instance;				//return the instance
	}
	
	private ChannelEventDispatcher(){
		cpManager = ChannelPoolManager.getInstance();
	}
	
	
	/**
	 * @param event event to be published
	 * @param listOfChannels list of channel names to which the event must be published to 
	 */
	public void postEvent(AbstractEvent event, List<String> listOfChannels) {
		
		for(String channelName : listOfChannels) {
			AbstractChannel channel = cpManager.findChannel(channelName);
			if(channel == null) {
				channel = ChannelCreator.getInstance().addChannel(channelName);
			}
			System.out.println("Channel " + channelName + " has event " + EventType.values()[(int)event.getEventType()] + " from publisher " + event.getEventPublisher() + "\n");
			channel.publishEvent(event);
		}
	}
	
	
}

