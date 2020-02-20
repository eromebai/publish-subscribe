package states.subscriber;

import events.AbstractEvent;

abstract class AbstractState implements IState {


    @Override
    public void handleEvent(AbstractEvent event, String channelName) {
        ;
    }
}
