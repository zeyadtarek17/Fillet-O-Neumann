import java.io.*;
import java.util.*;

import javax.print.attribute.standard.Destination;

public class Architecture {

	Register[] registerFile = new Register[32];
	Register destinationRegister;
	int pc;
	String[] memory = new String[2048];
	int instPtr = 0;
	int dataPtr = 1024;
	int programInstructions;
	// Boolean read;
	Boolean write = true;
	boolean memRead = false;
	int memReadAddress;
	int writeBackValue;
	String instruction;
	String[] decoded;
	ArrayList<String> running = new ArrayList<>();

	public Architecture() throws Exception {

		for (int i = 0; i < 32; i++) {
			registerFile[i] = new Register(0);
		}
		encode();
	}

	public String complementFunc(String binary) {
		String OneComp = "";
		for (int i = 0; i < binary.length(); i++) {
			if (binary.charAt(i) == '0')
				OneComp += '1';
			else
				OneComp += '0';
		}
		return OneComp;
	}

	public String getBinary(int numOfBits, int num) {

		String x = Integer.toBinaryString(num);
		int size = x.length();

		for (int i = 0; i < numOfBits - size; i++) {
			if (num >= 0) {
				x = "0" + x;
			} else {
				x = "1" + x;

			}
		}
		return x;
	}

	public void encode() throws Exception {
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
							// System.out.println(currentInstruction[i + 1].substring(1));
							machineCode += getBinary(5, Integer.parseInt(regNumber));
							machineCode += getBinary(5, Integer.parseInt(currentInstruction[i + 1].substring(1)));
							if (currentInstruction[i + 2].contains("R")) {
								machineCode += getBinary(5, Integer.parseInt(currentInstruction[i + 2].substring(1)));
								machineCode += getBinary(13, 0);
							}

							else {
								machineCode += getBinary(5, 0);
								machineCode += getBinary(13, Integer.parseInt(currentInstruction[i + 2]));
							}
							break;
						}

					}

				} else if (format == 'i') {
					for (int i = 1; i < currentInstruction.length; i++) {
						if (currentInstruction[i].contains("R") && currentInstruction[0].equals("movi")) {

							String num = currentInstruction[i].substring(1);
							machineCode = machineCode + getBinary(5, Integer.parseInt(num));
							machineCode = machineCode
									+ getBinary(5, Integer.parseInt(currentInstruction[i + 1].substring(1)));
							machineCode = machineCode + getBinary(18, Integer.parseInt(currentInstruction[i + 2]));
							break;

						} else if (currentInstruction[0].equalsIgnoreCase("movi")) {
							machineCode += getBinary(5, Integer.parseInt(currentInstruction[1].substring(1)));
							machineCode += getBinary(5, 0);
							machineCode += getBinary(18, Integer.parseInt(currentInstruction[2]));
							break;

						}
					}
				} else {// format = j
					machineCode = machineCode + getBinary(28, Integer.parseInt(currentInstruction[1]));
					if (Integer.parseInt(currentInstruction[1]) >= 1024) {
						throw new Exception("Address out of range");
					}
				}

				if (instPtr < 1024) {
					memory[instPtr] = machineCode;
					instPtr++;
				} else {
					throw new Exception("Instruction memory overflow");
				}
				programInstructions++;
			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		}

	}

	public String fetch() {
		String instruction = memory[pc];
		pc++;
		if (instruction == null) {
			return null;
		}
		return instruction;

		// decode(instruction);

	}

	public String[] decode(String instruction) throws Exception {

		if (instruction.isEmpty()) {
			return null;
		}

		String opCode = ""; // bits31:28
		String r1 = ""; // bits27:23
		String r2 = ""; // bit22:18
		String r3 = ""; // bits17:13
		String shamt = ""; // bits13:0
		String imm = ""; // bits17:0
		String address = ""; // bits27:0
		// char format =getFormat(opCode);

		opCode = instruction.substring(0, 4);

		opCode = instruction.substring(0, 4);
		r1 = instruction.substring(4, 9);
		r2 = instruction.substring(9, 14);
		r3 = instruction.substring(14, 19);
		shamt = instruction.substring(19, 32);
		imm = instruction.substring(19, 32);
		address = instruction.substring(4, 32);

		String[] decoded = new String[7];
		decoded[0] = opCode;
		decoded[1] = r1;
		decoded[2] = r2;
		decoded[3] = r3;
		decoded[4] = shamt;
		decoded[5] = imm;
		decoded[6] = address;
		return decoded;

	}

	public char getFormat(String opCode) {
		if (opCode.equals("0000") || opCode.equals("0001") || opCode.equals("0010") || opCode.equals("0101")) {
			return 'r';
		} else if (opCode.equals("1000") || opCode.equals("0100") || opCode.equals("0110") || opCode.equals("1010")
				|| opCode.equals("1011") || opCode.equals("1001")) {
			return 'i';
		} else {
			return 'j';
		}
	}

	public void execute(String[] decoded) {

		if (decoded == null) {
			return;
		}
		String opCode = decoded[0];
		String r1 = decoded[1];
		String r2 = decoded[2];
		String r3 = decoded[3];
		String shamt = decoded[4];
		String imm = decoded[5];
		String address = decoded[6];
		Register dest = registerFile[Integer.parseInt(r1, 2)];
		int r1Value = registerFile[Integer.parseInt(r1, 2)].getValue();
		int r2Value = registerFile[Integer.parseInt(r2, 2)].getValue();
		int r3Value = registerFile[Integer.parseInt(r3, 2)].getValue();

		switch (opCode) {
			case "0000":
				// writeBack(registerFile[Integer.parseInt(r1, 2)],r3Value + r2Value );
				writeBackValue = r3Value + r2Value;
				// registerFile[Integer.parseInt(r1, 2)].setValue(r1Value);
				destinationRegister = dest;
				write = true;
				break;
			case "0001":
				writeBackValue = r2Value - r3Value;
				// registerFile[Integer.parseInt(r1, 2)].setValue(r1Value);
				destinationRegister = dest;
				write = true;
				break;
			case "0010":
				writeBackValue = r3Value * r2Value;
				// registerFile[Integer.parseInt(r1, 2)].setValue(r1Value);
				destinationRegister = dest;
				break;
			case "0011":
				writeBackValue = Integer.parseInt(imm, 2);
				// registerFile[Integer.parseInt(r1, 2)].setValue(r1Value);
				destinationRegister = dest;
				break;
			case "0100":
				if (r1Value == r2Value) {
					pc += 1 + Integer.parseInt(imm, 2);
				}
				break;
			case "0101":
				writeBackValue = r3Value & r2Value;
				// registerFile[Integer.parseInt(r1, 2)].setValue(r1Value);
				destinationRegister = dest;
				break;
			case "0110":
				writeBackValue = r2Value ^ Integer.parseInt(imm, 2);
				// registerFile[Integer.parseInt(r1, 2)].setValue(r1Value);
				destinationRegister = dest;
				break;
			case "0111":
				System.out.println("JUMP" + Integer.parseInt(address, 2));
				// get 4 bits of pc and concatenate with address
				String temp = getBinary(32, pc);
				pc = Integer.parseInt(temp.substring(0, 4) + address, 2);
				break;
			case "1000":
				writeBackValue = r2Value << Integer.parseInt(shamt, 2);
				// registerFile[Integer.parseInt(r1, 2)].setValue(r1Value);
				destinationRegister = dest;
				break;
			case "1001":
				writeBackValue = r1Value >> Integer.parseInt(shamt, 2);
				// registerFile[Integer.parseInt(r1, 2)].setValue(r1Value);
				destinationRegister = dest;
				break;
			case "1010":
				memReadAddress = r2Value + Integer.parseInt(imm, 2);
				// write = true;
				memRead = true;
				destinationRegister = dest;
				// r1Value = Integer.parseInt(memory[r2Value + Integer.parseInt(imm, 2)]);
				// writeBack(registerFile[Integer.parseInt(r1, 2)], r1Value);
				break;
			case "1011":
				writeBackValue = r1Value;
				memReadAddress = r2Value + Integer.parseInt(imm, 2) + 1024;
				write = false;
				memRead = true;
				// memory[r2Value + Integer.parseInt(imm, 2)] = r1Value+"";
				break;

			default:
				System.out.println("SYNTAX ERROR!");
		}
		// mem(r2Value + Integer.parseInt(imm, 2), "",false);
		// writeBack(registerFile[Integer.parseInt(r1, 2)],r1Value );
	}

	public void writeBack() {
		if (write && destinationRegister != null) {
			destinationRegister.setValue(writeBackValue);

		} else
			return;

	}

	public void mem() {
		if (memRead) {
			writeBackValue = Integer.parseInt(memory[memReadAddress]);
		}
		if (memRead && !write) {
			memory[memReadAddress] = writeBackValue + "";
		} else
			return;
	}

	public void pipeLine() throws Exception {
		String temp = "";
		String check = "";
		boolean checkHazard = false;

		int cycles = 7 + ((programInstructions - 1) * 2);
		for (int i = 1; i < cycles + 1; i++) {

			System.out.println("Cycle " + i + ":" + "PC=" + pc);
			System.out.println(registerFile[5].getValue());

			if (!checkHazard) {
				if (i % 2 == 0) {
					mem();
				} else {
					writeBack();
					execute(decoded);
					decoded = decode(temp);
					check = fetch();
				}
				System.out.println("Fetch: " + check);
			}
			
			if (check != null && check != "") {
				temp = check;
			}
			if(check == null && pc>programInstructions+1) {
				break;
			}
			if (checkControlHazard(temp) && !checkHazard) {
				System.out.println("Control Hazard" + temp);
				writeBack();
				execute(decoded);
				decoded = decode(temp);
				checkHazard = true;
				continue;
			}
			if (checkHazard) {
				checkHazard = false;
				System.out.println("Control Hazard 2" + temp);
				decoded = decode(temp);
				execute(decoded);
				if (memory[pc] == null)
					throw new Exception("Instruction memory overflow");
				continue;
			}

		}
	}

	private boolean checkControlHazard(String instruction) {
		String opCode = instruction.substring(0, 4);
		if (opCode.equals("0100") || opCode.equals("0111")) {
			return true;
		}
		return false;
	}

	private void executeInstruction() throws Exception {
		String instruction = fetch();
		String[] decoded = decode(instruction);
		execute(decoded);
		mem();
		writeBack();
	}

	public static void main(String[] args) throws Exception {
		Architecture arch = new Architecture();
		arch.registerFile[1].setValue(5);
		arch.registerFile[2].setValue(5);
		arch.registerFile[3].setValue(5);
		arch.registerFile[4].setValue(2);
		arch.registerFile[5].setValue(2);
		System.out.println(arch.registerFile[5].getValue());

		// arch.executeInstruction();
		// System.out.println(arch.pc);
		// arch.executeInstruction();
		// System.out.println(arch.pc);
		// arch.executeInstruction();
		// System.out.println(arch.pc);
		// arch.executeInstruction();
		// System.out.println(arch.pc);
		// arch.executeInstruction();

		arch.pipeLine();
		System.out.println(arch.registerFile[1].getValue());
		System.out.println(arch.registerFile[5].getValue());
		System.out.println(arch.registerFile[6].getValue());

	}
}
