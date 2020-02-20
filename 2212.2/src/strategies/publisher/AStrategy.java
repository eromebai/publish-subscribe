package strategies.publisher;

import java.util.Arrays;
import java.util.List;

import events.AbstractEvent;
import events.EventType;
import pubSubServer.ChannelDiscovery;
import pubSubServer.ChannelEventDispatcher;

public class AStrategy extends AbstractStrategy {
	
	public void doPublish(AbstractEvent event, int publisherId) {
    	String[] array = new String[ChannelDiscovery.getInstance().listChannels().size()-1];
    	String[] arrayA = new String[2];
    	ChannelDiscovery.getInstance().listChannels();
    	for(int i = 0; i < ChannelDiscovery.getInstance().listChannels().size()-1; i++){
    		array[i] = ChannelDiscovery.getInstance().listChannels().get(i).getChannelTopic();
    	}
    	for(int j = 0; j < 2; j++){
    		arrayA[j] = array[j];
    	}
    	List<String> finalArray = Arrays.asList(arrayA);
    	
    	ChannelEventDispatcher.getInstance().postEvent(event, finalArray);
        System.out.println("Publisher " + publisherId + " publishes event " + EventType.values()[(int)event.getEventType()]);
    }
}
