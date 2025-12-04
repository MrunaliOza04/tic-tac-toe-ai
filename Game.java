import java.nio.file.*;
import java.io.IOException;

public class Game {

    static final String OWNER = "MrunaliOza04";
    static final String REPO  = "tic-tac-toe-ai";

    public static void main(String[] args) throws Exception {

        if (args.length < 1) {
            System.err.println("usage: java Game MOVE_INDEX");
            System.exit(1);
        }

        int move = Integer.parseInt(args[0]);

        Path root = Paths.get(".");
        Path boardPath = root.resolve("board.json");
        Path diffPath  = root.resolve("difficulty.txt");
        Path readme    = root.resolve("README.md");

        Board board = Board.load(boardPath);

        // ---- READ DIFFICULTY ----
        String difficulty = "easy";
        if (Files.exists(diffPath)) {
            difficulty = new String(Files.readAllBytes(diffPath)).trim();
            if (difficulty.isEmpty()) difficulty = "easy";
        }

        // ---- CHECK IF GAME ALREADY FINISHED ----
        if (!board.checkWinner().equals("")) {
            // Just refresh README (winner screen will show)
            updateReadme(board, readme, diffPath);
            return;
        }

        // ---- VALIDATIONS ----
        if (move < 0 || move > 8) {
            System.err.println("Invalid move index.");
            return;
        }
        if (!board.isEmpty(move)) {
            System.err.println("Tile already taken.");
            return;
        }

        // ---- PLAYER MOVE ----
        board.cells[move] = 'X';
        board.moves++;

        // ---- CHECK IF PLAYER JUST WON ----
        String winner = board.checkWinner();
        if (!winner.equals("")) {
            board.winner = winner;
            board.save(boardPath);
            updateReadme(board, readme, diffPath);
            return;
        }

        // ---- AI MOVE ----
        int aiMove = switch (difficulty.toLowerCase()) {
            case "medium" -> AI.mediumMove(board);
            case "hard"   -> AI.hardMove(board);
            default       -> AI.easyMove(board);
        };

        if (aiMove >= 0) {
            board.cells[aiMove] = 'O';
            board.moves++;
        }

        // ---- CHECK IF AI WON ----
        winner = board.checkWinner();
        if (!winner.equals("")) {
            board.winner = winner;
        }

        // ---- SAVE + DISPLAY ----
        board.save(boardPath);
        updateReadme(board, readme, diffPath);

        System.out.println("Done");
    }

    // ===========================================================
    // UPDATE README (Your animated, 3-screen interface)
    // ===========================================================
    static void updateReadme(Board board, Path readme, Path diffPath) {
        try {
            String difficulty = "";
            if (Files.exists(diffPath)) {
                difficulty = new String(Files.readAllBytes(diffPath)).trim();
            }

            boolean chosen = difficulty.equals("easy") ||
                             difficulty.equals("medium") ||
                             difficulty.equals("hard");

            StringBuilder md = new StringBuilder();

          // SCREEN 1 — Difficulty Select
if (!chosen) {

    md.append("<h1 align=\"center\">🎮 Tic Tac Toe — Choose Difficulty</h1>");
    md.append("<p align=\"center\">Select difficulty to start.</p><br>");

    String diffUrl = "https://github.com/" + OWNER + "/" + REPO + "/actions/workflows/vote-difficulty.yml";

    md.append("<div align=\"center\" style=\"margin-top:25px; font-size:26px; font-weight:bold;\">");

    md.append("<a href=\"" + diffUrl + "\" style=\"margin:0 20px; color:#00e1ff; text-decoration:none;\">Easy</a>");
    md.append("<span style=\"color:#888;\">|</span>");
    md.append("<a href=\"" + diffUrl + "\" style=\"margin:0 20px; color:#ffaa00; text-decoration:none;\">Medium</a>");
    md.append("<span style=\"color:#888;\">|</span>");
    md.append("<a href=\"" + diffUrl + "\" style=\"margin:0 20px; color:#ff0066; text-decoration:none;\">Hard</a>");

    md.append("</div>");

    Files.write(readme, md.toString().getBytes());
    return;
}

}


            // -------------------------
            // SCREEN 2 — GAME OVER
            // -------------------------
            String winner = board.checkWinner();
            if (!winner.equals("")) {

                md.append("<h1 align=\"center\" style=\"font-size:50px;\">🏁 Game Over</h1>");

                if (winner.equals("X")) {
                    md.append(neonText("✨❌ YOU WIN! ❌✨","#00ffcc"));
                } else if (winner.equals("O")) {
                    md.append(neonText("💀⭕ AI WINS! ⭕💀","#ff3377"));
                } else {
                    md.append(neonText("😐 It's a Draw 😐","#cccccc"));
                }

                String restart = "https://github.com/" + OWNER + "/" + REPO + "/actions/workflows/vote-difficulty.yml";

                md.append("<div align=\"center\" style=\"margin-top:30px;\">");
                md.append(animatedButton(restart,"🔄 PLAY AGAIN"));
                md.append("</div>");

                md.append(animationCSS());

                Files.write(readme, md.toString().getBytes());
                return;
            }

            // -------------------------
            // SCREEN 3 — GAME BOARD
            // -------------------------
            md.append("<h1 align=\"center\">🎮 Neon Tic Tac Toe — AI</h1>");
            md.append("<p align=\"center\">Click a tile. You are <b>X</b>.</p>");

            md.append(boardHTML(board));

            md.append("<p align=\"center\">Difficulty: <b>"+difficulty+"</b></p>");

            Files.write(readme, md.toString().getBytes());

        } catch (IOException ex) {
            System.err.println("Failed to update README.");
        }
    }

    // Helper HTML Generators
    static String button(String link,String text,String bg,String color) {
        return "<a href=\"" + link + "\" style=\"padding:15px 30px; background:" + bg +
               "; color:" + color + "; border-radius:10px; margin:10px; text-decoration:none;" +
               "font-size:20px; font-weight:bold;\">" + text + "</a>";
    }

    static String animatedButton(String link,String text) {
        return "<a href=\"" + link +
               "\" style=\"padding:15px 40px; background:#00e1ff; color:black; border-radius:12px;" +
               "font-size:22px; font-weight:bold; text-decoration:none;" +
               "box-shadow:0 0 15px #00e1ff, 0 0 30px #00bcd4;" +
               "animation: button-pulse 1.5s infinite alternate;\">" +
               text + "</a>";
    }

    static String neonText(String text,String color) {
        return "<h2 align=\"center\" style=\"font-size:40px;color:"+color+
               ";text-shadow:0 0 15px "+color+",0 0 30px "+color+
               ";animation: neon-pulse 1.5s infinite alternate;\">"+text+"</h2>";
    }

    static String boardHTML(Board b) {
        StringBuilder md = new StringBuilder();
        md.append("<div align=\"center\" style=\"margin-top:20px;\">");
        md.append("<table style=\"border-collapse: collapse; border: 2px solid #00e1ff; box-shadow: 0 0 25px #00e1ff;\">");

        String playUrl = "https://github.com/" + OWNER + "/" + REPO + "/actions/workflows/play-move.yml";

        for (int r=0;r<3;r++) {
            md.append("<tr>");
            for (int c=0;c<3;c++) {
                int i = r*3 + c;

                String tile =
                    (b.cells[i] == 'X') ? "❌" :
                    (b.cells[i] == 'O') ? "⭕" : "⬜";

                md.append("<td align=\"center\" width=\"120\" height=\"120\" style=\"border:2px solid #00e1ff; padding:10px; font-size:40px; box-shadow: inset 0 0 10px #00e1ff;\">");

                if (b.cells[i] == ' ') {
                    md.append("<a href=\"" + playUrl + "\" style=\"color:#00e1ff; text-decoration:none; font-size:20px;\">"+tile+"<br><small>Play</small></a>");
                } else {
                    md.append(tile);
                }

                md.append("</td>");
            }
            md.append("</tr>");
        }
        md.append("</table></div>");
        return md.toString();
    }

    static String animationCSS() {
        return "<svg width=\"0\" height=\"0\"><style>"
             + "@keyframes neon-pulse { from{opacity:0.7;} to{opacity:1;transform:scale(1.08);} }"
             + "@keyframes button-pulse { from{box-shadow:0 0 10px #00e1ff;} to{box-shadow:0 0 25px #00ffff;transform:scale(1.05);} }"
             + "</style></svg>";
    }
}
