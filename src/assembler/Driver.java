package assembler;

public class Driver {

	public static void main(String[] args) {
		
		if (args.length < 1) {
			System.out.println("USAGE: java Driver <fileName>");
			System.exit(0);
		}
		
		assemble(args[0]);

	}
	
	public static void assemble (String fileName) {
		
		Parser parser = new Parser(fileName);
	}

}
