package melyna;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

public class CsvToMongo {

	public static void main(String[] args) {

		CsvToMongo obj = new CsvToMongo();
		obj.runInstallations();
		obj.runEquipements();

	}

	public void runEquipements() {
		try (	InputStream inputStream = CsvToMongo.class.getResourceAsStream("/batch/csv/equipements.csv");
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

			MongoClient mongo = new MongoClient();
			DB db = mongo.getDB("nosql-workshop");
			DBCollection collection = db.getCollection("collection");

			String line = reader.readLine();


			while ((line = reader.readLine()) != null) {
				String[] values = line.split(",");
				

				DBObject equipement = new BasicDBObject("numero", values[4])
						.append("nom", values[5])
						.append("type", values[7])
						.append("famille", values[9]);
				
				DBObject id = new BasicDBObject("_id", values[2]);
				
				collection.findAndModify(id,  new BasicDBObject("$push", new BasicDBObject("equipements", equipement)));
				
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}


	}

	public void runInstallations() {

		try (	InputStream inputStream = CsvToMongo.class.getResourceAsStream("/batch/csv/installations.csv");
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

			MongoClient mongo = new MongoClient();
			DB db = mongo.getDB("nosql-workshop");
			DBCollection collection = db.getCollection("collection");
			collection.drop();
			String line = reader.readLine();
			while ((line = reader.readLine()) != null) {

				String[] values = line.split("\",\"");

				DBObject adresse = new BasicDBObject("numero", values[6])
						.append("voie", values[7])
						.append("lieuDit", values[5])
						.append("codePostal", values[4])
						.append("commune", values[2]);

				DBObject location = new BasicDBObject("type", "Point")
						.append("coordinates", values[8]);

				DBObject document = new BasicDBObject("_id", values[1])
						.append("nom", values[0].substring(1, values[0].length()-1))
						.append("adresse", adresse)
						.append("location", location)
						.append("multiCommune", values[16])
						.append("nbPlacesParking", values[17])
						.append("nbPlacesParkingHandicapes", values[18])
						.append("dateMisaAJourFiche", values[28].substring(0, values[28].length()-1))
						.append("equipements", Arrays.asList());

				collection.insert(document);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

	}
}


