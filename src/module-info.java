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
	requires com.h2database;
	requires atlantafx.base;
	requires org.kordamp.ikonli.javafx;
	requires org.kordamp.ikonli.bootstrapicons;
	
	opens applicationMain to javafx.graphics, javafx.fxml;
}
