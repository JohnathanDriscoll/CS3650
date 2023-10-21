
/*
 *  compile in java with all files in same directory using command:
 *  javac *.java
 *  to compile all files with .java ext
 *  run assembler and pass .asm files in same directory using command:
 *  java assembler <file>.asm
 *  .hack file will be output to same directory
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Assembler {
	public static  int counter=0;
	public static int nextRam = 16;
	public static String compT,destT,jumpT; // temp's
	public static void main(String[] args) {

		//Get name for .hack file from .asm file
		String name = args[0].substring(0, args[0].indexOf('.'));	
		String outFileName = name+".hack";
		
		//create tables using symboltable.java and code.java
		SymbolTable st = new SymbolTable(); 	
		Code ct = new Code();  

		//parser class from parser.java to create parser object
		Parser newParser = new Parser(args[0]);

		//output the .hack file
		File out = new File(outFileName);
		FileWriter fw = null;
		try {
			fw = new FileWriter(out.getAbsoluteFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedWriter bw = new BufferedWriter(fw);

		//first pass
		while(newParser.hasMoreCommand()) {  
		if(newParser.commandType()== Parser.commandType.L_COMMAND) { 
			//adds found symbol to symbol table
			st.addEntry(newParser.symbol(),Integer.toString(counter)) ;
		}
		//move to next line
		else counter++;
		//advance to next command
		newParser.advance();
		}
		// resets counter
		newParser.lineCount =0;   
		
		//second pass
		while(newParser.hasMoreCommand())
		{
			//@xxx
			if(newParser.commandType()== Parser.commandType.A_COMMAND) 
			{
				if(newParser.strFileArr[newParser.lineCount].startsWith("@"))
				{
				//returns xxx
				String tmp  = newParser.symbol(); 
					//checks if xxx is number
					if(newParser.isNum(tmp))  
					{
						int xxx = Integer.parseInt(tmp);
						//returns binary value of xxx
						tmp = Parser.dexToBin(xxx);
						tmp = newParser.addZero(tmp);
						try {
							//write to .hack file
							bw.write(tmp + '\n');
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
					else  //if not number
					{
						// if not in sbol table, add to symbol table
						if(!st.containKey(tmp))  
						{
							st.addEntry(tmp,Integer.toString(nextRam));
							nextRam++;
						}
						 // if in Symbol Table, write to .hack file
						 if(st.containKey(tmp))
							{
							String tmp2 = st.getValue(tmp);
							int xxx = Integer.parseInt(tmp2);
							tmp2 = Parser.dexToBin(xxx);
							tmp2 = newParser.addZero(tmp2);
							try {
								bw.write(tmp2+'\n'); 
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}//if command type A_COMMAND 
			if(newParser.commandType()== Parser.commandType.C_COMMAND)
			{
				if(newParser.strFileArr[newParser.lineCount].contains("="))
				{
					destT = ct.getDest(newParser.dest());
					compT = ct.getComp(newParser.comp());
					jumpT = ct.getJump("NULL");
					try {
						bw.write("111" + compT + destT + jumpT +'\n');
					} catch (IOException e) {
						e.printStackTrace();
					}	
				}
				//jump
				else if(newParser.strFileArr[newParser.lineCount].contains(";"))
				{
					destT = ct.getDest("NULL");
					compT = ct.getComp(newParser.comp());
					jumpT = ct.getJump(newParser.jump());
					
					try {
						bw.write("111" + compT + destT + jumpT +'\n');
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}//if command type C_COMMAND 
			newParser.advance();		
		}
		
try {
	bw.close();
} catch (IOException e) {
	e.printStackTrace();
}
}

}
