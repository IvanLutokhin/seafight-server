package seafight.managers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager {
	private static volatile ThreadPoolManager instance = null;
	
	public synchronized static ThreadPoolManager getInstance() {
		if(instance == null)
			instance = new ThreadPoolManager();
		
		return instance;
	}
	
	private ExecutorService executorService = Executors.newFixedThreadPool(8);
	
	private ScheduledExecutorService scheduleExecutorService = Executors.newScheduledThreadPool(4);
	
	private ThreadPoolManager() { }
	
	public void execute(Runnable r) {
		this.executorService.execute(r);
	}
	
	public ScheduledFuture<?> schedule(Runnable r, long delay) {
		return this.scheduleExecutorService.schedule(r, delay, TimeUnit.MILLISECONDS);
	}
	
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable r, long initial, long delay) {
		return this.scheduleExecutorService.scheduleAtFixedRate(r, initial, delay, TimeUnit.MILLISECONDS);
	}
}