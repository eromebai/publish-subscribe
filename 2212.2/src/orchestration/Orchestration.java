package orchestration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import events.EventFactory;
import events.EventMessage;
import events.EventType;
import pubSubServer.AbstractChannel;
import pubSubServer.ChannelDiscovery;
import pubSubServer.SubscriptionManager;
import publishers.AbstractPublisher;
import publishers.PublisherFactory;
import publishers.PublisherType;
import states.subscriber.StateName;
import strategies.publisher.StrategyName;
import subscribers.AbstractSubscriber;
import subscribers.SubscriberFactory;
import subscribers.SubscriberType;

public class Orchestration {

	public static void main(String[] args) {

		List<AbstractPublisher> listOfPublishers = new ArrayList<>();
		List<AbstractSubscriber> listOfSubscribers = new ArrayList<>();
		Orchestration testHarness = new Orchestration();
		try {
			listOfPublishers = testHarness.createPublishers();
			listOfSubscribers = testHarness.createSubscribers();
			
			List<AbstractChannel> channels = ChannelDiscovery.getInstance().listChannels();
			//For demonstration purposes only
			try {
			BufferedReader initialChannels = new BufferedReader(new FileReader(new File("Channels.chl")));
			List<String> channelList = new ArrayList<String>();
			String line = "";
			while((line = initialChannels.readLine()) != null )
				channelList.add(line);
			int subscriberIndex = 0;
			for(AbstractSubscriber subscriber : listOfSubscribers) {
				subscriber.subscribe(channelList.get(subscriberIndex%channelList.size()));
				subscriberIndex++;
			}
			initialChannels.close();
			}catch(IOException ioe) {
				System.out.println("Loading Channels from file failed proceeding with random selection");
				for(AbstractSubscriber subscriber : listOfSubscribers) {
					int index = (int) Math.round((Math.random()*10))/3;
					SubscriptionManager.getInstance().subscribe(channels.get(index).getChannelTopic(), subscriber);
				}
			}
			for(AbstractPublisher publisher : listOfPublishers) {
				publisher.publish();
			}
			
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			System.out.println("Will now terminate");
			return;
		}
		for(AbstractPublisher publisher : listOfPublishers) {
			publisher.publish();
		}
		
		try{
			BufferedReader instructions = new BufferedReader(new FileReader(new File("instructions.txt")));
		
			String nline = "";
			while((nline = instructions.readLine()) != null){
				String[] instructionLine = nline.split("\t");
				switch(instructionLine[0]){
				
				case "PUB":
					if(instructionLine.length == 5){
						for(AbstractPublisher publisher : listOfPublishers){
							if(publisher.hashCode() == Integer.parseInt(instructionLine[1])){
								char[] ch = instructionLine[2].toCharArray();
								int type = (int)ch[4];
								type = type - 64;
								publisher.publish(EventFactory.createEvent(EventType.values()[type], publisher.hashCode(), new EventMessage(instructionLine[3], instructionLine[4])));
								
							}
						}
					}
					else{
						for(AbstractPublisher publisher : listOfPublishers){
							if(publisher.hashCode() == Integer.parseInt(instructionLine[1])){
								publisher.publish();
							}
						}
					}
					continue;
										
				
				case "SUB":
					for(AbstractSubscriber subscriber : listOfSubscribers){
						if(subscriber.hashCode() == Integer.parseInt(instructionLine[1])){
							subscriber.subscribe(instructionLine[2]);
						}
					}
					continue;
					
				case "BLOCK":
					AbstractChannel foundChannel = null;
					boolean found = false;
					for(AbstractSubscriber subscriber : listOfSubscribers){
						if(subscriber.hashCode() == Integer.parseInt(instructionLine[1])){
							 List<AbstractChannel> channels = ChannelDiscovery.getInstance().listChannels();
							 for(AbstractChannel channel : channels){
								 if(channel.getChannelTopic().equals(instructionLine[2])){
									 foundChannel = channel;
									 found = true;
								 }
							 }
							 if(found && !foundChannel.isSubscriberBlocked(subscriber)){
								 foundChannel.blockSubscriber(subscriber);
							 }
						}
					}
					continue;
				
				case "UNBLOCK":
					AbstractChannel foundChannel2 = null;
					boolean found2 = false;
					for(AbstractSubscriber subscriber : listOfSubscribers){
						if(subscriber.hashCode() == Integer.parseInt(instructionLine[1])){
							 List<AbstractChannel> channels = ChannelDiscovery.getInstance().listChannels();
							 for(AbstractChannel channel : channels){
								 if(channel.getChannelTopic().equals(instructionLine[2])){
									 foundChannel2 = channel;
									 found2 = true;
								 }
							 }
							 if(found2 && foundChannel2.isSubscriberBlocked(subscriber)){
								 foundChannel2.unblockSubscriber(subscriber);
							 }
						}
					}
					continue;
				default:
					continue;
				}
				
					
				
			}
			instructions.close();
		}
		catch(IOException ioe){
			System.out.println(ioe.getMessage());
			System.out.println("Will now terminate");
			return;
		}
	}

	
	private List<AbstractPublisher> createPublishers() throws IOException{
		List<AbstractPublisher> listOfPublishers = new ArrayList<>();
		AbstractPublisher newPub;
		BufferedReader StrategyBufferedReader = new BufferedReader(new FileReader(new File("Strategies.str")));
		while(StrategyBufferedReader.ready()) {
			String PublisherConfigLine = StrategyBufferedReader.readLine();
			String[] PublisherConfigArray = PublisherConfigLine.split("\t");
			int[] PublisherConfigIntArray = new int[2];
			for(int i = 0; i < PublisherConfigArray.length; i++)
				PublisherConfigIntArray[i] = Integer.parseInt(PublisherConfigArray[i]);
			newPub = PublisherFactory.createPublisher(
					PublisherType.values()[PublisherConfigIntArray[0]],
					StrategyName.values()[PublisherConfigIntArray[1]]);
			listOfPublishers.add(newPub);
		}
		StrategyBufferedReader.close();
		return listOfPublishers;
	}
	
	private List<AbstractSubscriber> createSubscribers() throws IOException{
		List<AbstractSubscriber> listOfSubscribers = new ArrayList<>();
		AbstractSubscriber newSub;
		BufferedReader StateBufferedReader = new BufferedReader(new FileReader(new File("States.sts")));
		while(StateBufferedReader.ready()) {
			String StateConfigLine = StateBufferedReader.readLine();
			String[] StateConfigArray = StateConfigLine.split("\t");
			int[] StateConfigIntArray = new int[2];
			for(int i = 0; i < StateConfigArray.length; i++)
				StateConfigIntArray[i] = Integer.parseInt(StateConfigArray[i]);
			newSub = SubscriberFactory.createSubscriber(
					SubscriberType.values()[StateConfigIntArray[0]], 
					StateName.values()[StateConfigIntArray[1]]);
			listOfSubscribers.add(newSub);
		}
		StateBufferedReader.close();
		return listOfSubscribers;
	}
	
	
	
}
