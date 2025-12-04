
import java.nio.file.*;
import java.io.IOException;
import java.util.Arrays;

public class Board {
    public char[] cells = new char[9];
    public String turn = "X";
    public String winner = "";
    public int moves = 0;

    public Board() {
        Arrays.fill(cells, ' ');
    }

    public boolean isEmpty(int i) {
    return cells[i] == ' ';
}

    public static Board load(Path p) throws IOException {
        if (!Files.exists(p)) {
            Board b = new Board();
            b.save(p);
            return b;
        }
        String txt = new String(Files.readAllBytes(p));
        // very small parser for our known structure
        Board b = new Board();
        int start = txt.indexOf('"', txt.indexOf("\"board\"")) + 1;
        start = txt.indexOf('[', start);
        int end = txt.indexOf(']', start);
        String arr = txt.substring(start+1, end).trim();
        if (!arr.isEmpty()) {
            String[] parts = arr.split(",");
            for (int i = 0; i < Math.min(parts.length, 9); i++) {
                String v = parts[i].trim();
                v = v.replaceAll("\"", "").trim();
                if (v.equals("") || v.equals(" ") || v.equals("' '")) b.cells[i] = ' ';
                else b.cells[i] = v.charAt(0);
            }
        }
        // read turn
        int tIdx = txt.indexOf("\"turn\"");
        if (tIdx >= 0) {
            int colon = txt.indexOf(':', tIdx);
            int comma = txt.indexOf(',', colon);
            String t = txt.substring(colon+1, comma).replaceAll("[\"\\s]", "");
            if (!t.isEmpty()) b.turn = t;
        }
        // winner
        int wIdx = txt.indexOf("\"winner\"");
        if (wIdx >= 0) {
            int colon = txt.indexOf(':', wIdx);
            int comma = txt.indexOf(',', colon);
            if (comma == -1) comma = txt.indexOf('}', colon);
            String w = txt.substring(colon+1, comma).replaceAll("[\"\\s]", "");
            if (!w.isEmpty()) b.winner = w;
        }
        // moves
        int mIdx = txt.indexOf("\"moves\"");
        if (mIdx >= 0) {
            int colon = txt.indexOf(':', mIdx);
            int comma = txt.indexOf(',', colon);
            if (comma == -1) comma = txt.indexOf('}', colon);
            String mv = txt.substring(colon+1, comma).replaceAll("[\\s]", "");
            try { b.moves = Integer.parseInt(mv); } catch(Exception ex) { b.moves = 0; }
        }
        return b;
    }

    public void save(Path p) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"board\": [");
        for (int i = 0; i < 9; i++) {
            char ch = cells[i];
            String out = " ";
            if (ch == ' ') out = " ";
            else out = String.valueOf(ch);
            sb.append("\"").append(out).append("\"");
            if (i < 8) sb.append(", ");
        }
        sb.append("],\n");
        sb.append("  \"turn\": \"").append(turn).append("\",\n");
        sb.append("  \"winner\": \"").append(winner).append("\",\n");
        sb.append("  \"moves\": ").append(moves).append("\n");
        sb.append("}\n");
        Files.write(p, sb.toString().getBytes());
    }

   String checkWinner() {
    int[][] wins = {
        {0,1,2}, {3,4,5}, {6,7,8}, // rows
        {0,3,6}, {1,4,7}, {2,5,8}, // columns
        {0,4,8}, {2,4,6}           // diagonals
    };

    for (int[] w : wins) {
        if (cells[w[0]] != ' ' &&
            cells[w[0]] == cells[w[1]] &&
            cells[w[1]] == cells[w[2]]) {

            return String.valueOf(cells[w[0]]);
        }
    }

    // Check draw
    boolean full = true;
    for (char c : cells) {
        if (c == ' ') {
            full = false;
            break;
        }
    }
    
    if (full) return "D"; // Draw

    return ""; // No winner
}

}
