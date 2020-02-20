package events;

import publishers.AbstractPublisher;


/**
 * @author kkontog, ktsiouni, mgrigori 
 *
 */
public class EventFactory {

	
	/**
	 * This is an implementation of the Factory Method design pattern
	 * Creates an instance of any of the subclasses derived from the top level class AbstractEvent
	 * 
	 * @param eventType a member of the {@link EventType} enumeration
	 * @param eventPublisherId a number generated by invoking the  {@link AbstractPublisher#hashCode()} on the {@link AbstractPublisher} instance issuing the event
	 * @param payload an object of type {@link EventMessage}
	 * @return
	 */
	public static AbstractEvent createEvent(EventType eventType, int eventPublisherId, EventMessage payload) {
		
		long eventID = EventIDMaker.getNewEventID(); 						//get the next eventID number
		
		switch(eventType) {									//determine the eventType to be made
													//call the appropriate event type constructor
			case TypeA:									//return the created event
				return new EventTypeA(eventID, eventPublisherId, payload);
			
			case TypeB:
				return new EventTypeB(eventID, eventPublisherId, payload);
			
			case TypeC:
				return new EventTypeC(eventID, eventPublisherId, payload);
			
			default :
				return null;
		}
	}
	
}
