package org.midnightas.langton;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A timer with the ability to change the period of running a {@code TimerTask}
 * 
 * @author midnightas
 */
public class LangtonTimer {

	private Timer timer;
	private Runnable runnable;

	private int period = 1000;

	public LangtonTimer(Runnable runnable) {
		this.runnable = runnable;
	}

	public void start() {
		stop();

		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				runnable.run();
			}
		}, 0, period);
	}

	public void stop() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	public void setPeriod(int period) {
		stop();
		this.period = period;
		start();
	}

	public int getPeriod() {
		return this.period;
	}

}
