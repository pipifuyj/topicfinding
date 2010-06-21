package termweighting;
import java.util.Random;
import java.util.ArrayList;

public class Kmeans {
		//文档集合里的doc数量
		private int _coordCount;  
		
		//文档集合的term 权重向量表示
	    private double[][] _coordinates;
	    
	    //聚类的数量
	    private int _k;
	    
	    //聚类,一共有_k个类，所以应该有_k个_clusters
	    public Cluster[] _clusters;

/*	    private Cluster[] Clusters
	    {
	        get { return _clusters; }
	    } 
*/
	    // 定义一个变量用于记录和跟踪每个资料点属于哪个群聚类
	    // _clusterAssignments[j]=i;
	    // 表示第 j 个资料点对象属于第 i 个群聚类
	    private int[] _clusterAssignments;
	    
	    // 定义一个变量用于记录和跟踪每个资料点离哪一个聚类中心最近
	    private  int[] _nearestCluster;
	    
	    // 定义一个变量，来表示资料点到聚类中心点的距离,
	    // 其中_distanceCache[i][j]表示第i个资料点到第j个群聚对象中心点的距离；
	    private  double[][] _distanceCache;
	    
	    // 用来初始化的随机种子
	    private static Random _rnd = new Random(1);
	    
	    /**
	     * Kmeans的构造函数
	     * @param data 数据类型为:double[][],表示文档向量表示矩阵，行＝ doc, 列＝term
	     * @param K 表示把文档分成多少个类别
	     */	
	    public Kmeans(double[][] data, int K)
	    {
	        _coordinates = data;//在函数内作“文档向量表示矩阵”的复制，复制到_coordinates
	        
	        _coordCount = data.length;//doc个数
	        
	        _k = K;//一共要把文档分成多少类
	        
	        _clusters = new Cluster[K];//k个类
	        
	        _clusterAssignments = new int[_coordCount];//记录第i个文档属于哪个一个类
	        for(int i=0; i <_coordCount; i++)
	        	_clusterAssignments[i] = -1;
	        
	        _nearestCluster = new int[_coordCount];//记录第i个文档的最近cluster是哪个
	        for(int i=0; i <_coordCount; i++)
	        	_nearestCluster[i] = -1;
	        
	        _distanceCache = new double[_coordCount][_coordCount];//记录文档与文档之间的距离
	        InitRandom();
	    }

	    public void Start()
	    {
	    	System.out.print("\n");
	    	for(int i = 0; i < _coordCount; i ++){
	    		for(int j = 0; j < _coordinates[0].length; j ++){
	    			System.out.print(_coordinates[i][j]+",");
	    		}
	    		System.out.print("\n");
	    	}
	    	
	        int iter = 0;
	        while (true)
	        {
	          //  System.out.print("Iteration " + (iter++) + "");
	            //1、重新计算每个聚类的均值
	            for (int i = 0; i < _k; i++)
	            {
	                _clusters[i].UpdateMean(_coordinates);
	            }
	            
	            
	            System.out.print("\n");
	            for(int i = 0; i <_k; i++){
	            	System.out.print("聚类"+i+":(");
	            	for(int j = 0; j < _clusters[i].Mean.length; j ++){
	            		System.out.print(_clusters[i].Mean[j]+",");	            	
	            	}
	            	System.out.print(")\n");
	            	for(int j = 0; j < _clusters[i].CurrentMembership.size(); j ++){
	            		System.out.print("doc "+((Integer)_clusters[i].CurrentMembership.get(j)).intValue()+",");
	            	}
	            	/*
	            	for(int j = 0; j < _clusters[i].Mean.length; j ++){
	            		System.out.print(_clusters[i].Mean[j]+" ");	            	}
                    }*/
	            	System.out.print("\n");
	            }
	            	

	            //2、计算每个doc和每个聚类中心的距离
	            for (int i = 0; i < _coordCount; i++)
	            {
	            	System.out.print("doc "+i+": ");
	                for (int j = 0; j < _k; j++)
	                {
	                    double dist = getDistance(_coordinates[i], _clusters[j].Mean);
	                    _distanceCache[i][j] = dist;
	                    System.out.print(" 距离"+"中心("+j+"):"+dist);
	                }
	                System.out.print("\n");
	            }

	            //3、计算每个数据离哪个聚类最近
	            for (int i = 0; i < _coordCount; i++)
	            {
	                _nearestCluster[i] = nearestCluster(i);
	                System.out.print("doc"+i+"最近聚类"+_nearestCluster[i]+" 所属类别："+_clusterAssignments[i]+"\n");
	            }
	            System.out.print("cluster size: ");
	            for(int i = 0; i < _k; i ++)
	            	System.out.print(_clusters[i].CurrentMembership.size()+" ");	
	            //4、比较每个doc最近的聚类是否就是它所属的聚类
	            //如果全相等，表示所有的点已经是最佳距离了，直接返回；
	            int k = 0;
	            for (int i = 0; i < _coordCount; i++){
	                if (_nearestCluster[i] == _clusterAssignments[i])
	                    k++;
	            }
	            
	            if (k == _coordCount) //Kmeans算法的终止的条件
	                break;

	            //5、否则需要重新调整资料点和群聚类的关系，调整完毕后再重新开始循环；
	            //需要修改每个聚类的成员和表示某个数据属于哪个聚类的变量
	            for (int j = 0; j < _k; j++){
	                _clusters[j].CurrentMembership.clear();//清空
	            }
	            for (int i = 0; i < _coordCount; i++) {
	                _clusters[_nearestCluster[i]].CurrentMembership.add(i);
	                _clusterAssignments[i] = _nearestCluster[i];
	            }            
	        }
	    }


	    // 计算某个数据离哪个聚类最近
	    int nearestCluster(int ndx)
	    {
	        int nearest = -1;
	        double min = Double.MAX_VALUE;
	        for (int c = 0; c < _k; c++)
	        {
	            double d = _distanceCache[ndx][c];
	            if (d < min)
	            {
	                min = d;
	                nearest = c;
	            }
	      
	        }
	        if(nearest==-1)
	        {
	            System.err.print("找不到最近的聚类");
	            System.exit(0);
	        }
	        return nearest;
	    }

	    /// 计算某数据离某聚类中心的距离
	    static double getDistance(double[] coord, double[] center)
	    {
	        if(coord.length != center.length)
	        	System.err.print("两个数组长度不一致");
	        double sumSquared = 0.0;
	        for (int i = 0; i < coord.length; i++)
	        {
	            double v = coord[i] - center[i];
	            sumSquared = sumSquared + v*v; //平方差
	        }
	        return sumSquared;
	        //return Math.sqrt(sumSquared);
	    } 
	    
	    /// 随机初始化k个聚类，随机数不应该重复
	    private void InitRandom()
	    {
	    	ArrayList numlist=  new ArrayList();
	    	int i =0;
	    	
	    	while(numlist.size()<_k){
	    		int temp = _rnd.nextInt(_coordCount);
	    		if(!numlist.contains(temp)){
	    			numlist.add(temp);
	            	System.out.print("\n第 "+temp+" doc属于类别"+i);
	            	 _clusterAssignments[temp] = i; //记录第temp个资料属于第i个聚类
	 	            _clusters[i] = new Cluster(temp,_coordinates[temp]);
	 	            i ++;
	            }
	    	}
	    }


	public static void main(String[] args) {
			

	}

}
