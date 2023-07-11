package it.polito.tdp.nyc;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.nyc.model.ArcoPeso;
import it.polito.tdp.nyc.model.Model;
import it.polito.tdp.nyc.model.NTA;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {

	Model model;
	
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnAdiacenti"
    private Button btnAdiacenti; // Value injected by FXMLLoader

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnCreaLista"
    private Button btnCreaLista; // Value injected by FXMLLoader

    @FXML // fx:id="clPeso"
    private TableColumn<?, ?> clPeso; // Value injected by FXMLLoader

    @FXML // fx:id="clV1"
    private TableColumn<?, ?> clV1; // Value injected by FXMLLoader

    @FXML // fx:id="clV2"
    private TableColumn<?, ?> clV2; // Value injected by FXMLLoader

    @FXML // fx:id="cmbBorough"
    private ComboBox<String> cmbBorough; // Value injected by FXMLLoader

    @FXML // fx:id="tblArchi"
    private TableView<?> tblArchi; // Value injected by FXMLLoader

    @FXML // fx:id="txtDurata"
    private TextField txtDurata; // Value injected by FXMLLoader

    @FXML // fx:id="txtProb"
    private TextField txtProb; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doAnalisiArchi(ActionEvent event) {
    	 model.archiPesoMaggiore();
    	
    	txtResult.setText("PESO MEDIO: " + model.getPesoMedio() + '\n');
    	txtResult.appendText("ARCHI CON PESO MAGGIORE DEL PESO MEDIO: " + model.archiPesoMaggiore().size() + "\n\n");
    	
    	for (ArcoPeso ap : model.archiPesoMaggiore())
    		txtResult.appendText(ap.toString() + '\n');

    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	
    	if (this.cmbBorough.getValue() == null) {
    		txtResult.setText("Scegli un borgo");
    		return;
    	}
    	
    	SimpleWeightedGraph<NTA, DefaultWeightedEdge> graph = model.creaGrafo(cmbBorough.getValue());
    	
    	txtResult.setText("Grafo creato con " + graph.vertexSet().size() + " vertici e " + graph.edgeSet().size() + " archi.\n\n");
    	
    	this.btnAdiacenti.setDisable(false);
    	this.btnCreaLista.setDisable(false);
    }

    @FXML
    void doSimula(ActionEvent event) {
    	
    	if (this.txtDurata.getText() == "") {
    		txtResult.setText("Inserisci una durata");
    		return;
    	}
    	
    	if (this.txtProb.getText() == "") {
    		txtResult.setText("Inserisci una probabilità");
    		return;
    	}
    	
    	try {
    		Double.parseDouble(txtProb.getText());
    		Integer.parseInt(txtDurata.getText());
    		if (Double.parseDouble(txtProb.getText()) < 0.2 || Double.parseDouble(txtProb.getText()) > 0.9){
    				txtResult.setText("Inserisci una probabillità tra 0.2 e 0.9");
    				return;
    	}
    		
    	}
    	catch (Exception e) {
    		txtResult.setText("Inserisci dei valori numerici");
    		return;
    	}
    	
    	model.run(Double.parseDouble(txtProb.getText()),Integer.parseInt(txtDurata.getText()));
    	
    	List<NTA> vertici = new ArrayList<>(model.getGraph().vertexSet());
    	
    	for (NTA nta : vertici) {
    		txtResult.appendText(nta.toString() + ": \ntotale condivisi: " + nta.getMaxCondivisi() + "\nattualmente in condivisione: " + nta.getNumFileCondivisi() + "\n\n");
    	}

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnAdiacenti != null : "fx:id=\"btnAdiacenti\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCreaLista != null : "fx:id=\"btnCreaLista\" was not injected: check your FXML file 'Scene.fxml'.";
        assert clPeso != null : "fx:id=\"clPeso\" was not injected: check your FXML file 'Scene.fxml'.";
        assert clV1 != null : "fx:id=\"clV1\" was not injected: check your FXML file 'Scene.fxml'.";
        assert clV2 != null : "fx:id=\"clV2\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbBorough != null : "fx:id=\"cmbBorough\" was not injected: check your FXML file 'Scene.fxml'.";
        assert tblArchi != null : "fx:id=\"tblArchi\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtDurata != null : "fx:id=\"txtDurata\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtProb != null : "fx:id=\"txtProb\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";

        
    }
    
    public void setModel(Model model) {
    	this.model = model;
    	
    	this.cmbBorough.getItems().addAll(model.getAllBorough());
    	
    	this.btnAdiacenti.setDisable(true);
    	this.btnCreaLista.setDisable(true);
    }

}
