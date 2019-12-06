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

		String countryURI = "http://www.example.com/country";
		String cityURI = "http://schema.org/City";
		String stationURI = "http://www.example.com/station";
		String availabilityURI = "http://www.example.com/availability";

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
