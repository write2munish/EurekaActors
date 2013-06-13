package org.akka.essentials.service.provider;

import akka.actor.UntypedActor;

public class ServiceA extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof String) {
			System.out.println("Message received ->" + message);
			getSender().tell("Message from Service A ->" + message, getSelf());
		}
	}
}
