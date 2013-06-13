package org.akka.essentials.service.consumer;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.InstanceInfo.InstanceStatus;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryManager;

public class ServiceConsumerA extends UntypedActor {

	private static final DynamicPropertyFactory configInstance = com.netflix.config.DynamicPropertyFactory
			.getInstance();

	ActorRef remoteServiceActor;

	@Override
	public void preStart() {
		registerWithEureka();
	}

	@Override
	public void postStop() {
		unRegisterWithEureka();
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof String) {
			System.out.println("Message receieved ->" + message);
		}

	}

	private void registerWithEureka() {
		// Register with Eureka
		DiscoveryManager.getInstance().initComponent(
				new MyDataCenterInstanceConfig(),
				new DefaultEurekaClientConfig());
		ApplicationInfoManager.getInstance().setInstanceStatus(
				InstanceStatus.UP);
		String vipAddress = configInstance.getStringProperty(
				"eureka.vipAddress", "/user/serviceA").get();
		InstanceInfo nextServerInfo = DiscoveryManager.getInstance()
				.getDiscoveryClient()
				.getNextServerFromEureka(vipAddress, false);

		String connectionString = "akka://serviceProvider@" +

		nextServerInfo.getIPAddr() + ":" + nextServerInfo.getPort()
				+ nextServerInfo.getVIPAddress();

		System.out.println("Remote Actor URL " + connectionString);

		remoteServiceActor = getContext().actorFor(connectionString);

		if (remoteServiceActor != null) {
			System.out.println("About to send message to remote actor");
			System.out.println("Remote actor path ->"+remoteServiceActor.path().toString());
			remoteServiceActor.tell("Sending message from remote client", getSelf());
			
		}else{
			System.out.println("remoteservice actor null");
		}

	}

	private void unRegisterWithEureka() {
		// Un register from eureka.
		DiscoveryManager.getInstance().shutdownComponent();
	}
}

