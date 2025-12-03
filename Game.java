import java.nio.file.*;
import java.io.IOException;

public class Game {
    // CONFIG - change only if your repo differs
    static final String OWNER = "MrunaliOza04";
    static final String REPO  = "tic-tac-toe-ai";

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("usage: java Game MOVE_INDEX");
            System.exit(1);
        }
        int move = Integer.parseInt(args[0]);
        Path repoRoot = Paths.get(".");
        Path boardPath = repoRoot.resolve("board.json");
        Path diffPath  = repoRoot.resolve("difficulty.txt");
        Path readme    = repoRoot.resolve("README.md");

        Board board = Board.load(boardPath);

        // reset if game finished
        if (board.winner != null && !board.winner.equals("")) {
            board = new Board();
        }

        if (move < 0 || move > 8) {
            System.err.println("move out of range");
            System.exit(1);
        }
        if (!board.isEmpty(move)) {
            System.err.println("Invalid move: occupied");
            System.exit(1);
        }

        // Apply player move
        board.cells[move] = 'X';
        board.moves += 1;
        board.turn = "O";

        // check if player already won
        String w = board.checkWinner();
        if (!w.equals("")) {
            board.winner = w;
            board.save(boardPath);
            updateReadme(board, readme, diffPath);
            System.out.println("Player finished: " + w);
            System.exit(0);
        }

        // read difficulty
        String difficulty = "easy";
        if (Files.exists(diffPath)) {
            difficulty = new String(Files.readAllBytes(diffPath)).trim();
            if (difficulty.equals("")) difficulty = "easy";
        }

        int aiMove = -1;
        if (difficulty.equalsIgnoreCase("easy")) {
            aiMove = AI.easyMove(board);
        } else if (difficulty.equalsIgnoreCase("medium")) {
            aiMove = AI.mediumMove(board);
        } else {
            aiMove = AI.hardMove(board);
        }

        if (aiMove >= 0) {
            board.cells[aiMove] = 'O';
            board.moves += 1;
            board.turn = "X";
        }

        w = board.checkWinner();
        if (!w.equals("")) board.winner = w;

        // save and update readme
        board.save(boardPath);
        updateReadme(board, readme, diffPath);
        System.out.println("Done");
    }

    static void updateReadme(Board board, Path readme, Path diffPath) {
        try {
            String diff = Files.exists(diffPath) ? new String(Files.readAllBytes(diffPath)).trim() : "easy";
            StringBuilder md = new StringBuilder();
            md.append("# 🎮 Tic Tac Toe — AI Game\n\n");
            md.append("Click a tile to play (you are **X**). After you click, run the `Play Move` workflow.\n\n");
            md.append("<table>\n");
            for (int r=0;r<3;r++) {
                md.append("<tr>\n");
                for (int c=0;c<3;c++) {
                    int i = r*3 + c;
                    String text = board.cells[i] == ' ' ? ("[ " + (i+1) + " ]") : String.valueOf(board.cells[i]);
                    String url = "https://github.com/" + OWNER + "/" + REPO + "/actions/workflows/play-move.yml";
                    md.append("<td align=\"center\" width=\"140\" height=\"140\">");
                    md.append(text);
                    md.append("<br><a href=\"" + url + "\">Play</a>");
                    md.append("</td>\n");
                }
                md.append("</tr>\n");
            }
            md.append("</table>\n\n");
            md.append("Current difficulty: **" + diff + "**\n");
            Files.write(readme, md.toString().getBytes());
        } catch (IOException ex) {
            System.err.println("Failed to update README: " + ex.getMessage());
        }
    }
}

