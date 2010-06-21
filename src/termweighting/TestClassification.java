package termweighting;

import java.lang.String;
import java.util.ArrayList;


public class TestClassification {
	

	
	public static void main(String[] args) 
	{
	    //1����ȡ�ĵ�����
	    String[] docs = {
	    		"���� ȭ�� �볡ȯ ���� ���� ������ ��� ���� ���� ˮ��",
	    		"ĳ ���� ���� վ ���� �� �� �� ��ѯ �� �� λ �� ����",
	    		"�м� Ů ���� ��Χ ���� ��Ӿ ���� �� ���� ��ʷ �� �� ��һ",
	    		"�˶�Ա ���� �� ���� �� �� �¡� ���� ��� ��Ա ���� ʵ����",
	    		"���� Ʊ�� ���� �ɹ� ��Ʊ �� Ӧ ��ʱ �� ���� ���� ���� ����",	    		
	    		"���� Ҫ ��� �Լ� �� Ŀ��",
	    		"ӡ��˰ ֮ ���� �ļ�",
	    		"���� ���� �� ���� ��ף ӡ��˰ �µ�",	    		
	    		"�� ��Ǯ �� ���� �� ���� ����", 
	    		"���� һ �� ASP  ϵ�� �̳�",
	    		"�� ASP �� ʵ�� �۲��� ģʽ �� �� �� �� �� ����",
	    		"ASP ҳ�� ִ�� ���� ����",
	    		"ASP �ؼ� ���� ��ʾ �ؼ� ����",
	    		"ASP �Զ��� �ؼ� ���� ���� ��"
	    };
	
	    //2����ʼ��TFIDF����������������ÿ���ĵ���TFIDFȨ��
	    TFIDF tf = new TFIDF (docs);
	    tf.MyInit();
		System.out.print("DocNum: "+tf._numDocs+"\n");
		System.out.print("TermNum: "+tf._numTerms+"\n");
	    
		System.out.print("��������ĵ����ϵ�terms\n");
		for(int i = 0; i < tf._terms.size(); i ++)
			System.out.print((String) tf._terms.get(i)+" ");
		System.out.print("\n");
		
		System.out.print("Term Freq Matrix: \n");
		for(int j =0; j <tf._numDocs; j ++){
			for(int i=0; i < tf._numTerms; i ++){
				System.out.print(tf._termFreq[i][j]+" ");
			}
			System.out.print("\n");
		}
		
		System.out.print("term wight matrix:\n");
		for(int j =0; j <tf._numDocs; j ++){
			for(int i=0; i < tf._numTerms; i ++){
				System.out.print(tf._termWeight[i][j]+" ");
			}
			System.out.print("\n");
		}
	    
	    int K = 3; //�۳�3������

	    //3������k-means���������ݣ���һ���������飬��һά��ʾ�ĵ�������
	    //�ڶ�ά��ʾ�����ĵ��ֳ��������д�
	    double [][] data = new double [docs.length][];
	    int docCount = docs.length; //�ĵ�����
	    int dimension = tf._numTerms;//���дʵ���Ŀ
	    for (int i = 0; i < docCount; i++)
	    {
	        data[i] = tf.GetTermVector(i); //��ȡ��i���ĵ���TFIDFȨ������ 
	    }
	    
	    for(int j =0; j <tf._numDocs; j ++){
	    	for(int i=0; i < tf._numTerms; i ++){
	    		if(data[j][i] != tf._termWeight[i][j]){
	    			System.err.print("�����������");
	    		     System.exit(0);
	    		}
	    	}
	    }
	    System.out.print("�ĵ������������");
	    
	    //4����ʼ��k-means�㷨����һ��������ʾ�������ݣ��ڶ���������ʾҪ�۳ɼ�����
	    Kmeans kmeans = new Kmeans(data, K);
	    
	    //5����ʼ����
	    kmeans.Start();

	    //6����ȡ�����������
	    Cluster[] clusters = kmeans._clusters;
	    for(int i =0; i < clusters.length; i++){
	        ArrayList members = clusters[i].CurrentMembership;
	        System.out.print("-----------------");
	        System.out.print("\n");
	        for (int j =0; j <members.size(); j++){
	        	int docID = ((Integer)members.get(j)).intValue();
	            System.out.print("doc"+docID+": "+docs[docID]+"\n");
	        }
	        System.out.print("\n");
	    }
	}
}
