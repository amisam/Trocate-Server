import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Written to convert the locations file to a disired format
 * (rewritten many times, fill of throw away code.... was)
 * @author hartesamu
 *
 */
public class Convert {

	private static String open	=	"temp.txt";
	private static String newFile = "newFormat.txt";
	private static int skipLines = 0;

	public static void main(String[] args){
		File openf = new File(open);
		if(!openf.exists()){
			System.out.println("location filepath invalid");
			System.exit(-1);
		}
		File outFile = new File(newFile);
		// will take a file and convert the given locations into GPS locations
		try {
			PrintStream out = new PrintStream(outFile);
			Scanner sc = new Scanner(openf);
			for(int i = 0; i < skipLines; i++)
				sc.nextLine();
			while (sc.hasNextLine()){
				String line = sc.nextLine();
				String[] split = line.split("\t");
				out.println(split[1]+"\t"+split[2]+"\tLitter_Bin");
			}
			sc.close();
			out.close();
			//letsTrySomthingNew(locations);
		} catch(FileNotFoundException e){;}

	}

	private static void format(){
		File locations = new File(open);
		if(!locations.exists()){
			System.out.println("location filepath invalid");
			System.exit(-1);
		}

		File outFile = new File(newFile);
		// will take a file and convert the given locations into GPS locations
		try {
			PrintStream out = new PrintStream(outFile);
			Scanner sc = new Scanner(locations);
			for(int i = 0; i < skipLines; i++)
				sc.nextLine();
			while (sc.hasNextLine()){
				String line = sc.nextLine();
				String[] split = line.split("\t");
				out.println(split[4]+"\t"+split[5]);
			}
			sc.close();
			out.close();
			//letsTrySomthingNew(locations);
		} catch(FileNotFoundException e){;}
	}
}
