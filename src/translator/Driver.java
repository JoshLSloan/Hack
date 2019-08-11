package translator;

public class Driver {

	public static void main(String[] args) {
		
		if (args.length < 1) {
			System.out.println("USAGE: java translator.Driver <filename>");
			System.exit(0);
		}
		
		Parser parse = new Parser(args[0]);

	}

}
