Lexical Scanner of VC (Variance of C)
The Lexical Scanner is a tool designed to analyze and tokenize input text into meaningful units, such as keywords, identifiers, operators, and literals. It serves as the first step in the compilation process, breaking down source code into smaller components for further processing.
This program is designed for my compiler course.
Contributing
https://github.com/daibangsamac
Flow: The program will first handle data from AutomatonData.dat. It contains all the tokens we need to parse down a VC program:
+ CHARACTER: Formated as ASCII which is used in a VC program
+ DIGIT: As it name
+ KEYWORD: These things can't be used as identifier
+ STARTING_STATE: Typically, there's only 1
+ ENDING_STATE: All the ending state can be
+ TRANSITION TABLE: TABLE use for mapping 1 state to another
  Next, the program will parse down every single tokens into a string and put them into an ArrayList of String
  Finally, the program will write down all tokens to output files which are formated
How to use the program:
+ Copy input file (*.vc) to Input folder
+ Add filename (the * part) to List_INPUT inside initialized function in Main.java
+ Run the program
+ 2 output file will be inside the Output folder
