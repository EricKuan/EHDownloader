package tw.com.kuan;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class getEHPhoto {
	private static int threadcount = 0;
	
	public static void main(String[] args) throws Exception {

		Executor executor = Executors.newFixedThreadPool(10);

		FileInputStream fileInStreamObj = new FileInputStream("./conf/downliadList.txt");
		InputStream inStreamObject = fileInStreamObj;
		Scanner sc = new Scanner(inStreamObject);
		while (sc.hasNext()) {
			String url = sc.nextLine();
			getEHThread get = new getEHThread(url); 
			executor.execute(get);
			threadcount++;
			Thread.sleep(5000L);
		}
		sc.close();
		inStreamObject.close();
		fileInStreamObj.close();
		while(threadcount>0){
			System.out.println(threadcount + " thread Still Running!");
			Thread.sleep(10000L);
		}
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
