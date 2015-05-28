import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import InformationRetrieval_hw2.Documents;


public class SizeHandler {

	public SizeHandler() {
		// TODO Auto-generated constructor stub
	}

	public static long getUncompressedIndexSize(Map<String, InformationRetrieval_hw2.IndexStorage> uncompressedIndex){
		long length = 0;
		for(String word : uncompressedIndex.keySet()){
			InformationRetrieval_hw2.IndexStorage temp = uncompressedIndex.get(word);
			//Find the length of the each field and docList size
			length += temp.word.length() + 2*Integer.SIZE + 8;
			//length += temp.word.length() + 2*Integer.SIZE;
			// list size * (size(docId) + size(tf))
			length += temp.docList.size() *2*Integer.SIZE;  
		}
		return length;
	}
	public static long getCompressedIndexSize( Map<String, IndexStorage> compressedIndex) {		
		long length = 0;
		for(String word : compressedIndex.keySet()){
			IndexStorage indexStorage = compressedIndex.get(word);
			// Length(word) + size(df) + size(tf) + size of pointer
			length += indexStorage.word.length() + indexStorage.docFrequency.length + indexStorage.termFrequency.length + 8;  
			length += getSizeOfCompressedPostingList(indexStorage.docList);
		}
		return length;
	}
	
	public static long getSizeOfCompressedPostingList(List<Documents> docList) {
		long length = 0;
		for(Documents doc : docList){
			length += doc.docID.length + doc.frequency.length;  
		}
		return length;
	}

	public static Map<String, IndexStorage> compressversion1Index(Map<String, InformationRetrieval_hw2.IndexStorage> indexStorage) {	
		Map<String,IndexStorage> deltaCompressedIndex = new HashMap<String, IndexStorage>();	
		for(String word : indexStorage.keySet()){
			InformationRetrieval_hw2.IndexStorage temp = indexStorage.get(word); 	
			List<Documents> docList = new ArrayList<>(temp.docList.size());
			int prevDocId = 0;	
			for(InformationRetrieval_hw2.Documents document: temp.docList){
				byte[] docId = deltaCode(document.docID - prevDocId);
				prevDocId = document.docID;
				byte[] frequency = deltaCode(document.frequency);
				docList.add(new Documents(docId, frequency));
			}	
			//Not sure if we need to compress document frequency and term frequency?
			byte[] docFrequency = deltaCode(temp.docFrequency);
			byte[] termFrequency = deltaCode(temp.termFrequency);
			IndexStorage compressedIndexStorage = new IndexStorage(word, docFrequency, termFrequency, docList);
			deltaCompressedIndex.put(word, compressedIndexStorage);
		}
		return deltaCompressedIndex;
	}
	
	public static Map<String, IndexStorage> compressversion2Index(Map<String, InformationRetrieval_hw2.IndexStorage> indexStorage) {	
		Map<String,IndexStorage> gamaCompressedIndex = new HashMap<String, IndexStorage>();	
		for(String word : indexStorage.keySet()){
			InformationRetrieval_hw2.IndexStorage temp = indexStorage.get(word); 	
			List<Documents> docList = new ArrayList<>(temp.docList.size());
			int prevDocId = 0;	
			for(InformationRetrieval_hw2.Documents document: temp.docList){
				byte[] docId = gammaCode(document.docID - prevDocId);
				prevDocId = document.docID;
				byte[] frequency = gammaCode(document.frequency);
				docList.add(new Documents(docId, frequency));
			}	
			//Not sure if we need to compress document frequency and term frequency?
			byte[] docFrequency = gammaCode(temp.docFrequency);
			byte[] termFrequency = gammaCode(temp.termFrequency);
			IndexStorage compressedIndexStorage = new IndexStorage(word, docFrequency, termFrequency, docList);
			gamaCompressedIndex.put(word, compressedIndexStorage);
		}
		return gamaCompressedIndex;
	}
	
	public static void frontEncode(DataOutputStream dos, List<String> words) throws IOException{
        String prefix = "";
        int i = 1;
        while(i < words.size()) {
            if (prefix.length() > 1) {
                int j = i + 1;
                while(j < words.size() && words.get(j).startsWith(prefix)) {
                    j++;
                }
                int lengthPrefix = prefix.length();
                dos.writeByte(words.get(i - 1).length());
                dos.writeBytes(words.get(i - 1).substring(0, lengthPrefix));
                dos.writeByte((byte)'*');
                dos.writeBytes(words.get(i - 1).substring(lengthPrefix));
                for (int k = i; k < j; k++) {
                    dos.writeByte(words.get(k).substring(lengthPrefix).length());
                    dos.writeByte((byte)'$');
                    dos.writeBytes(words.get(k).substring(lengthPrefix));
                }
                i = j + 1;
            } else {
                dos.writeByte(words.get(i - 1).length());
                dos.writeBytes(words.get(i - 1));
                i++;
            }
        }
        if (i == words.size()) {
            ((DataOutputStream) words).writeByte(words.get(i - 1).length());
            dos.writeBytes(words.get(i - 1));
        }
    }
	
public static void blockedCompression(DataOutputStream dos, List<String> words) throws IOException
{
	
    String prefix = "";
    int i = 1;
    while(i < words.size()) {
        if (prefix.length() > 1) {
            int j = i + 1;
            int k = 8;
            while(j < words.size() && words.get(j).startsWith(prefix) && prefix.length() % k ==0) {
                j++;
            }
            int lengthPrefix = prefix.length();
            dos.writeByte(words.get(i - 1).length());
            dos.writeBytes(words.get(i - 1).substring(0, lengthPrefix));
            dos.writeByte((byte)'*');
            dos.writeBytes(words.get(i - 1).substring(lengthPrefix));
            for (int x= i; k < j; k++) {
                dos.writeByte(words.get(k).substring(lengthPrefix).length());
                dos.writeByte((byte)'$');
                dos.writeBytes(words.get(k).substring(lengthPrefix));
            }
            i = j + 1;
        } else {
            dos.writeByte(words.get(i - 1).length());
            dos.writeBytes(words.get(i - 1));
            i++;
        }
    }
    if (i == words.size()) {
        ((DataOutputStream) words).writeByte(words.get(i - 1).length());
        dos.writeBytes(words.get(i - 1));
    }
}
    

	
	public static byte[] deltaCode(int number){
		String binaryRep = Integer.toBinaryString(number);
		String gammaCode = getGammaCode(binaryRep.length());
		String offset = binaryRep.substring(1);
		return convertToByte(gammaCode.concat(offset));
	}
	
	public static byte[] gammaCode(int number){
		String gammacode = getGammaCode(number);		
		return convertToByte(gammacode);
	}
		
	private static String getGammaCode(int number) {
		String binaryForm = Integer.toBinaryString(number);
		String offset = binaryForm.substring(1);	
		String unaryValue = getUnaryValue(offset.length());	
		String gammacode =  unaryValue.concat("0").concat(offset);
		return gammacode;
	}
	
	private static String getUnaryValue(int length) {
		String unaryValue="";
		for(int i=0;i<length;i++){
			unaryValue=unaryValue.concat("1");
		}
		return unaryValue;
	}
	private static byte[] convertToByte(String gammacode) {
		BitSet bitSet = new BitSet(gammacode.length());
		for(int i = 0; i < gammacode.length(); i ++){
			Boolean flag = gammacode.charAt(i) == '1' ? true : false;
			bitSet.set(i, flag);
		}
		return bitSet.toByteArray();
	}
	public static class IndexStorage implements  Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		String word;
		byte[] docFrequency;
		byte[] termFrequency;
		List<Documents> docList;
	
		public IndexStorage(String word, byte[] docFrequency, byte[] termFrequency, List<Documents> docList) {
			this.word = word;
			this.docFrequency = docFrequency;
			this.termFrequency = termFrequency;
			this.docList = docList;
		}
	
	}
	static class Documents implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		byte[] docID;
		byte[] frequency;
		
		public Documents(byte[] docID, byte[] frequency) {
			this.docID = docID;
			this.frequency = frequency;
		}
	}
}
