package termweighting;


/**
*
*/
public class TermVector {
		
		//TermVector��ĸó�Ա�������������ĵ���cos���ƶ�
		public static double ComputeCosineSimilarity(double[] vector1, double[] vector2)
		{
			try{
				if (vector1.length != vector2.length)				
					throw new Exception("DIFER LENGTH");
			}
			catch(Exception unequal){
				System.err.print("vector1.length != vector2.length");
			}
	

			double denom=(VectorLength(vector1) * VectorLength(vector2));
			if (denom == 0F)				
				return 0F;				
			else				
				return (InnerProduct(vector1, vector2) / denom);
			
		}
		
		//TermVector��ĸó�Ա�������������������ڻ�
		public static double InnerProduct(double[] vector1, double[] vector2)
		{
			try{
				if (vector1.length != vector2.length)
					throw new Exception("DIFFER LENGTH ARE NOT ALLOWED");
			}
			catch(Exception unequal){
				System.err.print("vector1.length != vector2.length");
			}
		
			double result=0F;
			for (int i=0; i < vector1.length; i++)				
				result += vector1[i] * vector2[i];
			
			return result;
		}
		//TermVector�ĸó�Ա�������������ĳ��ȣ�ע�ⲻ�������ķ�������
		public static double VectorLength(double[] vector)
		{			
			double sum=0.0F;
			for (int i=0; i < vector.length; i++)				
				sum=sum + (vector[i] * vector[i]);
					
			return (double)Math.sqrt(sum);
		}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
