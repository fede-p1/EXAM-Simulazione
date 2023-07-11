package it.polito.tdp.nyc.model;

import java.util.*;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import it.polito.tdp.nyc.db.NYCDao;
import it.polito.tdp.nyc.model.Evento.EventType;

public class Model {
	
	NYCDao dao;
	SimpleWeightedGraph<NTA, DefaultWeightedEdge> graph;
	
	public Model() {
		dao = new NYCDao();
	}
	
	public List<String> getAllBorough(){
		return dao.getAllBorough();
	}
	
	public SimpleWeightedGraph<NTA, DefaultWeightedEdge> creaGrafo(String borough){
		
		graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		List<NTA> vertex = new ArrayList<>(dao.getAllNTA(borough));
		
		Graphs.addAllVertices(graph, vertex);
		
		for (NTA nta1 : graph.vertexSet())
			for (NTA nta2 : graph.vertexSet())
				if (!nta1.equals(nta2)) {
					Set<String> union = new HashSet<>(nta1.getSSID());
					union.addAll(nta2.getSSID());
					if (union.size() > 0)
						Graphs.addEdge(graph, nta1, nta2, union.size());
				}
		return graph;
	}
	
	private double pesoMedio;
	
	public List<ArcoPeso> archiPesoMaggiore(){
		
		pesoMedio = 0;
		
		List<ArcoPeso> archi = new ArrayList<>();
		
		for (DefaultWeightedEdge edge : graph.edgeSet())
			pesoMedio += graph.getEdgeWeight(edge);
		
		pesoMedio = pesoMedio/graph.edgeSet().size();
		
		for (DefaultWeightedEdge edge : graph.edgeSet())
			if (graph.getEdgeWeight(edge) > pesoMedio)
				archi.add(new ArcoPeso(edge,graph.getEdgeWeight(edge),graph.getEdgeSource(edge),graph.getEdgeTarget(edge)));
		
		Collections.sort(archi);
		return archi;
		
		
	}

	public SimpleWeightedGraph<NTA, DefaultWeightedEdge> getGraph() {
		return graph;
	}

	public double getPesoMedio() {
		return pesoMedio;
	}
	
	public void run(double p, int d) {
		
		List<NTA> vertex = new ArrayList<>(graph.vertexSet());
		
		PriorityQueue<Evento> queue = new PriorityQueue<>();
		
		for (int i=1; i<=100; i++) {
			if (Math.random() < p) {
				NTA scelto = vertex.get((int) (vertex.size()*Math.random()));
				scelto.aggiungiFile();
				Evento event = new Evento(scelto,i,d, EventType.CONDIVISIONE);
				queue.add(event);
			}
		}
		
		while (!queue.isEmpty()) {
			
			Evento event = queue.poll();
			NTA ntaCorrente = event.getNta();
			int duration = event.getDurata();
			int time = event.getTime();
			EventType type = event.getType();
			
			
			
			if (time > 100)
				return;
			System.out.println(event.toString());
			switch(type){
				
			case CONDIVISIONE:
				
				NTA vicino = trovaNTA(ntaCorrente);
				
				if (vicino != null) {
				
				Evento ricondivisione = new Evento(vicino,time+1,(int) Math.ceil(duration/2), EventType.CONDIVISIONE);
				Evento termine = new Evento(ntaCorrente,time+duration, 0, EventType.TERMINE);
				
				queue.add(termine);
				
				if (ricondivisione.getDurata() > 0) {
					queue.add(ricondivisione);
					vicino.aggiungiFile();
				}
				}
				
				break;
				
			case TERMINE:
				
				ntaCorrente.rimuoviFile();

				break;
			}			
		}
		
	}
	
	private NTA trovaNTA(NTA corrente) {
		
		List<NTA> vicini = Graphs.neighborListOf(graph, corrente);
		List<NTA> copiaVicini = Graphs.neighborListOf(graph, corrente);
		
		for (NTA nta : copiaVicini)
			if (nta.getNumFileCondivisi() > 0)
				vicini.remove(nta);
		
		double pesoMax = 0.0;
		
		for (NTA vicino : vicini)
			if (graph.getEdgeWeight(graph.getEdge(vicino, corrente)) > pesoMax)
				pesoMax = graph.getEdgeWeight(graph.getEdge(vicino, corrente));
		
		for (NTA vicino : vicini)
			if (graph.getEdgeWeight(graph.getEdge(vicino, corrente)) == pesoMax)
				return vicino;
		
		return null;
		
	}
	
}
