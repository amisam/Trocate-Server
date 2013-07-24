import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

/**
 * a few server tests
 * @author hartesamu
 *
 */
public class ServerTests {

	Server server = Server.getServer();
	Bin[] testBins = new Bin[]{
			new Bin(-41.290706, 174.77452, "test_bin"),
			new Bin(-41.298706, 174.77552, "test_bin"),
			new Bin(-41.290736, 174.77452, "test_bin"),
			new Bin(-41.290506, 174.77452, "test_bin"),
			new Bin(-41.290701, 174.77452, "test_bin")
	};

	/*tests the addbin method and the confirm bin method*/
	@Test
	public void addBinTest() {
		for(int i = 0; i < testBins.length; i++)
			server.addBin(testBins[i].lat, testBins[i].lng, testBins[i].type);


		String[] confirm = server.assessBins();
		if(confirm == null) System.out.println("confirm is null");
		System.out.println("test add bin");
		for(int i = 0; i< confirm.length; i++){
			confirm[i]= confirm[i].trim();
			System.out.print(confirm[i] +":"+testBins[i].location());
			if(confirm[i].equals(testBins[i].location())) System.out.println("\tmatch");
			else 	fail();
		}

		System.out.println();
	}

	/*tests the flagbin method and the confirm flag method*/
	@Test
	public void flagTest(){
		for(int i = 0; i < testBins.length; i++)
			server.flagBin(testBins[i].lat, testBins[i].lng, testBins[i].type);

System.out.println();
		String[] flags = server.assessFlags();
		if(flags == null) System.out.println("flags are null");
		System.out.println("test flag bin");
		for(int i = 0; i< flags.length; i++){
			System.out.print(flags[i] +"\t:\t"+testBins[i].location());
			if(flags[i].equals(testBins[i].location())) System.out.println("\tmatch");
			else 	fail();
		}
	}

	/*tests that the server has alot of bins*/
	@Test
	public void allBinsTest(){
		List<Bin> all = server.allBins();
		if(all.size() > 800)
			System.out.println("all bins test passes");
	}
}
