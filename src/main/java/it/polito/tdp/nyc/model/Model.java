package it.polito.tdp.nyc.model;

import java.util.*;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.nyc.db.NYCDao;

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
	
	
	
	
	
}
