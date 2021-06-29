/**
 * FileSizeAnalyzer.java
 *
 * Count Files and sizes in a directory and all subdirs
 * 
 * Author: M. Huebner, HAW Hamburg
 *
 */

import java.io.*;
import java.util.*;

public class FileSizeAnalyzer {
   private final int blockSize = 4096;
	private long byteSum = 0;
	private long fileCount = 0;
	private long fileCountLessThanBlocksize = 0;
	private long waste = 0;   

	public void traverse(File dir) {
		File currentFile;
		long len;
		int i;

		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("not a directory: "
					+ dir.getName());
		}
		//System.out.println("Analyzing " + dir.getName());

		/* Visit all entries in directory */
		File[] entries = dir.listFiles();

		if (entries != null) {
			for (i = 0; i < entries.length; ++i) {
				currentFile = entries[i];
				len = currentFile.length();
				fileCount++;
				byteSum = byteSum + len;
            // Anzahl Dateien < blockSize
				if (len < blockSize) {
					fileCountLessThanBlocksize++;
				}
            // Verschnitt (verschwendeter Platz)
            waste += blockSize - len % blockSize;
				if (currentFile.isDirectory()) {
					/* Recursive call */
					traverse(currentFile);
				}
			}
		}
	}

    	public void printResult() {
			/* Show Output */
    		System.out.println("---------------------------------------------");
    		System.out.println("Anzahl Dateien: " + fileCount);         
    		System.out.println("\nAnzahl Dateien < " + blockSize + " Byte: " + fileCountLessThanBlocksize + ", Anteil " +
							 String.format("%.1f%%", fileCountLessThanBlocksize*100.0/fileCount));
    		/* System.out.println("\nDurchschnittliche Dateigroesse: "
    				+ String.format("%.1f KB", (float) byteSum / fileCount / 1000.0)); */
    		System.out.println("\nBelegter Speicherplatz: "
    				+ String.format("%.1f MB", byteSum / 1000000.0));                    
    		System.out.println("\nVerschnitt: "
    				+ String.format("%.1f MB", waste / 1000000.0));               

	}

	public static void main(String args[]) {
		FileSizeAnalyzer ft = new FileSizeAnalyzer();
		Scanner dirIn = new Scanner(System.in);
		String startDir;

		/* Get start directory from user */
		System.out.print("Verzeichnis: ");
		startDir = dirIn.nextLine();
		System.out.println("Analysiere " + startDir + " ...");
		
		/* Start analysis */
		ft.traverse(new File(startDir));
		
		/* Analysis finished */
		ft.printResult();
	}

}
