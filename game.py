#!/usr/bin/env python3
import json
import sys
from pathlib import Path

# --------------------------
# CONFIG (EDIT THESE)
# --------------------------
owner = "MrunaliOza04"
repo  = "tic-tac-toe-ai"
# --------------------------

REPO = Path(".")
BOARD_FILE = REPO / "board.json"
DIFF_FILE  = REPO / "difficulty.txt"
README     = REPO / "README.md"

wins = [
    (0,1,2),(3,4,5),(6,7,8),
    (0,3,6),(1,4,7),(2,5,8),
    (0,4,8),(2,4,6)
]

def load():
    return json.loads(BOARD_FILE.read_text())

def save(data):
    BOARD_FILE.write_text(json.dumps(data, indent=2))

def check_winner(b):
    for a,bidx,c in wins:
        if b[a] != " " and b[a] == b[bidx] == b[c]:
            return b[a]
    if " " not in b:
        return "D"
    return ""

def minimax(board, player):
    w = check_winner(board)
    if w == "O": return {"score": 1}
    if w == "X": return {"score": -1}
    if w == "D": return {"score": 0}

    moves = []
    for i in range(9):
        if board[i] == " ":
            board[i] = player
            result = minimax(board, "O" if player == "X" else "X")
            moves.append({"index": i, "score": result["score"]})
            board[i] = " "

    if player == "O":
        return max(moves, key=lambda x: x["score"])
    else:
        return min(moves, key=lambda x: x["score"])

def ai_move(data, difficulty):
    b = data["board"]

    if difficulty == "easy":
        import random
        empties = [i for i,v in]()
