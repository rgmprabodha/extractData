package complete;

import java.io.File;
import java.io.FileInputStream;
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

import org.apache.jena.query.DatasetAccessor;
import org.apache.jena.query.DatasetAccessorFactory;
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
	private static final String FUESKI_LOCAL_ENDPOINT = "http://localhost:3030/bicycle"; // I have two databases; classes and instances, so put two values, add three 
	static String stationURIPrefix;
	static String[] cities = {"SAINT-ETIENNE", "LYON", "PARIS"};
	static String exNS;
	static String geoNS;
	
	public static void main(String args[]) throws JSONException, IOException {
		CreateModel.sslResolve();
		CreateModel.initializeModel();
		for (String city : cities) {
			List<Station> stations = null;
			if(city == "SAINT-ETIENNE") {
				StaticSaintEtienne se = new StaticSaintEtienne();
				stations = se.processData();
				addCityToModel(stations);
			}
			else  if(city == "LYON") {
				StaticLyon ly = new StaticLyon();
				stations = ly.processData();
				addCityToModel(stations);
			}
			else  if(city == "PARIS") {
				StaticParis pa = new StaticParis();
				stations = pa.processData();
				addCityToModel(stations);
			}
			
			// Save the model in fueski server
			saveToFueski();
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
	
	/**
	 * what is the best way?
	 * 1. save to a file and put it to server
	 * 2. directly put it to server
	 */
	public static void saveToFueski() {
		try {
			model.write(new FileOutputStream(new File(staticDBFile)), "TURTLE");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Model m = ModelFactory.createDefaultModel();
		try (FileInputStream in = new FileInputStream(staticDBFile)) {
			m.read(in, null, "TURTLE");			
			DatasetAccessor accessor = DatasetAccessorFactory.createHTTP(FUESKI_LOCAL_ENDPOINT);
			accessor.putModel(m);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
}
