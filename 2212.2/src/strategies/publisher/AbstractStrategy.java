package strategies.publisher;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import events.AbstractEvent;
import events.EventType;
import pubSubServer.AbstractChannel;
import pubSubServer.ChannelDiscovery;
import pubSubServer.ChannelEventDispatcher;

abstract public class AbstractStrategy implements IStrategy {
    @Override
    public void doPublish(int publisherId) {
        System.out.println("Publisher " + publisherId + " publishes event");
    }

    @Override
    public void doPublish(AbstractEvent event, int publisherId) {
    	String[] array = new String[ChannelDiscovery.getInstance().listChannels().size()-1];
    	ChannelDiscovery.getInstance().listChannels();
    	for(int i = 0; i < ChannelDiscovery.getInstance().listChannels().size()-1; i++){
    		array[i] = ChannelDiscovery.getInstance().listChannels().get(i).getChannelTopic();
    	}
    	List<String> finalArray = Arrays.asList(array);
    	
    	ChannelEventDispatcher.getInstance().postEvent(event, finalArray);
        System.out.println("Publisher " + publisherId + " publishes event " + EventType.values()[(int)event.getEventType()]);
    }
}
