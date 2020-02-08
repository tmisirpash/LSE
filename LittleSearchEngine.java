package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	// DELETE LATER
//	public void printerMethod()
//	{
//		for (String key : keywordsIndex.keySet())
//		{
//			System.out.println("WORD: " + key);
//			for (Occurrence o : keywordsIndex.get(key))
//			{
//				System.out.println("FILE NAME: " + o.document);
//				System.out.println("# OF OCCURRENCES : " + o.frequency);
//			}
//			System.out.println();
//		}
//	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException 
	{
		/** COMPLETE THIS METHOD **/
		HashMap<String,Occurrence> map = new HashMap<String, Occurrence>();
		Scanner sc = new Scanner(new File(docFile));
		sc = new Scanner(new File(docFile));
		while (sc.hasNext()) 
		{
			String [] line = sc.next().toString().split("\\s+");
			for (String x : line)
			{
					String keyword = getKeyword(x);
					if (keyword != null)
					{
						if (map.containsKey(keyword))
						{
							int f = map.get(keyword).frequency;
							map.put(keyword, new Occurrence(docFile, f+1));
						}
						else
						{
							map.put(keyword, new Occurrence(docFile, 1));
						}
					}
			}
		}
		sc.close();
		return map;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) 
	{
		/** COMPLETE THIS METHOD **/
		for (String key : kws.keySet())
		{
			if (!keywordsIndex.containsKey(key))
			{
				ArrayList<Occurrence> arr = new ArrayList<Occurrence>();
				arr.add(kws.get(key));
				keywordsIndex.put(key, arr);
			}
			else
			{
				ArrayList<Occurrence> arr = keywordsIndex.get(key);
				arr.add(kws.get(key));
				insertLastOccurrence(arr);
				keywordsIndex.put(key, arr);
			}
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) 
	{
		/** COMPLETE THIS METHOD **/
		word = word.toLowerCase();
		// If the first character of the word is not an alphabetical letter
		if (!noiseWords.contains(word.substring(0,1)))
		{
			return null;
		}
		else
		{
			
			String temp = word.substring(0, 1);
			for (int i = 1; i < word.length(); i++)
			{
				//If the given character is not an alphabetical letter or one of the allowed punctuation marks
				if (!noiseWords.contains(word.substring(i,i+1)) && !isLegitPunctuation(word.substring(i,i+1)))
				{
					return null;
				}
				// If the given character is an alphabetical letter
				else if (noiseWords.contains(word.substring(i,i+1)))
				{
					// If the previous character was not an alphabetical letter
					if (!noiseWords.contains(word.substring(i-1, i)))
					{
						return null;
					}
					else
					{
						// The alphabetical letter is added to the end of temp
						temp += word.substring(i,i+1);
					}
				}
			}
			if (noiseWords.contains(temp))
			{
				return null;
			}
			return temp;
		}
	}
	private boolean isLegitPunctuation(String character)
	{
		return (character.contentEquals(".") || character.contentEquals(",") || character.contentEquals("?") || character.contentEquals(":")  || character.contentEquals(";") || character.contentEquals("!"));
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) 
	{
		/** COMPLETE THIS METHOD **/
		if (occs.size() == 0 || occs.size() == 1)
		{
			return null;
		}
		ArrayList<Integer> output = new ArrayList<Integer>();
		int l = 0;
		int r = occs.size()-2;
		int m = (l+r)/2;
		while (l <= r)
		{
			m = (l+r)/2;
			output.add(m);
			
			if (occs.get(m).frequency < occs.get(occs.size()-1).frequency)
			{
				r = m - 1;
			}
			else if (occs.get(m).frequency > occs.get(occs.size()-1).frequency)
			{
				l = m + 1;
				if (r <= m)
				{
					m++;
				}
			}
			else
			{
				break;
			}
		}
		occs.add(m, occs.get(occs.size()-1));
		occs.remove(occs.size()-1);
		return output;
	}
//	//DELETE LATEr
//	public ArrayList<Integer> tempSorter(ArrayList<Integer> arr)
//	{
//		for (Integer i : arr)
//		{
//			System.out.print(i + ", ");
//		}
//		System.out.println();
//		ArrayList<Integer> output = new ArrayList<Integer>();
//		int l = 0;
//		int r = arr.size()-2;
//		int m = (l + r)/2;
//		while (l <= r)
//		{
//			m = (l + r)/2;
//			output.add(m);
//			if (arr.get(m) == arr.get(arr.size()-1))
//			{
//				break;
//			}
//			if (arr.get(m) < arr.get(arr.size()-1))
//			{
//				r = m-1;
//			}
//			else if (arr.get(m) > arr.get(arr.size()-1))
//			{
//				l = m+1;
//				if (r <= m)
//				{
//					m++;
//				}
//			}
//		}
//		arr.add(m, arr.get(arr.size()-1));
//		arr.remove(arr.size()-1);
//		for (Integer i : arr)
//		{
//			System.out.print(i + ", ");
//		}
//		System.out.println();
//		return output;
//	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
//	//DELETE LATER
//	public void tempWordTest(String noiseWordsFile) throws FileNotFoundException
//	{
//		Scanner sc = new Scanner(new File(noiseWordsFile));
//		while (sc.hasNext()) {
//			String word = sc.next();
//			noiseWords.add(word);
//		}
//		sc.close();
//	}
//	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) 
	{
		/** COMPLETE THIS METHOD **/
		ArrayList<Occurrence> kw1Occs = new ArrayList<Occurrence>();
		ArrayList<Occurrence> kw2Occs = new ArrayList<Occurrence>();
		ArrayList<String> top5Occs = new ArrayList<String>();
		if (keywordsIndex.containsKey(kw1))
		{
			for (Occurrence o : keywordsIndex.get(kw1)) 
			{
				kw1Occs.add(o);
			}
		}
		if (keywordsIndex.containsKey(kw2))
		{
			for (Occurrence o : keywordsIndex.get(kw2)) 
			{
				kw2Occs.add(o);
			}
		}
		if (kw1Occs.isEmpty() && kw2Occs.isEmpty())
		{
			return null;
		}
		else if (kw1Occs.isEmpty())
		{
			if (kw2Occs.size() <= 5)
			{
				while (!kw2Occs.isEmpty())
				{
					top5Occs.add(kw2Occs.get(0).document);
					kw2Occs.remove(0);
				}
			}
			else
			{
				while (top5Occs.size() != 5)
				{
					top5Occs.add(kw2Occs.get(0).document);
					kw2Occs.remove(0);
				}
				return top5Occs;
			}
		}
		else if (kw2Occs.isEmpty())
		{
			if (kw1Occs.size() <= 5)
			{
				while (!kw1Occs.isEmpty())
				{
					top5Occs.add(kw1Occs.get(0).document);
					kw1Occs.remove(0);
				}
			}
			else
			{
				while (top5Occs.size() != 5)
				{
					top5Occs.add(kw1Occs.get(0).document);
					kw1Occs.remove(0);
				}
				return top5Occs;
			}
		}
		while (top5Occs.size() != 5 && !kw1Occs.isEmpty() && !kw2Occs.isEmpty())
		{
			if (kw1Occs.get(0).frequency > kw2Occs.get(0).frequency)
			{
				if (!top5Occs.contains(kw1Occs.get(0).document))
				{
					top5Occs.add(kw1Occs.get(0).document);
				}
				kw1Occs.remove(0);
			}
			else if (kw1Occs.get(0).frequency < kw2Occs.get(0).frequency)
			{
				if (!top5Occs.contains(kw2Occs.get(0).document))
				{
					top5Occs.add(kw2Occs.get(0).document);
				}
				kw2Occs.remove(0);
			}
			else
			{
				if (!top5Occs.contains(kw1Occs.get(0).document))
				{
					top5Occs.add(kw1Occs.get(0).document);
				}
				kw1Occs.remove(0);
				
				if (top5Occs.size() != 5)
				{
					if (!top5Occs.contains(kw2Occs.get(0).document))
					{
						top5Occs.add(kw2Occs.get(0).document);
					}
					kw2Occs.remove(0);
				}
			}
			if (kw1Occs.isEmpty() && !kw2Occs.isEmpty())
			{
				if (kw2Occs.size() + top5Occs.size() <= 5)
				{
					while (!kw2Occs.isEmpty())
					{
						if (!top5Occs.contains(kw2Occs.get(0).document))
						{
							top5Occs.add(kw2Occs.get(0).document);
						}
						kw2Occs.remove(0);
					}
				}
				else
				{
					while (top5Occs.size() != 5)
					{
						if (!top5Occs.contains(kw2Occs.get(0).document))
						{
							top5Occs.add(kw2Occs.get(0).document);
						}
						kw2Occs.remove(0);
					}
				}
			}
			else if (!kw1Occs.isEmpty() && kw2Occs.isEmpty())
			{
				if (kw1Occs.size() + top5Occs.size() <= 5)
				{
					while (!kw1Occs.isEmpty())
					{
						if (!top5Occs.contains(kw1Occs.get(0).document))
						{
							top5Occs.add(kw1Occs.get(0).document);
						}
						kw1Occs.remove(0);
					}
				}
				else
				{
					while (top5Occs.size() != 5)
					{
						if (!top5Occs.contains(kw1Occs.get(0).document))
						{	
							top5Occs.add(kw1Occs.get(0).document);
						}
						kw1Occs.remove(0);
					}
				}
			}
		}
		return top5Occs;
	
	}
}
