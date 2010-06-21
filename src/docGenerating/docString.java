package docGenerating;
import java.util.*;
import java.io.*;
/*��ȡ�ĵ�������һ��ArrayList��*/
public class docString {
	static int total_class=7;
	static int size_class1=10*50;
	static int size_class2=20*50;
	static int size_class3=20*50;
	static int size_class4=10*50;
	static int size_class5=20*50;
	static int size_class6=10*50;
	static int size_noisy_class1=10*50;
	static int doc_count=100*50;
	//getDocStringToArrayList���������ĵ���stringt��
	public ArrayList<String> getDocStringToArrayList() {
		// TODO Auto-generated method stub
		ArrayList<String> list=new ArrayList<String>();
		for(int i=1;i<doc_count;i++){
			int cluster=0;
			if(i<=size_class1)
				cluster=1;
			else
				if(i<=size_class2+size_class1)
					cluster=2;
				else
					if(i<=size_class3+size_class2+size_class1)
						cluster=3;
					else
						if(i<=size_class4+size_class3+size_class2+size_class1)
							cluster=4;
						else
							if(i<=size_class5+size_class4+size_class3+size_class2+size_class1)
								cluster=5;
							else
								if(i<=size_class6+size_class5+size_class4+size_class3+size_class2+size_class1)
									cluster=6;
								else
									cluster=7;
			
			String path="E:\\workspace\\TopicFinding\\doc1\\"+"doc"+i+"_cluster"+cluster+".txt";
			try{
			FileReader reader=new FileReader(path);
			BufferedReader file   =new BufferedReader(reader);
			list.add(file.readLine());
			
			}catch(IOException e){}
		}
/*������
	for(int j=0;j<500;j++)
	{
		System.out.println(j+1);
		System.out.println(list.get(j));
	}
	*/
		return list;
}
	public int[] getClusterSize() {
		int[] clusterSize = new int[7];
		clusterSize[0] = size_class1;
		clusterSize[1] = size_class1+size_class2;
		clusterSize[2] = size_class1+size_class2+size_class3;
		clusterSize[3] = size_class1+size_class2+size_class3+size_class4;
		clusterSize[4] = size_class1+size_class2+size_class3+size_class4+size_class5;
		clusterSize[5] = size_class1+size_class2+size_class3+size_class4+size_class5+size_class6;
		clusterSize[6] = size_noisy_class1;
		
		return clusterSize;
	}
}