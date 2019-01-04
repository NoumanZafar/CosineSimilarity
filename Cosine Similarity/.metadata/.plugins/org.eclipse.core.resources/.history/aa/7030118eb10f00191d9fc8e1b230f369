package ie.gmit.sw;

import java.util.concurrent.*;

/**
 * This class uses ExecutorService where ThreadPool is created. Multiple threads
 * runs the {@link FileParser} to read the file. And another ThreadPool submit
 * {@link QueryShingleTaker}. Where the results of that ThreadPool are assigned
 * to Future interface to generate the ConcurrentHashMap<K,V>.
 * 
 * @author Nouman Zafar
 * @version 1.0
 * @since 1.8
 * @see ExecutorService,Future,ConcurrentHashMap
 *
 */
public class QueryProcess {
	private BlockingQueue<Shingle> queue;
	private ExecutorService pool;
	private ConcurrentHashMap<String, Integer> queryMap;
	private int k_mer_size;

	/**
	 * 
	 * @param k_mer_size - Number of characters each buffer is reading
	 */
	public QueryProcess(int k_mer_size) {
		super();
		this.k_mer_size = k_mer_size;
	}

	/**
	 * This method runs multiple threads to get BlockingQueue populated in the
	 * FileParser class and generates the ConcurrentHashMap<K,V> with the help of
	 * Future interface.
	 * 
	 * @param fileName  - Name of the file
	 * @param queueSize - Size of the BlockingQueue. (Maximum capacity)
	 * @param poolSize  - Size of the ThreadPool
	 * @return - ConcurrentHashMap <String,Integer> where String is the Shingle and
	 *         the Integer is the number of appearance of that Shingle in the file.
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public ConcurrentHashMap<String, Integer> queryFileRead(String fileName, int queueSize, int poolSize)
			throws InterruptedException, ExecutionException {
		queryMap = new ConcurrentHashMap<String, Integer>();
		queue = new ArrayBlockingQueue<Shingle>(queueSize);
		pool = Executors.newFixedThreadPool(poolSize);
		Future<ConcurrentHashMap<String, Integer>> map = pool.submit(new QueryShingleTaker(queue));
		/**
		 * Put thread to sleep for 10 milliseconds
		 */
		Thread.sleep(10);
		pool.submit(new FileParser(queue, fileName, k_mer_size));
		Thread.sleep(10);
		/**
		 * Get the value of Future. .get() method is a blocking method thread will stay
		 * alive until results are not returned.
		 */
		this.queryMap = map.get();
		/**
		 * Stop the ThreadPool.
		 */
		pool.shutdown();
		return queryMap;
	}
}
