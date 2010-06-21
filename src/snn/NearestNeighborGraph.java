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
	 * ����docNum*k,��i�б�ʾdoc i��top k nearest neighbor
	 */
	private int[][] nearest_neighbor_list;
	/**
	 * ʹ��һ����СΪn��n�ľ�������ʾ shared neighbor graph�� ���shared_neighbor_graph[i][j]��0������doc i��
	 * doc j֮��û��link����������weighted link strength����� shared neighbor graph[i][j] �� double number 
	 * > 0������doc i��doc j֮���weighted link strength Ϊ��double����
	 */
	private double[][] shared_neighbor_graph;
	/**
	 * ����termweighting.TFIDF��õ����ĵ����ƶȾ���
	 */
	private double[][] similarity;
	/**
	 * �ĵ������е���doc��Ŀ
	 */
	private int docNum;
	/**
	 * ʹ��һ����СΪdocNum��docNum��boolean���;���nearest_neighbor_graph[][]����
	 * ʾnearest neighbor graph�� nearest_neighbor_graph[i][j]��true��ʾdoc i��
	 * doc j֮����link
	 */
	private boolean[][] nearest_neighbor_graph;
	/**
	 * ������¼ĳһ��doc���ж��ٸ�strong link��doc_StrongLink_num[i]��ʾ��i��doc��
	 * strong link����Ŀ
	 */
	private int[] doc_StrongLink_num;
	/**
	 * ����ֵ����weighted link strength> strong_link_threshold,
	 * ������Ϊ��link��strong link
	 */
	private int strong_link_threshold;
	/**
	 * ����ֵ����doc_StrongLink_num > topic_threshold,��Ϊ��docΪrepresentative
	 */
	private int topic_threshold;
	/**
	 * ����ֵ����doc_StrongLink_num < noisy_threshold����Ϊ��docΪnoisy
	 */ 
	private int noisy_threshold;
	/**
	 * ����ֵ����weighted link strength > merged_theshold,��Ϊ��doc����merge��һ��
	 */
	private double merged_threshold;
	/**
	 * ����ֵ���������noisy��topic֮���term���ж���ֵ
	 */
	private double labeling_threshold;
	/**
	 * ʹ��һ��k*k�ľ���cluster[k][k]����ʾ�ı������������ cluster[i][j]=cluster[j][i]=1,˵��doc i��
     *doc j��ͬһ��cluster�һ��clusterʵ����һ��������ͬͼ��
	 */
	private boolean[][]  cluster;
	/**
	 * node_classification��ʾ���ĵ��ķ��ࣺrepresentative, noisy, 
	 * between_noisy_and_represent���ֱ��Ӧ������0��1��2
	 */
	private int[] node_classification;
	/**
	 * 
	 */
	private ArrayList ClusterList;
	/**
	 * @function ���캯�������û��������κβ�����Ĭ��Ϊ���в���Ĭ��Ϊ0
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
	 * @function ���캯��
	 * @param doc_similarity���ı������е�doc֮������ƶȾ���
	 * @param top_k����ʾnearest neighbor list�ĳ��ȣ�����ϵͳ��top���ٸ������Ƶ��ĵ�
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
	 * @function ��ÿһ���ĵ���Top K Nearest Neighbor
	 * @param Double[k][k] DocSimilarity   �ĵ����ĵ����ƶȾ���
	 * @return Integer[k][n] nearest_neighbor_list 
	 * @�������� �������2���ĵ�(doc1 doc2)���ĵ�A�ǳ����ƣ�����������ʱ���������ĵ����ƶ���ȣ�
	 * ��ô�����ƶ������޷�������doc1��doc2���ã�����һ��nearest neighbor list��ͬʱ��������
	 * ��ͬ���ı�id
	 * @author fuyanjie 2008.11.20
	 */
	public void ComputeTopKNearestNeighborList()
	{
		for(int i= 0; i< docNum; i++){
			double[] DocSim1 = new double[docNum];//��i���ĵ��������ĵ������ƶȵ�����ȡ����
			ArrayList DocSim2 = new ArrayList (); 
			for(int j = 0; j < docNum; j++){
				DocSim1[j] = similarity[i][j];
				DocSim2.add(j,similarity[i][j]);
			}
			Arrays.sort(DocSim1);//��С��������,��������Ҫ�Ӵ�Сѡ��
			for(int j =0; j< k; j++){
				nearest_neighbor_list[i][j] 
				                         = 
					DocSim2.indexOf(DocSim1[docNum-1-j]);			
			}					
		}
	}
	/**
	 *@function ����nearest neighbor graph
	 *@paramInteger[docNum][NeighborListLength] nearest_neighbor_list    �ĵ�-top n nearest neighbor����
	 *@return  Boolean[docNum][docNum] nearest_neighbor_graph
	 *@author fuyanjie 2008.11.20
	 */
	public void CreateNearestNeighborGraph()//�õ�һ���Գƾ���nearest_neighbor_graph
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
	 * @function �ж�doc1��doc2��nearest neighbor list�Ƿ��໥����
	 * @param nearest_neighbor_list 
	 * @param doc1 doc1��ID
	 * @param doc2 doc2��ID
	 * @return ����list�Ƿ��໥�����Է���boolean���
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
	 *@function ���� Shared Neighbor Graph(�ĵ�weighted link strengthͼ)
	 *@param Integer[docNum][ListLength] nearest_neighbor_list    �ĵ�-top n nearest neighbor����
     *@param Boolean[docNum][ListLength] nearest_neighbor_graph	  �ĵ��޼�Ȩlinkͼ
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
	 * @function ����ͼ���С������ļ���doc1��doc2֮��ļ�Ȩ Link Strength�ķ���
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
	 *@function �ڸ���strong link threshold��ǰ���¼���һ���ĵ���Strong Link�ĸ���
	 *@param Integer[docNum] strong_link
	 *@return Integer[docNum] doc_StrongLink_num
	 *@author fuyanjie 2008.11.20
	 */
	public void ComputeDocStrongLinkNum()
	{
//		strong_link_threshold = 2;//��Ϊָ��strong link threshold
		for(int i = 0; i < docNum; i ++){
			for(int j = 0; j < docNum; j ++){
				if(shared_neighbor_graph[i][j] > strong_link_threshold){
					doc_StrongLink_num[i]++;
				}
			}	
		}
	}
	/**
	 *@function �ڸ���topic threshold noisy threshold��Ѱ�Ҵ����
	 *@param Integer[k] strong_link
	 *@return Integer[k] node_classification �ֺ���ĵ�
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
	 * @function ��document��clustering��ʹ��һ��k*k�ľ���cluster[k][k]����ʾ
	 * �ı�������
	 * ��� cluster[i][j]=cluster[j][i]=1,˵��doc i��doc j��ͬһ��cluster��
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
	input��boolean[k][k] cluster
	output��boolean[k][k] cluster
	*/
	public void ReClustering(){
		//labeling_threshold = 2;
		double strength=0; //��¼Ŀǰ����link strength
		int point; //��¼Ŀǰstrength����jֵ
		for(int i = 0; i < k; i ++){
			if (getSum(i) == 1)  //˵��doc[i]û�б�����
		     for(int j=i+1; j < k; j ++){ //�����˹����㣨������һ��û�б������doc���Ĵ�����
		    	 if(getSum(j) > 1){  //ֻ��doc[j]�Ѿ��ڵ��߲��ϱ������ˣ��ż���doc[i]�Ƿ�����doc[j]���ڵ���
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
	 * @�������ã�����cluster��ĵ�row�е���ֵ��
	 * @param row ��ʾ�û���cluster������ĵ�row�е���ֵ��
	 * @return cluster[][]��row�е���ֵ��
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
	 * @function ���ø����Ӻ���������㷨�ļ���
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
/*		System.out.print("ÿһ���ĵ���StrongLink����\n");
		for(int i = 0; i < docNum; i ++){
			System.out.print(doc_StrongLink_num[i]+"\n");
		}
		
		System.out.print("doc��������topic OR noisy OR between two\n");
*/		FindRepresentNode();
/*		for(int i = 0; i < docNum; i ++){
			System.out.print(node_classification[i]+"\n");
		}
		
		System.out.print("clustering���(������topic��)\n");
*/		DocumentClustering();
/*		for(int i = 0; i < docNum; i ++){
			for(int j = 0; j < docNum; j ++){
				System.out.print(cluster[i][j]+" ");
			}
			System.out.print("\n");
		}
		
		System.out.print("ReClustering���(����topic��noisytopic֮�� )\n");
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
 *  �������ܣ������㷨ǰԤ����ֵ
 * @param StrongLink ��ʾStrong Link Threshold
 * @param Topic ��ʾTopic Threshold
 * @param Noisy ��ʾNoisy Threshold
 * @param Label ��ʾLabel Threshold
 * @param Merge ��ʾMerged Threshold
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
		System.out.print("Clustring���ս����\n");
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
