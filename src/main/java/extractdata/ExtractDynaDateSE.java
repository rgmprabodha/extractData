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
import org.apache.jena.rdf.model.ModelMaker;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ExtractDynaDateSE {
//	String exNS = "http://www.example.com/";
//	String geoNS = "https://www.w3.org/2003/01/geo/wgs84_pos#";

	public static void main(String[] args) throws IOException, JSONException {
		String url = "https://saint-etienne-gbfs.klervi.net/gbfs/en/station_status.json";
		JSONObject json = readJsonFromUrl(url);
		JSONObject data = (JSONObject) json.get("data");
		JSONArray stations = (JSONArray) data.get("stations");
		processStationDyna(stations);
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

	private static void processStationDyna(JSONArray stations) {

//		String staticDBFile = "C:\\Users\\roven\\git\\extractData\\src\\main\\java\\extractdata\\staticDB";
//		ModelMaker maker;
//		Model model = maker.getModel("\\model");

//		String exNS = "http://www.example.com/";
//		String geoNS = "https://www.w3.org/2003/01/geo/wgs84_pos#";

		extractStaticSE.model.setNsPrefix("ex", extractStaticSE.exNS);
		extractStaticSE.model.setNsPrefix("geo",extractStaticSE.geoNS);
		
//		String stationURIPrefix = exNS + "Station:";

		for (Object station : stations) {

			JSONObject stationJson = (JSONObject) station;
			String ID = (String) stationJson.get("station_id");
			int nava = (Integer) stationJson.get("num_bikes_available");
			int ndisa = (Integer) stationJson.get("num_bikes_disabled");
			int ndocava = (Integer) stationJson.get("num_docks_available");
			int ninstall = (Integer) stationJson.get("is_installed");
			int nrenting = (Integer) stationJson.get("is_renting");
			int nreturn = (Integer) stationJson.get("is_returning");
			int nupdatetime = (Integer) stationJson.get("last_reported");
//			double lat = (Double) stationJson.get("lat");
//			double lon = (Double) stationJson.get("lon");
//			int capacity =  (Integer) stationJson.get("capacity");

			// Create Station Resource
//			Resource Station = model.createResource(stationURIPrefix + ID);
			Resource StationDyna = extractStaticSE.model.getResource(extractStaticSE.stationURIPrefix + ID);
			System.out.print(StationDyna);
//			StationDyna.addProperty(RDF.type, RDFS.Class); // TODO station instance should be type of station class
//			StationDyna.addProperty(FOAF.name, name);
//			StationDyna.addProperty(RDF.value, String.valueOf(capacity));
			StationDyna.addLiteral(extractStaticSE.model.createProperty(extractStaticSE.exNS + "num_bikes_available"), nava);
			StationDyna.addLiteral(extractStaticSE.model.createProperty(extractStaticSE.exNS + "num_bikes_disabled"), ndisa);
			StationDyna.addLiteral(extractStaticSE.model.createProperty(extractStaticSE.exNS + "num_docks_available"), ndocava);
			StationDyna.addLiteral(extractStaticSE.model.createProperty(extractStaticSE.exNS + "is_installed"), ninstall);
			StationDyna.addLiteral(extractStaticSE.model.createProperty(extractStaticSE.exNS + "is_renting"), nrenting);
			StationDyna.addLiteral(extractStaticSE.model.createProperty(extractStaticSE.exNS + "is_returning"), nreturn);
			StationDyna.addLiteral(extractStaticSE.model.createProperty(extractStaticSE.exNS + "last_reported"), nupdatetime);
			
//			StationDyna.addLiteral(model.createProperty(exNS + "long"), lon);
		}

		extractStaticSE.model.write(System.out, "turtle");
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
