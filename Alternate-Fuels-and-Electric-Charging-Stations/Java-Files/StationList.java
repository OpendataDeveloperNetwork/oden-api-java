/**This file was created for Terratap-Technologies-Inc by 
 * Cody Clattenburg, Sam Collins, Martin Suryadi, and Sergio Josue Villegas. 
 * This file is under the protection of the Apache 2.0 License.
 **/

package alternateFuelStations;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import com.google.gson.stream.JsonReader;

/**
 * <p>The StationList class is used to hold a collection of Station objects.
 * The Stations are to be read in from JSON files containing geographic data
 * about alternative fueling stations and formatted in the oden unified format.</p>
 * 
 * <p>StationList has several useful methods to sort, search, and filter the 
 * stations. It also includes multiple ways to read in and access the data.</p>
 * 
 * <p>This class makes use of the Station class which is used to represent a 
 * single alternative fueling station.</p>
 * @author Sam Collins
 * @version 1.7
 */
public class StationList {
	
	private ArrayList<Station> inputStations;
	private ArrayList<String> fuelTypes;
	
	private Station[] stations;
	
	/**
	 * <h1>StationList</h1>
	 * <p>public StationList(Reader json) throws IOException</p>
	 * <p>Creates a new StationList when given a Reader to a JSON file.
	 * For the list to be created properly the JSON file must contain
	 * geographic data (GeoJSON) for alternative fueling stations that 
	 * is in the oden unified format.</p>
	 * @param json - The reader reading in data from a JSON file
	 * @throws IOException if there is a problem with the Reader given
	 * through the parameters
	 */
	public StationList(Reader json) throws IOException {
		
		inputStations = new ArrayList<Station>();
		JsonReader reader = new JsonReader(json);
		
		try {
			inputStations = readStations(reader);
		} catch (IOException e) {
			System.err.println("Caught IOException: " + e.getMessage());
		} finally {
			reader.close();
		}
		
		stations = inputStations.toArray(new Station[inputStations.size()]);
		fuelTypes = readFuelTypes(stations);
	}
	
	/**
	 * <h1>StationList</h1>
	 * <p>public StationList(String filePath) throws IOException</p>
	 * <p>Creates a new StationList when given a file path (as a String) to a 
	 * folder containing JSON files. The file path must be the full system path 
	 * and the folder must only contain JSON files containing geographic data 
	 * (GeoJSON) for alternative fueling stations that is in the oden unified format.
	 * @param filePath - The file path to the folder containing the JSON files
	 * @throws IOException if there is a problem reading in data from the files
	 */
	public StationList(String filePath) throws IOException {
		
		inputStations = new ArrayList<Station>();
		File folder = new File(filePath);
		File[] fileList = folder.listFiles();
		
		try {
			
			FileInputStream fileStream;
			for (int i = 0; i < fileList.length; i++) {
				
				if (fileList[i].isFile() && fileList[i].getName().endsWith(".json")) {
					
					fileStream = new FileInputStream(fileList[i]);
					ArrayList<Station> temp = readStations(new JsonReader(new InputStreamReader(fileStream, "UTF-8")));
					inputStations.addAll(temp);
				}
			}
		} catch (IOException e) {
			System.err.println("Caught IOException: " + e.getMessage());
		} 
		
		stations = inputStations.toArray(new Station[inputStations.size()]);
		fuelTypes = readFuelTypes(stations);
	}
	
	/**
	 * <h1>StationList</h1>
	 * <p>public StationList(InputStream[] inputStreams) throws IOException</p>
	 * <p>Creates a new StationList when given an array of InputStream objects that are 
	 * being reading JSON files. The InputStreams must be from JSON files 
	 * containing geographic data (GeoJSON) for alternative fueling stations that are 
	 * in the oden unified format.
	 * <p>It is recommended that this constructor be used with Android.</p>
	 * @param inputStreams - The array of InputStreams to the JSON files
	 * @throws IOException if there is a problem reading in data from the files
	 */
	public StationList(InputStream[] inputStreams) throws IOException {
		
		inputStations = new ArrayList<Station>();
		
		try {
			
			for (int i = 0; i < inputStreams.length; i++) {
									
				ArrayList<Station> temp = readStations(new JsonReader(new InputStreamReader(inputStreams[i], "UTF-8")));
				inputStations.addAll(temp);
			}
		} catch (IOException e) {
			System.err.println("Caught IOException: " + e.getMessage());
		} 
		
		stations = inputStations.toArray(new Station[inputStations.size()]);
		fuelTypes = readFuelTypes(stations);
	}
	
	/**
	 * <h1>getAllStations</h1>
	 * <p>public Station[] getAllStations()</p>
	 * <p>Retrieves the full list of Stations as an array.</p>
	 * @return The full list of Stations
	 */
	public Station[] getAllStations() {
		return stations;
	}
	
	/**
	 * <h1>getFuelTypes</h1>
	 * <p>public String[] getFuelTypes</p>
	 * <p>Retrieves the list of fuel types as an array of Strings.</p>
	 * @return The list of fuel types
	 */
	public String[] getFuelTypes() {
		return fuelTypes.toArray(new String[fuelTypes.size()]);
	}
	
	/**
	 * <h1>getClosestStation</h1>
	 * <p>public Station getClosestStation(double latitude, double longitude)</p>
	 * <p>Takes in the latitude and longitude as doubles and returns the 
	 * Station in the list that is closest to the given coordinates.</p>
	 * @param latitude - The latitude of the User
	 * @param longitude - The longitude of the User
	 * @return the Station closest to the given latitude and longitude
	 */
	public Station getClosestStation(double latitude, double longitude) {
		
		Station closest = null;
		double shortestDist = 0;

		if (stations != null) {
			
			closest = stations[0];
			shortestDist = Math.sqrt(Math.pow((stations[0].getLatitude() - latitude), 2) 
					+ Math.pow((stations[0].getLongitude() - longitude), 2));
		}
		
		for (int i = 0; i < stations.length; i++) {
			double currentDist = Math.sqrt(Math.pow((stations[i].getLatitude() - latitude), 2) 
					+ Math.pow((stations[i].getLongitude() - longitude), 2));
			
			if (currentDist < shortestDist) {
				shortestDist = currentDist;
				closest = stations[i];
			}
		}
		
		return closest;
	}
	
	/**
	 * <h1>sortByDistance</h1>
	 * <p>public Station[] sortByDistance(final double latitude, final double longitude)</p>
	 * <p>Takes in the latitude and longitude as doubles and returns the list of Stations
	 * sorted from closest to farthest away from the given coordinates. The list is returned
	 * as an array of Stations.</p>
	 * @param latitude - The latitude of the User
	 * @param longitude - The longitude of the User
	 * @return the list of Stations sorted by their distance to the given latitude and longitude
	 */
	public Station[] sortByDistance(final double latitude, final double longitude) {
		Station[] sortedStations = stations;
		
		Arrays.sort(sortedStations, new Comparator<Station>() {
			@Override
			public int compare(Station first, Station second) {

				double firstDist = Math.sqrt(Math.pow((first.getLatitude() - latitude), 2) 
						+ Math.pow((first.getLongitude() - longitude), 2));
				
				double secondDist = Math.sqrt(Math.pow((second.getLatitude() - latitude), 2) 
						+ Math.pow((second.getLongitude() - longitude), 2));
				
				if (firstDist < secondDist) {
					return -1;
				} else if (firstDist > secondDist) {
					return 1;
				} else {
					return 0;
				}
			}
		});
		
		return sortedStations;
	}
	
	/**
	 * <h1>searchByName</h1>
	 * <p>public Station[] searchByName(String name)</p>
	 * <p>Takes in a String and returns an array of all
	 * stations that have a name containing the keyword given.</p>
	 * <p>This method returns <b>null</b> if no results are found.</p>
	 * @param name - The name of the Station being searched for
	 * @return all the stations that have a name containing the given keyword
	 */
	public Station[] searchByName(String name) {
		
		ArrayList<Station> searchStations = new ArrayList<Station>();

		for (int i = 0; i < stations.length; i++) {
			if (stations[i].getName().toLowerCase().contains(name.toLowerCase())) {
				searchStations.add(stations[i]);
			}
		}
		
		if (searchStations.size() > 0) {
			return searchStations.toArray(new Station[searchStations.size()]);
		}
		
		return null;
	}
	
	/**
	 * <h1>searchByAddress</h1>
	 * <p>public Station[] searchByAddress(String address)</p>
	 * <p>Takes in a String and returns an array of all
	 * stations that have an address containing the keyword given.</p>
	 * <p>This method returns <b>null</b> if no results are found.</p>
	 * @param address - The address of the Station being searched for
	 * @return all the stations that have an address containing the given keyword
	 */
	public Station[] searchByAddress(String address) {
		ArrayList<Station> searchStations = new ArrayList<Station>();

		for (int i = 0; i < stations.length; i++) {
			if (stations[i].getAddress().toLowerCase().contains(address.toLowerCase())) {
				searchStations.add(stations[i]);
			}
		}
		
		if (searchStations.size() > 0) {
			return searchStations.toArray(new Station[searchStations.size()]);
		}
		
		return null;
	}

	/**
	 * <h1>getPublicStations</h1>
	 * <p>public Station[] getPublicStations()</p>
	 * <p>Returns an array of all stations that have an access value equal
	 * to "Public".</p>
	 * <p>This method returns <b>null</b> if no results are found.</p>
	 * @return a list of all stations with an accessibility of "Public"
	 */
	public Station[] getPublicStations() {
		ArrayList<Station> filterStations = new ArrayList<Station>();
		
		for (int i = 0; i < stations.length; i++) {
			if (stations[i].getAccess().equals("Public")) {
				filterStations.add(stations[i]);
			}
		}
		
		if (filterStations.size() > 0) {
			return filterStations.toArray(new Station[filterStations.size()]);
		}
		
		return null;
	}
	
	/**
	 * <h1>getPrivateStations</h1>
	 * <p>public Station[] getPrivateStations()</p>
	 * <p>Returns an array of all stations that have an access values
	 * other than "Public". Values can include "Private", "City Vehicle use only",
	 * etc.</p> 
	 * <p>This method returns <b>null</b> if no results are found.</p>
	 * @return a list of all stations with an accessibility other than "Public"  
	 */
	public Station[] getPrivateStations() {
		ArrayList<Station> filterStations = new ArrayList<Station>();
		
		for (int i = 0; i < stations.length; i++) {
			if (!stations[i].getAccess().equals("Public")) {
				filterStations.add(stations[i]);
			}
		}
		
		if (filterStations.size() > 0) {
			return filterStations.toArray(new Station[filterStations.size()]);
		}
		
		return null;
	}
	
	/**
	 * <h1>filterByFuelType</h1>
	 * <p>public Station[] filterByFuelTypes()</p>
	 * <p>Takes in a fuel type as a String and returns an array of 
	 * all stations with the given fuel type.</p>
	 * <p>The fuel type entered must be one of the already existing fuel types
	 * stored in the "fuelTypes" list in this class. To view the existing fuel
	 * types call the getFuelTypes() method.</p>
	 * <p>This method returns <b>null</b> if no results are found.</p>
	 * @param fuelType - The fuel type the stations are filtered by
	 * @return a list of all stations with the fuel type specified
	 */
	public Station[] filterByFuelType(String fuelType) {
		
		if (!fuelTypes.contains(fuelType)) {
			return null;
		}
		
		ArrayList<Station> filterStations = new ArrayList<Station>();
		
		for (int i = 0; i < stations.length; i++) {
			if (stations[i].getFuelType().equalsIgnoreCase(fuelType)) {
				filterStations.add(stations[i]);
			}
		}
		
		if (filterStations.size() > 0) {
			return filterStations.toArray(new Station[filterStations.size()]);
		}
		
		return null;
	}
	
	//Reads in the fuel types during construction
	private ArrayList<String> readFuelTypes(Station[] stations) {
		ArrayList<String> fuelTypes = new ArrayList<String>();
		
		if (stations != null) {
			fuelTypes.add(stations[0].getFuelType());
		}

		for (int i = 0; i < stations.length; i++) {
				
			if (!fuelTypes.contains(stations[i].getFuelType())) {
				fuelTypes.add(stations[i].getFuelType());
			}
		}
		
		return fuelTypes;
	}
	
	private ArrayList<Station> readStations(JsonReader reader) throws IOException {
		ArrayList<Station> readStations = new ArrayList<Station>();

		reader.beginArray();
		while (reader.hasNext()) {
			readStations.add(readStation(reader));
		}
		reader.endArray();
		
		return readStations;
	}
	
	private Station readStation(JsonReader reader) throws IOException {
		ArrayList<Double> coordinates = null;
		String fuelType = null;
		String name = null;
		String address = null;
		String access = null;
		
		reader.beginObject(); //Start of each station in json file
		while (reader.hasNext()) {
			String json = reader.nextName();
			if (json.equals("geometry")) { // Start of geometry section
				reader.beginObject();
				while (reader.hasNext()) {
					if (reader.nextName().equals("coordinates")) {
						coordinates = readCoordinates(reader); //Reads through the coordinate array in the json file
					} else {
						reader.skipValue(); //Ignores "type" : "feature"
					}
				}
				reader.endObject();
			} else if (json.equals("properties")) { //Start of properties section
				reader.beginObject();
				while (reader.hasNext()) {
					String property = reader.nextName();
					if (property.equals("fT")) {
						fuelType = reader.nextString();
					} else if (property.equals("nm")) {
						name = reader.nextString();
					} else if (property.equals("adr")) {
						address = reader.nextString();
					} else if (property.equals("ac")) {
						access = reader.nextString();
					} else {
						reader.skipValue();
					}
				}
				reader.endObject();
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		
		return new Station(coordinates.get(0), coordinates.get(1), name, fuelType, address, access);
	}
	
	private ArrayList<Double> readCoordinates(JsonReader reader) throws IOException {
	     ArrayList<Double> coordinates = new ArrayList<Double>();

	     reader.beginArray();
	     while (reader.hasNext()) {
	       coordinates.add(reader.nextDouble());
	     }
	     reader.endArray();
	     return coordinates;
	   }	
}