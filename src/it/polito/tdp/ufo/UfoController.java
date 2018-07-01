/**
 * Sample Skeleton for 'Ufo.fxml' Controller Class
 */

package it.polito.tdp.ufo;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.ufo.model.AnnoCount;
import it.polito.tdp.ufo.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class UfoController {
	
	private Model model ;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    
    @FXML // fx:id="boxAnno"
    private ComboBox<AnnoCount> boxAnno; // Value injected by FXMLLoader
    //(1)POPOLO TENDINA ALL'AVVIO DEL PROGRAMMA
    
    
    @FXML // fx:id="boxStato"
    private ComboBox<String> boxStato; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    //inizzializzo tendina appena avvio il model
    public void setModel(Model m) {
    	this.model = m ;
    	boxAnno.getItems().addAll(model.getAnniAvvistamenti()) ;
    }
    
    
    @FXML
    void handleAvvistamenti(ActionEvent event) {
    	
    	//leggo anno dove valutare avvistamenti
    	AnnoCount anno = boxAnno.getValue() ;
    	if(anno==null) {
    		txtResult.appendText("ERRORE: selezionare un anno\n");
    		return ;
    	}
    	
    	//se anno selezionato creo grafo
    	model.creaGrafo(anno.getAnno());
    	
    	//dopo aver creato grafo posso aggiungere valori alla seconda tendina
    	//con gli stati con almeno 1 avvistamento nell anno in corso
    	this.boxStato.getItems().clear();
    	this.boxStato.getItems().addAll(model.getStati());
    	
    }
    
    @FXML
    void handleAnalizza(ActionEvent event) {
    
    	//vedo se ho selezionato uno stato nella seconda tendina
    	String stato = this.boxStato.getValue();
    	if (stato == null){
    		txtResult.appendText("ERRORE: selezionare uno stato\n");
    		return ;
    	}
    		
    	//salvo i valori ottetuti dai filtri del model (punto c)
    	List <String> successivi = model.getStatiSuccessivi(stato);
    	List <String> precedenti = model.getStatiPrecedenti(stato);
    	List <String> raggiugibili = model.getStatiRaggiungibili(stato);
    	
    	//stampo i risultati
    	this.txtResult.appendText("\nStato di partenza: " + stato + "\n");
    	
    	this.txtResult.appendText("Stati SUCCESSIVI\n");
    	this.txtResult.appendText(successivi.toString() + "\n");
    	
    	this.txtResult.appendText("Stati PRECEDENTI\n");
    	this.txtResult.appendText(precedenti.toString() + "\n");
    	
    	this.txtResult.appendText("Stati RAGGIUNGIBILI\n");
    	this.txtResult.appendText(raggiugibili.toString() + "\n");
    	
    }


    @FXML
    void handleSequenza(ActionEvent event) {

    	//leggo stato di partenza
    	String stato = this.boxStato.getValue();
    	if (stato == null){
    		txtResult.appendText("ERRORE: selezionare uno stato\n");
    		return ;
    	}
    	
    	//prendo lista di sequenza max ottenuta dalla ricorsiva 
    	List <String> sequenza = model.getPercorsoMassimo(stato);
    	
    	this.txtResult.appendText("\nStato di partenza: " + stato + "\n");
    	this.txtResult.appendText("Percorso massimo: " + sequenza + "\n");
    	
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert boxAnno != null : "fx:id=\"boxAnno\" was not injected: check your FXML file 'Ufo.fxml'.";
        assert boxStato != null : "fx:id=\"boxStato\" was not injected: check your FXML file 'Ufo.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Ufo.fxml'.";

    }
    
  
}
