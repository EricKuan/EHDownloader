package tw.com.kuan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@SpringBootApplication
public class getEHPhoto {
	private static int threadcount = 0;
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(getEHPhoto.class, args);
		startClam();
	}

	private static void startClam() throws InterruptedException, IOException {
		ExecutorService executor  = Executors.newFixedThreadPool(5);
		FileInputStream fileInStreamObj = new FileInputStream("./conf/downliadList.txt");
		InputStream inStreamObject = fileInStreamObj;
		Scanner sc = new Scanner(inStreamObject);
		ArrayList<Future> result = new ArrayList<>();
		while (sc.hasNext()) {
			String url = sc.nextLine();
			getEHThread get = new getEHThread(url);
			result.add(executor.submit(get));
			Thread.sleep(5000L);
		}
		sc.close();
		inStreamObject.close();
		fileInStreamObj.close();
		ArrayList<Future> jobs = null;
		do {
			jobs = (ArrayList<Future>) result.stream().filter(single -> single.isDone()).collect(Collectors.toList());
			System.out.println("==========\nTotal job: " + result.size());
			System.out.println("Finsh job: " + jobs.size() + "\n==========");
			Thread.sleep(5000L);
		}while(result.size() > jobs.size());


		System.out.println("Mission Completed");
		executor = null;
		delCompleteFile();
		System.exit(0);
	}

	private static void delCompleteFile() throws IOException {
		String filePath = "./conf/downliadList.txt";
		File myFile = new File(filePath);
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		String backPath = filePath.substring(0, filePath.length() - 4) + "." + sdf.format(cal.getTime()) + ".txt";
		System.out.println(backPath);
		myFile.renameTo(new File(backPath));
		myFile = new File(filePath);
		if ((myFile.createNewFile())) {
			System.out.println("downloadList.txt reset Completed!");
		}
	}

	public static void minusThreadCount() {
		getEHPhoto.threadcount--;
	}
}
