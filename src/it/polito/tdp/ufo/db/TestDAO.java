package it.polito.tdp.ufo.db;

import java.util.List;

import it.polito.tdp.ufo.model.Sighting;

public class TestDAO extends SightingsDAO {

	public static void main(String[] args) {
		SightingsDAO dao = new SightingsDAO() ;
		
		List<Sighting> list = dao.getSightings() ;
		
		for(Sighting s: list)
			System.out.println(s);

	}

}
