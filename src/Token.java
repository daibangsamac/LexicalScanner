public class Token {
    private final String word;
    private final String type;
    private final int line;
    private final int length;
    private final int end;
    public Token(String _name,String _type,int _line,int _length,int _end) {
        this.word = _name;
        this.type = _type;
        line = _line;
        length = _length;
        end = _end;
    }

    public String getWord() {return word;}
    public String getType() {return type;}

    public String getPosition() {
        return line + "("+ (end-length+2)+").."+line +"("+end+")";
    }
}
