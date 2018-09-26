
public class FitnessUtil {
	public static int[] findIndividualWithBestRecord(Matrix fitness) {
		int[] result = new int[4];
		int wins = 0, draws = 0, losses = 0;
		
		for(int i = 0; i < fitness.rows(); i++) {
			
			wins = (int)(fitness.row(i)[1]);
			draws = (int)(fitness.row(i)[2]);
			losses = (int)(fitness.row(i)[3]);
			
			if(FitnessUtil.compareRecords(result[1], result[2], result[3], wins, draws, losses) < 0) {
				result[0] = i;
				result[1] = wins;
				result[2] = draws;
				result[3] = losses;
			}
		}
		
		return result;
	}
	
	public static int compareRecords(int win1, int draw1, int loss1, int win2, int draw2, int loss2) {
		int result = win1 - win2;
		if(result == 0) {
			result = draw1 - draw2;
			if(result == 0) {
				result = loss1 - loss2;
				result *= -1;
			}
		}
		return result;
	}
}
