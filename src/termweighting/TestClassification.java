package termweighting;

import java.lang.String;
import java.util.ArrayList;


public class TestClassification {
	

	
	public static void main(String[] args) 
	{
	    //1、获取文档输入
	    String[] docs = {
	    		"奥运 拳击 入场券 基本 分罄 邹市明 夺冠 对手 浮出 水面",
	    		"某 心理 健康 站 开张 后 首 个 咨询 者 是 位 新 股民",
	    		"残疾 女 青年 入围 奥运 游泳 比赛 创 奥运 历史 两 项 第一",
	    		"运动员 行李 将 “后 上 先 下” 奥运 相关 人员 行李 实名制",
	    		"奥运 票务 网上 成功 订票 后 应 及时 到 银行 代售 网点 付款",	    		
	    		"股民 要 清楚 自己 的 目的",
	    		"印花税 之 股民 四季",
	    		"杭州 股民 放 鞭炮 庆祝 印花税 下调",	    		
	    		"输 大钱 的 股民 给 我们 启迪", 
	    		"介绍 一 个 ASP  系列 教程",
	    		"在 ASP 中 实现 观察者 模式 或 有 更 好 的 方法",
	    		"ASP 页面 执行 流程 分析",
	    		"ASP 控件 开发 显示 控件 内容",
	    		"ASP 自定义 控件 复杂 属性 声"
	    };
	
	    //2、初始化TFIDF测量器，用来生产每个文档的TFIDF权重
	    TFIDF tf = new TFIDF (docs);
	    tf.MyInit();
		System.out.print("DocNum: "+tf._numDocs+"\n");
		System.out.print("TermNum: "+tf._numTerms+"\n");
	    
		System.out.print("输出整个文档集合的terms\n");
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
	    
	    int K = 3; //聚成3个聚类

	    //3、生成k-means的输入数据，是一个联合数组，第一维表示文档个数，
	    //第二维表示所有文档分出来的所有词
	    double [][] data = new double [docs.length][];
	    int docCount = docs.length; //文档个数
	    int dimension = tf._numTerms;//所有词的数目
	    for (int i = 0; i < docCount; i++)
	    {
	        data[i] = tf.GetTermVector(i); //获取第i个文档的TFIDF权重向量 
	    }
	    
	    for(int j =0; j <tf._numDocs; j ++){
	    	for(int i=0; i < tf._numTerms; i ++){
	    		if(data[j][i] != tf._termWeight[i][j]){
	    			System.err.print("两个矩阵不相等");
	    		     System.exit(0);
	    		}
	    	}
	    }
	    System.out.print("文档向量矩阵相等");
	    
	    //4、初始化k-means算法，第一个参数表示输入数据，第二个参数表示要聚成几个类
	    Kmeans kmeans = new Kmeans(data, K);
	    
	    //5、开始迭代
	    kmeans.Start();

	    //6、获取聚类结果并输出
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
