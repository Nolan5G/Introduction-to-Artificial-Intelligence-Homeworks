import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

class Game
{
	final double numberTournaments = 10;
	final double normalDeviation = 0.74;
	final double mutationRate = 0.94;
	final double probability_of_winner_survival = 0.52;
	final int numberOfChromosomes = 200;
	
	Random r = new Random();
	Matrix population = new Matrix(numberOfChromosomes, 291);
	Matrix fitness = new Matrix(numberOfChromosomes, 4);
	

	double[] evolveWeights() throws Exception
	{
		// Create a random initial population
		for(int i = 0; i < numberOfChromosomes; i++)
		{
			double[] chromosome = population.row(i);
			for(int j = 0; j < chromosome.length; j++) {
				chromosome[j] = normalDeviation * r.nextGaussian();
			}
			
			double[] chromosomeFitness = fitness.row(i);
			chromosomeFitness[0] = 0; // Tracks Generation
			chromosomeFitness[1] = 0; // Tracks Wins
			chromosomeFitness[2] = 0; // Tracks Draws
			chromosomeFitness[3] = 0; // Tracks Losses
		}

		// Evolve the population
		// todo: YOUR CODE WILL START HERE.
		//       Please write some code to evolve this population.
		//       (For tournament selection, you will need to call Controller.doBattleNoGui(agent1, agent2).)
		for(int trials = 0; trials < numberTournaments; trials++) {
			for(int individual = 0; individual < population.rows(); individual++) {
				double chanceEvent = r.nextDouble();
				if(chanceEvent < mutationRate) {
					mutate(individual);
				}
				if(chanceEvent < 0.90) {
					int individualB = r.nextInt(population.rows());
					doTournament(individual, individualB);
				}
			}
			// Monitor the fitness
			int[] result = FitnessUtil.findIndividualWithBestRecord(fitness);
			System.out.println(trials + "|" + result[0] + "|" + (int)fitness.row(result[0])[0] + "|" + result[1] + "|" + result[2] + "|" + result[3]);
		}

		// Return an arbitrary member from the population
		return population.row(r.nextInt(population.rows()));
	}
	
	void mutate(int individual) {
		double[] chromosome = population.row(individual);
		int element = r.nextInt(population.cols());
		chromosome[element] = normalDeviation * r.nextGaussian();
	}
	
	void doTournament(int individualA, int individualB) throws Exception {
		int result = Controller.doBattleNoGui(new NeuralAgent(population.row(individualA)), new NeuralAgent(population.row(individualB)));
		
		if(result == 1) {
			fitness.row(individualA)[1]++;
			fitness.row(individualB)[3]++;
		}
		else if(result == -1) {
			fitness.row(individualA)[3]++;
			fitness.row(individualB)[1]++;
		}
		else if(result == 0) {
			fitness.row(individualA)[2]++;
			fitness.row(individualB)[2]++;
		}
		
		if(r.nextDouble() < probability_of_winner_survival) {
			if(result == 1) {
				population.removeRow(individualA);
				fitness.removeRow(individualA);
				replenishPopulation();
			}
			else if(result == -1) {
				population.removeRow(individualB);
				fitness.removeRow(individualB);
				replenishPopulation();
			}
		}
		else {
			if(result == 1) {
				population.removeRow(individualB);
				fitness.removeRow(individualB);
				replenishPopulation();
			}
			else if(result == -1){
				population.removeRow(individualA);
				fitness.removeRow(individualA);
				replenishPopulation();
			}
		}
	}
	
	void replenishPopulation() {
		// Based my cross-over on the Genetic Algorithms tutorial for Order One Crossovers
		// I'm taking some faith that Order One Crossover is supposedly the most efficient crossover operation, so that's that.
		// Link to video: https://www.youtube.com/watch?v=4YjNe3qvVlI
		// Also referenced this tutorial on how the Crossover operation works: http://www.rubicite.com/Tutorials/GeneticAlgorithms/CrossoverOperators/Order1CrossoverOperator.aspx
		
		double[] child = population.newRow();
		double[] childFitness = fitness.newRow();
		int rowLength = population.rows();
		int colLength = population.cols();
		
		// Choose parents
		int parent1Index = r.nextInt(rowLength);
		int parent2Index = r.nextInt(rowLength);
		final double[] parent1 = population.row(parent1Index);
		final double[] parent2 = population.row(parent2Index);
		
		// Create new child that will be the crossover of p1 and p2
		for(int i = 0; i < colLength; i++) {
			child[i] = Double.MIN_VALUE;
		}
		
		// Generate index points for crossover.  Ensure that the left is >= right of the copy section
		int sectionLeft = r.nextInt(colLength);
		int sectionRight = r.nextInt(colLength);
		while(sectionLeft == sectionRight) { 
			sectionLeft = r.nextInt(colLength); 
			sectionRight = r.nextInt(colLength);
		}
		if(sectionLeft > sectionRight) {
			int temp = sectionLeft;
			sectionLeft = sectionRight;
			sectionRight = temp;
		}
		
		// Copy the section of parent #1 to child
		for(int i = sectionLeft; i <= sectionRight; i++) {
			child[i] = parent1[i];
		}
		
		// I stopped caring at this point about crossover correctness.
		if(sectionRight != (colLength - 1)) {
			for(int i = sectionRight; i < colLength; i++ ) {
				child[i] = parent2[i];
			}
		}
		if(sectionLeft != 0) {
			for(int i = 0; i < sectionLeft; i++) {
				child[i] = parent2[i];
			}
		}
		
		// Set child's generation #
		childFitness[0] = Math.max(fitness.row(parent1Index)[0], fitness.row(parent2Index)[2]) + 1;
		
		// Set child's win loss draw record at default
		childFitness[1] = 0;
		childFitness[2] = 0;
		childFitness[3] = 0;
	}
	
	void findResponse() throws Exception {
		
		ArrayList<double[]> candidateList = new ArrayList<double[]>();
		System.out.println("Generating Weights...");
		double[] w = evolveWeights();
		System.out.println("Weight Generation Complete!");
		System.out.println("Finding Winners...");
		ArrayList<IAgent> agents = new ArrayList<IAgent>();
		agents.add(new ReflexAgent());
		agents.add(new NeuralAgent(w));
		for(int i = 0; i < 1000; i++) {
			int result = Controller.doBattleNoGui(new ReflexAgent(), new NeuralAgent(w));
			if(result == -1) {
				candidateList.add(w);
			}
			w = population.row(r.nextInt(population.rows()));
		}
		System.out.println("Winners Found!");
		System.out.println("Finding Best Winner's Weight...");
		
		// Find the weight set with the best long term record
		int bestIndex = -1;
		int bestVictories = 0, bestTies = 0, bestDefeats = 0;
		int totalVictories = 0, totalDefeats = 0, totalTies = 0;
		
		for(int i = 0; i < candidateList.size(); i++) {
			// Reset 
			totalVictories = 0;
			totalDefeats = 0;
			totalTies = 0;
			
			// Trial
			for(int trial = 0; trial < 20; trial++) {
				int result = Controller.doBattleNoGui(new ReflexAgent(), new NeuralAgent(candidateList.get(i)));
				if(result == -1) {
					totalVictories++;
				}
				else if(result == 1) {
					totalDefeats++;
				}
				else if(result == 0) {
					totalTies++;
				}
			}
			
			// If current candidate is better, swap	
			if(FitnessUtil.compareRecords(bestVictories, bestTies, bestDefeats, totalVictories, totalTies, totalDefeats) < 0) {
				bestVictories = totalVictories;
				bestTies = totalTies;
				bestDefeats = totalDefeats;
				bestIndex = i;
			}	
		}
		
		System.out.println("Best Index (" + bestIndex + ") = Wins: " + totalVictories + ", Ties: " + totalTies + ", Losses: " + totalDefeats);
		System.out.println("Program Complete!");
	}
	
	public static void testWeight(double[] w) throws Exception {
		int totalVictories = 0, totalDefeats = 0, totalTies = 0;
		ArrayList<IAgent> agents = new ArrayList<IAgent>();
		agents.add(new ReflexAgent());
		agents.add(new NeuralAgent(w));
		for(int i = 0; i < 1000; i++) {
			int result = Controller.doBattleNoGui(new ReflexAgent(), new NeuralAgent(w));
			if(result == -1) {
				totalVictories++;
			}
			else if(result == 1) {
				totalDefeats++;
			}
			else if(result == 0) {
				totalTies++;
			}
		}
		System.out.println("Wins: " + totalVictories + ", Ties: " + totalTies + ", Losses: " + totalDefeats);
	}


	public static void main(String[] args) throws Exception
	{
		//Game game = new Game();
		//game.findResponse();
		
		double[] w = {0.17439399586272497, 0.0418128053624745, 0.8021884410574724, -0.36404917370416157, 0.9406218376552192, 0.9723295821804203, -0.37248743138097334, 1.1744851331085846, -1.6846823850080928, -0.1536772223887433, 0.6990238221523368, -1.7408197083425023, 0.5985137141232624, -0.22109233666345307, -0.6071688816018341, -0.11311075012565497, 0.19367767348524154, 0.4889503515051415, 0.7394480340441671, -0.8557375277464301, -0.3010209607956026, -0.4974063747583901, 0.6046978091744173, -0.14256343450844156, 0.0011041155067207018, 1.9120682460425504, 0.0632047775567107, -1.4182088749923227, 0.6841772117447636, 0.28095018863203447, -0.2681048375712328, -1.554333073646085, -0.8287173466893528, -1.450043824576616, 0.8761000878904324, -0.966107890523423, 1.0584667907666039, -0.6845752341979903, 0.41484313026508934, 0.736958196198176, 0.049973109250952726, 0.13761912600490592, -1.4377014442871687, -0.784780851306446, 0.35112413674144294, -0.1262672371688785, 0.4382551441638005, 0.39280398012435364, 0.9773949960111346, 0.34579105515868164, 0.6208254201093087, -0.4312924705995128, 0.9132375086805296, 1.033844526794784, -1.2300602314873768, -1.3108204008145086, 0.6579245449184047, 0.9729613796910412, -0.27696837559222315, -0.3222035070862554, -0.3699617534520545, -0.25529937302114686, 0.3965269775949895, -1.0542123121198363, 0.6712374361637858, -0.1311571327373433, 0.05893343489421193, -0.008815438438972669, -1.3317415322137884, 0.15246447798339408, -1.0888590198445771, -0.44296778577451906, -1.0349047895818868, 0.1671498143980891, 0.4462860740589021, 0.03962717337400465, -0.09040929224688599, 0.267815703438667, 0.2872895472719745, -1.1993142486532518, -2.1318980374090724, -0.3495727043931131, -0.5318248854613661, 0.9266929232442109, 1.0088565045827198, -0.7992958690633472, -0.9201668187793121, 0.046577293209644134, -0.3488049099077616, -0.14254375531194519, 0.008987288419855088, 0.9387995991417131, -0.28124492097945614, -0.43676011877241255, -0.40317638683063334, 0.85285732620615, 1.338397984590488, -0.04550019277143333, -0.46502773818840365, 0.7653232442684534, 0.7541519802975757, 1.3957862951983113, 0.285243854552461, -0.03933666369563137, 2.0135718410054846, 0.6912997695222926, 1.2587981577020875, 0.5610278453611498, -1.7612930349626597, -0.5818696641632468, -0.1576843228382511, 1.0376732976057834, -1.085705034550888, -0.29714867827792013, 0.07737814781472405, 0.8972543143015645, -1.2231369183810914, -0.44813688839051424, 0.01033052282834853, 0.26939451776580625, 0.035649071799423906, 0.12774005226759594, 1.8003391249457905, -1.095865111636731, 0.789769082839479, -1.1568051483412918, -0.47738308024927384, 0.944951038303268, 1.0593078858854035, 0.556086982289304, -0.46787997024789224, -0.8024711227413903, 1.4381896609414446, -0.0401945973782575, 1.407209118299964, 0.9215693965482316, -0.29182124472069304, -0.7525217343849819, -0.11431635562915063, -0.22576429393393085, -0.5063353443753499, -1.011291272371668, 1.0145888082604133, -0.47806586377574406, -0.2519394439481957, -0.6186965573298248, 0.6022967885914827, 0.2487810725132432, -0.38630071135701144, 0.44126063083922473, -0.12448502681970876, -0.4934986338412902, 0.3321659664326151, 0.03439946428748652, -0.762176261777284, 0.04187677213406981, 0.3094192736651332, 0.1951631156529636, -0.7393746620472463, -0.9006430066290498, 0.5447419896939883, 0.4855393815730071, -0.4760285406693968, -0.12530185328863724, -0.5587149934701141, 0.4356212657984019, -0.027928028977729103, 1.9748870796760158, 0.7192900028657399, -0.5242835789604496, -0.3792446156135987, -0.4342142801764584, -0.33616023938003486, 1.0225554446172263, -0.43572479107433454, -0.8577584858321197, 0.1399151665632708, 1.0648006727214274, 0.43163913944951493, 0.24013388004873415, 0.31336401877481557, -0.2006068175914818, -1.6621020428034996, -0.682140365691177, 0.45094903526791563, -0.2664176323219697, 0.739965283502392, -1.3732493052877908, -1.709606382591592, 0.555079819961802, 0.0383891126838063, 0.23492427494169724, 0.7821918469074222, 0.43730764005614975, -0.1093480355982583, 0.6598792773272322, -0.8365025447825113, -0.15958926850836516, 0.4923151381138471, -0.6151634792875912, 0.9866771069952087, -0.5572747161335522, 1.0989237565760228, 0.5729780134309358, 0.664281644255099, -0.050063883111634254, -0.27936481032397, 0.00342840304950496, 1.0762062139432456, 1.28658337205433, 0.7539472550968342, -0.1974615189779519, 0.40528024802050866, -0.28412352559740106, 0.40228347179316143, -1.08387749571502, 0.14513403364587288, -1.0043945728048993, -0.893562776154105, 0.21973727588234634, -0.163040526006858, 2.58463398781766, 0.7726034357531755, -0.7193281391089609, -1.1101792874373815, 0.3988434110524394, 0.39319561045272416, -0.8562174606114228, -1.0331914486473615, 0.3667440023827088, 0.3646118282488227, 1.019151981911587, 0.23821018113875173, 0.7937353021639638, 0.4923311263068359, 0.4701089054323509, 0.30961185687323717, 0.7608385967398826, 0.8686330314948144, 0.39863323780314325, -0.2174583574598012, -0.6901522546442544, 0.94004350031416, 0.9507184886313895, 0.6650963939729003, 0.3632506500102971, 0.04319431594473855, 0.17554372248148312, 0.20515681029528118, -0.5843723178058372, -0.47312467179559176, -0.6521123957892719, 0.23688049481711582, -0.22069454243310835, -0.34533701604141737, -0.5280831265100223, 0.2091408671713197, -0.03794818311675052, -0.42655801338463295, 0.4886082605335427, 1.0710118267915867, -0.5761135677624272, 0.3918219362162616, 0.1269745934455069, -1.2675666932831966, -0.3955189777533144, 1.6057270731102131, 0.3362620788573162, -1.358238944562203, -0.05383610686726982, 0.39421671700164856, 0.43966537015164436, -0.37991360773681326, -0.5659546834562246, -0.429988873662237, -0.12018918934141348, 0.8370111215339443, 1.1650606597342648, 0.2883390745301943, 0.0701074068153532, -0.30311692582092065, 0.3493732177214192, -0.09135129745691789, -1.6731719140239214, 0.1569561180851781, -0.5638065122561703, -0.7612465599861475, -0.5472240943505613, -0.4268007906932879, 1.894491855435151, 0.5228836993051327};
		
		//testWeight(w);
		
		Controller.doBattle(new ReflexAgent(), new NeuralAgent(w));
	}

}
