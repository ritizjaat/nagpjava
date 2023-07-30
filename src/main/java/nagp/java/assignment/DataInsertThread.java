package nagp.java.assignment;

import java.util.TimerTask;

public class DataInsertThread extends TimerTask {
	
	@Override
	public void run() {
		DatabaseConnector database = new DatabaseConnector();
		database.InsertRecords();		
	}

}
