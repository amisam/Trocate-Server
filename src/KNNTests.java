
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * a couple of test written to test the K nearest neighbour method
 * @author hartesamu
 *
 */
public class KNNTests {

	Server server = Server.getServer();

	@Test
	public void findOne(){
		KNearestNeighbour KNN = new KNearestNeighbour(-41.29533972, 174.77131389, 1, server.allBins(), "all");
		String[] found = KNN.findBins();
		System.out.println("test one found: "+found[0]);
		assertEquals(found[0], "-41.29533972 174.77131389");
	}

	@Test
	public void findTen(){
		KNearestNeighbour KNN = new KNearestNeighbour(-41.29533972, 174.77131389, 10, server.allBins(), "all");
		String[] found = KNN.findBins();
		for(int i = 0; i < 10; i++){
			System.out.println("test ten found: "+found[i]);
		}
		assertEquals(found[0], "-41.29533972 174.77131389");
	}

}
