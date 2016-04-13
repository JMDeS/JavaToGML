package hw6;

import database.ActorMap;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by JMDeS on 4/13/2016.
 */
public class Driver {



    private ActorMap actorMap;
    private LinkedHashMap<Integer,HashSet<String>> idMap;
    private Graph graph;

    public static void main(String[] args) throws IOException {
        Driver driver = new Driver();
//        driver.getActorMap().forEach(System.out::println);
    }

    public Driver() throws IOException {
        actorMap = new ActorMap();
        int n = actorMap.size();
        System.out.println(n);
        graph = new Graph(n);
        numbify();
        initGraph();
    }

    public void numbify(){ //converts actors' names into unique integer ids
        idMap = new LinkedHashMap<>();
        int i = 0;
        for (Map.Entry<String,HashSet<String>> map : actorMap.entrySet())
            idMap.put(i++,map.getValue());
        
    }

    public void initGraph() {
        for (Map.Entry<Integer,HashSet<String>> actor1 : idMap.entrySet()) {
            for (Map.Entry<Integer,HashSet<String>> actor2 : idMap.entrySet()) {
                for(String movie1 : actor1.getValue()){
                    for(String movie2 : actor2.getValue()){
                        if (movie1.equals(movie2)
                                && actor1.getKey() != actor2.getKey()
                                && !graph.adjacent(actor1.getKey(),actor2.getKey()))
                            graph.setEdge(actor1.getKey(),actor2.getKey());
                    }
                }
            }
        }
        System.out.println(graph.edgeCounter());
        graph.printVertices();
    }
    public LinkedHashMap<String,HashSet<String>> getActorMap() {return actorMap;}

}
