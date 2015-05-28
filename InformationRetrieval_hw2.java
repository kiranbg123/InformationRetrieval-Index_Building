import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.security.KeyStore.Entry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;


public class InformationRetrieval_hw2 {

	public static void main(String[] args) throws Exception
	{
		if(args.length < 2)
		{
			System.out.println("You need to provide the path as first argument");
			System.exit(1);
		}
		String cranfield = args[0];
		String stopWordsPath = args[1];
		Set<String> stopWords = parseStopWords(stopWordsPath);
		System.out.println(cranfield);
		//Function to read Cranfield directory into string
		String cranfieldContent = new String();
		
		File cranfieldFiles = new File(cranfield);
		//Programm time starts
		//long startTime = System.currentTimeMillis();
		
		//parseFile(cranfieldFiles);
		IndexBuilder indexBuilder = new IndexBuilder();
		boolean stemming = false;
		Long startTime = System.currentTimeMillis();
		Map<String, IndexStorage> indexVersion1Uncompressed = IndexBuilder.parseFile(cranfieldFiles, stopWords, false);
		Long endTime = System.currentTimeMillis();
		
		System.out.println("Time Taken to Build IndexVersion1 Uncompressd : " + (endTime - startTime) + " ms");
		startTime = endTime = (long) 0.00;
		startTime = System.currentTimeMillis();
		Map<String, SizeHandler.IndexStorage> indexVersion1CompressedIndex = SizeHandler.compressversion1Index(indexVersion1Uncompressed);
		endTime = System.currentTimeMillis();
		System.out.println("Time Taken to Build IndexVersion2 UnCompressd : " + (endTime - startTime) +  " ms");
		//indexVersion2 contains stemmed words
		stemming = true;
		startTime = endTime = (long) 0.00;
		startTime = System.currentTimeMillis();
		Map<String, IndexStorage> indexVersion2Uncompressed = IndexBuilder.parseFile(cranfieldFiles, stopWords, stemming);
		endTime = System.currentTimeMillis();
		System.out.println("Time Taken to Build IndexVersion2 Uncompressd : " + (endTime - startTime) + " ms");
		startTime = endTime = (long) 0.00;
		startTime = System.currentTimeMillis();
		Map<String, SizeHandler.IndexStorage> indexVersion2Compressed = SizeHandler.compressversion2Index(indexVersion2Uncompressed);
		endTime = System.currentTimeMillis();
		System.out.println("Time Taken to Build IndexVersion2 Compressd : " + (endTime - startTime) + " ms");
		
		//Print the size of the inverted lists
		
		System.out.println("Number of the posting list for Uncompressed version1 : " + indexVersion2Uncompressed.size());
		System.out.println("Number of the posting list for Uncompressed version2 : " + indexVersion1Uncompressed.size());
		
		ArrayList<String> testingWords = new ArrayList<>();
		testingWords.add("Reynold");
		testingWords.add("nasa");
		testingWords.add("prandtl");
		testingWords.add("flow");
		testingWords.add("pressure");
		testingWords.add("boundary");
		testingWords.add("shock");
		
		System.out.println("Index Version1: Uncompressed");
		for(int i =0; i < testingWords.size();i++)
		{
		for(java.util.Map.Entry<String, IndexStorage> entry : indexVersion1Uncompressed.entrySet()){
			if(entry.getKey().equals(testingWords.get(i)))
			{
				
				IndexStorage temp = entry.getValue();
				List<Documents> docList = temp.docList;
				int length = 0;
				
				length = temp.docList.size() *2*Integer.SIZE;  
				
				System.out.println("Word: " + entry.getKey() + " df :" + temp.docFrequency + " tf :" + temp.termFrequency + " inverted List length in Bytes :" + length);
			}
		}
		}
		
		System.out.println("Index Version 2: Uncompressed");
		
		for(int i =0; i < testingWords.size();i++)
		{
		for(java.util.Map.Entry<String, IndexStorage> entry : indexVersion2Uncompressed.entrySet()){
			if(entry.getKey().equals(testingWords.get(i)))
			{
				
				IndexStorage temp = entry.getValue();
				List<Documents> docList = temp.docList;
				int length = 0;
				
				length = temp.docList.size() *2*Integer.SIZE;  
				
				System.out.println("Word: " + entry.getKey() + " df :" + temp.docFrequency + " tf :" + temp.termFrequency + " inverted List length in Bytes :" + length);
			}
		}
		}
		System.out.println("Index Version1: Compressed");
		for(int i =0; i < testingWords.size();i++)
		{
		for(java.util.Map.Entry<String, SizeHandler.IndexStorage> entry : indexVersion1CompressedIndex.entrySet()){
			if(entry.getKey().equals(testingWords.get(i)))
			{
				
				SizeHandler.IndexStorage temp = entry.getValue();
				List<SizeHandler.Documents> docList = temp.docList;
				
				long length = 0;
				for(SizeHandler.Documents doc : docList){
					length += doc.docID.length + doc.frequency.length;  
				}
				
				System.out.println("Word: " + entry.getKey() + " inverted List length in Bytes :" + length);
			}
		}
		}
		
		System.out.println("Index Version2:Compressed");
		for(int i =0; i < testingWords.size();i++)
		{
		for(java.util.Map.Entry<String, SizeHandler.IndexStorage> entry : indexVersion2Compressed.entrySet()){
			if(entry.getKey().equals(testingWords.get(i)))
			{
				
				SizeHandler.IndexStorage temp = entry.getValue();
				List<SizeHandler.Documents> docList = temp.docList;
				
				long length = 0;
				for(SizeHandler.Documents doc : docList){
					length += doc.docID.length + doc.frequency.length;  
				}
				
				System.out.println("Word: " + entry.getKey() + " inverted List length in Bytes" + length);
			}
		}
		}
		/* for(java.util.Map.Entry<String, IndexStorage> ent : indexVersion2Uncompressed.entrySet()){
			//if(ent.getKey().equals("flow"))
			System.out.println(ent.getKey() +"->"+ ent.getValue().toString());
		}*/
		
		//print the content of a map to File
		//Write Uncompressed File
		 File indexVerson1Uncomressed=new File("indexVersion2Uncompressed.txt");
		    FileOutputStream fos=new FileOutputStream(indexVerson1Uncomressed);
		        ObjectOutputStream oos=new ObjectOutputStream(fos);

		        //oos.writeObject(indexVersion1Uncompressed);
		       // PrintWriter pw=new PrintWriter(fos);
		        writeFileUncompressed(oos, indexVersion1Uncompressed);
		        
		        fos.close();

		        File indexVersion1Compressed=new File("indexVersion2Compressed.txt");
		       
		        //Write Compressed File
			   fos=new FileOutputStream(indexVersion1Compressed);
			         oos=new ObjectOutputStream(fos);

			       // oos.writeObject(indexVersion1CompressedIndex);
			     writeFileCompressed(oos, indexVersion1CompressedIndex); 
			      
			     fos.close();
			     
			     //Write Uncompressed File version2
			     
			     File indexVerson2Uncomressed=new File("indexVersion1Uncompressed.txt");
				    fos=new FileOutputStream(indexVerson2Uncomressed);
				         oos=new ObjectOutputStream(fos);

				        writeFileUncompressed(oos, indexVersion2Uncompressed);
				        
				        fos.close();
				 //Write Compressed File Version 2
				        
				        File indexVersion2Comressed=new File("indexVersion1Compressed.txt");
					       
				        //Write Compressed File
					   fos=new FileOutputStream(indexVersion2Comressed);
					         oos=new ObjectOutputStream(fos);

					      //  oos.writeObject(indexVersion2CompressedIndex);
					     writeFileCompressed(oos, indexVersion2Compressed); 
					      
					     fos.close();
				        
		
		
	}
	
	public static void writeFileUncompressed(ObjectOutputStream oos, Map<String, IndexStorage> indexVersion1UncompressedIndex ) throws IOException
	{
		for(Map.Entry<String,IndexStorage> entry :indexVersion1UncompressedIndex.entrySet()){
	           //pw.println(entry.getKey()+"="+entry.getValue());
	        	IndexStorage temp = entry.getValue();
	        	oos.writeChars(temp.word);
	        	oos.writeChars(Integer.toString(temp.docFrequency));
	        	oos.writeChars(Integer.toString(temp.termFrequency));
	        	List<Documents> tempList = temp.docList;
	        	
	        	//Iterator<Documents> entryDoc = tempList.iterator();
	        	for (Documents entryDoc : tempList) {
	    			//oos.writeChars(entryDoc.
	    			oos.writeChars(Integer.toString(entryDoc.docID));
	    			oos.writeChars(":");
	    			oos.writeChars(Integer.toString(entryDoc.frequency));
	    			
	    		}
	        }
	}
	
	public static void writeFileCompressed(ObjectOutputStream oos, Map<String, SizeHandler.IndexStorage> indexVersion1Compressed ) throws IOException
	{
		for(Map.Entry<String,SizeHandler.IndexStorage> entry :indexVersion1Compressed.entrySet()){
	           //pw.println(entry.getKey()+"="+entry.getValue());
	        	SizeHandler.IndexStorage temp = entry.getValue();
	        	oos.writeChars(temp.word);
	        	oos.write(temp.docFrequency);
	        	oos.write(temp.termFrequency);
	        	List<SizeHandler.Documents> tempList = temp.docList;
	        	
	        	//Iterator<Documents> entryDoc = tempList.iterator();
	        	for (SizeHandler.Documents entryDoc : tempList) {
	    			//oos.writeChars(entryDoc.
	    			oos.write(entryDoc.docID);
	    			oos.write(entryDoc.frequency);
	    			
	    		}
	        }
	}
	public static Set<String> parseStopWords(String filename) throws FileNotFoundException {
		Set<String> stopWords = new HashSet<>(); 
		Scanner scanner = new Scanner(new File(filename));
		while(scanner.hasNext()){
			stopWords.add(scanner.next());
		}
		scanner.close();
		return stopWords;
	}
	public static class IndexStorage {
		String word;
		int docFrequency;
		int termFrequency;
		List<Documents> docList;
	
		public IndexStorage(String word, int docFrequency, int termFrequency, List<Documents> docList) {
			this.word = word;
			this.docFrequency = docFrequency;
			this.termFrequency = termFrequency;
			this.docList = docList;
		}

		@Override
		public String toString() {
			return "IndexStorage [word=" + word + ", docFrequency="
					+ docFrequency + ", termFrequency=" + termFrequency
					+ ", docList=" + docList + "]";
		}

		/*
		@Override
		public String toString() {
			StringBuilder stringBuilder =   new StringBuilder("");
			stringBuilder.append("\n" + term + " " + docFrequency + "/" + termFrequency +"->"); 
			for(PostingEntry postingEntry : postingList){
				stringBuilder.append(postingEntry); 
			}
			stringBuilder.length();
			return stringBuilder.toString();
		}
		*/
	}
	
	static class Documents{
		int docID;
		int frequency;
		
		public Documents(int docID, int frequency) {
			this.docID = docID;
			this.frequency = frequency;
		}

		@Override
		public String toString() {
			return "Documents [docID=" + docID + ", frequency=" + frequency
					+ "]";
		}

		/*
		@Override
		
		public String toString() {
			return docID + "/" + frequency + ",";
		}
		*/
	}


}
