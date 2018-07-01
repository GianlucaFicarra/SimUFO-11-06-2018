package it.polito.tdp.ufo.model;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.ufo.db.SightingsDAO;

public class Model {
	
	private List<AnnoCount> anniAvvistamenti ;
	private Graph<String, DefaultEdge> graph ;
	private List<String> stati; //stati con almeno 1 avvistamento nell anno in corso
	
	private List <String> ottima;
	
	//(1) ottengo anni degli avvistamenti per la tendina
	//-->creo metodo nel dao e classe con elenco anni e numero avvistamenti
	public List<AnnoCount> getAnniAvvistamenti() {
		SightingsDAO dao = new SightingsDAO();
		this.anniAvvistamenti = dao.getAnni() ;
		return this.anniAvvistamenti ;
	}
	
	
	//(2)metodi per costruire grafo degli avvistamenti in dato anno
	public void creaGrafo(Year anno) {
		this.graph = new SimpleDirectedGraph<>(DefaultEdge.class) ;
		
		SightingsDAO dao = new SightingsDAO() ;
		
		//vertitci grafo sono gli stati con almeno 1 avvistamento nell anno in corso
		this.stati = dao.getStati(anno) ;
		
		//aggiungo vertici al grafo
		Graphs.addAllVertices(this.graph, stati) ;
		
//PRIMO MODO PER CREARE GLI ARCHI(meno efficente)
//		for(String stato1: graph.vertexSet()) {
//			for(String stato2: graph.vertexSet()) {
//				if(!stato1.equals(stato2)) {
//					if(dao.esisteArco(stato1, stato2, anno)) {  //se esiste arco nell'anno
//						graph.addEdge(stato1, stato2) ;     //aggiungo allora l'arco
//					} 
//				}
//			}
//		}

//SECONDO MODO PER ARCHI, CHIEDO AL DAO OGGETTO CHE CONTIENE COPPIA DI STRINGHE
//-->class StringPair
		List<StringPair>archi = dao.getEdges(anno) ;
		for(StringPair sp : archi) 
			graph.addEdge(sp.getState1(), sp.getState2()) ;
		
		
		System.out.println("Numero vertici: "+graph.vertexSet().size()+ " - Numero archi: "+ graph.edgeSet().size());
	}

	
	//popolo secondo menu a tendina
	public List <String> getStati(){
		return this.stati;
	}
	
	//metodi per 1.C  uso i metodi della libreria
	public List <String> getStatiPrecedenti (String stato) {
		return Graphs.predecessorListOf(this.graph, stato);
	}
	
	public List <String> getStatiSuccessivi (String stato){
		return Graphs.successorListOf(this.graph, stato);
	}
	
	
	//altri stati raggiungibili attraversando 1 o più archi in avanti
	//VISITA IN AMPIEZZA PARTENDO DA UN VERTICE 
	public List <String> getStatiRaggiungibili (String stato){
		
		// visita in ampiezza: creazione di un iteratore: PARAMETRI IL GRAFO E IL VERTICE DI PARTENZA
		BreadthFirstIterator<String, DefaultEdge> bfi = new BreadthFirstIterator<>(this.graph, stato);
		
		List <String> raggiungibili = new ArrayList<>(); //salvo i vertici trovati  
		
		bfi.next(); // non includere lo stato di partenza ma voglio gli altri stati
		while (bfi.hasNext()) //finche ho un prossimo vertice
			raggiungibili.add(bfi.next()); //li aggiunge ed avanza l'iteratore
		
		return raggiungibili;
	}
	
	
	// ------------ SOLUZIONE PUNTO 2
	
	//restituisce lista di stati ordinati in base al percorso massimo dallo stato di partenza
	public List <String> getPercorsoMassimo (String stato){
		
		this.ottima = new ArrayList<>(); //iniz nella chiamante la soluzione ottima
		
		//parziale è array con solo 'elemento di partenza
		List <String> parziale = new ArrayList<>();
		parziale.add(stato);
		
		this.cercaSequenza(parziale);//lancio ricorsione
		
		return ottima; //restituisco ottima(popolata nella ricorsiva) al chiamante
	}
	
	//il metodo pubblico usa questo privato per fare la ricorsione
	//riceve la lista parziale di stati ed accresce la lista
	private void cercaSequenza (List <String> parziale) {
		
		// caso terminale: non ho più successori, quella rimasta è la soluz ottima
		//se soluz parziale è più lunga dell'ottima, diventa la ottima 
		if (parziale.size() > ottima.size())
			this.ottima = new ArrayList<>(parziale); //deepcopy perche voglio la migliore
		
		
		// passo ricorsivo
		//i candidati solo i successivi dell'ultimo elemento della lista parziale
		List <String> candidati = this.getStatiSuccessivi(parziale.get(parziale.size() - 1));
		
//		effettua una sottrazione tra insiemi: controllare questa funzione per andare più veloce
//		parziale.retainAll(candidati); sottrazione degli elementi del primo meno il secodno 
		
		//provo tutti i candidati, se non lo contiene ancora lo aggiungo
		//altrimenti no perche non arebbe più semplice
		for (String prova : candidati) 
			if (!parziale.contains(prova)) {
				parziale.add(prova); //aggiungo il nuovo candidati e continuo la ricorsione
				this.cercaSequenza(parziale);
				parziale.remove(parziale.size() - 1); //rimuovo il candidato per esplorare nuovo ramo
			}	
		
		//-->se non ho candidati validi, faccio giro vuoto nel for e vado al caso terminale
	}
	
	
}
