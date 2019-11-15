package extractdata;

import java.io.BufferedReader;
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

public class extractStaticParis {

	public static void main(String[] args) throws IOException, JSONException {
		String url = "https://opendata.paris.fr/api/records/1.0/search/?dataset=velib-emplacement-des-stations";
		JSONObject json = readJsonFromUrl(url);
		JSONObject data = (JSONObject) json.get("records");
		JSONArray stations = (JSONArray) data.get("stations");
		processStations(stations);
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

	private static void processStations(JSONArray stations) {

		String staticDBFile = "E:\\CPS2\\Year_2\\Semantic_Web\\Jena\\extractdata\\src\\main\\java\\extractdata\\staticDB";
		Model model = ModelFactory.createDefaultModel();

		String exNS = "http://www.example.com/";
		String geoNS = "https://www.w3.org/2003/01/geo/wgs84_pos#";

		model.setNsPrefix("ex", exNS);
		model.setNsPrefix("geo", geoNS);
		
		String stationURIPrefix = exNS + "Station:";

		for (Object station : stations) {

			JSONObject stationJson = (JSONObject) station;
			String ID = (String) stationJson.get("station_id");
			String name = (String) stationJson.get("name");
			double lat = (Double) stationJson.get("lat");
			double lon = (Double) stationJson.get("lon");
			int capacity =  (Integer) stationJson.get("capacity");

			// Create Station Resource
			Resource Station = model.createResource(stationURIPrefix + ID);
			Station.addProperty(RDF.type, RDFS.Class); // TODO station instance should be type of station class
			Station.addProperty(FOAF.name, name);
			Station.addProperty(RDF.value, String.valueOf(capacity));
			Station.addLiteral(model.createProperty(geoNS + "lat"), lat);
			Station.addLiteral(model.createProperty(geoNS + "long"), lon);
		}

		model.write(System.out, "turtle");
//		try {
//
//			if (!(new File(staticDBFile)).exists()) {
//				model.write(new FileOutputStream(new File(staticDBFile)), "TURTLE");
//			} else {
//				// TODO append to file
//			}
//
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
