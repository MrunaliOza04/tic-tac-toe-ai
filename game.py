#!/usr/bin/env python3
import json
import sys
from pathlib import Path

REPO = Path('.')
BOARD_FILE = REPO / 'board.json'
DIFF_FILE = REPO / 'difficulty.txt'
README = REPO / 'README.md'

# Set your GitHub username and repo name here (replace exactly)
owner = 'MrunaliOza04'
repo = 'tic-tac-toe-ai'

# winning lines
wins = [(0,1,2),(3,4,5),(6,7,8),(0,3,6),(1,4,7),(2,5,8),(0,4,8),(2,4,6)]

def load():
    if not BOARD_FILE.exists():
        BOARD_FILE.write_text(json.dumps({"board": [" "]*9, "turn":"X", "winner":"", "moves":0}, indent=2))
    return json.loads(BOARD_FILE.read_text())

def save(data):
    BOARD_FILE.write_text(json.dumps(data, indent=2))

def check_winner(bd):
    for a, bidx, c in wins:
        if bd[a] != ' ' and bd[a] == bd[bidx] == bd[c]:
            return bd[a]
    if ' ' not in bd:
        return 'D'  # draw
    return ''

# Minimax for 'O' (AI). Player is 'X'.
def minimax(board, player):
    winner = check_winner(board)
    if winner == 'O':
        return {'score': 1}
    elif winner == 'X':
        return {'score': -1}
    elif winner == 'D':
        return {'score': 0}

    moves = []
    for i in range(9):
        if board[i] == ' ':
            board[i] = player
            result = minimax(board, 'O' if player == 'X' else 'X')
            moves.append({'index': i, 'score': result['score']})
            board[i] = ' '

    if player == 'O':
        best = max(moves, key=lambda x: x['score'])
    else:
        best = min(moves, key=lambda x: x['score'])
    return best

def ai_move(data, difficulty):
    b = data['board']
    if difficulty == 'easy':
        import random
        empties = [i for i,v in enumerate(b) if v == ' ']
        return random.choice(empties) if empties else -1
    elif difficulty == 'medium':
        # try to win
        for i in range(9):
            if b[i] == ' ':
                b[i] = 'O'
                if check_winner(b) == 'O':
                    b[i] = ' '
                    return i
                b[i] = ' '
        # block X
        for i in range(9):
            if b[i] == ' ':
                b[i] = 'X'
                if check_winner(b) == 'X':
                    b[i] = ' '
                    return i
                b[i] = ' '
        if b[4] == ' ':
            return 4
        import random
        empties = [i for i,v in enumerate(b) if v == ' ']
        return random.choice(empties) if empties else -1
    else:
        copyb = b.copy()
        best = minimax(copyb, 'O')
        return best['index']

def update_readme(data):
    cells = data['board']
    lines = []
    lines.append('# Tic-Tac-Toe — Play with clicks')
    lines.append('')
    lines.append('Click a tile to play (you are X).')
    lines.append('')
    lines.append('<table>')
    for r in range(3):
        lines.append('<tr>')
        for c in

