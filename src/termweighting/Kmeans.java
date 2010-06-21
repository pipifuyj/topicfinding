package termweighting;
import java.util.Random;
import java.util.ArrayList;

public class Kmeans {
		//�ĵ��������doc����
		private int _coordCount;  
		
		//�ĵ����ϵ�term Ȩ��������ʾ
	    private double[][] _coordinates;
	    
	    //���������
	    private int _k;
	    
	    //����,һ����_k���࣬����Ӧ����_k��_clusters
	    public Cluster[] _clusters;

/*	    private Cluster[] Clusters
	    {
	        get { return _clusters; }
	    } 
*/
	    // ����һ���������ڼ�¼�͸���ÿ�����ϵ������ĸ�Ⱥ����
	    // _clusterAssignments[j]=i;
	    // ��ʾ�� j �����ϵ�������ڵ� i ��Ⱥ����
	    private int[] _clusterAssignments;
	    
	    // ����һ���������ڼ�¼�͸���ÿ�����ϵ�����һ�������������
	    private  int[] _nearestCluster;
	    
	    // ����һ������������ʾ���ϵ㵽�������ĵ�ľ���,
	    // ����_distanceCache[i][j]��ʾ��i�����ϵ㵽��j��Ⱥ�۶������ĵ�ľ��룻
	    private  double[][] _distanceCache;
	    
	    // ������ʼ�����������
	    private static Random _rnd = new Random(1);
	    
	    /**
	     * Kmeans�Ĺ��캯��
	     * @param data ��������Ϊ:double[][],��ʾ�ĵ�������ʾ�����У� doc, �У�term
	     * @param K ��ʾ���ĵ��ֳɶ��ٸ����
	     */	
	    public Kmeans(double[][] data, int K)
	    {
	        _coordinates = data;//�ں����������ĵ�������ʾ���󡱵ĸ��ƣ����Ƶ�_coordinates
	        
	        _coordCount = data.length;//doc����
	        
	        _k = K;//һ��Ҫ���ĵ��ֳɶ�����
	        
	        _clusters = new Cluster[K];//k����
	        
	        _clusterAssignments = new int[_coordCount];//��¼��i���ĵ������ĸ�һ����
	        for(int i=0; i <_coordCount; i++)
	        	_clusterAssignments[i] = -1;
	        
	        _nearestCluster = new int[_coordCount];//��¼��i���ĵ������cluster���ĸ�
	        for(int i=0; i <_coordCount; i++)
	        	_nearestCluster[i] = -1;
	        
	        _distanceCache = new double[_coordCount][_coordCount];//��¼�ĵ����ĵ�֮��ľ���
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
	            //1�����¼���ÿ������ľ�ֵ
	            for (int i = 0; i < _k; i++)
	            {
	                _clusters[i].UpdateMean(_coordinates);
	            }
	            
	            
	            System.out.print("\n");
	            for(int i = 0; i <_k; i++){
	            	System.out.print("����"+i+":(");
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
	            	

	            //2������ÿ��doc��ÿ���������ĵľ���
	            for (int i = 0; i < _coordCount; i++)
	            {
	            	System.out.print("doc "+i+": ");
	                for (int j = 0; j < _k; j++)
	                {
	                    double dist = getDistance(_coordinates[i], _clusters[j].Mean);
	                    _distanceCache[i][j] = dist;
	                    System.out.print(" ����"+"����("+j+"):"+dist);
	                }
	                System.out.print("\n");
	            }

	            //3������ÿ���������ĸ��������
	            for (int i = 0; i < _coordCount; i++)
	            {
	                _nearestCluster[i] = nearestCluster(i);
	                System.out.print("doc"+i+"�������"+_nearestCluster[i]+" �������"+_clusterAssignments[i]+"\n");
	            }
	            System.out.print("cluster size: ");
	            for(int i = 0; i < _k; i ++)
	            	System.out.print(_clusters[i].CurrentMembership.size()+" ");	
	            //4���Ƚ�ÿ��doc����ľ����Ƿ�����������ľ���
	            //���ȫ��ȣ���ʾ���еĵ��Ѿ�����Ѿ����ˣ�ֱ�ӷ��أ�
	            int k = 0;
	            for (int i = 0; i < _coordCount; i++){
	                if (_nearestCluster[i] == _clusterAssignments[i])
	                    k++;
	            }
	            
	            if (k == _coordCount) //Kmeans�㷨����ֹ������
	                break;

	            //5��������Ҫ���µ������ϵ��Ⱥ����Ĺ�ϵ��������Ϻ������¿�ʼѭ����
	            //��Ҫ�޸�ÿ������ĳ�Ա�ͱ�ʾĳ�����������ĸ�����ı���
	            for (int j = 0; j < _k; j++){
	                _clusters[j].CurrentMembership.clear();//���
	            }
	            for (int i = 0; i < _coordCount; i++) {
	                _clusters[_nearestCluster[i]].CurrentMembership.add(i);
	                _clusterAssignments[i] = _nearestCluster[i];
	            }            
	        }
	    }


	    // ����ĳ���������ĸ��������
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
	            System.err.print("�Ҳ�������ľ���");
	            System.exit(0);
	        }
	        return nearest;
	    }

	    /// ����ĳ������ĳ�������ĵľ���
	    static double getDistance(double[] coord, double[] center)
	    {
	        if(coord.length != center.length)
	        	System.err.print("�������鳤�Ȳ�һ��");
	        double sumSquared = 0.0;
	        for (int i = 0; i < coord.length; i++)
	        {
	            double v = coord[i] - center[i];
	            sumSquared = sumSquared + v*v; //ƽ����
	        }
	        return sumSquared;
	        //return Math.sqrt(sumSquared);
	    } 
	    
	    /// �����ʼ��k�����࣬�������Ӧ���ظ�
	    private void InitRandom()
	    {
	    	ArrayList numlist=  new ArrayList();
	    	int i =0;
	    	
	    	while(numlist.size()<_k){
	    		int temp = _rnd.nextInt(_coordCount);
	    		if(!numlist.contains(temp)){
	    			numlist.add(temp);
	            	System.out.print("\n�� "+temp+" doc�������"+i);
	            	 _clusterAssignments[temp] = i; //��¼��temp���������ڵ�i������
	 	            _clusters[i] = new Cluster(temp,_coordinates[temp]);
	 	            i ++;
	            }
	    	}
	    }


	public static void main(String[] args) {
			

	}

}
