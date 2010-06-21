package termweighting;


/**
*
*/
public class TermVector {
		
		//TermVector类的该成员函数计算两个文档的cos相似度
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
		
		//TermVector类的该成员函数计算两个向量的内积
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
		//TermVector的该成员函数计算向量的长度，注意不是向量的分量个数
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
