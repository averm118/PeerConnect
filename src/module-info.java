/**
 * This module contains the FoundationsF25 application.
 *
 * @author Thinh Tang, Stuart King, Aditya Verma, Aaron Hazarika
 */

module FoundationsF25 {
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.fxml;
	requires java.sql;
	requires org.junit.jupiter.api;   
	
	opens applicationMain to javafx.graphics, javafx.fxml;
}
