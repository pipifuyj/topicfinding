package nearest_neighbor_graph;

import termweighting.*;
import docGenerating.docString;
import java.util.ArrayList;

public class NNGTest {
	
	public static void main(String[] args) {
		
		docString getDoc = new docString();
 		ArrayList DOCs = getDoc.getDocStringToArrayList();
 		int[] clusterSize = getDoc.getClusterSize();
 		
 		int docNum = clusterSize[5] + clusterSize[6];
 		String[] docs = new String[DOCs.size()];
 		for(int i = 0; i < docs.length; i ++){
 			docs[i] = (String) DOCs.get(i);
 		}
 		
		TFIDF tf = new TFIDF(docs);
	    int top_k = docNum / 7;		// get the experienced k value
//	    int top_k = 14;
//	    for(int k = 10; k < 100; k = k + 10)
	    {
	    NearestNeighborGraph test = new NearestNeighborGraph(tf._DocSim, top_k);

        test.SetThresholdParameter(0, 3, 2, 0, 0);
	    test.MyStartAlgorithm();
//	    test.ShowClusteringResult();
//	    test.ScaleOfWeight();
//	    test.ScaleOfStrongLinks();
	    test.ShowConfusionMatrix(clusterSize);
	    }
	}
}
