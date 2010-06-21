package termweighting;

import java.util.ArrayList;

 class Cluster
{
	//该聚类的数据成员索引
	public ArrayList CurrentMembership = new ArrayList();
	
	// 该聚类的中心=各个文档向量表示的各维分量term weight求平均值
    public double[] Mean; //每一个文档用一个term weight向量表示
    
    public Cluster(){//如果一开始没有doc被加入, doc数量为0
    	Mean = null;
    }
    
    public Cluster(int dataindex,double[] data) {//初始化有一个doc被加入，该doc id ＝ datindex,文档向量为data
        CurrentMembership.add(dataindex);
        //Mean = data;//这种方法导致Mean直接应用data，所以更新Mean同时也更新data对应的数组。发生错误。
        Mean = new double[data.length];
        for(int i = 0;i < data.length; i ++){
        	Mean[i] = data[i];
        }
    }
     
    
    /*private boolean add(int dataindex){
     * 	return CurrentMembership.add(dataindex);
     * }
     * 
     * private boolean remove(int dataindex){
     * 	return CurrentMembership.remove(dataindex)
     * }
     */
    
    
    /** 该方法计算聚类文档的均值 
    *输入：coordinates[][]＝ 文档权重向量矩阵，行＝ doc，列＝term 
    */
    public void UpdateMean(double[][] coordinates)
    {
    	//先把Mean[]置零
   	    for(int t = 0; t < coordinates[0].length; t++)
    		Mean[t] = 0;
    	
   	    //再求在该文本类中的各个文档doc向量之和
        for (int i = 0; i < CurrentMembership.size(); i++)
        {
            double[] coord = coordinates[((Integer)CurrentMembership.get(i)).intValue()];
            for (int j = 0; j < coord.length; j++)
            {
                Mean[j] += coord[j]; 
            }          
        }
        
        for (int k = 0; k < Mean.length; k++)
        {
            Mean[k] /= CurrentMembership.size(); 
        }
    }
}

