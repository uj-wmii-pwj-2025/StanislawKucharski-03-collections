package uj.wmii.pwj.collections;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Stack;

public class BrainFuckImpl implements Brainfuck{
    PrintStream out;
    InputStream in;
    String instructions;
    int instruction_pointer = 0;
    int data_pointer = 0;
    final byte[] data;
    Stack<Integer> stack;

    public BrainFuckImpl(String program, PrintStream out, InputStream in, int stackSize){
        if (program == null || program.isEmpty())
            throw new IllegalArgumentException("error: program is null or empty!");
        if (out == null)
            throw new IllegalArgumentException("error: OutputStream is null!");
        if (in == null)
            throw new IllegalArgumentException("error: InputStream is null");
        if (stackSize < 1)
            throw new IllegalArgumentException("error: StackSize too small (less than 1)");
        this.data = new byte[stackSize];
        this.instructions = program;//.replaceAll("[^<>+\\-\\[\\]\\.,]", "");
        this.out = out;
        this.in = in;
        stack = new Stack<Integer>();
    }

    public void execute() {
        while(instruction_pointer < instructions.length()){
            char instruction = instructions.charAt(instruction_pointer++);
            switch (instruction){
                case '>' -> data_pointer++;
                case '<' -> data_pointer--;
                case '+' -> data[data_pointer]++;
                case '-' -> data[data_pointer]--;
                case '.' -> out.print((char)data[data_pointer]);
                case ',' -> {
                    try {
                        data[data_pointer] = (byte)in.read();
                    } catch (IOException e) {
                        throw new RuntimeException("Brainfuck: InputStream input failed");
                    }
                }
                case '[' ->{
                    if(data[data_pointer] == 0){
                        int unclosed = 1;
                        while (unclosed != 0){
                            instruction = instructions.charAt(instruction_pointer++);
                            if(instruction == '[')unclosed++;
                            else if(instruction == ']')unclosed--;
                        }
                    }
                    else{
                        stack.push(instruction_pointer);
                    }
                }
                case ']' ->{
                    if(data[data_pointer] != 0){
                        instruction_pointer = stack.peek();
                    }
                    else{
                        stack.pop();
                    }
                }
                //default -> throw new UnsupportedOperationException("Unfamiliar instruction: " + String.valueOf(instruction));
            }
        }
    }
}
