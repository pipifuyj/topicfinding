package termweighting;


import java.lang.String;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Hashtable;
import java.util.Iterator;

public class TFIDF {
	/**
	 * Assume that you have a corpora of 1000 documents and your task is to compute the
	 *  similarity between two given documents (or a document and a query). 
	 *  The following describes the steps of acquiring the similarity value:
	 *  Document pre-processing steps
	 *Step1: Tokenization:
	 * 				A document is treated as a string (or bag of words),
	 * 				and then partitioned into a list of tokens.
	 *Step2: Removing stop words: 
	 * 				Stop words are frequently occurring, insignificant words. 
	 * 				This step eliminates the stop words.
	 * 
	 *Step3: Stemming word: 
	 * 				This step is the process of conflating tokens to their root 
	 * 				form (connection -> connect). 
	 *Step4: Document representation
	 * 				We generate N-distinct words from the corpora and call them as 
	 * 				index terms (or the vocabulary). The document collection is then 
	 * 				represented as a N-dimensional vector in term space. 
	 *Step5: Computing Term weights
	 * 				Term Frequency.
	 * 				Inverse Document Frequency.
	 * 				Compute the TF-IDF weighting. 
	 *Step6: Measuring similarity between two documents
	 *				We capture the similarity of two documents using cosine similarity 
	 *				measurement. The cosine similarity is calculated by measuring the 
	 */
	
	public String[] _docs;
	public String[][] _ngramDoc;
	public int _numDocs=0;//�ĵ�����
	public int _numTerms=0;//�ĵ�������term���ܸ���
	public ArrayList _terms;//
	public int[][] _termFreq;//tf������Ϊdoc id������Ϊdoc��������Ϊterm id������Ϊterm����
	public double[][] _termWeight;//term weight����
	public int[] _maxTermFreq;//��� tf��������ʲô��
	public int[] _docFreq;//df���飬����Ϊterm����,��ʾÿһ��term�����ڶ��ٸ�doc��
	public double[][] _DocSim;
	
/**
*˽�г�Ա��Idictionary ʵ����һ��hashtable
*/    
	public Hashtable _wordsIndex=new Hashtable() ;
	//public IDictionary _wordsIndex=new Hashtable() ;
	
/**
*TFIDF���constructor
*���룺һ��doc������Ϊһ��string����Ԫ�أ��Ӷ���ɵ�string���飬����Ϊdoc ����
*/
	public TFIDF(String[] documents)
	{
		_docs=documents;//string���飬����Ϊ�ĵ�������doc�ܸ�����ÿһ������Ԫ�ش����Ÿ�doc������
		_numDocs=documents.length ;//���ĵ�����
/*		System.out.print("the number of docs: "+_numDocs+"\n");
		for(int i = 0; i <_numDocs; i ++){
			System.out.print("DOC "+i+" :"+_docs[i]+"\n");
		}*/
		MyInit();
	}

	public void GeneratNgramText()
	{
		
	}
	
	
/**
 * ɨ���ĵ�������ÿһƪ�ĵ������ݣ�Ҫ���������룺string[] docs�ĺ��壬string�ۣ�
 *����ĳ���Ϊdoc ������string��i�������ÿһ��Ԫ��Ϊstring���ͣ�������Ϊ��i���ĵ���
 *���ݣ�ɨ����ϾͲ���һ��term�б���term�б��������ĵ����ϵĴʵ䣬���ұ�֤���໥����ͬ
 */
	public ArrayList GenerateTerms(String[] docs)
	{
		ArrayList uniques=new ArrayList() ;
		_ngramDoc=new String[_numDocs][] ;
		for (int i=0; i < docs.length ; i++)
		{
		    //ʹ��һ�� tokenizer���󣬸ö����а�doc����string��i�ݷ����һ��һ��term�ĺ���
			StringTokenizer tokenizer=new StringTokenizer( docs[i] );

			//System.out.print(tokenizer.countTokens()+"\n");
			String[] words = new String [tokenizer.countTokens()];
		
			for( int j = 0; tokenizer.hasMoreTokens(); j++){
				words[j]=tokenizer.nextToken();
				//System.out.print(words[j]+" ");
			}
			//System.out.print("\n");
			
			//��ÿһ��doc��term���ϣ������һ��һ���໥����ͬ��term��������ArrayList�������֮
			for (int j=0; j < words.length ; j++)
				if (!uniques.contains(words[j]) )				
					uniques.add(words[j]) ;
							
		}
		return uniques;
	}
	

	public static Object AddElement(Hashtable collection, Object key, Object newValue)
//	public static Object AddElement(IDictionary collection, object key, object newValue)
	{
		Object element=collection.get(key);
		collection.put(key,newValue);
		return element;
	}
	
/**
*��һ��term�����term��index
*/
	public int GetTermIndex(String term)
	{
		Object index= _wordsIndex.get(term);
		//Object index=_wordsIndex[term];
		if (index == null) return -1;
		return ((Integer) index).intValue();
		//return index;
	}

	public void MyInit()
	{
		_terms=GenerateTerms(_docs );//ɨ�������ĵ��������һ��term�ʵ䣬��Arraylist���棬��֤term�໥��ͬ
		_numTerms=_terms.size() ;//��������ļ����ϵ�term��Ŀ

		_maxTermFreq=new int[_numDocs] ;//����ÿһ���ĵ��е����tf
		_docFreq=new int[_numTerms] ;//��¼df��ÿһ��term�ڶ��ٸ�doc����֣������飬����Ϊterm����
		_termFreq =new int[_numTerms][] ;//��¼tf(һ��term��һ��doc��ĳ��ִ���)����ά���飬�д���term id���д���doc id
		_termWeight=new double[_numTerms][] ;//��¼term��Ȩ�أ���ά���飬�д���term id���д���doc id
		_DocSim=new double[_numDocs][_numDocs];
		
		for(int i=0; i < _terms.size(); i++)//��term weight ��tf����仯Ϊ��ά��ʵ�壬����ռ�
		{
			_termWeight[i]=new double[_numDocs] ;
			_termFreq[i]=new int[_numDocs] ;

			AddElement(_wordsIndex, (_terms.toArray())[i], i);			
		}
		
		GenerateTermFrequency ();//��ÿһ��doc��ĸ���term��TF
		GenerateTermWeight();		//��ÿһ��doc��ĸ���Term Weight	
		ComputeDocToDocSimilarity();	
	}
	
/**
*����math��ʵ����log����
*/
	public double Log(double num)
	{
		return (double) Math.log(num) ;//log2
	}

	
	
	
/**
* ��ÿһdoc���term��TF
*/
	public void GenerateTermFrequency()
	{
		for(int i=0; i < _numDocs  ; i++)
		{								
			String curDoc=_docs[i];//��doc[i]���ĵ����ݸ��Ƶ���ǰ�ĵ�curDoc��
			
			Hashtable freq= GetWordFrequency(curDoc);// GetWordFrequency�������ص�hashtable������ĳһ���ĵ���һ�ԶԵ�(String:term, int:tf)
			//IDictionary freq= GetWordFrequency(curDoc);//��ǰ��i���ĵ�curDoc��tf
			
			//IDictionaryEnumerator enums=freq.GetEnumerator() ;�����Hashtable�����������һ�ַ���
			_maxTermFreq[i]=Integer.MIN_VALUE ;
			
			for  (Iterator it =freq.keySet().iterator(); it.hasNext(); ){
				String word =(String)it.next();   
				int wordFreq =((Integer)freq.get(word)).intValue();
				int termIndex=GetTermIndex(word);
				_termFreq [termIndex][i]=wordFreq;//��������� wordfreq��д�뵽��¼���飺_termFreq
				_docFreq[termIndex] ++;//��Ϊterm��doc i�г��ֹ�������docFreqҲӦ�ã���
				if (wordFreq > _maxTermFreq[i]) _maxTermFreq[i]=wordFreq;
			}
			
			/*
			while (enums.MoveNext())
			{
				String word=(String)enums.Key;
				int wordFreq=(int)enums.Value ;
				int termIndex=GetTermIndex(word);

				_termFreq [termIndex][i]=wordFreq;
				_docFreq[termIndex] ++;

				if (wordFreq > _maxTermFreq[i]) _maxTermFreq[i]=wordFreq;					
			}
			*/
			
		}
	}
	

	public void GenerateTermWeight()
	{			
		for(int i=0; i < _numTerms   ; i++)
		{
			for(int j=0; j < _numDocs ; j++){				
				_termWeight[i][j]=ComputeTermWeight (i, j);	
				//System.out.print("�ĵ�"+j+"��term"+i+"��Ȩ��:"+_termWeight[i][j]+"\n");
			}		
		}
	}

	public double GetTermFrequency(int term, int doc)
	{			
		int freq=_termFreq [term][doc];
		int maxfreq=_maxTermFreq[doc];	
		return Math.log(1+freq);
		//return (double)(Log((double)(Log((double)freq)+1.0))+1.0);//�ο�ppt�����³��ù�ʽ
//		return ( (double) freq/(double)maxfreq );//�ο��α���һ��
	}

	public double GetInverseDocumentFrequency(int term)
	{
		double df=_docFreq[term];
		double numdoc = _numDocs+1;
		//return (floatMath.log((double) (_numDocs) / (double) df);
//		return Log(numdoc/df );
		return Math.log((double)numdoc/df+1);
	}

	public double ComputeTermWeight(int term, int doc)
	{
		double tf= GetTermFrequency (term, doc);
		double idf=GetInverseDocumentFrequency(term);
		//System.out.print("��ǰ����doc "+doc+" term "+term+"��Ȩ��:"+tf+"*"+idf+"\n");
		return tf * idf;
	}
	
	public  double[] GetTermVector(int doc)
	{
		double[] w=new double[_numTerms] ;
		for (int i=0; i < _numTerms; i++)											
			w[i]=_termWeight[i][doc];
		
			
		return w;
	}
/**
* ���������ĵ�֮������ƶȣ������ĵ��������ڻ���������ͽ����
* ���룺�����ĵ�id
* ����������ĵ����ƶ�ֵ
*/
	public double GetSimilarity(int doc_i, int doc_j)
	{
		double[] vector1=GetTermVector (doc_i);
		double[] vector2=GetTermVector (doc_j);

		return TermVector.ComputeCosineSimilarity(vector1, vector2) ;

	}


//����һ���ĵ������ݣ�����һ���ĵ���		
	public Hashtable GetWordFrequency(String input)
	//public IDictionary GetWordFrequency(String input)
	{
		
		//String convertedInput=input.ToLower() ;//��input�е�doc�����ݣ�ȫ��ת��ΪСд��ĸ
		
		StringTokenizer tokenizer=new StringTokenizer(input);
		//Tokenizer tokenizer=new Tokenizer() ; //��tokenizer�ཨ��tokenizer����
		
		
		//String[] words=tokenizer.Partition(convertedInput);//ʹ�øö������Դ��ĳ�Ա������input�е��ĵ����ݣ������һ��һ��term��������string����words��	����֮		
		//��һ��ʹ��һ�����۵ķ������
		String[] words = new String [tokenizer.countTokens()];
		
		for( int j = 0; tokenizer.hasMoreTokens(); j++){
			words[j]=tokenizer.nextToken();
			//System.out.print(words[j]+" ");
		}
				
		Arrays.sort(words);//�Ե�term����words�ۣݽ�������ע��words�ۣ������ﻹ�����������ظ���term
		
		String[] distinctWords=GetDistinctWords(words);//ȥ��words�ۣݵ��ظ�term��ʹ������term֮���໥��ͬ��������words���飬�õ�һ��doc��term�б�
		
		Hashtable result = new Hashtable();
		//IDictionary result=new Hashtable();
		for (int i=0; i < distinctWords.length; i++)//ɨ��doc��distinct term�б�����ÿһ��term�ڸ��ĵ��е�tf
		{
			Object tmp;
			tmp=CountWords(distinctWords[i], words);//string words�ۣ���һ��doc�����дʻ㣨�����ظ��ģ��б�
			result.put(distinctWords[i], tmp);
			//result[distinctWords[i]]=tmp;//��ϣ����¼��term��doc�еĸ���tf��ʵ�ʻ���һ������				
		}			
		return result;//����һ���ĵ��ĸ���term��tf
	}				

/**
*����ĵ����ϵ����дʻ�֮��Ҫ��ȡ���ʵ䣬Ҳ�����ҳ�������ͬ�Ĵʻ㣬������string[]������
* �������룺�ĵ����ϵ����дʻ㣬�����ö��ظ��Ĵʻ㣬input[]��ÿһ��Ԫ�ر�ʾdoc��һ��term�����鳤��Ϊdoc��term�ܸ���
* ��������� �޳���ͬ�ʻ�֮����ֵ䣬������string�ۣ����������֮���໥��ͬ
*/				
	public String[] GetDistinctWords(String[] input)
	{				
		if (input == null)			
			return new String[0];			
		else
		{
			ArrayList list=new ArrayList() ;
			
			for (int i=0; i < input.length; i++)
				if (!list.contains(input[i])) //�ж�term input��i����list����û�г��֣�����Ѿ��ظ����֣����ü���list���Ӷ��޳��ظ��ʻ�				
					list.add(input[i]);
			String[] DistinctWords = new String[list.size()];
			list.toArray(DistinctWords);
			
			return DistinctWords; 
			//return Tokenizer.ArrayListToArray(list) ; ����һ��doc��distinct Term�б�ȷ��term����Ψһ�Ĳ��ظ���
		}
	}
/**
*����һ��term����words[],��������ﺬ�ж��ٸ�ĳһ���ض���term��ע�⣺words�ۣ�������ÿһ��Ԫ�أ�һ��term
*/		
	public int CountWords(String word, String[] words)
	{
		int itemIdx=Arrays.binarySearch(words, word);//ʹ�ö��ֲ��ҷ��ҵ����ض�word��λ��
		
		if (itemIdx > 0)//�����ң��ҵ���С��index�ĸ��ض�term�ĳ���λ��
			while (itemIdx > 0 && words[itemIdx].equals(word))				
				itemIdx--;				
					
		int count=0;
		while (itemIdx < words.length && itemIdx >= 0)//��ǰ��������β����¼��һ��term�ж��ٸ�
		{
			if (words[itemIdx].equals(word)) count++;								
			itemIdx++;
			if (itemIdx < words.length)				
				if (!words[itemIdx].equals(word)) break;								
		}
		
		return count;//����term������ĳ�ض�term���ܸ���
	}				

	public void ComputeDocToDocSimilarity()
	{
		for(int i = 0; i < _numDocs; i ++){
			for(int j = 0; j < _numDocs; j ++){
				_DocSim[i][j]=GetSimilarity(i,j);
			}
		}
	}
	
	public static void main(String[] args) {
		String[] DOCs = {
				"2006 ���籭 �¹� ���� ���籭  �ھ� �����",
				"2002 ���籭  ���� �ձ� ���� �ھ�  ����",
				"2010 ���籭 �Ϸ� ����"
				};
		TFIDF test = new TFIDF(DOCs);
		test.MyInit();
		
		System.out.print("�ĵ����ƶ�:\n");
		for(int i =0; i <test._numDocs; i ++){
			for(int j=0; j < test._numDocs; j ++){
				System.out.print(
						"DOC"+i+" VS DOC"+j+":"+
						test.GetSimilarity(i,j)+"   "
				);
			}
			System.out.print("\n");
		}
	}
}
