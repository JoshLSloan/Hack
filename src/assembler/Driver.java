package assembler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Driver {
	
	private SymbolTable st;
	private String file;
	private Parser parser;

	public Driver (String pFileName) {
		file = pFileName;
		st = new SymbolTable();
		parser = new Parser(file + ".asm");
		buildSymbolTable();
	}
	
	public void assemble () {
	
		try (BufferedWriter w = new BufferedWriter(new FileWriter(file + ".hack"))) {
			Code code = new Code();
			StringBuilder sb = new StringBuilder(16);
			parser.reset();
			
			int ram = 16;
			while(parser.hasMoreCommands()) {
				if (parser.commandType() == Parser.A_COMMAND) {
					sb.append("0");
					
					if (Character.isDigit(parser.symbol().charAt(0))) {
						// Not a symbol
						String binary = String.format("%15s",
								Integer.toBinaryString(Integer.parseInt(parser.symbol())));
						binary = binary.replaceAll("\\s", "0");
						sb.append(binary);
					} else {
						// Its a symbol
						if (st.contains(parser.symbol())) {
							sb.append(st.getAddress(parser.symbol()));
						} else {
							st.addEntry(parser.symbol(), ram);
							ram++;
							sb.append(st.getAddress(parser.symbol()));
						}
					}
		
				} else if (parser.commandType() == Parser.C_COMMAND){
					sb.append("111");
					sb.append(code.comp(parser.comp()));
					sb.append(code.dest(parser.dest()));
					sb.append(code.jump(parser.jump()));
				} else {
					sb.setLength(0);
					parser.advance();
					continue;
				}
				
				sb.append("\n");
				w.write(sb.toString());
				
				sb.setLength(0);
				parser.advance();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void buildSymbolTable () {
		parser.reset();
		
		int rom = 0;
		
		while (parser.hasMoreCommands()) {
			if (parser.commandType() == Parser.L_COMMAND) {
				this.st.addEntry(parser.symbol(), rom);
			} else {
				rom++;
			}
			
			parser.advance();
		}
		parser.reset();
	}
	
	public static void main(String[] args) {
		
		if (args.length < 1) {
			System.out.println("USAGE: java Driver <fileName>");
			System.exit(0);
		}
	
		Driver d = new Driver(args[0]);
		d.assemble();
	}
}
