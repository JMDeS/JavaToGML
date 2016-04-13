package database;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.connection.Connection;
import org.bson.Document;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class ActorMap extends LinkedHashMap<String,HashSet<String>> {

	private MongoDatabase db;
	@SuppressWarnings("deprecation")
	public ActorMap() throws IOException{
		super();
		//connecting to a running mongodb server
		MongoClient mongoClient = new MongoClient("localhost", 27017);

		//creating/finding a database named "movies"
		db = mongoClient.getDatabase("movies");

		// creating/finding a collection named "movie"
		MongoCollection<Document> movies = db.getCollection("movies");
		// creating/finding a collection named "actor"
		MongoCollection<Document> actors = db.getCollection("actors");

		// building a list of movie documents
		mDocs = loadMovieDocs();
		// building a list of actor documents
		aDocs = loadActorDocs();

		// inserting data into tables
		movies.insertMany(mDocs);
		actors.insertMany(aDocs);
		
		// finds all movies which Tom Hanks has played in
//		performQuery(db);
		fillActorMap();
		
		// disconnecting from the server
		mongoClient.close();

	}

	private static List<Document> loadMovieDocs() throws IOException {
		path = readJSON();
		jsonFileContainer = new MoviesContainer[path.length];
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		// loop through JSON files in movies folder
		for (int f=1; f< jsonFileContainer.length ; f++) {
			MoviesContainer jsonFile = mapper.readValue(
				new File("movies/page" + f + ".json") , MoviesContainer.class);
			jsonFileContainer[f-1] = jsonFile;

			// loop through movies in each JSON file
			for (int m=0 ; m < jsonFile.getMovies().length; m++)
			{ 	
				Movie[] movieArr = jsonFile.getMovies();
				Movie movie = movieArr[m];

				Document doc = new Document();
				doc.append("id", movie.getId());
				doc.append("mpaa_rating", movie.getMpaaRating());
				doc.append("audience_score", movie.getRatings().getAudienceScore())	;
				doc.append("critics_score", movie.getRatings().getCriticsScore());
				doc.append("title", movie.getTitle());
				doc.append("year", movie.getYear());
				mList = new ArrayList<>();

				Actor[] actorArr = movie.getAbridged_cast();
				// loop through actors in each movie
				for (int a=0 ; a < actorArr.length ; a++)
				{ 
					String[] characters = actorArr[a].getCharacters();
					try {
						// loop through characters of each actor
						for (int c=0 ; c < characters.length ; c++)
						{
							mList.add(new Document("actorId", actorArr[a].getId()));
							mList.add(new Document("character", characters[c]));
						} // end character loop
					} catch(NullPointerException npe){}
				} // end actor loop
				doc.append("characters", mList);
				mDocs.add(doc);
			} // end movie loop
		} // end file loop
		return mDocs;
	}

	private static List<Document> loadActorDocs() throws IOException {
		path = readJSON();
		jsonFileContainer = new MoviesContainer[path.length];
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		for (int f=1; f< jsonFileContainer.length ; f++) 
		{ // i=1 b/c no page0
			MoviesContainer jsonFile = mapper.readValue(
				new File("movies/page" + f +".json"), MoviesContainer.class);
			jsonFileContainer[f-1] = jsonFile;
			Movie[] movieArr = jsonFile.getMovies();
			
			for (int m=0 ; m < jsonFile.getMovies().length; m++) 
			{
				Actor[] actorArr = movieArr[m].getAbridged_cast();
				for (int a=0 ; a < actorArr.length ; a++)
				{
					Document doc = new Document();
					doc.append("id", actorArr[a].getId());
					doc.append("name", actorArr[a].getName()); //.replace("'","''"));
					doc.append("title", movieArr[m].getTitle());
					aList = new ArrayList<>();
					
					String[] characters = actorArr[a].getCharacters();
					try {
						for (int c=0 ; c < characters.length ; c++)
						{
							Document charDoc = new Document();
							/*----use charDoc to combine movieId,character&movieTitle---*/
							charDoc.append("movieId", movieArr[m].getId());
							charDoc.append("character", characters[c]);
							charDoc.append("movieTitle", movieArr[m].getTitle());
							aList.add(charDoc);
						} // end character loop
					} catch(NullPointerException npe2){}
					doc.append("characters", aList);
					aDocs.add(doc);
				} // end actor loop
			} // end movie loop
		} // end file loop
		return aDocs;
	}


	public void fillActorMap() throws IOException {

		MongoCursor<Document> results =
				db.getCollection("actors").find().iterator();
		HashSet<String> movieSet;
		for (Document doc : aDocs){
			movieSet = new HashSet<>();
			if (this.containsKey(doc.get("name"))){
				movieSet = this.get(doc.get("name"));
				movieSet.add(doc.get("title").toString());
				this.get(doc.get("name")).addAll(movieSet);
			} else {
				movieSet.add(doc.get("title").toString());
				this.put(doc.get("name").toString(),movieSet);
			}

		}
//			for (Document doc : aDocs){
//				System.out.println(doc.get("title"));
//			}
//				this.put(doc.get("name").toString(),0);
	}


	private static void performQueryAlternate(MongoDatabase db) {
		// List containing only the Documents where name == "Tom Hanks"
		List<Document> aggregateList = new ArrayList<>();
		aggregateList.add(
				new Document("$match", new Document("name", "Tom Hanks")));
		
		db.getCollection("actors").aggregate(aggregateList)
			.forEach((Block<Document>) document -> {
                List<Document> resultList = new ArrayList<>();
                resultList = (List<Document>) document.get("characters");
                for (Document doc : resultList)
                    System.out.println(
                            "\t" + "\"" + doc.get("character") + "\""
                            + " in "
                            + "\"" + doc.get("movieTitle") + "\"");
                });
	}

	public static String[] readJSON() throws IOException {
		String fileLocation = System.getProperty("user.dir");
		File folder = new File(fileLocation + "/movies/");
		String[] pathArray = new String[25];
		File[] listOfFiles = folder.listFiles();
		int count = 0;
		for (int i=0 ; i < listOfFiles.length; i++) {
			if(listOfFiles[i].isFile()) {
				pathArray[i] = listOfFiles[i].getAbsolutePath();
				count++;
			}
		}
		return pathArray;
	}

	private static MoviesContainer[] jsonFileContainer;
	private static ObjectMapper mapper = new ObjectMapper();
	private Connection conn = null;
	private static String[] path; 
	private static List<Document> mList = new ArrayList<Document>();
	private static List<Document> aList = new ArrayList<Document>();
	private static List<Document> mDocs = new ArrayList<Document>();
	private static List<Document> aDocs = new ArrayList<Document>();
	
}
