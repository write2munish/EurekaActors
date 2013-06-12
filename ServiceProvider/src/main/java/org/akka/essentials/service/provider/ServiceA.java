package org.akka.essentials.service.provider;

import akka.actor.UntypedActor;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.InstanceInfo.InstanceStatus;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryManager;

public class ServiceA extends UntypedActor {

	private static final DynamicPropertyFactory configInstance = com.netflix.config.DynamicPropertyFactory
			.getInstance();

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
			getSender().tell("Message from Service A ->" + message, getSelf());
		}
	}

	private void registerWithEureka() {
		// Register with Eureka
		DiscoveryManager.getInstance().initComponent(
				new MyDataCenterInstanceConfig(),
				new DefaultEurekaClientConfig());
		//set the service instance UP
		ApplicationInfoManager.getInstance().setInstanceStatus(
				InstanceStatus.UP);
		
		String vipAddress = configInstance.getStringProperty(
				"eureka.vipAddress", "serviceA.akka.essentials.org").get();
		InstanceInfo nextServerInfo = null;
		
		while (nextServerInfo == null) {
			try {
				nextServerInfo = DiscoveryManager.getInstance()
						.getDiscoveryClient()
						.getNextServerFromEureka(vipAddress, false);
			} catch (Throwable e) {
				System.out
						.println("Waiting for service to register with eureka..");

				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}
		System.out.println("Service started and ready to process requests..");

	}

	private void unRegisterWithEureka() {
		// Un register from eureka.
		DiscoveryManager.getInstance().shutdownComponent();
		System.out.println("Shutting down server.");
	}
}

