package termweighting;

import java.util.ArrayList;

 class Cluster
{
	//�þ�������ݳ�Ա����
	public ArrayList CurrentMembership = new ArrayList();
	
	// �þ��������=�����ĵ�������ʾ�ĸ�ά����term weight��ƽ��ֵ
    public double[] Mean; //ÿһ���ĵ���һ��term weight������ʾ
    
    public Cluster(){//���һ��ʼû��doc������, doc����Ϊ0
    	Mean = null;
    }
    
    public Cluster(int dataindex,double[] data) {//��ʼ����һ��doc�����룬��doc id �� datindex,�ĵ�����Ϊdata
        CurrentMembership.add(dataindex);
        //Mean = data;//���ַ�������Meanֱ��Ӧ��data�����Ը���MeanͬʱҲ����data��Ӧ�����顣��������
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
    
    
    /** �÷�����������ĵ��ľ�ֵ 
    *���룺coordinates[][]�� �ĵ�Ȩ�����������У� doc���У�term 
    */
    public void UpdateMean(double[][] coordinates)
    {
    	//�Ȱ�Mean[]����
   	    for(int t = 0; t < coordinates[0].length; t++)
    		Mean[t] = 0;
    	
   	    //�����ڸ��ı����еĸ����ĵ�doc����֮��
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

