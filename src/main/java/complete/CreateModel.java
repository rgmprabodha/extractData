package complete;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.json.JSONException;
import org.json.JSONObject;

public class CreateModel {
	static Model model;
	static String staticDBFile = "E:\\CPS2\\Year_2\\Semantic_Web\\Jena\\extractdata\\src\\main\\java\\extractdata\\static.ttl";
	static String stationURIPrefix;
	static String[] cities = {"SAINT-ETIENNE", "LYON", "PARIS"};
	static String exNS;
	static String geoNS;
	
	public static void main(String args[]) throws JSONException, IOException {
		CreateModel.sslResolve();
		CreateModel.initializeModel();
		for (String city : cities) {
			if(city == "SAINT-ETIENNE") {
				StaticSaintEtienne se = new StaticSaintEtienne();
				List<Station> stations = se.processData();
				addCityToModel(stations);
			}else if(city == "Lyon") {
				
			}
		}
		try {
			model.write(new FileOutputStream(new File(staticDBFile)), "TURTLE");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void sslResolve() {

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

	}

	public static void initializeModel() {
		model = ModelFactory.createDefaultModel();
		exNS = "http://www.example.com/";
		geoNS = "https://www.w3.org/2003/01/geo/wgs84_pos#";

		model.setNsPrefix("ex", exNS);
		model.setNsPrefix("geo", geoNS);

		stationURIPrefix = exNS + "Station:";
	}
	
	public static void addCityToModel(List<Station> stations) {
		for (Station station : stations) {	

			Resource StationRs = model.createResource(stationURIPrefix + station.getID());
			StationRs.addProperty(RDF.type, RDFS.Class); // TODO station instance should be type of station class
			StationRs.addProperty(FOAF.name, station.getName());
			StationRs.addProperty(RDF.value, String.valueOf(station.getCapacity()));
			StationRs.addLiteral(model.createProperty(geoNS + "lat"), station.getLat());
			StationRs.addLiteral(model.createProperty(geoNS + "long"), station.getLon());
        }
	}
	
}
