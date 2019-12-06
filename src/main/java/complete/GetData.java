package complete;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.RDFNode;

public class GetData {

	private static final String FUESKI_LOCAL_ENDPOINT = "http://localhost:3030/bicycle"; // I have two databases; classes and instances, so put two values, add three 

	public static void main(String args[]) {
		String[] a = getStationsByCity();
	}
	
	public static String[] getStationsByCity() {
		String stations[] = null;
		String query = "SELECT * WHERE { ?s ?p ?o} limit 10";
		QueryExecution q = QueryExecutionFactory.sparqlService(FUESKI_LOCAL_ENDPOINT, query);
		ResultSet results = q.execSelect();

		ResultSetFormatter.out(System.out, results);

		while (results.hasNext()) {
			QuerySolution soln = results.nextSolution();
			RDFNode x = soln.get("x");
			System.out.println(x);
		}
		return stations;
	}
}
