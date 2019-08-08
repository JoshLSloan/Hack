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
		Code code = new Code();
		StringBuilder sb = new StringBuilder(16);
		
		while(parser.hasMoreCommands()) {
			if (parser.commandType() == Parser.A_COMMAND) {
				sb.append("0");
				
				String binary = String.format("%15s", Integer.toBinaryString(Integer.parseInt(parser.symbol()))); //Negatives?
				binary = binary.replaceAll("\\s", "0");
				
				
				sb.append(binary);
				
				
			} else {
				sb.append("111");
				sb.append(code.comp(parser.comp()));
				sb.append(code.dest(parser.dest()));
				sb.append(code.jump(parser.jump()));
			}
			
			System.out.println(sb.toString());
			sb.setLength(0);
			parser.advance();
		}

	}
}
