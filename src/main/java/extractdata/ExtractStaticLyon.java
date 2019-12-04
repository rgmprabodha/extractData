package extractdata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ExtractStaticLyon {
	final String CITYNAME = "LYON";
	static JSONArray stations = new JSONArray();

	public static void main(String[] args) throws IOException, JSONException {
		String url = "https://download.data.grandlyon.com/ws/grandlyon/pvo_patrimoine_voirie.pvostationvelov/all.json?maxfeatures=100&start=1";
		JSONObject json = readJsonFromUrl(url);
		processFile(url);
	}

	public static void processFile(String url) {
		System.out.println("data extracting: " + url);
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
		System.out.println(stations);
		System.out.println(stations.length());

//		String staticDBFile = "C:\\Users\\roven\\git\\extractData\\src\\main\\java\\extractdata\\staticDB";
//		Model model = ModelFactory.createDefaultModel();

		String exNS = "http://www.example.com/";
		String geoNS = "https://www.w3.org/2003/01/geo/wgs84_pos#";

		extractStaticSE.model.setNsPrefix("ex", exNS);
		extractStaticSE.model.setNsPrefix("geo", geoNS);

		String stationURIPrefix = exNS + "Station:";

		for (Object station : stations) {

			JSONObject stationJson = (JSONObject) station;
			String ID = (String) stationJson.get("station_id");
			String name = (String) stationJson.get("name");
			double lat = (Double) stationJson.get("lat");
			double lon = (Double) stationJson.get("lon");
			int capacity = (Integer) stationJson.get("capacity");

			// Create Station Resource
			Resource Station = extractStaticSE.model.createResource(stationURIPrefix + ID);
			Station.addProperty(RDF.type, RDFS.Class); // TODO station instance should be type of station class
			Station.addProperty(FOAF.name, name);
			Station.addProperty(RDF.value, String.valueOf(capacity));
			Station.addLiteral(extractStaticSE.model.createProperty(geoNS + "lat"), lat);
			Station.addLiteral(extractStaticSE.model.createProperty(geoNS + "long"), lon);
		}

		extractStaticSE.model.write(System.out, "turtle");

		try {

			if (!(new File(extractStaticSE.staticDBFile)).exists()) {
				extractStaticSE.model.write(new FileOutputStream(new File(extractStaticSE.staticDBFile)), "TURTLE");
			} else {
				// TODO append to file
				 //fos = new FileOutputStream(file,true);//这里构造方法多了一个参数true,表示在文件末尾追加写入
				//extractStaticSE.model.write(new FileOutputStream(new File(extractStaticSE.staticDBFile),ture));
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
