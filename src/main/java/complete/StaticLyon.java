package complete;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


// json file is written in FRENCH
public class StaticLyon {

	final String CITYNAME = "LYON";
	static String url = "https://download.data.grandlyon.com/ws/grandlyon/pvo_patrimoine_voirie.pvostationvelov/all.json?maxfeatures=100&start=1";
	static List<Station> stationsList = new ArrayList<Station>();
	static JSONArray stations = new JSONArray();
	
	public List<Station> processData() throws JSONException, IOException {
		processFile(url);
		processStations();
		return stationsList;		
	}
	
	public static void processFile(String url) {
		JSONObject json;
		try {
			json = readJsonFromUrl(url);
			JSONArray stationsOfPage = (JSONArray) json.get("values");
			concatArrays(stationsOfPage);
			if (json.has("next")) {
				String next = (String) json.get("next");
				processFile(next);
			} else {
				processStations();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public static void concatArrays(JSONArray stationsOfPage) {
		for (int i = 0; i < stationsOfPage.length(); i++) {
			stations.put(stationsOfPage.get(i));
		}
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	private static void processStations() {
		System.out.println(stations.length());
		for (Object station : stations) {
			JSONObject stationJson = (JSONObject) station;
			String ID = (String) stationJson.get("idstation");
			String name = (String) stationJson.get("nom");
			double lat = 0;
			double lon = 0;
			int capacity =  0;

			Station ss = new Station(ID, name, lat, lon, capacity);
			stationsList.add(ss);
		}
	}
}
