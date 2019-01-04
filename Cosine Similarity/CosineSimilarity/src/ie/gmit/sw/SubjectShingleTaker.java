package ie.gmit.sw;

import java.util.concurrent.*;

/**
 * This class is responsible for processing the data in the BlockingQueue and
 * the calculate the frequency of the shingles and generate the
 * ConcurentHashMap.
 * 
 * @author Nouman Zafar
 * @version 1.0
 * @since 1.8
 *
 */
public class SubjectShingleTaker implements Callable<BlockingQueue<ConcurrentHashMap<String, Integer>>> {
	private BlockingQueue<Shingle> queue;
	private int fileCount;
	private int maxFiles;
	private boolean keepRunning = true;
	private ConcurrentHashMap<String, Integer> subjectMap;
	private BlockingQueue<ConcurrentHashMap<String, Integer>> mapQueue;

	/**
	 * 
	 * @param queue     BlockigQueue which holds the instances of Shingle class
	 * @param fileCount Number of files in the folder
	 * @param maxFiles  Max capacity of files which will be processed. if amount of
	 *                  files exceeds then allowed that will cause problem.
	 */
	public SubjectShingleTaker(BlockingQueue<Shingle> queue, int fileCount, int maxFiles) {
		super();
		this.queue = queue;
		this.fileCount = fileCount;
		this.maxFiles = maxFiles;
	}

	public BlockingQueue<ConcurrentHashMap<String, Integer>> call() throws Exception {
		subjectMap = new ConcurrentHashMap<String, Integer>();
		mapQueue = new ArrayBlockingQueue<ConcurrentHashMap<String, Integer>>(maxFiles);

		while (fileCount > 0 && keepRunning) {
			Shingle w = queue.take();
			/**
			 * If the shingle (k-mer) is instance of the Poison class then add the map to
			 * the queue and subtract the fileCount by one.
			 */
			if (w instanceof Poison) {
				mapQueue.put(subjectMap);
				subjectMap = new ConcurrentHashMap<String, Integer>();
				fileCount--;
			} else {
				String shingle = w.getShingle();
				/**
				 * Count the frequency of the Shingle instance appeared in the file.
				 */
				Integer frequency = subjectMap.get(shingle);
				/**
				 * Ternary if statement if Shingle appeared first time then set frequency to 1
				 * otherwise add 1 to the existing frequency
				 */
				frequency = (frequency == null) ? 1 : ++frequency;
				subjectMap.put(shingle, frequency);
			}
		}
		return mapQueue;
	}
}
