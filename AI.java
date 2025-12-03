import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AI {
    public static int easyMove(Board b) {
        List<Integer> empties = new ArrayList<>();
        for (int i=0;i<9;i++) if (b.cells[i]==' ') empties.add(i);
        return empties.get(new Random().nextInt(empties.size()));
    }

    public static int mediumMove(Board b) {
        // try to win
        for (int i=0;i<9;i++) {
            if (b.cells[i]==' ') {
                b.cells[i] = 'O';
                if (b.checkWinner().equals("O")) { b.cells[i] = ' '; return i; }
                b.cells[i] = ' ';
            }
        }
        // block X
        for (int i=0;i<9;i++) {
            if (b.cells[i]==' ') {
                b.cells[i] = 'X';
                if (b.checkWinner().equals("X")) { b.cells[i] = ' '; return i; }
                b.cells[i] = ' ';
            }
        }
        // take center
        if (b.cells[4]==' ') return 4;
        // else random
        return easyMove(b);
    }

    // Minimax for perfect play (O is maximizing)
    public static int hardMove(Board b) {
        Result r = minimax(b, 'O');
        return r.index;
    }

    private static Result minimax(Board b, char player) {
        String w = b.checkWinner();
        if (w.equals("O")) return new Result(1, -1);
        if (w.equals("X")) return new Result(-1, -1);
        if (w.equals("D")) return new Result(0, -1);

        List<Result> moves = new ArrayList<>();
        for (int i=0;i<9;i++) {
            if (b.cells[i]==' ') {
                b.cells[i] = player;
                Result res = minimax(b, (player == 'O') ? 'X' : 'O');
                moves.add(new Result(res.score, i));
                b.cells[i] = ' ';
            }
        }
        if (player == 'O') {
            Result best = moves.get(0);
            for (Result r : moves) if (r.score > best.score) best = r;
            return best;
        } else {
            Result best = moves.get(0);
            for (Result r : moves) if (r.score < best.score) best = r;
            return best;
        }
    }

    private static class Result {
        int score;
        int index;
        Result(int s,int i){ score=s; index=i; }
    }
}
