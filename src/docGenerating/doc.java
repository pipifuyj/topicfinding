package docGenerating;
import java.io.*;
import java.util.*;
/*String[] datamining={"cube","mining","classfication","OLAP","confidence",
			"rule","correlation","cluster","support","class","Gini",
			"CART","CF","ROCK","CMAR","noisy","extraction","DataDetective",
			"FPtrees","AGM","IDF","threshold","sequence","warehouse","itemset",
			"sampling","VFDT","VIPS","WaveCluster","MineSet"};
	String[]database={"database","SQL","Oracle","VFP","ACCESS","Sqlserver",
			"field","segment","record","relation","domain","attribute",
			"tuple","ODBC","view","constraint","trigger","dictionary","DML",
			"role","index","DB","OLE","DBMS","DDL","MySql","SDDL","query",
			"key","cursor"
			};
	String[]genal_IT={"IT","backup","bell","bit","BOOL","bus","byte","BA","BC",
			"BYS","base","feedback","FAC","FAD","gate","giga","host","hardware",
			"HAB","keyboard","KE","line","language","number","NAF","NAH","NAI",
			"information","technology","data","computer","AI","software","communication",
			"workstation","storage","retrival","digital","terminal","cybernetics",
			"cell","hypermedia","coding","simulation","CAM","code","CIMS","3D","knowledge",
			"minicomputer","notebook","desktop","pattern","model","disk","driver","memory",
			"cable","buffer","unit","CPU","control","ROM","DRAM","RAM","counter","chip",
			"decoder","register","card","IC","VLSI","monitor","track","screen","adapter",
			"power","interface","algorithm","user","text","file","document","development","program",
			"metadata","shareware","structure","compiler","statment","directory","identifier",
			"system","engineering","chart","design","evaluation","reliability","complexity",
			"analysis"};
 * */
/*
 * concept_sport={"sport","IOC","winner","record_holder","record_breaker",
"world_record","champion","runner-up","third_place","medalist","Gold_Medal",
"Silver_Medal","Bronze_Medal","final_result","total_points","placement",
"rank","final_placing","medal_tally","win_title","defend_title",
"victory_ceremony","judge","referee","starter","field","sideline",
"outside","foul","team","Palalympics","Olympics","goalkeeper","torch",
"Coubertin","midfield","violation","round","start","final","red_card",
"yellow_card","doping","suspension","warn_off","match_point","game_point",
"tournament","club","team_doctor","compete","play","game","match","host",
"Ampfitryon","Samaranch","Rogge","opening_ceremony","closing_ceremony",
"closure","Athens","Lausanne","sprint","spurt","playoff","skittle_out",
"eliminate","medals_count","gold_medal_tally","Medals_podium",
"medal_presentation","rival","competitor","flame","tendon","pull",
"track","stamina","strength","Bubka","Pele","Ali","clerk","roll-call",
"urine_test","retire","steroid","run","jump","push","throw","shot","dive",
"Atlanta","Barcelona","Los_Angeles","Seoul","Muenchen","Helsinki","Paris",
"St.Louis","London","Stockholm","Antwerp","Amsterdam","Berlin","Melbourne",
"Rome","Tokyo","Mexico_city","Montreal","Moscow","Sydney","Chinese_Taipei",
"National_Stadium","National_Aquatics_Center","Beijing_Shooting_Range_Hall",
"Beijing_Olympic_Basketball_Gymnasium","Laoshan_Velodrome",
"Shunyi_Olympic_Rowing-Canoeing_Park","China_Agricultural_University_Gymnasium",
"Peking_University_Gymnasium","Beijing_Science_and_Technology_University_Gymnasium",
"Beijing_University_of_Technology_Gymnasium","CCTV-5","Beijing_Olympic_Green_Tennis_Court",
"Olympic_Sports_Center_Stadium","Olympic_Sports_Center_Gymnasium",
"Beijing_Workers'_Stadium","Capital_Indoor_Stadium","Fengtai_Sports_Center_Softball_Field"
,"Yingdong_Natatorium_of_National_Olympic_Sports_Center","Laoshan_Mountain_Bike_Course",
"Beijing_Shooting_Range_CTF","Beijing_Institute_of_Technology_Gymnasium",
"Beijing_University_of_Aeronautics_&_Astronautics_Gymnasium",
"Fencing_Hall_of_National_Convention_Center","Beijing_Olympic_Green_Hockey_Stadium",
"Beijing_Olympic_Green_Archery_Field","Beijing_Wukesong_Sports_Center_Baseball_Field",
"Chaoyang_Park_Beach_Volleyball_Ground","Laoshan_Bicycle_Moto_Cross_(BMX)_Venue",
"Triathlon_Venue","Road_Cycling_Course","Water_Cube","Bird_Nest","bodybuilding",
"playground","sneaker","racket","bat","pass","team_work","cooperate","privity"};

 * */


public class doc {
	static String[] concept1={"cube","mining","classfication","OLAP","confidence",
		"rule","correlation","cluster","support","class","Gini",
		"CART","CF","ROCK","CMAR","noisy","extraction","DataDetective",
		"FPtrees","AGM","IDF","threshold","sequence","warehouse","itemset",
		"sampling","VFDT","VIPS","WaveCluster","MineSet"};
	static int length_concept1=concept1.length;
	
	static String[] concept2={"database","SQL","Oracle","VFP","ACCESS","Sqlserver",
		"field","segment","record","relation","domain","attribute",
		"tuple","ODBC","view","constraint","trigger","dictionary","DML",
		"role","index","DB","OLE","DBMS","DDL","MySql","SDDL","query",
		"key","cursor"
		};
	static int length_concept2=concept2.length;
	
	static String[] concept3={"OS","Windows","unix","linux","MAC","BSD","shell","kernel","operating",
		"system","thread","process","solaris","Microsoft","","IBM","HP","core","scalability","performance",
		"persecond","operation","run","security","background","application","priority","handle","operand",
		"osfile","bin","etc"
		};
	static int length_concept3=concept3.length;
	
	static String[] genalconcept1={"IT","backup","bell","bit","BOOL","bus","byte","BA","BC",
		"BYS","base","feedback","FAC","FAD","gate","giga","host","hardware",
		"HAB","keyboard","KE","line","language","number","NAF","NAH","NAI",
		"information","technology","data","computer","AI","software","communication",
		"workstation","storage","retrival","digital","terminal","cybernetics",
		"cell","hypermedia","coding","simulation","CAM","code","CIMS","3D","knowledge",
		"minicomputer","notebook","desktop","pattern","model","disk","driver","memory",
		"cable","buffer","unit","CPU","control","ROM","DRAM","RAM","counter","chip",
		"decoder","register","card","IC","VLSI","monitor","track","screen","adapter",
		"power","interface","algorithm","user","text","file","document","development","program",
		"metadata","shareware","structure","compiler","statment","directory","identifier",
		"system","engineering","chart","design","evaluation","reliability","complexity",
		"analysis"};
	static int length_genalconcept1=genalconcept1.length;
	
	static String[] concept4={"soccer","football","corner","slide_tackle","header","penalty_kick",
		"offside","free_kick","kick-off_circle","half-way_line","goaltender","goalie",
		"left_back","right_back","centre_half_back","half_back","left_half_back",
		"right_half_back","centre_forward","bicycle_kick","chest-high_ball","goal_kick",
		"grounder","spot_kick","throw-in","chesting","dribbling","finger-tip_save","FIFA","UEFA"};
	static int length_concept4=concept4.length;
	
	static String[] concept5={"track_and_field","athletics","sprint","middle-distance_race",
		"long-distance_race","marathon","relay_race","hurdles","steeplechase","javelin","discas",
		"decathlon","heptathlon","triathlon","triplr_jump","hammer","walking","shot_put",
		"high_jump","long_jump","pole_vault","clear_the_bar","obstacle_race","crouch_start",
		"attack_the_hurdle","beat_the_gun","baton_exchange","Dayron","Robles","Liu_Xiang"};
	
	static int length_concept5=concept5.length;
	static String[] concept6={"gymnastics","piked_jump","buck","high_bar","shoulder_stand","hand_ring",
		"required_routine","bounding_table","parallel_bars","horizontal_bar","uneven_bars",
		"balance_beam","gym_bench","floor_exercise","exercise_with_clubs","exercise_with_ribbons",
		"vault","vaulting_block","back_handspring","front_handspring","cartwheel","body_bent",
		"back_flip","dismount","free_flight","grip_change","handstand_turn","handstand_with_swing"
		,"tish_flop","running_on_toes"};
	static int length_concept6=concept6.length;
	
	static String[] genalconcept2={"sport","IOC","winner","record_holder","record_breaker",
		"world_record","champion","runner-up","third_place","medalist","Gold_Medal",
		"Silver_Medal","Bronze_Medal","final_result","total_points","placement",
		"rank","final_placing","medal_tally","win_title","defend_title",
		"victory_ceremony","judge","referee","starter","field","sideline",
		"outside","foul","team","Palalympics","Olympics","goalkeeper","torch",
		"Coubertin","midfield","violation","round","start","final","red_card",
		"yellow_card","doping","suspension","warn_off","match_point","game_point",
		"tournament","club","team_doctor","compete","play","game","match","host",
		"Ampfitryon","Samaranch","Rogge","opening_ceremony","closing_ceremony",
		"closure","Athens","Lausanne","sprint","spurt","playoff","skittle_out",
		"eliminate","medals_count","gold_medal_tally","Medals_podium",
		"medal_presentation","rival","competitor","flame","tendon","pull",
		"track","stamina","strength","Bubka","Pele","Ali","clerk","roll-call",
		"urine_test","retire","steroid","run","jump","push","throw","shot","dive",
		"Atlanta","Barcelona","Los_Angeles","Seoul","Muenchen","Helsinki","Paris",
		"St.Louis","London","Stockholm","Antwerp","Amsterdam","Berlin","Melbourne",
		"Rome","Tokyo","Mexico_city","Montreal","Moscow","Sydney","Chinese_Taipei",
		"National_Stadium","National_Aquatics_Center","Beijing_Shooting_Range_Hall",
		"Beijing_Olympic_Basketball_Gymnasium","Laoshan_Velodrome",
		"Shunyi_Olympic_Rowing-Canoeing_Park","China_Agricultural_University_Gymnasium",
		"Peking_University_Gymnasium","Beijing_Science_and_Technology_University_Gymnasium",
		"Beijing_University_of_Technology_Gymnasium","CCTV-5","Beijing_Olympic_Green_Tennis_Court",
		"Olympic_Sports_Center_Stadium","Olympic_Sports_Center_Gymnasium",
		"Beijing_Workers'_Stadium","Capital_Indoor_Stadium","Fengtai_Sports_Center_Softball_Field"
		,"Yingdong_Natatorium_of_National_Olympic_Sports_Center","Laoshan_Mountain_Bike_Course",
		"Beijing_Shooting_Range_CTF","Beijing_Institute_of_Technology_Gymnasium",
		"Beijing_University_of_Aeronautics_&_Astronautics_Gymnasium",
		"Fencing_Hall_of_National_Convention_Center","Beijing_Olympic_Green_Hockey_Stadium",
		"Beijing_Olympic_Green_Archery_Field","Beijing_Wukesong_Sports_Center_Baseball_Field",
		"Chaoyang_Park_Beach_Volleyball_Ground","Laoshan_Bicycle_Moto_Cross_(BMX)_Venue",
		"Triathlon_Venue","Road_Cycling_Course","Water_Cube","Bird_Nest","bodybuilding",
		"playground","sneaker","racket","bat","pass","team_work","cooperate","privity"};

	static int length_genalconcept2=genalconcept2.length;
	static String[] unioconcept={"abacus","abolish","abstract","abandon","abase","abate","abbey",
		"abdicate","abdomen","abhor","abide","abnarmal","abound","ban","banana","beer","bear",
		"bad","ball","bake","back","bound","broad","board","blind","blue","bright","black","base",
		"bolish","business","bike","bar","bus","build","bat","bank","bury","brond","brave","break",
		"breath","bath","bring","brown","bush","book","car","cat","cash","cave","case","count",
		"call","currency","crazy","crime","chip","chick","clock","cook","company","corperation",
		"camp","campus","collge","clever","clear","clean","colegue","check","clerk","column",
		"classes","cup","cap","capsure","care","cut","cotton","coke","cheap","data","down","dish",
		"date","desk","dive","document","door","dark","dawn","due","dig","dog","deer","dare","disk",
		"dance","evening","eve","erase","electronic","electrisity","election","eye","ear","empty",
		"evil","edge","factory","far","farm","form","find","found","fund","fake","fate","face",
		"feed","finger","fuse","function","fan","fun","glass","grass","green","glue","gold",
		"golden","god","go","get","guy","gate","guard","gas","garden","guilt","grow","great",
		"ground","grain","glory","high","hip","hop","hide","have","huge","hug","hate","hit",
		"heat","hot","hen","hand","head","he","hind","human","history","host","house","hurt",
		"horse","home","hero","hunger","hope","image","imagine","ill","ink","inn","in","island",
		"isolate","identity","integer","infant","insure","injure","illostrate","income","indoor",
		"if","jacket","jet","joke","joy","jinkle","juice","know","knowledge","knee","knife","keep",
		"kiss","light","leg","link","land","lever","language","list","lie","lay","leaf","leave",
		"let","listen","lesson","lake","legal","lip","lap","lamp","lang","like","love","luck"};
	static int length_unioconcept=unioconcept.length;

	static int total_class=7;
	static int size_class1=10*50;
	static int size_class2=20*50;
	static int size_class3=20*50;
	static int size_class4=10*50;
	static int size_class5=20*50;
	static int size_class6=10*50;
	static int size_noisy_class1=10*50;
	static int doc_count=100*50;


	static int n=20;
	static int m=100;

	static double percentOfConcept=0.2;
	static double percentOfGenalconcept=0.3;
	static double percentOfUnionconcept=0.5;
	//////////////////////////////////////////////////////////////////////
	
	public static void main(String[] args) {
		int cluster=0;
		for(int i=1;i<=doc_count;i++){

			if(i<=size_class1)
				cluster=1;	
			else
				if(i<=size_class1+size_class2)
					cluster=2;
				else
					if(i<=size_class1+size_class2+size_class3)
						cluster=3;
					else
						if(i<=size_class1+size_class2+size_class3+size_class4)
							cluster=4;
						else
							if(i<=size_class1+size_class2+size_class3+size_class4+size_class5)
								cluster=5;
							else
								if(i<=size_class1+size_class2+size_class3+size_class4+size_class5+size_class6)
									cluster=6;
								else
									cluster=7;
					int wordCountOfDoc=getRandom(n,m);//�������ĵ���word��
			creatDocs(cluster,wordCountOfDoc,i);
		}
	}
	////////////////////////////////////////////////////////////////////////////
	//���濪ʼ���庯��
	/*����n��m���һ���������*/
	private static int getRandom(int n,int m){
		Random r=new Random();
		return r.nextInt(m)+n;	
	}
	
	/*����ĵ�?
	 * words��concept, genalconcept, unioconcept�а�������?
	 * word_conntΪ���ĵ�������
	 * clusterΪ���ĵ�������
	 * kΪ�ĵ���
	 * �ĵ���������ǣ�doci_clusterj.txt*/
	static void creatDocs(int cluster,int word_count,int k){
		
		try{
			File Myfile=new File("E:\\workspace\\TopicFinding\\doc1\\"+"doc"+k+"_cluster"+cluster+".txt");
			FileWriter fos = new FileWriter(Myfile);
			BufferedWriter a=new BufferedWriter(fos);
			if(cluster!=7){
			//���ѭ�����ֱ��concept��genalconcept��unionconcept�г��?
			for(int j=1;j<=(int)(word_count*percentOfConcept);j++){
				if(cluster==1){
					int r=getRandom(0,length_concept1-1);
					a.write(concept1[r]+" ");
					a.flush();
				}
				if(cluster==2){
					int r=getRandom(0,length_concept2-1);
					a.write(concept2[r]+" ");
					a.flush();
				}
				if(cluster==3){
					int r=getRandom(0,length_concept3-1);
					a.write(concept3[r]+" ");
					a.flush();
				}
				if(cluster==4){
					int r=getRandom(0,length_concept4-1);
					a.write(concept4[r]+" ");
					a.flush();
				}
				if(cluster==5){
					int r=getRandom(0,length_concept5-1);
					a.write(concept5[r]+" ");
					a.flush();
				}
				if(cluster==6){
					int r=getRandom(0,length_concept6-1);
					a.write(concept6[r]+" ");
					a.flush();
				}
			}
			for(int j=1+(int)(word_count*percentOfConcept);j<=(int)(word_count*percentOfGenalconcept);j++){
				if(cluster==1||cluster==2||cluster==3){
				int r=getRandom(0,length_genalconcept1-1);
				a.write(genalconcept1[r]+" ");
				a.flush();
				}
				else{
					int r=getRandom(0,length_genalconcept2-1);
					a.write(genalconcept2[r]+" ");
					a.flush();
				}					
			}
			for(int j=1+(int)(word_count*percentOfGenalconcept);j<=word_count;j++){
				int r=getRandom(0,length_unioconcept-1);
				a.write(unioconcept[r]+" ");
				a.flush();
			}
			}
			else
				for(int j=1;j<=word_count;j++){
					int r=getRandom(0,length_unioconcept-1);
					a.write(unioconcept[r]+" ");
					a.flush();
				}
				
			}catch(IOException e){} 	
	}	
}
