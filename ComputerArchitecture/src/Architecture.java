import java.io.*;
import java.util.*;
public class Architecture {
   
	
	static int[] regesterFile = new int[31];
	static int PCcounter = 0;
	static Integer[] memory = new Integer[2048];
	static final int Data = 1024;
	static int instPtr = 0;

	public static String complementFunc(String binary){
		String OneComp = "";
		for (int i = 0; i < binary.length(); i++) {
			if (binary.charAt(i) == '0')
				OneComp += '1';
			else
				OneComp += '0';
		}
		return OneComp;
	}

	public static int fetch() {
		return memory[PCcounter++];
	}
 
	
	public static String getBinary(int numOfBits, int num) {
		//append zeros at the beginning
		if (num >= 0) {
            String zero = "0";
			String x = Integer.toBinaryString(num);
			 for(int i=0;i<numOfBits - x.length();i++){
			 	x=zero.append(x);
			 }
			//return zero.repeat(numOfBits - x.length()) + x;
			return x;
		} 
        else {
			String x = Integer.toBinaryString(num);
			return x.substring(x.length() - numOfBits);
		}

	}
	public static void InstructionMemory() {
		try {
		File file = new File("MIPS.txt");
		Scanner reader = new Scanner(file);

		while (reader.hasNextLine()) {
			String binary = "";
			char format = 'n';
			boolean shamtFlag = false;
			String instruction = reader.nextLine();
			String[] InstrucArray = instruction.split(" ");

			switch (InstrucArray[0].toLowerCase()) {
			case "add":
				binary = binary + "0000";
				format = 'r';
				break;
			case "sub":
				binary = binary + "0001";
				format = 'r';
				break;
			case "mul":
				binary = binary + "0010";
				format = 'r';
				break;
			case "movi":
				binary = binary + "0011";
				format = 'i';
				break;
			case "jeq":
				binary = binary + "0100";
				format = 'i';
				break;
			case "and":
				binary = binary + "0101";
				format = 'r';
				break;
			case "xori":
				binary = binary + "0110";
				format = 'i';
				break;
			case "jmp":
				binary = binary + "0111";
				format = 'j';
				break;
			case "lsl":
				binary = binary + "1000";
				format = 'r';
				break;
			case "lsr":
				binary = binary + "1001";
				format = 'r';
				break;
			case "movr":
				binary = binary + "1010";
				format = 'i';
				break;
			case "movm":
				binary = binary + "1011";
				format = 'i';
				break;
			default:
				System.out.println("SYNTAX ERROR!");
			}
			if (format == 'r') {
				for (int i = 1; i < InstrucArray.length; i++) {
					if (InstrucArray[i].contains("R")) {
						String num = InstrucArray[i].substring(1);
						binary = binary + getBinary(5, Integer.parseInt(num));
					} else {
						binary = binary + getBinary(18, Integer.parseInt(InstrucArray[i]));
						shamtFlag = true;
					}
				}
				if (shamtFlag == false) {
					binary = binary + getBinary(13, 0);
				}
				
			} else if (format == 'i') {
				for (int i = 1; i < InstrucArray.length; i++) {
					if (InstrucArray[i].contains("R")) {
						String num = InstrucArray[i].substring(1);
						binary = binary + getBinary(5, Integer.parseInt(num));
					} else if (InstrucArray[0].equalsIgnoreCase("movi")) {
						binary += getBinary(5, 0);
						binary = binary + getBinary(18, Integer.parseInt(InstrucArray[i]));
					} else {
						binary = binary + getBinary(18, Integer.parseInt(InstrucArray[i]));
					}
				}
			} else {
				binary = binary + getBinary(28, Integer.parseInt(InstrucArray[1]));
			}

			if (binary.charAt(0) == '0') // check if positive
				memory[instPtr] = Integer.parseInt(binary, 2);
			else { //not positive
				String OneComp = complementFunc(binary);
				int ones = Integer.parseInt(OneComp, 2);
				
				ones++; //HABDA 3shan mkanetsh zabta
				ones *= -1;
				memory[instPtr] = ones;
			}
			instPtr++;
		}
		reader.close();
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found");
		}
		
	}

	public static int[] decode(int instruction){

		int opcode = 0; // bits31:28
		int r1 = 0; // bits27:23
		int r2 = 0; // bit22:18
		int r3 = 0; // bits17:13
		int shamt = 0; // bits13:0
		int imm = 0; // bits17:0
		int address = 0; // bits27:0

		


	}

	public static int[] execute(int opcode, int operand1, int operand2,int imm){
		int result = 0;

		switch (opcode) {
			case 0:
				result = operand1 + operand2;
				break;
			case 1:
				// System.out.println(operand1 +" "+operand2);
				result = operand1 - operand2;
				break;
			case 2:

				result = operand1 * operand2;
				break;

			case 3:
				result = operand2;
				break;
			case 5:
				result = operand1 & operand2;
				break;
			case 6:
				result = operand1 ^ operand2;
				break;

			case 8:
				result = operand1 << operand2;
				break;
			case 9:
				result = operand1 >>> operand2;
				break;
			case 10:
				result = operand1 + operand2;
				break;
			case 11:
				result = operand1 + operand2;
			}

	}

	
	
}

