
import java.io.*;
import java.util.ArrayList;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    private static final int[][] map = new int[60][30]; // map[state][content]
    private static int tableContentCounter = 0; // Use to count how many content in the table
    private static final String[] tableContent = new String[30];    // Use to store table's content in String
                                                                    // as I use ID as int for an easier approach
    private static final String[] endState = new String[60];        // Store all End_State in String

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

    // Initialize some customized data such as the token i want to display and file input name
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

        // This is all the tokens i want to display
        List_TOKENACCEPTABLE.add("IDENTIFIER");
        List_TOKENACCEPTABLE.add("KEYWORD");
        List_TOKENACCEPTABLE.add("FLOAT_LITERAL");
        List_TOKENACCEPTABLE.add("INT_LITERAL");
        List_TOKENACCEPTABLE.add("OPERATOR");
        List_TOKENACCEPTABLE.add("STRING_LITERAL");
        List_TOKENACCEPTABLE.add("SEPARATOR");
        List_TOKENACCEPTABLE.add("ENDOFFILE");

        // This is the file input
        List_INPUT.add("example_fib");
        List_INPUT.add("example_gcd");
    }


    // This will handle all the automated data
    public static void getAutomatonData(String fileName) {
        File file = new File(fileName);
        try {
            Scanner myReader = new Scanner(file);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                // Get all the CHARACTER in ASCII used in a VC program
                if (Objects.equals(data, "CHARACTER")) {
                    String temp = myReader.nextLine();
                        for (int i = 0; i < temp.length(); i++) {
                            List_CHARACTER.add(temp.charAt(i));
                        }
                }
                // Same as the digit
                if (data .equals("DIGIT")) {
                    String temp = myReader.nextLine();
                    for (int i = 0; i < temp.length(); i++) {
                        List_DIGIT.add(temp.charAt(i));
                    }
                }
                // Get all accepted separator
                if (data.equals("SEPARATOR")) {
                    String temp = myReader.nextLine();
                    for (int i = 0; i < temp.length(); i++) {
                        List_SEPARATOR.add(temp.charAt(i));
                    }
                }
                // Store all ENDING_STATE in both int and string
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
                // Store the mapping state
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
                // Get the Keyword which are string but can't be treated as identifier
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


    // Return the content for each char as int
    // by looking up for them in the generated ArrayLists above
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

    // Parse down file into tokens including Whitespace, tabs or comment
    public static void analyseFile(String fileName) {
        // Clear tokens before parse down a new file input
        List_Token.clear();
        int currentState = 0;
        int line = 1;
        int length = 1;
        int end = 1;
        File file = new File("Input/"+fileName+".vc");

        // Run through the file 1 by 1 char
        // if getContent() of that char is not -1
        // then the state will go to map[currentState][getContent(c)]
        // if the state is an END it will take down a token
        // It is even count the length, line and end of the token for position
        // After running through the file, add a token with the type ENDOFFILE
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


    // This is for test only
    public static void testAutomatonDataRead() {
        System.out.println("table content counter: " + tableContentCounter);
        for (int i=1;i<=tableContentCounter;i++) System.out.println(tableContent[i]);
        for (Integer integer : List_ENDSTATE) System.out.println(integer);
    }

    // This is for test only
    public static void testFileAnalyse() {
        System.out.println("token counter: " + List_Token.size());
        int n=0;
        for (Token token : List_Token)
            if (List_TOKENACCEPTABLE.contains(token.getType()))
            {n++;
                System.out.println(token.getWord() + " " + token.getType());}
        System.out.println(n);
    }

    // This will make 2 output file in Output folder
    // 1 .vctok contain all the useful tokens
    // 2.verbose.vctok contain all the useful tokens with Type, Spelling, Position inside the program
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

    // This will run the main activity, analyse all input and parse them down to tokens
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