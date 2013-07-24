
/**Hub through which all url commands pass
 *
 * @author hartesamu
 *
 */
public class ServerFacade {

	private static int numLocations = 10;

	private static final String REPORT_SENT = "Thank you, your report has been sent to the admin";
	private static final String REPORT_ERROR = "There was an error while trying to send your report.\n please try again later";
	private static final String SEARCH_ERROR = "Please include a filter when searching";

	private static UsernamePassword usernameService = UsernamePassword.getUsernameService();

	private static Server server = Server.getServer();

	/**Takes a URL with the following parameters and searches for the closest num of Bins
	 * @param lat, the lat of a user to find bins for
	 * @param lng, the lng of a user to find bins for
	 * @param num, the number of close bins to find
	 * @param filter, a filter of which bins to find
	 * @return String[], an array of the bin information
	 */
	public static String[] getCloseBins(double lat, double lng, int num, String filter){
		int binNum = num;
		if(binNum < 1) binNum = numLocations;
		if(binNum >= server.allBins().size())
			binNum = server.allBins().size();
		if(filter == null) return new String[]{SEARCH_ERROR};
		KNearestNeighbour KNN = new KNearestNeighbour(lat, lng , binNum, server.allBins(), filter);

		String[] returnValue = KNN.findBins();
		if(returnValue == null) System.out.println("find bins is broken");
		//if(strArray.length != 2) System.out.println("wrong amount of arguments");

		return returnValue;
	}

	/**Takes a subject and some message content and sends a report to the admin
	 * @param subject, the topic of the report
	 * @param message, a description/complaint of the report
	 * @return String, a small message informing the user if the report was made
	 */
	public static String sendReport(String subject, String message){
		if(subject == null)	return REPORT_ERROR;
		SendEmail.textMessage(subject, message);
		return REPORT_SENT;
	}

	/**Will talk to the server and ask that it adds a new bin to its list of new bins
	 * @param lat, lat of the new bin
	 * @param lng, lng of the new bin
	 * @param type, type of the new bin
	 * @return boolean, returns result of the servers add bin method
	 */
	public static boolean addBin(double lat, double lng, String type){
		if(lat < -90  || lat > 90 ) return false;
		if(lng < -180 || lng > 180) return false;
		return server.addBin(lat, lng, type);
	}
	/**Will talk to the server and ask that it flags an old bin to its list of old bins
	 * @param lat, lat of the old bin
	 * @param lng, lng of the old bin
	 * @param type, type of the old bin
	 * @return boolean, returns result of the servers flag bin method
	 */
	public static boolean removeBin(double lat, double lng, String type){
		if(lat < -90  || lat > 90 ) return false;
		if(lng < -180 || lng > 180) return false;
		return server.flagBin(lat, lng, type);
	}

	/*==========Admin methods==========*/
	/*=================================*/
	/**Get the bin information for all the bins in the server
	 * @return String[], an array containing all the bin information in the server
	 */
	public static String[] allBins(){	// for admin's map
		return server.allBins().toArray(new String[0]);
	}

	/**Get the list of new bins submitted by users
	 * @return String[], an array containing information about the new proposed bins
	 */
	public static String[] getNewBins(){
		return server.assessBins();
	}
	/**A way for the admin to confirm or deny a new bin
	 * @param binInfo, the information of the bin in question
	 * @param add, a boolean indicating weather the bin is to be added or not
	 * @return boolean, returns the result from the servers confirmBin method
	 */
	public static boolean confirmNewBin(String username, String password, String binInfo, boolean add){
		if(usernameService.matchPassword(username, password))
			return server.confirmBin(binInfo, add);
		return false;
	}

	/**Get the list of old bins flagged by users
	 * @return String[], an array containing information about the old
	 * 					passibly missing bins
	 */
	public static String[] getFlaggedBins(){
		return server.assessFlags();
	}
	/**A way for the admin to confirm or deny a missing bin
	 * @param binInfo, the information of the bin in question
	 * @param remove, a boolean indicating weather the bin is to be removed or not
	 * @return boolean, returns the result from the servers confirmFlag method
	 */
	public static boolean confirmFlag(String username, String password, String binInfo, boolean remove){
		if(usernameService.matchPassword(username, password))
			return server.confirmFlag(binInfo, remove);

		return false;
	}


}
