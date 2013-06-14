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

import com.typesafe.config.ConfigFactory;

public class ServiceInit implements Bootable {

	ActorSystem _system = ActorSystem.apply("serviceProvider", ConfigFactory
			.load().getConfig("serviceApp"));

	ActorRef serviceA;
	ActorRef registerEureka;

	@Override
	public void shutdown() {
		if (!registerEureka.isTerminated()) {
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
		registerEureka = _system.actorOf(new Props(RegisterEureka.class),
				"registerEureka");
		serviceA = _system.actorOf(new Props(ServiceA.class), "serviceA");
	}

}

