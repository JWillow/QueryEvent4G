package org.qe4g.request

import java.lang.ref.WeakReference;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.blueprints.pgm.Vertex;

class EventLifeManager {

	private long maxInterval = 100000000l

	private long oldEventCreationTime = 0l

	private Queue<WeakReference<Vertex>> history = new ConcurrentLinkedQueue<WeakReference<Vertex>>();

	final static Logger logger = LoggerFactory.getLogger(EventLifeManager.class);

	public void handle(Vertex vEvent) {
		if(history.isEmpty()) {
			oldEventCreationTime = vEvent.event.getTime()
		}
		history.add(new WeakReference<Vertex>(vEvent));
		long interval = vEvent.event.getTime() - oldEventCreationTime
		if(interval < maxInterval) {
			return
		}
		Vertex oldVEvent = null;
		while(true) {
			oldVEvent = history.peek().get();
			if(oldVEvent != null &&
			(vEvent.event.getTime() - oldVEvent.event.getTime()) < maxInterval) {
				oldEventCreationTime = oldVEvent.event.getTime()
				break;
			}
			Object forLogging = history.poll()
			logger.info("Removing from history : {}", forLogging.get());
			if(oldVEvent != null) {
				oldVEvent --
			}
		}
	}
}
