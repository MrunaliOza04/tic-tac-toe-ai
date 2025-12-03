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
        

        if (move < 0 || move > 8) {
            System.err.println("move out of range");
            System.exit(1);
        }
        if (!board.isEmpty(move)) {
            System.err.println("Invalid move: occupied");
            System.exit(1);
        }

       // Apply player move
board.makeMove(move, 'X');

// Check if player wins
String winner = board.checkWinner();
if (!winner.equals("")) {
    board.save(boardPath);
    updateReadme(board, readmePath, diffPath);
    return;
}

// AI move
board.makeAIMove(difficulty);

// Check if AI wins
winner = board.checkWinner();
if (!winner.equals("")) {
    board.save(boardPath);
    updateReadme(board, readmePath, diffPath);
    return;
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
        String difficulty = "";
        if (Files.exists(diffPath)) {
            difficulty = new String(Files.readAllBytes(diffPath)).trim();
        }

        boolean difficultyChosen = difficulty.equals("easy") ||
                                   difficulty.equals("medium") ||
                                   difficulty.equals("hard");

        StringBuilder md = new StringBuilder();

        // ---------------------------------------------------
        // SCREEN 1 → Difficulty Selection
        // ---------------------------------------------------
        if (!difficultyChosen) {
            md.append("<h1 align=\"center\">🎮 Tic Tac Toe — Choose Difficulty</h1>");
            md.append("<p align=\"center\">Select difficulty to start a new game.</p><br>");

            String diffUrl = "https://github.com/" + OWNER + "/" + REPO + "/actions/workflows/vote-difficulty.yml";

            md.append("<div align=\"center\" style=\"margin-top:20px;\">\n");

            md.append("<a href=\"" + diffUrl + "\" style=\"padding:15px 30px; background:#00e1ff; color:black; border-radius:10px; margin:10px; text-decoration:none; font-size:20px; font-weight:bold;\">EASY</a>");

            md.append("<a href=\"" + diffUrl + "\" style=\"padding:15px 30px; background:#ffaa00; color:black; border-radius:10px; margin:10px; text-decoration:none; font-size:20px; font-weight:bold;\">MEDIUM</a>");

            md.append("<a href=\"" + diffUrl + "\" style=\"padding:15px 30px; background:#ff0066; color:white; border-radius:10px; margin:10px; text-decoration:none; font-size:20px; font-weight:bold;\">HARD</a>");

            md.append("</div>");

            Files.write(readme, md.toString().getBytes());
            return;
        }


       // ---------------------------------------------------
// SCREEN 2 → GAME OVER WITH ANIMATIONS
// ---------------------------------------------------
String winner = board.checkWinner();
if (!winner.equals("")) {

    md.append("<h1 align=\"center\" style=\"font-size:50px;\">🏁 Game Over</h1>");

    if (winner.equals("X")) {
        md.append("<h2 align=\"center\" style=\""
            + "font-size:40px;"
            + "color:#00ffcc;"
            + "text-shadow:0 0 15px #00fff2, 0 0 30px #00e1ff;"
            + "animation: neon-pulse 1.5s infinite alternate;"
            + "\">"
            + "✨❌ YOU WIN! ❌✨</h2>");
    } else if (winner.equals("O")) {
        md.append("<h2 align=\"center\" style=\""
            + "font-size:40px;"
            + "color:#ff3377;"
            + "text-shadow:0 0 15px #ff3377, 0 0 35px #ff0055;"
            + "animation: neon-pulse 1.5s infinite alternate;"
            + "\">"
            + "💀⭕ AI WINS! ⭕💀</h2>");
    } else {
        md.append("<h2 align=\"center\" style=\""
            + "font-size:40px;"
            + "color:#cccccc;"
            + "text-shadow:0 0 10px #999;"
            + "animation: fade-pulse 2s infinite alternate;"
            + "\">"
            + "😐 It's a Draw 😐</h2>");
    }

    // ANIMATED PLAY AGAIN BUTTON
    String restartUrl = "https://github.com/" + OWNER + "/" + REPO + "/actions/workflows/vote-difficulty.yml";

    md.append("<div align=\"center\" style=\"margin-top:30px;\">");
    md.append("<a href=\"" + restartUrl + "\" style=\""
        + "padding:15px 40px;"
        + "background:#00e1ff;"
        + "color:black;"
        + "border-radius:12px;"
        + "font-size:22px;"
        + "font-weight:bold;"
        + "text-decoration:none;"
        + "box-shadow:0 0 15px #00e1ff, 0 0 30px #00bcd4;"
        + "animation: button-pulse 1.5s infinite alternate;"
        + "\">"
        + "🔄 PLAY AGAIN"
        + "</a>");
    md.append("</div>");

    // EXTRA: embed animations using inline SVG (allowed by GitHub)
    md.append("<svg width=\"0\" height=\"0\">"
        + "<style>"
        + "@keyframes neon-pulse {"
        + "  from { opacity: 0.7; }"
        + "  to { opacity: 1; transform: scale(1.08); }"
        + "}"
        + "@keyframes fade-pulse {"
        + "  from { opacity: 0.5; }"
        + "  to { opacity: 1; }"
        + "}"
        + "@keyframes button-pulse {"
        + "  from { box-shadow:0 0 10px #00e1ff; }"
        + "  to { box-shadow:0 0 25px #00ffff; transform:scale(1.05); }"
        + "}"
        + "</style>"
        + "</svg>");

    Files.write(readme, md.toString().getBytes());
    return;
}



        // ---------------------------------------------------
        // SCREEN 3 → NEON GAME BOARD
        // ---------------------------------------------------
        md.append("<h1 align=\"center\">🎮 Neon Tic Tac Toe — AI</h1>");
        md.append("<p align=\"center\">Click a tile to play. You are <b>X</b>. AI is <b>O</b>.</p>");

        md.append("<div align=\"center\" style=\"margin-top:20px;\">\n");
        md.append("<table style=\"border-collapse: collapse; border: 2px solid #00e1ff; box-shadow: 0 0 25px #00e1ff;\">\n");

        for (int r = 0; r < 3; r++) {
            md.append("<tr>");
            for (int c = 0; c < 3; c++) {
                int idx = r * 3 + c;

                String tile = switch (board.cells[idx]) {
                    case 'X' -> "❌";
                    case 'O' -> "⭕";
                    default -> "⬜";
                };

                String playUrl = "https://github.com/" + OWNER + "/" + REPO + "/actions/workflows/play-move.yml";

                md.append("<td align=\"center\" width=\"120\" height=\"120\" style=\"border:2px solid #00e1ff; padding:10px; font-size:40px; box-shadow: inset 0 0 10px #00e1ff;\">");

                if (board.cells[idx] == ' ') {
                    md.append("<a href=\"" + playUrl + "\" style=\"color:#00e1ff; text-decoration:none; font-size:20px;\">")
                      .append(tile).append("<br><small>Play</small></a>");
                } else {
                    md.append(tile);
                }

                md.append("</td>");
            }
            md.append("</tr>");
        }

        md.append("</table>\n</div>");
        md.append("<p align=\"center\">Current difficulty: <b>" + difficulty + "</b></p>");

        Files.write(readme, md.toString().getBytes());

    } catch (IOException ex) {
        System.err.println("Failed to update README: " + ex.getMessage());
    }
}

}

