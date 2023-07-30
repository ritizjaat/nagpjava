package nagp.java.assignment;

import java.util.Timer;

public class MainMethod {
	public static Timer timer = new Timer(true);

	public static void main(String[] args) {

		try {
		// Scheduling the Timer and passing the delay parameter as zero hence for the first time it will trigger the 
		//data insert method without  any delay and after that it will keep on calling the method after every 25 seconds
			DataInsertThread dataThread=new DataInsertThread();
			timer.schedule(dataThread, 0, 10000);
			getTshirtInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* Will ask the user preference and will show the details on the console
	 * If user want to continue then enter yes and if want to terminate then enter exit
	 * */
	public static void getTshirtInfo() {
		DatabaseConnector database = new DatabaseConnector();
		UserInput userInputDetails = new UserInput();
		database.getRecordsWithInput(userInputDetails.getUserDetails("colour"), userInputDetails.getUserDetails("size"),
				userInputDetails.getUserDetails("gender"), userInputDetails.getUserDetails("sorting"));
		String choice = userInputDetails.getUserDetails("termination");

		if (choice.equalsIgnoreCase("yes")) {
			getTshirtInfo();
		} else if (choice.equalsIgnoreCase("exit")) {
			System.out.println("closing the program");
			UserInput.sc.close();
			timer.cancel();
			System.exit(0);
		}
	}

}
