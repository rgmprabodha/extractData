package complete;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

public class StationClasses {

	public static void main(String[] args) {
		Model model = ModelFactory.createDefaultModel();

		String countryURI = "https://schema.org/Country";
		String cityURI = "http://schema.org/City";
		
		String localNS = "http://localhost:3030/";
		model.setNsPrefix("local", localNS);
		String stationURI = localNS + "Station:";
		String availabilityURI = localNS + "Availability:";

		Resource Country = model.createResource(countryURI);
		Resource City = model.createResource(cityURI);
		Resource Station = model.createResource(stationURI);
		Resource Availability = model.createResource(availabilityURI);

		Property hasCity = model.createProperty("hasCity");
		Property hasStation = model.createProperty("hasStation");
		Property hasAvailability = model.createProperty("hasAvailability");
		


		Country.addProperty(hasCity, City);
		Country.addProperty(RDF.type, RDFS.Class);

		City.addProperty(hasStation, Station);
		City.addProperty(RDF.type, RDFS.Class);

		Station.addProperty(RDF.type, RDFS.Class);
		Station.addProperty(hasAvailability, Availability);

		Availability.addProperty(RDF.type, RDFS.Class);

		model.write(System.out, "turtle");

	}
}
