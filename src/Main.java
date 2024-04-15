
import java.io.*;
import java.util.ArrayList;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    private static final int[][] map = new int[60][30]; // map[state][content]
    private static int tableContentCounter = 0;
    private static final String[] tableContent = new String[30];
    private static final String[] endState = new String[60];

    private static final ArrayList<Character> List_CHARACTER = new ArrayList<>();
    private static final ArrayList<Character> List_DIGIT = new ArrayList<>();
    private static final ArrayList<Character> List_SEPARATOR = new ArrayList<>();
    private static final ArrayList<Integer> List_ENDSTATE = new ArrayList<>();
    private static final ArrayList<Token> List_Token = new ArrayList<>();
    private static final ArrayList<Character> List_ESCAPE = new ArrayList<>();
    private static final ArrayList<Character> List_BLANK = new ArrayList<>();
    private static final ArrayList<String> List_KEYWORD = new ArrayList<>();
    private static final ArrayList<String> List_TOKENACCEPTABLE = new ArrayList<>();
    private static final ArrayList<String> List_INPUT = new ArrayList<>();
    private static void initialized() {
        List_ESCAPE.add('\b');
        List_ESCAPE.add('\f');
        List_ESCAPE.add('\n');
        List_ESCAPE.add('\r');
        List_ESCAPE.add('\t');
        List_ESCAPE.add('\'');
        List_ESCAPE.add('\"');
        List_ESCAPE.add('\\');

        List_BLANK.add(' ');
        List_BLANK.add('\t');
        List_BLANK.add('\n');
        List_BLANK.add('\f');
        List_BLANK.add('\b');
        List_BLANK.add('\r');

        List_TOKENACCEPTABLE.add("IDENTIFIER");
        List_TOKENACCEPTABLE.add("KEYWORD");
        List_TOKENACCEPTABLE.add("FLOAT_LITERAL");
        List_TOKENACCEPTABLE.add("INT_LITERAL");
        List_TOKENACCEPTABLE.add("OPERATOR");
        List_TOKENACCEPTABLE.add("STRING_LITERAL");
        List_TOKENACCEPTABLE.add("SEPARATOR");
        List_TOKENACCEPTABLE.add("ENDOFFILE");

        List_INPUT.add("example_fib");
        List_INPUT.add("example_gcd");
    }
    public static void getAutomatonData(String fileName) {
        File file = new File(fileName);
        try {
            Scanner myReader = new Scanner(file);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (Objects.equals(data, "CHARACTER")) {
                    String temp = myReader.nextLine();
                        for (int i = 0; i < temp.length(); i++) {
                            List_CHARACTER.add(temp.charAt(i));
                        }
                }
                if (data .equals("DIGIT")) {
                    String temp = myReader.nextLine();
                    for (int i = 0; i < temp.length(); i++) {
                        List_DIGIT.add(temp.charAt(i));
                    }
                }
                if (data.equals("SEPARATOR")) {
                    String temp = myReader.nextLine();
                    for (int i = 0; i < temp.length(); i++) {
                        List_SEPARATOR.add(temp.charAt(i));
                    }
                }
                if (data.equals("ENDING_STATE")) {
                    while (myReader.hasNextLine()) {
                        int state;
                        try {
                            state = Integer.parseInt(myReader.next());
                        } catch (Exception e) {
                            break;
                        }
                        String type = myReader.next();
                        List_ENDSTATE.add(state);
                        endState[List_ENDSTATE.indexOf(state)] = type;
                    }
                }
                if (data.equals("TABLE")) {
                    String contentRow = myReader.nextLine();
                    int previous_blank;
                    int current_blank = -1;
                    for (int i=0;i< contentRow.length();i++) {
                        if (contentRow.charAt(i) == ' ')
                        {
                            tableContentCounter++;
                            previous_blank = current_blank;
                            current_blank = i;

                            if (previous_blank != -1)
                            {
                                tableContent[tableContentCounter-1] = contentRow
                                        .substring(previous_blank+1,current_blank);
                            }
                        }
                    }
                    tableContent[tableContentCounter] = contentRow
                            .substring(current_blank+1);
                    while (myReader.hasNext()) {
                        int state = Integer.parseInt(myReader.next());
                        for (int i = 1; i<= tableContentCounter; i++) {
                            int stateNext = Integer.parseInt(myReader.next());
                            map[state][i]=stateNext;
                        }
                    }
                }
                if (data.equals("KEYWORD")) {
                    String temp = myReader.nextLine();
                    int previous_blank;
                    int current_blank = -1;
                    for (int i=0;i< temp.length();i++) {
                        if (temp.charAt(i) == ' ')
                        {
                            previous_blank = current_blank;
                            current_blank = i;
                            if (previous_blank != -1)
                            {
                                List_KEYWORD.add(temp
                                        .substring(previous_blank+1,current_blank));
                            }
                        }
                    }
                    List_KEYWORD.add(temp
                            .substring(current_blank+1));
                }
            }
            myReader.close();
        } catch (FileNotFoundException e)
        {
            System.out.println("File not found");
        }
    }

    private static int getContent(char c) {
        if (c =='\n')
            for (int i=1;i<=tableContentCounter;i++)
                if(Objects.equals(tableContent[i], "BREAK_LINE")) {
                    return i;
                }
        for (int i=1;i<=tableContentCounter;i++)
            if(Objects.equals(tableContent[i], Character.toString(c))) {
            return i;
        }
        if (List_CHARACTER.contains(c))
            for (int i=1;i<=tableContentCounter;i++)
                if(Objects.equals(tableContent[i], "CHARACTER")) {
                    return i;
                }
        if (List_DIGIT.contains(c))
            for (int i=1;i<=tableContentCounter;i++)
                if(Objects.equals(tableContent[i], "DIGIT")) {
                    return i;
                }
        if (List_SEPARATOR.contains(c))
            for (int i=1;i<=tableContentCounter;i++)
                if(Objects.equals(tableContent[i], "SEPARATOR")) {
                    return i;
                }
        if (List_BLANK.contains(c))
             for (int i=1;i<=tableContentCounter;i++)
                if(Objects.equals(tableContent[i], "BLANK")) {
                    return i;
                }
        return -1;
    }
    public static void analyseFile(String fileName) {
        List_Token.clear();
        int currentState = 0;
        int line = 1;
        int length = 1;
        int end = 1;
        File file = new File("Input/"+fileName+".vc");
        try {
            InputStream input = new FileInputStream(file);
            int ch;
            StringBuilder tokenWord = new StringBuilder();
            while ((ch= input.read())!=-1) {
                char c = (char) ch;
                if (getContent(c) != -1) {
                    currentState = map[currentState][getContent(c)];
                    }
                if (List_ENDSTATE.contains(currentState)) {
                    String type = endState[List_ENDSTATE.indexOf(currentState)];
                    if (List_KEYWORD.contains(tokenWord.toString())) type = "KEYWORD";
                    Token token = new Token(tokenWord.toString(),type,line,length,end-1);
                    List_Token.add(token);
                    currentState = map[0][getContent(c)];
                    tokenWord = new StringBuilder();
                    length=1;
                }
                if (!List_ENDSTATE.contains(currentState)) tokenWord.append(c);
                if (c == '\n') {line++;end=0;}
                length++;
                end++;
            }
            List_Token.add(new Token("$","ENDOFFILE",line,2,1));
            input.close();
        } catch (FileNotFoundException e)
        {
            System.out.println("File not found");
        }
        catch (IOException e) {
            System.out.println("IOException");
        }
    }


    public static void testAutomatonDataRead() {
        System.out.println("table content counter: " + tableContentCounter);
        for (int i=1;i<=tableContentCounter;i++) System.out.println(tableContent[i]);
        for (Integer integer : List_ENDSTATE) System.out.println(integer);
    }

    public static void testFileAnalyse() {
        System.out.println("token counter: " + List_Token.size());
        int n=0;
        for (Token token : List_Token)
            if (List_TOKENACCEPTABLE.contains(token.getType()))
            {n++;
                System.out.println(token.getWord() + " " + token.getType());}
        System.out.println(n);
    }

    public static void makeOutput(String filename) {
        String filename1 = "Output/" + filename + ".vctok";
        String filename2 = "Output/" + filename + ".verbose.vctok";
        try {
            File myObj = new File(filename1);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        try {
            FileWriter myWriter = new FileWriter(filename1);
            for (Token token : List_Token)
                if (List_TOKENACCEPTABLE.contains(token.getType())) myWriter.write(token.getWord()+"\n");
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        try {
            File myObj = new File(filename2);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        try {
            FileWriter myWriter = new FileWriter(filename2);
            myWriter.write("======= The VC compiler =======\n");
            for (Token token : List_Token)
                if (List_TOKENACCEPTABLE.contains(token.getType())) {
                    String s = "Kind = " + token.getType() + ", "
                            + "Spelling = \"" + token.getWord() + "\"" + ", "
                            + "position" + " = " + token.getPosition() ;
                    myWriter.write(s + "\n");
                }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    public static void mainActivity() {
        for (String string:List_INPUT) {
            analyseFile(string);
            makeOutput(string);
        }
    }
    public static void main(String[] args)     throws IOException
    {
        initialized();
        String automatonFile = "AutomatonData.dat";
        getAutomatonData(automatonFile);
        mainActivity();
    }
}