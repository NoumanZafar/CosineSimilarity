package ie.gmit.sw;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

public class SubjectProcess {
	private BlockingQueue<Shingle> queue;
	private ExecutorService pool;
	private int fileCount;
	private int maxFiles;
	private BlockingQueue<ConcurrentHashMap<String, Integer>> mapQueue;
	private List<String> filesList;
	private int k_mer_size;

	public SubjectProcess(List<String> filesList, int k_mer_size, int maxFiles) {
		super();
		this.filesList = filesList;
		this.k_mer_size = k_mer_size;
		this.maxFiles = maxFiles;
	}

	public BlockingQueue<ConcurrentHashMap<String, Integer>> readSubjectFolder(String folderName, int queueSize,
			int poolSize) throws InterruptedException, ExecutionException {
		mapQueue = new ArrayBlockingQueue<ConcurrentHashMap<String, Integer>>(maxFiles);
		filesList = new ArrayList<String>();
		queue = new ArrayBlockingQueue<>(queueSize);
		pool = Executors.newFixedThreadPool(poolSize);
		File folder = new File(folderName);
		if (folder.isDirectory()) {
			String[] files = folder.list();
			fileCount = files.length;
			Future<BlockingQueue<ConcurrentHashMap<String, Integer>>> list = pool
					.submit(new SubjectShingleTaker(queue, fileCount, maxFiles));
			for (String file : files) {
				pool.submit(new FileParser(queue, folderName + "/" + file, k_mer_size));
				filesList.add(file);
				Thread.sleep(10);
			}
			this.mapQueue = list.get();
		} else {
			fileCount = 1;
			Future<BlockingQueue<ConcurrentHashMap<String, Integer>>> list = pool
					.submit(new SubjectShingleTaker(queue, fileCount, maxFiles));
			Thread.sleep(10);
			pool.submit(new FileParser(queue, folderName, k_mer_size));
			Thread.sleep(10);
			this.mapQueue = list.get();
		}
		pool.shutdown();
		return mapQueue;
	}

	public List<String> getList() {
		return filesList;
	}
}