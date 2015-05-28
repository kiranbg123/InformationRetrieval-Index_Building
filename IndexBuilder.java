import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public class IndexBuilder {

	public static Map<String, InformationRetrieval_hw2.IndexStorage> indexMap = new HashMap<String, InformationRetrieval_hw2.IndexStorage>();
	public static Map<String, InformationRetrieval_hw2.IndexStorage> stemmedIndexMap = new HashMap<String, InformationRetrieval_hw2.IndexStorage>();
	public IndexBuilder() {
		// TODO Auto-generated constructor stub
	}

	public static void buildIndex(TreeMap<String, Integer> wordList,Set<String> stopWords, int docId, boolean stemming){
		int maxTermFrequency = 0;
		int docLength = 0;
		for(String word : wordList.keySet()){
			int termFrequency = wordList.get(word);			
			docLength +=  termFrequency;
			if(termFrequency > maxTermFrequency){
				maxTermFrequency = termFrequency;
			}
			if(stemming == true)
			{
				
			if(!stopWords.contains(word)){
				//updateDictionaryAndPosting(docId, term, termFreqTable.get(term));
				InformationRetrieval_hw2.IndexStorage stemmedIndexStorage = stemmedIndexMap.get(word);
				if(stemmedIndexStorage == null){
					stemmedIndexStorage = new InformationRetrieval_hw2.IndexStorage(word, 0, 0, new LinkedList<InformationRetrieval_hw2.Documents>());
					stemmedIndexMap.put(word, stemmedIndexStorage);
				}
				stemmedIndexStorage.docList.add(new InformationRetrieval_hw2.Documents(docId, termFrequency));
				stemmedIndexStorage.docFrequency += 1;
				stemmedIndexStorage.termFrequency += termFrequency;
			}
			}
			else
			{
				if(!stopWords.contains(word)){
					//updateDictionaryAndPosting(docId, term, termFreqTable.get(term));
					InformationRetrieval_hw2.IndexStorage indexStorage = indexMap.get(word);
					if(indexStorage == null){
						indexStorage = new InformationRetrieval_hw2.IndexStorage(word, 0, 0, new LinkedList<InformationRetrieval_hw2.Documents>());
						indexMap.put(word, indexStorage);
					}
					indexStorage.docList.add(new InformationRetrieval_hw2.Documents(docId, termFrequency));
					indexStorage.docFrequency += 1;
					indexStorage.termFrequency += termFrequency;
				}
			}
		}
		
	}
	public static Map<String, InformationRetrieval_hw2.IndexStorage> parseFile(File cranfieldFiles, Set<String> stopWords, boolean stemming) throws IOException
	{
			int docId = 0;
		    TreeMap<String, Integer> wordList = new TreeMap<String, Integer>();
		    TreeMap<String, Integer> stemmedWordList = new TreeMap<String, Integer>();
		    //Index map to be sent to main function
		    
		    //Map<String, Integer> indexMap = new HashMap<String, Integer>();
		    for (File file: cranfieldFiles.listFiles())
			{
				
				//read files recursively if path contains folder
				if(file.isDirectory())
				{
					parseFile(file, stopWords, stemming);
				}
				else
				{
					docId++;
					FileParser parser = new FileParser();
					if(stemming == true)
					{
						stemmedWordList = FileParser.readFile(file, stemming);
						buildIndex(stemmedWordList,stopWords, docId, stemming);
					}
					else
					{
					wordList = FileParser.readFile(file, stemming);
					buildIndex(wordList,stopWords, docId, stemming);
					
					}
				}
			}
		    
		    if(stemming == true)
		    {
		    	return stemmedIndexMap;
		    }
		    else
		    {
		    	return indexMap;
		    }
}		
}
