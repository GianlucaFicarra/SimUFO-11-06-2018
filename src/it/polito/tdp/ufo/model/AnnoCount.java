package it.polito.tdp.ufo.model;

import java.time.Year;

//classe creata perchè nel punto 1 con la dendina devo ritornare l'anno 
//ed il numero di vvistamenti in quell'anno:


public class AnnoCount {
	private Year anno ;
	private int count ;
	
	public AnnoCount(Year anno, int count) {
		super();
		this.anno = anno;
		this.count = count;
	}
	public Year getAnno() {
		return anno;
	}
	public void setAnno(Year anno) {
		this.anno = anno;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	//per modificare cosa andrò a vedere nella tendina:
	public String toString() {
		return anno.toString()+" ("+count+")" ;
	}
	
}
