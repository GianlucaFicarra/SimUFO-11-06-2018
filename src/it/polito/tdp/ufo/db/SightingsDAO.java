package it.polito.tdp.ufo.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.ufo.model.AnnoCount;
import it.polito.tdp.ufo.model.Sighting;
import it.polito.tdp.ufo.model.StringPair;

public class SightingsDAO {
	
	//GIA PRESENTE: carica tutti i campi degli avvistamenti
	
	public List<Sighting> getSightings() {
		String sql = "SELECT * FROM sighting" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Sighting> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				list.add(new Sighting(res.getInt("id"),
						res.getTimestamp("datetime").toLocalDateTime(),
						res.getString("city"), 
						res.getString("state"), 
						res.getString("country"),
						res.getString("shape"),
						res.getInt("duration"),
						res.getString("duration_hm"),
						res.getString("comments"),
						res.getDate("date_posted").toLocalDate(),
						res.getDouble("latitude"), 
						res.getDouble("longitude"))) ;
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	//CREATI
	
	//ritorno gli anni degli avvistamenti:
    public List<AnnoCount> getAnni() {
		                     //estraggo anno da DAYTIME come int     //numero avvistamento per anni
		String sql = "select distinct YEAR(datetime) as anno, count(id) as cnt " + 
				"from sighting " + 
				"where country='us' " +  //solo avvistamenti usa
				"group by anno " +      //raggruppo avvistamenti per anni per il conteggio
				"order by anno asc" ;  //ordine crescente
		
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet res = st.executeQuery() ;
			
			List<AnnoCount> result = new ArrayList<>() ;
			
			while( res.next( )) {
				result.add( new AnnoCount(
						Year.of(res.getInt("anno")), //getint per prendere anno preso come int dalla query
				//Year.of lo converto in oggetto Year che si aspetta l'oggetto
						res.getInt("cnt") ) ) ;
			}
			
			conn.close();
			return result ;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
    //metodo per trovare gli VERTIVI
    //--> gi stati americani con avvistamenti in quell'anno
	public List<String> getStati(Year anno) {
		String sql = "SELECT DISTINCT state " + 
				"FROM sighting " + 
				"WHERE country='us' " + 
				"AND year(datetime)=? " + 
				"order by state asc" ;
		//se non ho avvistamenti lo stato non compare
		
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<String> result = new ArrayList<>() ;
			
			st.setInt(1, anno.getValue());
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				result.add(res.getString("state")) ;
			}
			
			conn.close();
			return result ;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	
	
	/*1 Metodo per verificare se devo mettere arco tra due stati in un ano 
	
	public boolean esisteArco(String stato1, String stato2, Year anno) {
		
		/* join con se stessa, blocco l'anno e la nazione (passata)
		 * e metto gli stati di cui voglio conoscere se c'è arco,
		 * voglio solo quelle con data di 2 maggiore dell'1:
		 * se il contatore è >1 allora metto arco.
		 * ciascuno di questi è un istanza di un avvistamento in country1 
		 * seguito da una data successiva nello stesso anno in country2
		
		String sql = "select count(*) as c " + 
				"from sighting s1, sighting s2 " + 
				"where year(s1.datetime)=year(s2.datetime) " + 
				"and year(s1.datetime)=? " + 
				"and s1.country='us' " + 
				"and s2.country='us' " + 
				"and s1.state=? " + 
				"and s2.state=? " + 
				"and s2.datetime>s1.datetime" ;
		
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			st.setInt(1, anno.getValue());
			st.setString(2, stato1);
			st.setString(3, stato2);

			ResultSet res = st.executeQuery() ;
			res.first() ;  //punta alla prima riga essendo un solo valore per evitare eccezioni
			
			//vedo quanti risultati trovati
			int risultati = res.getInt("c") ;
			
			conn.close();
			
			if(risultati==0)  //se risultati=0 no archi
				return false ;
			else             //se trovo stati
				return true ;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false ;
		}
		
	}*/
	
	
	
	//2 Metodo, dammi lista di coppie di stringhe, cioè gli archi del grafo dato anno
	public List<StringPair> getEdges(Year anno) {
		
		//determina tutte le coppie di stati tra cui esistono avvistamenti successivi
		//-- cioè: elenca tutti gli archi del grafo
		String sql = "select s1.state as state1 , s2.state as state2, count(*) " + 
				"from sighting s1, sighting s2 " + 
				"where year(s1.datetime)=year(s2.datetime) " + 
				"and year(s1.datetime)=? " + 
				"and s1.country='us' " + 
				"and s2.country='us' " + 
				"and s2.datetime>s1.datetime " + 
				"and s1.state<>s2.state " + 
				"group by s1.state, s2.state " ;
		
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			
			st.setInt(1, anno.getValue());
			
			ResultSet res = st.executeQuery() ;
			
			List<StringPair>list = new ArrayList<>() ;
			while(res.next()) {
				list.add(new StringPair(res.getString("state1"), res.getString("state2"))) ;
			}
			conn.close();
			return list ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}

	}

}
