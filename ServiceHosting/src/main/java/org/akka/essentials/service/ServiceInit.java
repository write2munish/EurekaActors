package org.akka.essentials.service;

import static akka.pattern.Patterns.gracefulStop;

import java.util.concurrent.TimeUnit;

import org.akka.essentials.service.provider.ServiceA;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.kernel.Bootable;
import akka.pattern.AskTimeoutException;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.InstanceInfo.InstanceStatus;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryManager;
import com.typesafe.config.ConfigFactory;

public class ServiceInit implements Bootable {

	private static final DynamicPropertyFactory configInstance = com.netflix.config.DynamicPropertyFactory
			.getInstance();

	ActorSystem _system = ActorSystem.apply("serviceProvider", ConfigFactory
			.load().getConfig("serviceApp"));

	ActorRef serviceA;

	@Override
	public void shutdown() {
		unRegisterWithEureka();
		if (!serviceA.isTerminated()) {
			// trying graceful stop to the actor
			try {
				Future<Boolean> stopped = gracefulStop(serviceA,
						Duration.create(5, TimeUnit.SECONDS), _system);
				Await.result(stopped, Duration.create(6, TimeUnit.SECONDS));
				// the actor has been stopped
			} catch (AskTimeoutException e) {
				// the actor wasn't stopped within 5 seconds
			} catch (Exception e) {
				// the actor wasn't stopped within 5 seconds
			}
		}
	}

	@Override
	public void startup() {
		 registerWithEureka();
		serviceA = _system.actorOf(new Props(ServiceA.class), "serviceA");
	}

	private void registerWithEureka() {
		// Register with Eureka
		DiscoveryManager.getInstance().initComponent(
				new MyDataCenterInstanceConfig(),
				new DefaultEurekaClientConfig());
		// set the service instance UP
		ApplicationInfoManager.getInstance().setInstanceStatus(
				InstanceStatus.UP);

		String vipAddress = configInstance.getStringProperty(
				"eureka.vipAddress", "/user/serviceA").get();
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

