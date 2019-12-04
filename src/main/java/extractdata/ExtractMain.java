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
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ExtractMain {

	static Model model;
	static String staticDBFile = "E:\\CPS2\\Year_2\\Semantic_Web\\Jena\\extractdata\\src\\main\\java\\extractdata\\statiyyyyy";
	static String stationURIPrefix;

	static String exNS;
	static String geoNS;

	public static void main(String args[]) {

		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}

			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}
		}};

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (GeneralSecurityException e) {
		}

		initializeModel();
		String urlSE = "https://saint-etienne-gbfs.klervi.net/gbfs/en/station_information.json";
		JSONArray stations = extratData(urlSE);
		processStationsSE(stations);
		String urlParis = "https://opendata.paris.fr/api/records/1.0/search/?dataset=velib-emplacement-des-stations";
		JSONArray stationsParis = extratParisData(urlParis);
		processStationsParis(stationsParis);

	}

	private static void processStationsParis(JSONArray stations) {

		for (Object station : stations) {

			JSONObject stationJson = (JSONObject) station;
			String ID = (String) stationJson.get("recordid");
			JSONObject obj = stationJson.getJSONObject("fields");

			String name = (String) obj.get("name");
			double lat = (Double) obj.get("lat");
			double lon = (Double) obj.get("lon");
			int capacity = (Integer) obj.get("capacity");

			// Create Station Resource
			Resource Station = model.createResource(stationURIPrefix + ID);
			Station.addProperty(RDF.type, RDFS.Class); // TODO station instance should be type of station class
			Station.addProperty(FOAF.name, name);
			Station.addProperty(RDF.value, String.valueOf(capacity));
			Station.addLiteral(model.createProperty(geoNS + "lat"), lat);
			Station.addLiteral(model.createProperty(geoNS + "long"), lon);
		}

		try {
			model.write(new FileOutputStream(new File(staticDBFile)), "TURTLE");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void processStationsSE(JSONArray stations) {

		for (Object station : stations) {

			JSONObject stationJson = (JSONObject) station;
			String ID = (String) stationJson.get("station_id");
			String name = (String) stationJson.get("name");
			double lat = (Double) stationJson.get("lat");
			double lon = (Double) stationJson.get("lon");
			int capacity = (Integer) stationJson.get("capacity");

			// Create Station Resource
			Resource Station = model.createResource(stationURIPrefix + ID);
			Station.addProperty(RDF.type, RDFS.Class); // TODO station instance should be type of station class
			Station.addProperty(FOAF.name, name);
			Station.addProperty(RDF.value, String.valueOf(capacity));
			Station.addLiteral(model.createProperty(geoNS + "lat"), lat);
			Station.addLiteral(model.createProperty(geoNS + "long"), lon);
		}

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

	public static JSONArray extratData(String url) {
		JSONObject json = null;
		try {
			json = readJsonFromUrl(url);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject data = (JSONObject) json.get("data");
		JSONArray stations = (JSONArray) data.get("stations");
		return stations;
	}

	public static JSONArray extratParisData(String url) {
		JSONObject json = null;
		try {
			json = readJsonFromUrl(url);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONArray stations = (JSONArray) json.get("records");
		return stations;
	}

	public static void initializeModel() {
		model = ModelFactory.createDefaultModel();
		exNS = "http://www.example.com/";
		geoNS = "https://www.w3.org/2003/01/geo/wgs84_pos#";

		model.setNsPrefix("ex", exNS);
		model.setNsPrefix("geo", geoNS);

		stationURIPrefix = exNS + "Station:";
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
}
