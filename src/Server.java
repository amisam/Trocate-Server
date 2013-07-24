import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * A singleton server that holds and deals with Bin objects
 * @author hartesamu && John G
 *
 */
public class Server {

	private final String BIN_LOCATIONS = "locations.txt";

	private ArrayList<Bin> bins;
	private final String ALL_BINS = "allBins.txt";
	private File locations;

	private ArrayList<Bin> newBins;
	private final String UNCONFIRMED_BINS = "ToBeConfirmed.txt";
	private File unconfirmed;

	private ArrayList<Bin> oldBins;
	private final String FLAGGED_BINS = "FlaggedBins.txt";
	private File flagged;

	private final boolean ADD_TO_LIST		= true;
	private final boolean REMOVE_FROM_LIST	= false;

	private static Server server;

	/**
	 * If this server has not been created yet creates a
	 * single instance of the server and returns the server.
	 * @return Server, this server
	 */
	public static Server getServer(){
		if(server == null)	server = new Server();
		return server;
	}

	/**
	 * server constructor, loads data for the server
	 */
	private Server(){
		loadData();
	}

	/**
	 * If the serilizable file for all bins has no data
	 * (ie this is running for the first time) will load bins
	 * from a text file holding the Bins locations and bin type.
	 * Should only be called by loadData()
	 */
	private void readFromFile(){
		try {
			locations = new File(BIN_LOCATIONS);
			Scanner scan;
			scan = new Scanner(locations);
			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				String[] split = line.split("\t");
				double[] coords = new double[2];
				for(int i = 0; i < coords.length; i++)
					coords[i] = Double.parseDouble(split[i]);
				Bin newBin = new Bin(coords[0], coords[1], split[2]);
				bins.add(newBin);
			}
		} catch (IOException e) {
			System.out.println("404: File not found - "+BIN_LOCATIONS);
		}
		writeList(bins, ALL_BINS);
	}

	/**Loads the data for the server.
	 * Should only be called by the server's constructor.
	 * First:	load the data for all bins
	 * Second:	load the data for new bins yet to be confirmed
	 * Third:	load the data for flagged bins to be assessed
	 */
	private void loadData(){
		bins = new ArrayList<Bin>();
		newBins = new ArrayList<Bin>();
		oldBins = new ArrayList<Bin>();
		try{
			locations = new File(ALL_BINS);
			if(!locations.exists())	locations.createNewFile();
			unconfirmed = new File(UNCONFIRMED_BINS);
			if(!unconfirmed.exists()) unconfirmed.createNewFile();
			flagged = new File(FLAGGED_BINS);
			if(flagged.exists()) flagged.createNewFile();
		} catch (IOException e){
			System.out.println("Should not reach here!\n if a file does not exist this should create said file.");
		}

		InputStream file;
		InputStream buffer;
		ObjectInput input;

		try{
			//use buffering
			file = new FileInputStream(ALL_BINS);
			buffer = new BufferedInputStream(file );
			input = new ObjectInputStream( buffer );
			try{
				//deserialize the map
				Object readObject = input.readObject();
				bins = (ArrayList<Bin>)readObject;
			}
			finally{
				input.close();
			}
		} catch(ClassNotFoundException ex){
			System.out.println("Error in new bins file. Class not found.");
			readFromFile();
		} catch(IOException ex){
			System.out.println("404: File not found - "+ALL_BINS);
			readFromFile();
		}

		try{
			//use buffering
			file = new FileInputStream(unconfirmed);
			buffer = new BufferedInputStream(file );
			input = new ObjectInputStream( buffer );
			try{
				//deserialize the map
				Object readObject = input.readObject();
				newBins = (ArrayList<Bin>)readObject;
			}
			finally{
				input.close();
			}
		} catch(ClassNotFoundException ex){
			System.out.println("Error in new bins file. Class not found.");
		} catch(IOException ex){
			System.out.println("404: File not found - "+UNCONFIRMED_BINS);
			writeList(newBins, UNCONFIRMED_BINS);
		}

		try{
			//use buffering
			file = new FileInputStream(flagged);
			buffer = new BufferedInputStream(file );
			input = new ObjectInputStream( buffer );
			try{
				//deserialize the map
				Object readObject = input.readObject();
				oldBins = (ArrayList<Bin>)readObject;
			}
			finally{
				input.close();
			}
		} catch(ClassNotFoundException ex){
			System.out.println("Error in old bins file. Class not found.");
		} catch(IOException ex){
			System.out.println("404: File not found - "+FLAGGED_BINS);
			writeList(oldBins, FLAGGED_BINS);
		}

	}

	/**
	 * Get an unmodifiable list of the servers bins.
	 * This stops other classes from being able to
	 * add/remove from the servers bin list
	 * @return List <Bin>, a list of the servers bins
	 */
	protected List<Bin> allBins(){
		return Collections.unmodifiableList(bins);
	}


	/*==============Serilizable methods written by John and myself==================*/

	/*================= Writing to file methods Synchronized=================*/
	/**
	 * changes the new bins list by adding or removing bins. when finished will write
	 * the changed list to file
	 * @param bin, a bin to be added or removed
	 * @param add, a boolean informing us as to weather the given bin is
	 *			to be added	or removed
	 * @return boolean, true if the list newBin successfully writes to file
	 */
	private synchronized boolean changeNew(Bin bin, boolean add){
		if(add) newBins.add(bin);
		else	newBins.remove(bin);
		return writeList(newBins, UNCONFIRMED_BINS);
	}

	/**
	 * changes the old bins list by adding or removing bins. when finished will write
	 * the changed list to file
	 * @param bin, a bin to be added or removed
	 * @param add, a boolean informing us as to weather the given bin is
	 *			to be added	or removed
	 * @return boolean, true if the list oldBin successfully writes to file
	 */
	private synchronized boolean changeOld(Bin bin, boolean add){
		if(add)	oldBins.add(bin);
		else	oldBins.remove(bin);
		return writeList(oldBins, FLAGGED_BINS);
	}


	/*================= Add bins to a list to either be =================*/
	/*================= added or removed from all bins. =================*/
	/**
	 * Adds a bin object to a list waiting for an admin to accept or
	 * reject the proposed bin
	 * @param lat, lat of the new bin
	 * @param lng, lng of the new bin
	 * @param type, the new bins type
	 * @return boolean, returns the result of the changeNew method
	 */
	protected boolean addBin(double lat, double lng, String type){
		// add to the list of new bins (List<String> newBins)
		Bin newBin = new Bin(lat, lng, type);
		return changeNew(newBin, ADD_TO_LIST);
	}
	/**
	 * Adds a flag to a bin object, placing it into a list waiting for an admin
	 * to delete the bin or confirm it is there
	 * @param lat, lat of the flagged bin
	 * @param lng, lng of the flagged bin
	 * @param type, flagged bin type
	 * @return boolean, returns the result of the changeOld method
	 */
	protected boolean flagBin(double lat, double lng, String type){
		Bin newBin = new Bin(lat, lng, type);
		return changeOld(newBin, ADD_TO_LIST);
	}

	/*================= Assess bins to be added or removed. =================*/
	/**
	 * Gives a list of new bins to be assessed by the admin, to the admin
	 * @return String[], an array of bin info from the newBins list
	 */
	protected String[] assessBins(){
		//Bin[] temp = newBins.toArray(new Bin[0]);
		String[] returnValue = new String[newBins.size()];
		for(int i = 0; i < returnValue.length; i++)
			returnValue[i] = newBins.get(i).location();
		return returnValue;
	}
	/**Gives a list of old bins to be assess by the admin, to the admin.
	 * @return String[], an array of bin info from the oldBins list
	 */
	protected String[] assessFlags(){
		Bin[] temp = oldBins.toArray(new Bin[0]);
		String[] returnValue = new String[temp.length];
		for(int i = 0; i < temp.length; i++)
			returnValue[i] = temp[i].location();
		return returnValue;
	}

	/*================= confirm the new/old bins to be added/removed. =================*/
	/**When given some valid bin information the given bin can be confirmed as a real bin and
	 * added to the main list of bins. Weather the bin is confirmed or denied it is removed
	 * from the new bins list
	 * @param binInfo, information about the bin in question
	 * @param add, a boolean indication weather to add the given bin
	 * @return boolean, returns true if the server received to bin information
	 */
	protected boolean confirmBin(String binInfo, boolean add){
		String[] split = binInfo.split("\t");
		Bin bin = new Bin(Double.parseDouble(split[0]), Double.parseDouble(split[1]), split[2]);
		changeNew(bin, REMOVE_FROM_LIST);

		if(add)	alterMainList(bin, ADD_TO_LIST);

		return true;
	}
	/**When given some valid bin information the given bin can be confirmed as a bin is
	 * missing and should be removed from the list of all bins. Weather the bin is
	 * removed or confirmed it is removed from the old bins list
	 * @param binInfo, information about the bin in question
	 * @param remove, a boolean indication weather to remove the given bin
	 * @return boolean, returns true if the server received to bin information
	 */
	protected boolean confirmFlag(String binInfo, boolean remove){
		String[] split = binInfo.split("\t");
		Bin bin = new Bin(Double.parseDouble(split[0]), Double.parseDouble(split[1]), split[2]);
		changeOld(bin, REMOVE_FROM_LIST);

		if(remove)	alterMainList(bin, REMOVE_FROM_LIST);

		return true;
	}
	/**if a bin is be added or removed to/from the main list it will
	 * pass through here.
	 * @param bin, the bin in question
	 * @param add, indicates weather the bin is to be added or removed from
	 * 				the list of all bins
	 * @return boolean, true if the list of all bins was altered and written to file
	 */
	private synchronized boolean alterMainList(Bin bin, boolean add){
		if(add)	{
			if(bins.add(bin))
				return writeList(bins, ALL_BINS);
		}
		else {
			if(bins.remove(bin))
				return writeList(bins, ALL_BINS);
		}
		return false;
	}

	/**Takes a given list and file location and writes the ArrayList to a file located
	 * at the given filepath.
	 * @param write, the ArrayList to write to file
	 * @param filePath, the location of the file to be written to
	 * @return boolean, true if the array list is written to file
	 * 					false if an error occurred
	 */
	private synchronized boolean writeList(ArrayList<Bin> write, String filePath){
		try{
			//use buffering
			OutputStream file = new FileOutputStream(filePath);
			OutputStream buffer = new BufferedOutputStream( file );
			ObjectOutput output = new ObjectOutputStream( buffer );
			try{
				output.writeObject(write);
			}
			finally{
				output.close();
			}
		}
		catch(IOException ex){
			System.out.println("Unable to write to "+filePath);
			ex.printStackTrace();
			return false;
		}
		return true;
	}

}
