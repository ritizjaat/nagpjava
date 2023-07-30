package nagp.java.assignment;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class UserInput {
	public final static Scanner sc = new Scanner(System.in);
	private final List<String> sizeChoices = Arrays.asList("S", "M", "L", "XL", "XXL");
	private final List<String> genderChoices = Arrays.asList("M", "F");
	private final List<String> sortingChoices = Arrays.asList("PRICE", "RATING");
	private final List<String> terminateChoices = Arrays.asList("YES", "EXIT");

	 /* method Will ask the user to provide the input. It will keep asking users about the correct input until user 
	 * provides the input from the defined set of values
	 * 	 
	 */
	public String getUserDetails(String inputType) {
		String userInput = "";
		switch (inputType) {
		case "colour":
			System.out.print("Please enter the colour Choice::");
			String colour = sc.nextLine();
			userInput = colour;
			break;
		case "size":
			boolean correctSizeChoices = false;
			while (!correctSizeChoices) {
				System.out.print("Please provide the size Choice from the list:: Choose from S,M,L,XL,XXL::");
				String size = sc.nextLine();
				userInput = size;
				correctSizeChoices = sizeChoices.contains(size.toUpperCase());
			}
			break;
		case "gender":
			boolean correctGenderChoices = false;
			while (!correctGenderChoices) {
				System.out.print(
						"Please Provide The  Gender Preference from the List::M for male, F for female::");
				String gender = sc.nextLine();
				userInput = gender;
				correctGenderChoices = genderChoices.contains(gender.toUpperCase());
			}
			break;
		case "sorting":
			boolean correctPriorityChoices = false;
			while (!correctPriorityChoices) {
				System.out.print("Please Provide The  Sorting Preference from the List::Price or Rating::");
				String sorting = sc.nextLine();
				userInput = sorting;
				correctPriorityChoices = sortingChoices.contains(sorting.toUpperCase());
			}
			break;
		case "termination":
			boolean isTerminateProgram = false;
			String terminationInput = null;
			while (!isTerminateProgram) {
				System.out.print(
						"--To Continue Please Provide The Input from the List::1) Yes for continue and "
						+ "2) Exit for termination of the program::");
				terminationInput = sc.nextLine();
				userInput = terminationInput;
				isTerminateProgram = terminateChoices.contains(terminationInput.toUpperCase());
			}
			break;
		};

		return userInput;
	}

}
