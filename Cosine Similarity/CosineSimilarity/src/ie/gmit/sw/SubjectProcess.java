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
	private int charLength;

	public SubjectProcess(List<String> filesList,int charLength,int maxFiles) {
		super();
		this.filesList = filesList;
		this.charLength=charLength;
		this.maxFiles=maxFiles;
	}

	public BlockingQueue<ConcurrentHashMap<String, Integer>> readSubjectFolder(String folderName, int queueSize, int poolSize)
			throws InterruptedException, ExecutionException {
		mapQueue = new ArrayBlockingQueue<ConcurrentHashMap<String, Integer>>(maxFiles);
		filesList = new ArrayList<String>();
		queue = new ArrayBlockingQueue<>(queueSize);
		pool = Executors.newFixedThreadPool(poolSize);
		File folder = new File(folderName);
		String[] files = folder.list();
		fileCount = files.length;
		Future<BlockingQueue<ConcurrentHashMap<String, Integer>>> list = pool.submit(new SubjectShingleTaker(queue, fileCount,maxFiles));
		for (String file : files) {
			pool.submit(new FileParser(queue, folderName + "/" + file,charLength));
			filesList.add(file);
			Thread.sleep(10);
		}
		this.mapQueue = list.get();
		pool.shutdown();
		return mapQueue;
	}

	public List<String> getList() {
		return filesList;
	}
}