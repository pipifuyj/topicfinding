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
	public int _numDocs=0;//文档个数
	public int _numTerms=0;//文档集合中term的总个数
	public ArrayList _terms;//
	public int[][] _termFreq;//tf矩阵，列为doc id，长度为doc个数，行为term id，长度为term个数
	public double[][] _termWeight;//term weight矩阵，
	public int[] _maxTermFreq;//最大 tf？？？有什么用
	public int[] _docFreq;//df数组，长度为term个数,表示每一个term出现在多少个doc里
	public double[][] _DocSim;
	
/**
*私有成员：Idictionary 实质是一个hashtable
*/    
	public Hashtable _wordsIndex=new Hashtable() ;
	//public IDictionary _wordsIndex=new Hashtable() ;
	
/**
*TFIDF类的constructor
*输入：一个doc内容作为一个string数组元素，从而组成的string数组，长度为doc 个数
*/
	public TFIDF(String[] documents)
	{
		_docs=documents;//string数组，长度为文档集合中doc总个数，每一个数组元素储存着该doc的内容
		_numDocs=documents.length ;//求文档个数
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
 * 扫描文档集合中每一篇文档的内容，要深刻理解输入：string[] docs的含义，string［］
 *数组的长度为doc 个数，string［i］数组的每一个元素为string类型，其内容为第i个文档的
 *内容，扫描完毕就产生一个term列表，该term列表是整个文档集合的词典，而且保证了相互不相同
 */
	public ArrayList GenerateTerms(String[] docs)
	{
		ArrayList uniques=new ArrayList() ;
		_ngramDoc=new String[_numDocs][] ;
		for (int i=0; i < docs.length ; i++)
		{
		    //使用一个 tokenizer对象，该对象有把doc内容string［i］分离成一个一个term的函数
			StringTokenizer tokenizer=new StringTokenizer( docs[i] );

			//System.out.print(tokenizer.countTokens()+"\n");
			String[] words = new String [tokenizer.countTokens()];
		
			for( int j = 0; tokenizer.hasMoreTokens(); j++){
				words[j]=tokenizer.nextToken();
				//System.out.print(words[j]+" ");
			}
			//System.out.print("\n");
			
			//对每一个doc的term集合，分离成一个一个相互不相同的term，保存在ArrayList里，并返回之
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
*给一个term，求该term的index
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
		_terms=GenerateTerms(_docs );//扫描所有文档，分离出一个term词典，用Arraylist保存，保证term相互不同
		_numTerms=_terms.size() ;//获得整个文件集合的term数目

		_maxTermFreq=new int[_numDocs] ;//控制每一个文档中的最大tf
		_docFreq=new int[_numTerms] ;//记录df（每一个term在多少个doc里出现）的数组，长度为term个数
		_termFreq =new int[_numTerms][] ;//记录tf(一个term在一个doc里的出现次数)，二维数组，行代表term id，列代表doc id
		_termWeight=new double[_numTerms][] ;//记录term的权重，二维数组，行代表term id，列代表doc id
		_DocSim=new double[_numDocs][_numDocs];
		
		for(int i=0; i < _terms.size(); i++)//把term weight 和tf数组变化为二维的实体，分配空间
		{
			_termWeight[i]=new double[_numDocs] ;
			_termFreq[i]=new int[_numDocs] ;

			AddElement(_wordsIndex, (_terms.toArray())[i], i);			
		}
		
		GenerateTermFrequency ();//求每一个doc里的各个term的TF
		GenerateTermWeight();		//求每一个doc里的各个Term Weight	
		ComputeDocToDocSimilarity();	
	}
	
/**
*调用math库实现求log函数
*/
	public double Log(double num)
	{
		return (double) Math.log(num) ;//log2
	}

	
	
	
/**
* 求每一doc里的term的TF
*/
	public void GenerateTermFrequency()
	{
		for(int i=0; i < _numDocs  ; i++)
		{								
			String curDoc=_docs[i];//把doc[i]的文档内容复制到当前文档curDoc里
			
			Hashtable freq= GetWordFrequency(curDoc);// GetWordFrequency函数返回的hashtable，含有某一个文档的一对对的(String:term, int:tf)
			//IDictionary freq= GetWordFrequency(curDoc);//求当前第i个文档curDoc的tf
			
			//IDictionaryEnumerator enums=freq.GetEnumerator() ;想遍历Hashtable，下面改另外一种方法
			_maxTermFreq[i]=Integer.MIN_VALUE ;
			
			for  (Iterator it =freq.keySet().iterator(); it.hasNext(); ){
				String word =(String)it.next();   
				int wordFreq =((Integer)freq.get(word)).intValue();
				int termIndex=GetTermIndex(word);
				_termFreq [termIndex][i]=wordFreq;//在这里产生 wordfreq并写入到记录数组：_termFreq
				_docFreq[termIndex] ++;//因为term在doc i中出现过，所以docFreq也应该＋＋
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
				//System.out.print("文档"+j+"的term"+i+"的权重:"+_termWeight[i][j]+"\n");
			}		
		}
	}

	public double GetTermFrequency(int term, int doc)
	{			
		int freq=_termFreq [term][doc];
		int maxfreq=_maxTermFreq[doc];	
		return Math.log(1+freq);
		//return (double)(Log((double)(Log((double)freq)+1.0))+1.0);//参考ppt第三章常用公式
//		return ( (double) freq/(double)maxfreq );//参考课本归一化
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
		//System.out.print("当前计算doc "+doc+" term "+term+"的权重:"+tf+"*"+idf+"\n");
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
* 计算两个文档之间的相似度，两个文档向量作内积，返回求和结果。
* 输入：两个文档id
* 输出：两个文档相似度值
*/
	public double GetSimilarity(int doc_i, int doc_j)
	{
		double[] vector1=GetTermVector (doc_i);
		double[] vector2=GetTermVector (doc_j);

		return TermVector.ComputeCosineSimilarity(vector1, vector2) ;

	}


//输入一个文档的内容，计算一个文档的		
	public Hashtable GetWordFrequency(String input)
	//public IDictionary GetWordFrequency(String input)
	{
		
		//String convertedInput=input.ToLower() ;//把input中的doc的内容，全部转化为小写字母
		
		StringTokenizer tokenizer=new StringTokenizer(input);
		//Tokenizer tokenizer=new Tokenizer() ; //用tokenizer类建立tokenizer对象
		
		
		//String[] words=tokenizer.Partition(convertedInput);//使用该对象中自带的成员函数把input中的文档内容，分离出一个一个term，保存在string数组words里	返回之		
		//上一句使用一下曲折的方法解决
		String[] words = new String [tokenizer.countTokens()];
		
		for( int j = 0; tokenizer.hasMoreTokens(); j++){
			words[j]=tokenizer.nextToken();
			//System.out.print(words[j]+" ");
		}
				
		Arrays.sort(words);//对单term数组words［］进行排序，注意words［］数组里还包含许多可能重复的term
		
		String[] distinctWords=GetDistinctWords(words);//去除words［］的重复term，使得两两term之间相互不同，返回新words数组，得到一个doc的term列表
		
		Hashtable result = new Hashtable();
		//IDictionary result=new Hashtable();
		for (int i=0; i < distinctWords.length; i++)//扫描doc的distinct term列表，计算每一个term在该文档中的tf
		{
			Object tmp;
			tmp=CountWords(distinctWords[i], words);//string words［］是一个doc里所有词汇（包含重复的）列表
			result.put(distinctWords[i], tmp);
			//result[distinctWords[i]]=tmp;//哈希表：记录该term在doc中的个数tf，实质还是一个数组				
		}			
		return result;//返回一个文档的各个term的tf
	}				

/**
*获得文档集合的所有词汇之后，要提取出词典，也就是找出两两不同的词汇，保存在string[]数组里
* 函数输入：文档集合的所有词汇，包含好多重复的词汇，input[]的每一个元素表示doc里一个term，数组长度为doc的term总个数
* 函数输出： 剔除相同词汇之后的字典，保存在string［］数组里，两两之间相互不同
*/				
	public String[] GetDistinctWords(String[] input)
	{				
		if (input == null)			
			return new String[0];			
		else
		{
			ArrayList list=new ArrayList() ;
			
			for (int i=0; i < input.length; i++)
				if (!list.contains(input[i])) //判断term input［i］在list里有没有出现，如果已经重复出现，则不用加入list，从而剔除重复词汇				
					list.add(input[i]);
			String[] DistinctWords = new String[list.size()];
			list.toArray(DistinctWords);
			
			return DistinctWords; 
			//return Tokenizer.ArrayListToArray(list) ; 返回一个doc的distinct Term列表，确保term都是唯一的不重复的
		}
	}
/**
*输入一个term数组words[],求该数组里含有多少个某一个特定的term，注意：words［］数组中每一个元素＝一个term
*/		
	public int CountWords(String word, String[] words)
	{
		int itemIdx=Arrays.binarySearch(words, word);//使用二分查找法找到该特定word的位置
		
		if (itemIdx > 0)//往回找，找到最小的index的该特定term的出现位置
			while (itemIdx > 0 && words[itemIdx].equals(word))				
				itemIdx--;				
					
		int count=0;
		while (itemIdx < words.length && itemIdx >= 0)//往前搜索到结尾，记录该一共term有多少个
		{
			if (words[itemIdx].equals(word)) count++;								
			itemIdx++;
			if (itemIdx < words.length)				
				if (!words[itemIdx].equals(word)) break;								
		}
		
		return count;//返回term数组中某特定term的总个数
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
				"2006 世界杯 德国 举行 世界杯  冠军 意大利",
				"2002 世界杯  韩国 日本 举行 冠军  巴西",
				"2010 世界杯 南非 举行"
				};
		TFIDF test = new TFIDF(DOCs);
		test.MyInit();
		
		System.out.print("文档相似度:\n");
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
