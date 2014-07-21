package seafight.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class FileHelper {
	public static List<File> dir(File file) throws FileNotFoundException {
		return dir(file, new LinkedList<File>());
	}
	
	public static List<File> dir(File file, List<File> files) throws FileNotFoundException {
		if(!file.exists())
			throw new FileNotFoundException("File \"" + file.getAbsolutePath() + "\" not found!");
		
		files.add(file);
		
		if(file.isDirectory()) {
			for(File f : file.listFiles())
				dir(f, files);
		}
		
		return files;
	}
	
	public static String GetFile(String path) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(path));
		StringBuilder sb = new StringBuilder();
		String line;		
		while((line = br.readLine()) != null)
			sb.append(line);
		br.close();
		
		return sb.toString();
	}
}