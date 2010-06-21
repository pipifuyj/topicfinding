package nearest_neighbor_graph;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;

public class NearestNeighborGraph {
	/**
	 * top k nearest neighbor 
	 */
	private int k;
	/**
	 * 长度docNum*k,第i行表示doc i的top k nearest neighbor
	 */
	private int[][] nearest_neighbor_list;
	/**
	 * 使用一个大小为n＊n的矩阵来表示 shared neighbor graph， 如果shared_neighbor_graph[i][j]＝0，表明doc i和
	 * doc j之间没有link，更不存在weighted link strength。如果 shared neighbor graph[i][j] ＝ double number 
	 * > 0，表明doc i和doc j之间的weighted link strength 为此double数。
	 */
	private double[][] shared_neighbor_graph;
	/**
	 * 利用termweighting.TFIDF类得到的文档相似度矩阵
	 */
	private double[][] similarity;
	/**
	 * 文档集合中的总doc数目
	 */
	private int docNum;
	/**
	 * 使用一个大小为docNum＊docNum的boolean类型矩阵nearest_neighbor_graph[][]来表
	 * 示nearest neighbor graph， nearest_neighbor_graph[i][j]＝true表示doc i和
	 * doc j之间有link
	 */
	private boolean[][] nearest_neighbor_graph;
	/**
	 * 用来记录某一个doc含有多少个strong link，doc_StrongLink_num[i]表示第i个doc的
	 * strong link的数目
	 */
	private int[] doc_StrongLink_num;
	/**
	 * 经验值，当weighted link strength> strong_link_threshold,
	 * 我们认为该link是strong link
	 */
	private int strong_link_threshold;
	/**
	 * 经验值，当doc_StrongLink_num > topic_threshold,认为该doc为representative
	 */
	private int topic_threshold;
	/**
	 * 经验值，当doc_StrongLink_num < noisy_threshold，认为该doc为noisy
	 */ 
	private int noisy_threshold;
	/**
	 * 经验值，当weighted link strength > merged_theshold,认为两doc可以merge到一类
	 */
	private double merged_threshold;
	/**
	 * 经验值，处理介于noisy和topic之间的term的判断阈值
	 */
	private double labeling_threshold;
	/**
	 * 使用一个k*k的矩阵cluster[k][k]来表示文本聚类结果，如果 cluster[i][j]=cluster[j][i]=1,说明doc i和
     *doc j在同一个cluster里，一个cluster实质是一个无向连同图。
	 */
	private boolean[][]  cluster;
	/**
	 * node_classification表示该文档的分类：representative, noisy, 
	 * between_noisy_and_represent；分别对应正数：0，1，2
	 */
	private int[] node_classification;
	/**
	 * 
	 */
	private ArrayList ClusterList;
	/**
	 * @function 构造函数，当用户不输入任何参数，默认为所有参数默认为0
	 * @author fuyanjie
	 */
	public NearestNeighborGraph()
	{
		k = 0;
		docNum = 0;	
		similarity = null;
		nearest_neighbor_list = null;	
		nearest_neighbor_graph = null;
		doc_StrongLink_num = null;
		ClusterList = new ArrayList();
	}
	
	/**
	 * @function 构造函数
	 * @param doc_similarity：文本集合中的doc之间的相似度矩阵
	 * @param top_k：表示nearest neighbor list的长度，告诉系统求top多少个最相似的文档
	 * @author fuyanjie 2008.11.20
	 */
	public  NearestNeighborGraph(double[][] doc_similarity, int top_k)
	{
		k = top_k;
		docNum = doc_similarity.length;	
		similarity = new double [docNum][docNum];
		for(int i= 0; i< docNum; i++){
			for(int j= 0; j < docNum; j++){
				similarity[i][j] = doc_similarity[i][j];
			}
		}		
		nearest_neighbor_list = new int [docNum][k];
		for(int i= 0; i< docNum; i ++){
			for(int j= 0; j< k; j ++){
				nearest_neighbor_list[i][j] = 0;
			}
		}	
		nearest_neighbor_graph = new boolean [docNum][docNum];
		for(int i= 0; i< docNum; i ++){
			for(int j= 0; j< docNum; j ++){
				nearest_neighbor_graph[i][j] = false;
			}
		}	
		shared_neighbor_graph = new double[docNum][docNum];
		for(int i= 0; i< docNum; i ++){
			for(int j= 0; j< docNum; j ++){
				shared_neighbor_graph[i][j] = 0;
			}
		}	
		doc_StrongLink_num = new int [docNum];
		for(int i= 0; i< docNum; i ++){
			doc_StrongLink_num[i] = 0;
		}
		node_classification = new int [docNum];
		for(int i= 0; i< docNum; i ++){
			node_classification[i] = -1;
		}
		cluster = new boolean[docNum][docNum];
		for(int i= 0; i< docNum; i ++){
			for(int j= 0; j< docNum; j ++){
				cluster[i][j] = false;
			}
		}
		
		ClusterList = new ArrayList();
	}
	/**
	 * @function 求每一个文档的Top K Nearest Neighbor
	 * @param Double[k][k] DocSimilarity   文档－文档相似度矩阵
	 * @return Integer[k][n] nearest_neighbor_list 
	 * @存在问题 如果存在2个文档(doc1 doc2)和文档A非常相似，当四舍五入时导致两个文档相似度相等，
	 * 那么对相似度排序无法起到区分doc1和doc2作用，出现一个nearest neighbor list里同时出现两个
	 * 相同的文本id
	 * @author fuyanjie 2008.11.20
	 */
	public void ComputeTopKNearestNeighborList()
	{
		for(int i= 0; i< docNum; i++){
			double[] DocSim1 = new double[docNum];//第i个文档和其他文档的相似度单独抽取出来
			ArrayList DocSim2 = new ArrayList (); 
			for(int j = 0; j < docNum; j++){
				DocSim1[j] = similarity[i][j];
				DocSim2.add(j,similarity[i][j]);
			}
			Arrays.sort(DocSim1);//从小到大排列,所以下面要从大到小选择
			for(int j =0; j< k; j++){
				nearest_neighbor_list[i][j] 
				                         = 
					DocSim2.indexOf(DocSim1[docNum-1-j]);			
			}					
		}
	}
	/**
	 *@function 创建nearest neighbor graph
	 *@paramInteger[docNum][NeighborListLength] nearest_neighbor_list    文档-top n nearest neighbor矩阵
	 *@return  Boolean[docNum][docNum] nearest_neighbor_graph
	 *@author fuyanjie 2008.11.20
	 */
	public void CreateNearestNeighborGraph()//得到一个对称矩阵nearest_neighbor_graph
	{
		for(int i = 0; i < docNum; i ++){
			for(int j = 0; j < docNum; j ++){
				 if (IsComtainEachOther(nearest_neighbor_list, i,j) == true){
					 nearest_neighbor_graph[i][j]=true;
				 }
				 else{
					 nearest_neighbor_graph[i][j]=false;
				 }				 
			}
		}  
	}
	/**
	 * @function 判断doc1和doc2的nearest neighbor list是否相互包含
	 * @param nearest_neighbor_list 
	 * @param doc1 doc1的ID
	 * @param doc2 doc2的ID
	 * @return 两个list是否相互包含对方的boolean结果
	 * @author fuyanjie 2008.11.20
	 */
	public boolean IsComtainEachOther(int[][] nearest_neighbor_list, int doc1, int doc2)
	{
		ArrayList NeighborList1 = new ArrayList();
		ArrayList NeighborList2 = new ArrayList();
		for(int i = 0; i < k; i ++){
			NeighborList1.add(nearest_neighbor_list[doc1][i]);
			NeighborList2.add(nearest_neighbor_list[doc2][i]);
		}
		if(NeighborList2.contains(doc1)
				&&
				NeighborList1.contains(doc2)){			
			return true;	
		}
		else{
			return false;
		}	
	}
	/**
	 *@function 创建 Shared Neighbor Graph(文档weighted link strength图)
	 *@param Integer[docNum][ListLength] nearest_neighbor_list    文档-top n nearest neighbor矩阵
     *@param Boolean[docNum][ListLength] nearest_neighbor_graph	  文档无加权link图
	 *@return Double[docNum][ListLength] shared neighbor graph 
	 *@author fuyanjie 2008.11.20   
	 */
	public void CreateSharedNeighborGraph()
	{
		for(int i = 0; i < docNum; i ++){
			for(int j = 0; j < docNum; j ++){
				if(nearest_neighbor_graph[i][j] == true){
					shared_neighbor_graph[i][j] = 
						ComputeWeightedLinkStrength(nearest_neighbor_list, i, j);
				}
				else{
					shared_neighbor_graph[i][j] = 0;
				} 			
			}
		}					
	 }
	/**
	 * @function 国家图书馆小彭提出的计算doc1和doc2之间的加权 Link Strength的方法
	 * @param nearest_neighbor_list
	 * @param doc1
	 * @param doc2
	 * @return Weighted Link Strength
	 * @author fuyanjie 2008.11.20
	 */
	public double ComputeWeightedLinkStrength(int[][] nearest_neighbor_list, int doc1, int doc2)
	{
		double WeightedLinkStrength = 0;
		for(int i = 0; i < k; i ++){
			for(int j = 0; j < k; j ++){
				if(nearest_neighbor_list[doc1][i] 
				                               == 
				                            	   nearest_neighbor_list[doc2][j])
				{
					WeightedLinkStrength = WeightedLinkStrength + Math.exp(-Math.sqrt(i+j)/4);
						//WeightedLinkStrength + (1-(double)(i+j)/(double)(2*(k-1)));
				}
			}
		}
		return WeightedLinkStrength;
	}
	/**
	 *@function 在给定strong link threshold的前提下计算一个文档的Strong Link的个数
	 *@param Integer[docNum] strong_link
	 *@return Integer[docNum] doc_StrongLink_num
	 *@author fuyanjie 2008.11.20
	 */
	public void ComputeDocStrongLinkNum()
	{
//		strong_link_threshold = 2;//人为指定strong link threshold
		for(int i = 0; i < docNum; i ++){
			for(int j = 0; j < docNum; j ++){
				if(shared_neighbor_graph[i][j] > strong_link_threshold){
					doc_StrongLink_num[i]++;
				}
			}	
		}
	}
	/**
	 *@function 在给定topic threshold noisy threshold下寻找代表点
	 *@param Integer[k] strong_link
	 *@return Integer[k] node_classification 分好类的点
	 *@author fuyanjie 2008.11.20
	 */
	public void FindRepresentNode()
	{
//		topic_threshold = 3;
//		noisy_threshold = 1;
		for(int i = 0; i < docNum; i ++){
			if (doc_StrongLink_num[i] >= topic_threshold){
				node_classification[i]=0;
			}
			else if(doc_StrongLink_num[i] <= noisy_threshold){
				node_classification[i]=1;
			}
			else{
				node_classification[i]=2;
			//betwen represent and noisy, do what?	
			}
		}
	}
	/**
	 * @function 对document作clustering，使用一个k*k的矩阵cluster[k][k]来表示
	 * 文本聚类结果
	 * 如果 cluster[i][j]=cluster[j][i]=1,说明doc i和doc j在同一个cluster里
	 * @author fuyanjie 2008.11.20
	 */
	public void DocumentClustering()
	{
//		merged_threshold = 3;
		for(int i= 0; i< docNum; i ++){
			for(int j = 0; j < docNum; j ++){
				if(shared_neighbor_graph[i][j] > merged_threshold){
					if (
							node_classification[i] == 0 //tpic node
							|| node_classification[j] == 0) //topic node
					{
						cluster[i][j] = true;
					}
				}
			}
		}
	}
	/*
	input：boolean[k][k] cluster
	output：boolean[k][k] cluster
	*/
	public void ReClustering(){
		//labeling_threshold = 2;
		double strength=0; //记录目前最大的link strength
		int point; //记录目前strength最大的j值
		for(int i = 0; i < k; i ++){
			if (getSum(i) == 1)  //说明doc[i]没有被分类
		     for(int j=i+1; j < k; j ++){ //避免了孤立点（即在上一步没有被分类的doc）的传递性
		    	 if(getSum(j) > 1){  //只有doc[j]已经在第七步上被分类了，才计算doc[i]是否属于doc[j]所在的类
		            if (shared_neighbor_graph[i][j]>labeling_threshold)
		                if (strength < shared_neighbor_graph[i][j]){
		                	strength= shared_neighbor_graph[i][j];
		                	point=j; 	       		     
		                }
		            if (strength == 0){
				    	//doc[i] miss 
				    }     
				    else{
				    	cluster[i][j]=true;
				        cluster[j][i]=true;
				        strength = 0;
				    }
		    	 }
		     }
		         
		   
		          
		}
	}
	/**
	 * @函数作用：计算cluster里的第row行的数值和
	 * @param row 表示用户求cluster矩阵里的第row行的数值和
	 * @return cluster[][]第row行的数值和
	 */
	public int getSum(int row){
		int sum = 0;
		for(int i = 0; i < cluster.length; i ++){
			if(cluster[row][i] == true){
				sum = sum + 1;
			}			
		}
		return sum;
	}
/**
 */
	public void DFSTraverse(){
		boolean[] visited = new boolean [docNum];
		for ( int v = 0; v < docNum; ++v ){
			visited[v] = false;
		}
		for ( int v = 0; v < docNum; ++v ){
			if ( !visited[v] ){
				ArrayList newcluster = new ArrayList();
				ClusterList.add(newcluster);
				DFS(v, visited, ClusterList);	
			}
		}		
	}
	
	public void DFS(int v, boolean[] visited, ArrayList ClusterList){
		visited[v] = true;
		((ArrayList)ClusterList.get(ClusterList.size()-1)).add(v);
		for ( int w = 0; w < docNum; w ++){
			if ( cluster[v][w] == true && !visited[w] ){
				DFS(w,visited,ClusterList);
			}
		}	
	}
	
	/**
	 * @function 调用各个子函数，完成算法的计算
	 * @author fuyanjie 2008.11.20
	 */
	public void MyStartAlgorithm()
	{
		ComputeTopKNearestNeighborList();
/*		System.out.print("Nearest Neighbor List\n");
	    for(int i = 0; i < docNum; i ++){
	    	for(int j = 0; j < k; j ++){
				System.out.print(nearest_neighbor_list[i][j]+" ");
			}	
	    	System.out.print("\n");
	    }
	    
*/		CreateNearestNeighborGraph();
/*		System.out.print("Nearest Neighbor Graph \n");
		for(int i = 0; i < docNum; i ++){
			for(int j = 0; j < docNum; j ++){
				System.out.print(nearest_neighbor_graph[i][j]+" ");
			}
			System.out.print("\n");
		}
		
*/		CreateSharedNeighborGraph();
		ScaleOfWeight();			// determine strong_link_threshold and labeling and merged
/*		System.out.print("Weighted Shared Neighbor Graph \n");
		for(int i = 0; i < docNum; i ++){
			for(int j = 0; j < docNum; j ++){
				System.out.print(shared_neighbor_graph[i][j]+" ");
			}
			System.out.print("\n");
		}
		
*/		ComputeDocStrongLinkNum();
		ScaleOfStrongLinks();
/*		System.out.print("每一个文档的StrongLink个数\n");
		for(int i = 0; i < docNum; i ++){
			System.out.print(doc_StrongLink_num[i]+"\n");
		}
		
		System.out.print("doc分类结果：topic OR noisy OR between two\n");
*/		FindRepresentNode();
/*		for(int i = 0; i < docNum; i ++){
			System.out.print(node_classification[i]+"\n");
		}
		
		System.out.print("clustering结果(仅仅含topic点)\n");
*/		DocumentClustering();
/*		for(int i = 0; i < docNum; i ++){
			for(int j = 0; j < docNum; j ++){
				System.out.print(cluster[i][j]+" ");
			}
			System.out.print("\n");
		}
		
		System.out.print("ReClustering结果(包含topic和noisytopic之间 )\n");
*/		ReClustering();
/*		for(int i = 0; i < docNum; i ++){
			for(int j = 0; j < docNum; j ++){
				System.out.print(cluster[i][j]+" ");
			}
			System.out.print("\n");
		}
*/		
		DFSTraverse();
//		ShowClusteringResult();
		try{
			File powerfile=new File("E:\\workspace\\TopicFinding\\analyze\\"+"Parameters"+".txt");
			FileWriter fos = new FileWriter(powerfile);
			BufferedWriter a=new BufferedWriter(fos);
			a.write(k+"\n"+strong_link_threshold+"\n"+topic_threshold+"\n"+noisy_threshold+"\n"+labeling_threshold+"\n"+merged_threshold+"\n");
			a.flush();
		}catch(IOException e){};
	}
/**
 *  函数功能：运行算法前预设阈值
 * @param StrongLink 表示Strong Link Threshold
 * @param Topic 表示Topic Threshold
 * @param Noisy 表示Noisy Threshold
 * @param Label 表示Label Threshold
 * @param Merge 表示Merged Threshold
 */
	void SetThresholdParameter(
			int StrongLink, 
			int Topic, 
			int Noisy, 
			double Label, 
			double Merge
			)
	{
		strong_link_threshold = StrongLink;
		topic_threshold = Topic;
		noisy_threshold = Noisy;
		labeling_threshold = Label;
		merged_threshold = Merge;
	}
	
	public void ShowClusteringResult(){
		ArrayList currcluster;
		System.out.print("Clustring最终结果：\n");
		for(int i = 0; i < ClusterList.size(); i ++){
			currcluster = (ArrayList)ClusterList.get(i);
			System.out.print("Cluster"+i+": "+currcluster.toString()+"\n");
		}
	}
	
	public void ShowConfusionMatrix(int[] clusterSize){
		//compute the confusion matrix here
		//clusterSize is the int string containing the orginal cluster size
		int right = 0;
		int wrong = 0;
		int max = 0;
		double accuracy;
		int[] clusterNum = new int[7];
		try{
			File powerfile=new File("E:\\workspace\\TopicFinding\\analyze\\"+"ConfusionMatrix"+".txt");
			FileWriter fos = new FileWriter(powerfile);
			BufferedWriter a=new BufferedWriter(fos);
			
			for(int i = 0; i < 7; i ++)
				clusterNum[i] = 0;
			ArrayList tempList;
			for(int i = 0; i < ClusterList.size(); i ++)
			{
				tempList = (ArrayList)ClusterList.get(i);
//				int[] values = tempList.toArray();
				for(int j = 0; j < tempList.size(); j ++)
				{
					if(((Integer)tempList.get(j)).intValue() < clusterSize[0])
						clusterNum[0]++;
					else if(((Integer)tempList.get(j)).intValue() < clusterSize[1])
						clusterNum[1]++;
					else if(((Integer)tempList.get(j)).intValue() < clusterSize[2])
						clusterNum[2]++;
					else if(((Integer)tempList.get(j)).intValue() < clusterSize[3])
						clusterNum[3]++;
					else if(((Integer)tempList.get(j)).intValue() < clusterSize[4])
						clusterNum[4]++;
					else if(((Integer)tempList.get(j)).intValue() < clusterSize[5])
						clusterNum[5]++;
					else
						clusterNum[6]++;
				}
				for(int count = 0; count < 7; count ++)
				{
					if(clusterNum[max] < clusterNum[count])
						max = count;
				}
				if(clusterNum[max] != 1 || max == 6)
					right = right + clusterNum[max];
				else
					wrong = wrong + 1;
				for(int m = 0; m < 7; m ++)
				{
					if(m == max)
						continue;
					wrong= wrong + clusterNum[m];
				}
				a.write(right+"	"+wrong+"	");
				a.flush();
				for(int count = 0;count < 7;count ++)
				{
					a.write(clusterNum[count]+"	");
					a.flush();
				}
				a.write("\n");
				a.flush();
				for(int count = 0; count < 7; count ++)
					clusterNum[count] = 0;
				max = 0;
			}

		accuracy = (double)right/docNum;
		System.out.print(k+"	"+accuracy+"\n");
		a.write(accuracy+"\n");
		a.flush();
		}catch(IOException e){};
	}
	
	public void ScaleOfWeight(){
		//restore the weight of the doc to a txt for analyze
		//restore the number of docs in different link strength
		int[] store = new int[10];
		for(int i = 0;i < 10; i++)
			store[i] = 0;
		for(int i = 0; i < docNum; i++)
		{
			for(int j = 0; j < docNum; j++)
			{
				if(shared_neighbor_graph[i][j] > 0 && shared_neighbor_graph[i][j] < 1)
					store[0]++;
				else if(shared_neighbor_graph[i][j] >= 1 && shared_neighbor_graph[i][j] < 2)
					store[1]++;
				else if(shared_neighbor_graph[i][j] >= 2 && shared_neighbor_graph[i][j] < 3)
					store[2]++;
				else if(shared_neighbor_graph[i][j] >= 3 && shared_neighbor_graph[i][j] < 4)
					store[3]++;
				else if(shared_neighbor_graph[i][j] >= 4 && shared_neighbor_graph[i][j] < 5)
					store[4]++;
				else if(shared_neighbor_graph[i][j] >= 5 && shared_neighbor_graph[i][j] < 6)
					store[5]++;
				else if(shared_neighbor_graph[i][j] >= 6 && shared_neighbor_graph[i][j] < 7)
					store[6]++;
				else if(shared_neighbor_graph[i][j] >= 7 && shared_neighbor_graph[i][j] < 8)
					store[7]++;
				else if(shared_neighbor_graph[i][j] >= 8 && shared_neighbor_graph[i][j] < 9)
					store[8]++;
				else if(shared_neighbor_graph[i][j] >= 9 && shared_neighbor_graph[i][j] < 9.8)
					store[9]++;
			}
		}
		try{
		File powerfile=new File("E:\\workspace\\TopicFinding\\analyze\\"+"weightScale"+".txt");
		FileWriter fos = new FileWriter(powerfile);
		BufferedWriter a=new BufferedWriter(fos);
//		a.write("docNum="+docNum+"\n"+"k="+k+"\n"+"TopicThreshold="+topic_threshold+"\n"+
//				"NosiyThreshold="+noisy_threshold+"\n"+"StrongLinkThreshold="+strong_link_threshold+"\n"+
//				"MerageThreshold="+merged_threshold+"\n"+"LabelingThreshold="+labeling_threshold+"\n");
//		a.flush();
		for(int i = 0;i < 10;i ++)
		{
			a.write(store[i]+"	");
			a.flush();
		}
		}catch(IOException e){}
		strong_link_threshold = getStrongLink(store);
		labeling_threshold = strong_link_threshold + 1;
		merged_threshold = (double)strong_link_threshold + 2;
	}
	
	public void ScaleOfStrongLinks()
	{
		int[] store = new int[10];
		for(int i = 0;i < 10; i++)
			store[i] = 0;
		for(int i = 0; i < docNum; i++)
		{
				if(doc_StrongLink_num[i] >= 0*k/10 && doc_StrongLink_num[i] < 1*k/10)
					store[0]++;
				else if(doc_StrongLink_num[i] >= 1*k/10 && doc_StrongLink_num[i] < 2*k/10)
					store[1]++;
				else if(doc_StrongLink_num[i] >= 2*k/10 && doc_StrongLink_num[i] < 3*k/10)
					store[2]++;
				else if(doc_StrongLink_num[i] >= 3*k/10 && doc_StrongLink_num[i] < 4*k/10)
					store[3]++;
				else if(doc_StrongLink_num[i] >= 4*k/10 && doc_StrongLink_num[i] < 5*k/10)
					store[4]++;
				else if(doc_StrongLink_num[i] >= 5*k/10 && doc_StrongLink_num[i] < 6*k/10)
					store[5]++;
				else if(doc_StrongLink_num[i] >= 6*k/10 && doc_StrongLink_num[i] < 7*k/10)
					store[6]++;
				else if(doc_StrongLink_num[i] >= 7*k/10 && doc_StrongLink_num[i] < 8*k/10)
					store[7]++;
				else if(doc_StrongLink_num[i] >= 8*k/10 && doc_StrongLink_num[i] < 9*k/10)
					store[8]++;
				else if(doc_StrongLink_num[i] >= 9*k/10 && doc_StrongLink_num[i] <= k)
					store[9]++;
		}
		try{
		File powerfile=new File("E:\\workspace\\TopicFinding\\analyze\\"+"StronglinkScale"+".txt");
		FileWriter fos = new FileWriter(powerfile);
		BufferedWriter a=new BufferedWriter(fos);
//		a.write("docNum="+docNum+"\n"+"k="+k+"\n"+"TopicThreshold="+topic_threshold+"\n"+
//				"NosiyThreshold="+noisy_threshold+"\n"+"StrongLinkThreshold="+strong_link_threshold+"\n"+
//				"MerageThreshold="+merged_threshold+"\n"+"LabelingThreshold="+labeling_threshold+"\n");
//		a.flush();
		for(int i = 0; i < 10; i++)
		{
			a.write(store[i]+"	");
			a.flush();
		}
		}catch(IOException e){}
	}
	
	int getStrongLink(int[] store)
	{
		int[] difference1 = new int[9];
		int[] difference2 = new int[8];
		int index = 0;
		for(int i = 0; i < 9; i++)
			difference1[i] = store[i+1] - store[i];
		for(int i = 0; i < 8; i++)
		{
			if(difference1[i] < 0 || difference1[i] > difference1[i+1])
				difference1[i] = 0;
		}
		difference1[8] = 0;
		for(int i = 0; i < 8; i++)
			difference2[i] = difference1[i+1] - difference1[i];
		for(int i = 0; i < 8; i++)
			index = difference2[index] > difference2[i] ? index : i;

		return index + 1;
	}
	public static void main(String[] args)
	{

	}
}
