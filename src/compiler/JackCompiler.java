package compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JackCompiler {

	public static void main(String[] args) {
		
		if (args.length < 1) {
			System.out.println("USAGE: java compiler.JackAnalyzer <filename / directory>");
			System.exit(0);
		}
		
		String file[] = args[0].split("\\.");
		List<File> myFiles = new ArrayList<>();
		
		if (file.length == 1) {
			File directory = new File(args[0]);
			File[] tmp = directory.listFiles();
			
			for (int i = 0; i < tmp.length; i++) {
				String currentFile = tmp[i].getName();
				if (currentFile.substring(currentFile.length() - 5, 
						currentFile.length()).contentEquals(".jack")) {
					myFiles.add(tmp[i]);
				}
			}
		} else {
			myFiles.add(new File(args[0]));
		}
		
		
		for (int i = 0; i < myFiles.size(); i++) {
			String name = myFiles.get(i).getPath();
			JackTokenizer token = new JackTokenizer(name);
					
			int length = myFiles.get(i).getPath().length();
			name = name.substring(0, length - 5) + ".vm";
			
			VMWriter writer = new VMWriter(name);
			
			name = myFiles.get(i).getName();
			
			CompilationEngine engine = new CompilationEngine(name.substring(0, name.length() - 5), token, writer);
			
		}
	}
}
