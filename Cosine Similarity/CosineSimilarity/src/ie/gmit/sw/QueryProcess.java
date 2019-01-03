package ie.gmit.sw;

import java.util.concurrent.*;

public class QueryProcess {
	private BlockingQueue<Shingle> queue;
	private ExecutorService pool;
	private ConcurrentHashMap<String, Integer> queryMap;
	private int charLength;
	
	public QueryProcess(int charLength) {
		super();
		this.charLength=charLength;
	}

	public ConcurrentHashMap<String, Integer> queryFileRead(String fileName,int queueSize,int poolSize)
			throws InterruptedException, ExecutionException {
		queryMap = new ConcurrentHashMap<String, Integer>();
		queue = new ArrayBlockingQueue<Shingle>(queueSize);
		pool = Executors.newFixedThreadPool(poolSize);
		Future<ConcurrentHashMap<String, Integer>> map = pool.submit(new QueryShingleTaker(queue));
		Thread.sleep(10);
		pool.submit(new FileParser(queue, fileName,charLength));
		Thread.sleep(10);
		this.queryMap = map.get();
		pool.shutdown();
		return queryMap;
	}
}
