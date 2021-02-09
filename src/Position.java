public class Position  {
    private final String text;
    private int line, pos, index;

    public int getLine() {
        return line;
    }

    public int getPos() {
        return pos;
    }

    public int getIndex() {
        return index;
    }

    public String getText() {
        return text;
    }

    public Position(String text) {
        this.text = text;
        line = pos = 1;
        index = 0;
    }

    public Position(Position p) {
        this.text = p.getText();
        this.line = p.getLine();
        this.pos = p.getPos();
        this.index = p.getIndex();
    }

    public boolean isEOF() {
        return index == text.length();
    }

    public String toString() {
        return "(" + line + "," + pos + ")";
    }

    public int getChar() {
        return isEOF() ? -1 : text.codePointAt(index);
    }

    public boolean isWhitespace() {
        return !isEOF() && Character.isWhitespace(getChar());
    }

    public String getTail() {
        return text.substring(index);
    }

    public static Position skipWhitespaces(Position p) {
        Position result = new Position(p);
        while (result.isWhitespace()) {
            result = result.next();
        }
        return result;
    }

    public Position skipSymbols(int count) {
        Position result = new Position(this);
        result.index += count;
        result.pos += count;
        return result;
    }


    public boolean isNewLine() {
        if (index == text.length()) {
            return true;
        }

        if (text.charAt(index) == '\r' && index + 1 < text.length()) {
            return (text.charAt(index + 1) == '\n');
        }

        return (text.charAt(index) == '\n');
    }

    public Position next() {
        Position p = new Position(this);
        if (!p.isEOF()) {
            if (p.isNewLine()) {
                if (p.text.charAt(p.index) == '\r')
                    p.index++;
                p.line++;
                p.pos = 1;
            } else {
                if (Character.isHighSurrogate(p.text.charAt(p.index)))
                    p.index++;
                p.pos++;
            }
            p.index++;
        }
        return p;
    }
}
