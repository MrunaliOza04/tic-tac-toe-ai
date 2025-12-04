// script.js - simplified AI (easy / medium / hard-simple)
(() => {
  const cells = Array.from(document.querySelectorAll('.cell'));
  const message = document.getElementById('message');
  const restartBtn = document.getElementById('restart');
  const diffButtons = Array.from(document.querySelectorAll('.diff-btn'));
  const diffLabel = document.getElementById('diffLabel');
  const openProfile = document.getElementById('openProfile');

  let board = [' ',' ',' ',' ',' ',' ',' ',' ',' '];
  let player = 'X', ai = 'O';
  let running = true;
  let difficulty = 'easy';

  // set profile site link
  openProfile.href = location.href; // opens same page in new tab

  // utils
  function render() {
    cells.forEach((el,i) => {
      el.classList.remove('x','o');
      el.textContent = board[i] === ' ' ? '' : board[i];
      if (board[i] === 'X') el.classList.add('x');
      if (board[i] === 'O') el.classList.add('o');
    });
  }
  function isEmpty(i){ return board[i] === ' '; }
  function available(){ return board.map((v,i)=>v===' '?i:null).filter(v=>v!==null); }
  function winnerOf(b){
    const wins=[[0,1,2],[3,4,5],[6,7,8],[0,3,6],[1,4,7],[2,5,8],[0,4,8],[2,4,6]];
    for (const w of wins){
      const [a,b1,c] = w;
      if (b[a] !== ' ' && b[a] === b[b1] && b[b1] === b[c]) return b[a];
    }
    if (b.every(x=>x!==' ')) return 'D';
    return null;
  }

  // EASY: random
  function easyMove(){
    const opts = available();
    return opts[Math.floor(Math.random()*opts.length)];
  }

  // MEDIUM: try win, block, center, corner, random
  function mediumMove(){
    // 1) win
    for (let i of available()){
      const copy = board.slice(); copy[i] = ai;
      if (winnerOf(copy) === ai) return i;
    }
    // 2) block player
    for (let i of available()){
      const copy = board.slice(); copy[i] = player;
      if (winnerOf(copy) === player) return i;
    }
    // 3) center
    if (isEmpty(4)) return 4;
    // 4) take best corner
    const corners = [0,2,6,8].filter(isEmpty);
    if (corners.length) return corners[Math.floor(Math.random()*corners.length)];
    // 5) fallback
    return easyMove();
  }

  // HARD (simple improved): medium + prefer forks (basic)
  function hardMove(){
    // prefer medium move first
    const m = mediumMove();
    if (m !== undefined) {
      // occasionally look for a fork (very simple)
      // try each available and pick one that creates 2 threats
      const opts = available();
      for (let i of opts){
        const copy = board.slice(); copy[i] = ai;
        // count winning moves after this
        let threats = 0;
        for (let j of available()){
          const c2 = copy.slice(); c2[j] = ai;
          if (winnerOf(c2) === ai) threats++;
        }
        if (threats >= 2) return i;
      }
      return m;
    }
    return easyMove();
  }

  function aiMoveNow(){
    if (!running) return;
    const fn = difficulty === 'easy' ? easyMove : difficulty === 'medium' ? mediumMove : hardMove;
    const idx = fn();
    if (idx === undefined || idx === null) return;
    board[idx] = ai;
    render();
    checkEnd();
  }

  // user click
  cells.forEach(c => c.addEventListener('click', e=>{
    if (!running) return;
    const idx = Number(c.dataset.i);
    if (!isEmpty(idx)) return;
    board[idx] = player;
    render();
    if (checkEnd()) return;
    // small delay for AI
    setTimeout(aiMoveNow, 350);
  }));

  function checkEnd(){
    const w = winnerOf(board);
    if (!w) return false;
    running = false;
    if (w === player) {
      message.textContent = "🎉 You Win!";
      message.style.color = '#00e1ff';
    } else if (w === ai) {
      message.textContent = "💀 AI Wins!";
      message.style.color = '#ff4d8b';
    } else {
      message.textContent = "😐 Draw!";
      message.style.color = '#c6d3da';
    }
    return true;
  }

  // diff buttons
  diffButtons.forEach(btn => btn.addEventListener('click', ()=>{
    diffButtons.forEach(x=>x.classList.remove('active'));
    btn.classList.add('active');
    difficulty = btn.dataset.diff;
    diffLabel.textContent = difficulty;
    restartGame();
  }));

  restartBtn.addEventListener('click', restartGame);

  function restartGame(){
    board = [' ',' ',' ',' ',' ',' ',' ',' ',' '];
    running = true;
    message.textContent = '';
    render();
  }

  // initial
  render();
})();
