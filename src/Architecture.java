import java.io.*;
import java.util.*;

public class Architecture {

	static Register[] registerFile = new Register[31];
	// static int PCcounter = 0;
	static Register zeroRegister;
	static int pc;
	static String[] memory = new String[2048]; //data are strings "12" while instructions are binary "1100"
	// static final int data = 1024;
	// static final int instruction = 1024;
	static int instPtr = 0;
	static int dataPtr = 1024;

	public static String complementFunc(String binary) {
		String OneComp = "";
		for (int i = 0; i < binary.length(); i++) {
			if (binary.charAt(i) == '0')
				OneComp += '1';
			else
				OneComp += '0';
		}
		return OneComp;
	}

	public static void fetch() {
		String instruction = memory[pc];
		pc++;
		decode(instruction);

	}

	public static String getBinary(int numOfBits, int num) {

		String x = Integer.toBinaryString(num);
		for (int i = 0; i < numOfBits - x.length(); i++) {
			if (x.charAt(0) == '0')
				x = "0" + x;
			else
				x = "1" + x;
		}
		return x;
	}

	public static void encode() throws Exception {
		try {
			File file = new File("MIPS.txt");
			Scanner reader = new Scanner(file);

			while (reader.hasNextLine()) {
				String machineCode = "";
				char format = 'f';
				boolean shamtFlag = false;
				String instruction = reader.nextLine();
				String[] currentInstruction = instruction.split(" ");

				switch (currentInstruction[0].toLowerCase()) {
					case "add":
						machineCode = machineCode + "0000";
						format = 'r';
						break;
					case "sub":
						machineCode = machineCode + "0001";
						format = 'r';
						break;
					case "mul":
						machineCode = machineCode + "0010";
						format = 'r';
						break;
					case "movi":
						machineCode = machineCode + "0011";
						format = 'i';
						break;
					case "jeq":
						machineCode = machineCode + "0100";
						format = 'i';
						break;
					case "and":
						machineCode = machineCode + "0101";
						format = 'r';
						break;
					case "xori":
						machineCode = machineCode + "0110";
						format = 'i';
						break;
					case "jmp":
						machineCode = machineCode + "0111";
						format = 'j';
						break;
					case "lsl":
						machineCode = machineCode + "1000";
						format = 'r';
						shamtFlag = true;
						break;
					case "lsr":
						machineCode = machineCode + "1001";
						format = 'r';
						shamtFlag = true;
						break;
					case "movr":
						machineCode = machineCode + "1010";
						format = 'i';
						break;
					case "movm":
						machineCode = machineCode + "1011";
						format = 'i';
						break;
					default:
						System.out.println("SYNTAX ERROR!");
				}
				if (format == 'r') {
					for (int i = 1; i < currentInstruction.length; i++) {
						if (currentInstruction[i].contains("R")) {
							String regNumber = currentInstruction[i].substring(1);
							machineCode += getBinary(5, Integer.parseInt(regNumber));
							machineCode += getBinary(18, Integer.parseInt(currentInstruction[i+1]));
							break;
						} 
							
						
					}
					if (shamtFlag == false) {
						machineCode += getBinary(13, 0);
					}

				} else if (format == 'i') {
					for (int i = 1; i < currentInstruction.length; i++) {
						if (currentInstruction[i].contains("R") && !currentInstruction[0].equals("movi")) {

							String num = currentInstruction[i].substring(1);
							machineCode = machineCode + getBinary(5, Integer.parseInt(num));

						} else if (currentInstruction[0].equalsIgnoreCase("movi")) {
							machineCode += getBinary(5, Integer.parseInt(currentInstruction[1].substring(1)));
							machineCode += getBinary(5, 0);
							machineCode += getBinary(18, Integer.parseInt(currentInstruction[i]));

						} else {
							machineCode += getBinary(18, Integer.parseInt(currentInstruction[i]));
						}
					}
				} else {// format = j
					machineCode = machineCode + getBinary(28, Integer.parseInt(currentInstruction[1]));
				}

				if(instPtr < 1024){
					memory[instPtr] = machineCode;
					instPtr++;
				}
				else{
					throw new Exception("Instruction memory overflow");
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		}

	}

	public static void decode(String instruction) {

		String opCode = ""; // bits31:28
		String r1 = ""; // bits27:23
		String r2 = ""; // bit22:18
		String r3 = ""; // bits17:13
		String shamt = ""; // bits13:0
		String imm = ""; // bits17:0
		String address = ""; // bits27:0
	    //char format =getFormat(opCode);
		opCode = instruction.substring(0, 4);

		opCode= instruction.substring(0,4);
		r1= instruction.substring(4,9);
		r2= instruction.substring(9,14);
		r3= instruction.substring(14,19);
		shamt= instruction.substring(19,32);
		imm= instruction.substring(19,32);
		address= instruction.substring(4,32);

		String[] decoded = new String[6];
		decoded[0] = opCode;
		decoded[1] = r1;
		decoded[2] = r2;
		decoded[3] = r3;
		decoded[4] = shamt;
		decoded[5] = imm;
		decoded[6] = address;
		execute(decoded);






	}




	public static char getFormat(String opCode){
		if(opCode.equals("0000") || opCode.equals("0001") || opCode.equals("0010") || opCode.equals("0101")){
			return 'r';
		}
		else if(opCode.equals("1000") || opCode.equals("0100") || opCode.equals("0110") || opCode.equals("1010") || opCode.equals("1011") || opCode.equals("1001")){
			return 'i';
		}
		else{
			return 'j';
		}
	}

	public static void execute(String []decoded) {
		String opCode = decoded[0];
		String r1 = decoded[1];
		String r2 = decoded[2];
		String r3 = decoded[3];
		String shamt = decoded[4];
		String imm = decoded[5];
		String address = decoded[6];
		int r1Value = 0;
		int r2Value = registerFile[Integer.parseInt(r2, 2)].getValue();
		int r3Value = registerFile[Integer.parseInt(r3, 2)].getValue();


		switch(opCode){
			case "0000":
				r1Value = r3Value + r2Value;
				registerFile[Integer.parseInt(r1, 2)].setValue(r1Value);
				break;
			case "0001": 
				r1Value = r2Value - r3Value;
				registerFile[Integer.parseInt(r1, 2)].setValue(r1Value);
				break;
			case "0010": 
				r1Value = r3Value * r2Value;
				registerFile[Integer.parseInt(r1, 2)].setValue(r1Value);
				break;	
			case "0011": 
				r1Value = Integer.parseInt(imm, 2);
				registerFile[Integer.parseInt(r1, 2)].setValue(r1Value);
				break;
			case "0100": 
			if(r1Value == r2Value){
				pc+= +1 + Integer.parseInt(imm, 2);
				
			}
				break;
			case "0101": 
				r1Value = r3Value & r2Value;
				registerFile[Integer.parseInt(r1, 2)].setValue(r1Value);
				break;
			case "0110": 
				r1Value = r2Value ^ Integer.parseInt(imm, 2);
				registerFile[Integer.parseInt(r1, 2)].setValue(r1Value);
				break;
			case "0111": 
				pc += Integer.parseInt(address, 2);
				break;
			case "1000": 
				r1Value = r2Value << Integer.parseInt(shamt, 2);
				registerFile[Integer.parseInt(r1, 2)].setValue(r1Value);
				break;
			case "1001": 
				r1Value = r1Value >> Integer.parseInt(shamt, 2);
				registerFile[Integer.parseInt(r1, 2)].setValue(r1Value);
				break;
			case "1010": 
				r1Value = Integer.parseInt(memory[r2Value + Integer.parseInt(imm, 2)]);
				registerFile[Integer.parseInt(r1, 2)].setValue(r1Value);
				break;
			case "1011": 
				memory[r2Value + Integer.parseInt(imm, 2)] = r1Value+"";
				break;
			
			default: 
				System.out.println("SYNTAX ERROR!");
		}


	}

	

}
