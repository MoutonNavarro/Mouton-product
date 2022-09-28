package ph.Maymay.Mouton.IndentConverter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class IndentConverter {
	static int TABS = 3;
	static int TABSM = 3;

	public static void main(String...strings ) {
		if (strings.length < 1 || strings[0] == null || strings[0].equals("")) {
			System.err.println("Please set source directory name.(Not available current directory setting)");
			System.exit(1);
		}
		if (strings.length < 2 || strings[1] == null || strings[1].equals("")) {
			System.err.println("Please set target directory name.(Not available current directory setting)");
			System.exit(1);
		}

		if (strings.length >= 3 && !strings[2].equals("")) {
			try {
				TABS = Integer.parseInt(strings[2]);
				if (TABS <= 0 || TABS > 255) {
					System.err.println("Illegal head tab size detected. set default size (3)");
					System.err.println("Input: " + TABS);
					TABS = 3;
				}
			}catch (NumberFormatException e) {
				System.err.println("Please set correctly number format at head tab size.");
				System.exit(1);
			}
		}
		if (strings.length >= 4 && !strings[3].equals("")) {
			try {
				TABSM = Integer.parseInt(strings[3]);
				if (TABSM <= 0 || TABSM > 255) {
					System.err.println("Illegal body tab size detected. set default size (3)");
					System.err.println("Input: " + TABSM);
				}
			}catch (NumberFormatException e) {
				System.err.println("Please set correctly number format at body tab size.");
				System.exit(1);
			}
		}
		final File inputdir  = new File(strings[0]);
		final File outputdir = new File(strings[1]);
		final String inputpath = inputdir.getAbsolutePath();
		final String outputpath = outputdir.getAbsolutePath();
		if (!inputdir.exists()) {
//			System.err.println("Input directory is not exist.");
//			System.exit(1);
			errexit("Input directory is not exist.");
		}
		if (inputdir.isFile()) {
			//File mode
//			if (!outputdir.isFile()) {
//				System.err.println("Cannot convert file to other else.");
//				System.exit(1);
//			}
//			if (inputdir.getAbsoluteFile().equals(outputdir.getAbsoluteFile())) {
			if (inputpath.equals(outputpath)) {
				errexit("Cannot output to same input file.");
			}
			if (!fileExpansionCheck(inputdir.getName().toLowerCase())) {
//			if (!inputdir.getName().toLowerCase().endsWith(".java")) {
				errexit("The input file is not java source file.");
			}
			if (!inputdir.canRead()) {
				errexit("The input file is not readable.");
			}
			if (outputdir.exists() && !outputdir.canWrite()) {
				errexit("The output file is not writeable.");
			}
			try {
				convert(inputdir, outputdir);
			}catch (IOException | RuntimeException e) {
				e.printStackTrace();
				System.exit(2);
			}
			finish();	//End of program with file mode

		}else if(!inputdir.isDirectory()) {
			errexit("nput is not file or directory.");
		}
//		if(!outputdir.isDirectory()) {
//			System.err.println("Cannot convert directory to other else.");
//			System.exit(1);
//		}
//		if (inputdir.getAbsolutePath().equals(outputdir.getAbsolutePath())) {
		if (inputpath.equals(outputpath)) {
			errexit("Cannot output to same input directory.");
		}

		{
//			File comp = inputdir;
//			while ((comp = comp.getParentFile()) != null) {
//				if (comp.getAbsolutePath().equals(outputdir.getAbsolutePath())){
//					errexit("input directory is included in output directory.");
//				}
//			}
//			comp = outputdir;
//			while ((comp = comp.getParentFile()) != null) {
//				if (comp.getAbsolutePath().equals(inputdir.getAbsolutePath())){
//					errexit("output directory is included in input directory.");
//				}
//			}

//			File comp1 = inputdir.getAbsolutePath().length() > outputdir.getAbsolutePath().length() ? inputdir : outputdir;
			File comp1 = inputpath.length() > outputpath.length() ? inputdir : outputdir;
			final File comp2 = comp1 == inputdir ? inputdir : outputdir;
			while ((comp1 = comp1.getParentFile()) != null) {
				if (comp1.getAbsolutePath().equals(comp2.getAbsolutePath())){
					if (comp2 == outputdir) {
						errexit("input directory is included in output directory.");
					}
					errexit("output directory is included in input directory.");
				}
			}
		}
		Deque<File> dirque = new ArrayDeque<File>();
		dirque.addAll(Arrays.asList(inputdir.listFiles()));
		boolean isexception = false;
		{
			File input = null;
			File output = null;
//			String curoutdir = null;
			while ((input = dirque.pollFirst()) != null) {
				if (input.isDirectory()) {
					output = new File (input.getAbsolutePath().replace(inputpath, outputpath));
					File outpath = output.getParentFile();
					if (outpath.exists() && !outpath.isDirectory()) {
						System.err.println("Directory: " + outpath.getAbsolutePath() + "is already exists and not directory.");
						System.err.println("\tSkip this directory: " + input.getAbsolutePath());
						isexception = true;
						continue;
					}
					dirque.addAll(Arrays.asList(input.listFiles()));
					continue;
				}
				if (input.isFile()) {
//					if (!inputdir.getName().toLowerCase().endsWith(".java")) {
					if (!fileExpansionCheck(input.getName().toLowerCase())) {
//						System.err.println("The input file " + input.getAbsolutePath() + " is not java source file.");
						continue;
					}
					if (!input.canRead()) {
//						System.err.println("The input file: " + input.getAbsolutePath() + " is not readable.");
						continue;
					}
					try {
						output = new File (input.getAbsolutePath().replace(inputpath, outputpath));
						File outpath = output.getParentFile();
						if (!outpath.exists()) {
							outpath.mkdir();
						}else if(!outpath.isDirectory()) {
							throw new Error("Directory: " + outpath.getAbsolutePath() + " is already exists and not directory.");
						}
//						output.getParentFile().mkdir();
						convert(input, output);
					}catch (IOException | RuntimeException e) {
						System.err.println("File: " + input.getAbsolutePath() + " is failed to convert.");
						e.printStackTrace();
						isexception = true;
					}catch (Error e) {
						System.err.println("FATAL ERROR!");
						System.err.println("File: " + input.getAbsolutePath() + " is failed to convert.");
						System.err.println("Abort converting.");
//						e.printStackTrace();
						throw e;
					}
					continue;
				}
			}
		}
		finish(isexception ? "Finished but some files are not converted." : "Finished convert all files normally.");	//End of program with dorectory mode


	}
	private static void errexit(String str) {
		System.err.println(str);
		System.exit(3);
	}

	private static void finish(String str) {
//		if (str == null) {finish();}
		System.out.println(str);
		System.exit(0);
	}
	private static void finish() {
		System.out.println("Finished normally.");
		System.exit(0);
	}
	private static void convert(File inputfile, File outputfile) throws IOException{
		try(
			BufferedReader input = new BufferedReader(new FileReader(inputfile));
			BufferedWriter output = new BufferedWriter(new FileWriter(outputfile));
		){
			String line = null;

			StringBuilder out = null;
			while ((line = input.readLine()) != null) {
				//processing...
//				out = new StringBuilder(line.replaceAll("", ""))	//to first appeared statements
//					.append(line.replaceAll("", ""));	//to end of line
				out = new StringBuilder();
				line = convHead(line, out);
				convBody(line, out);
				output.write(out.toString());
				output.newLine();
				output.flush();
			}

		}catch (IOException | Error | RuntimeException e) {
			System.err.println("Unexpected error detected.");
//			e.printStackTrace();
//			System.exit(1);
			throw e;

		}

	}
	private static boolean fileExpansionCheck(String s) {
		return s.endsWith(".java")	|| s.endsWith(".txt")	|| s.endsWith(".htm")	|| s.endsWith(".html")
			 || s.endsWith(".xml")	|| s.endsWith(".css")	|| s.endsWith(".js")		|| s.endsWith(".csv")
			 || s.endsWith(".xvg")	|| s.endsWith(".ini")	|| s.endsWith(".pl")		|| s.endsWith(".php")
			 || s.endsWith(".cgi")	|| s.endsWith(".rb")		|| s.endsWith(".bat")	|| s.endsWith(".pm");
	}

	private static String convHead(String input, StringBuilder out) {
//		StringBuilder out = new StringBuilder();
		StringBuilder buffer = new StringBuilder(TABS);
		char c = '\u007F';
		int i = 0;
		char lastchar = '\u007F';
		boolean ismulticomment = false;
		for (i = 0;i < input.length(); i++) {
			c = input.charAt(i);
			if (c == '\u0009') {
				if (input.charAt(i + 1) == '\u0020') {	//space character
					i++;
					fillSpaceBody(buffer);
					out.append(buffer);
					buffer.delete(0, TABSM);
					if (input.charAt(i + 1) == '\u0020' || input.charAt(i + 1) == '\u0009' || input.charAt(i + 1) == '/') {
						lastchar = '\u0020';
//						buffer.delete(0, TABSM);
						continue;
					}
//					buffer.delete(0, TABS);
					break ;
				}
				lastchar = '\u0009';
				fillSpaceHead(buffer);
				out.append(buffer);
				buffer.delete(0, TABS);
				continue;
			}else if (c == '/') {
				if (lastchar != '\u0020' && lastchar != '\u0009' && lastchar != '\u007F') {
					break;
				}
				if (input.charAt(i + 1) == '/') {	//comment sequence
					i++;
					buffer.append("//");
					lastchar = '/';
				}else if (input.charAt(i + 1) == '*') {
					if (ismulticomment) {
						break;
					}
					ismulticomment = true;
					i++;
					buffer.append("/*");
					if (input.charAt(i + 1) == '*') {
						i++;
						buffer.append('*');
					}
					lastchar = '*';
				}else{
					buffer.append('/');
					lastchar = '/';
				}
				while (buffer.length() >= TABS) {
					out.append(buffer, 0, TABS);
					buffer.delete(0, TABS);
				}
				continue;
			}else if (c == '*') {
				if (ismulticomment || lastchar != '\u0020' && lastchar != '\u0009' && lastchar != '\u007F') {
					break;
				}
				ismulticomment = true;
				if (input.charAt(i + 1) == '/') {	//multi comment end sequence
					i++;
					buffer.append("*/");
					lastchar = '/';
				}else {
					buffer.append("*");
					lastchar = '*';
				}
				while (buffer.length() >= TABS) {
					out.append(buffer, 0, TABS);
					buffer.delete(0, TABS);
				}
				continue;
			}else if (c == '#') {
				buffer.append('/');
				lastchar = '/';
				continue;
			}else if(c == '\u0020') {
				lastchar = '\u0020';
				buffer.append(c);
				while (buffer.length() >= TABS) {
					out.append(buffer, 0, TABS);
					buffer.delete(0, TABS);
				}
				continue;

			}
			break;
		}
//		while (buffer.length() >= TABS) {
//			out.append(buffer, 0, TABS);
//			buffer.delete(0, TABS);
//		}
		i -= buffer.length();
//		buffer.delete(0, TABS);
		return input.substring(i);
	}

	private static String convBody(String input, StringBuilder out) {
		StringBuilder buffer = new StringBuilder(TABSM);
		char c = '\u007F';
		for (int i = 0;i < input.length(); i++) {
			c = input.charAt(i);
			if (c == '\u0009') {
				fillSpaceBody(buffer);
			}else {
				buffer.append(c);
			}
			while (buffer.length() == TABSM) {
				out.append(buffer);
				buffer.delete(0, TABSM);
			}
		}
		out.append(buffer);
		return input;
	}
	private static void fillSpaceHead(StringBuilder buffer) {
		while (buffer.length() < TABS) {
			buffer.append('\u0020'); //Space
		}
	}

	private static void fillSpaceBody(StringBuilder buffer) {
		while (buffer.length() < TABSM) {
			buffer.append('\u0020'); //Space
		}
	}
}
