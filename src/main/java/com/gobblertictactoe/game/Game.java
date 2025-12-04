package com.gobblertictactoe.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.gobblertictactoe.model.AI;
import com.gobblertictactoe.model.Board;
import com.gobblertictactoe.model.Move;
import com.gobblertictactoe.model.Piece;
import com.gobblertictactoe.model.Player;
import com.gobblertictactoe.model.RandomAI;
import com.gobblertictactoe.view.BoardView;

public class Game {

    private final Board board;
    private final Player pO;
    private final Player pB;
    private Player player;
    
    private final Scanner sc = new Scanner(System.in);
    private final BoardView view = new BoardView();
    private int count;
    private AI aiForB = null; // if not null,B is AI

    public Game(int size){
        this.board = new Board(size);
        this.pO = new Player('O', "\u001B[34m");       // blue
        this.pB = new Player('B', "\u001B[38;5;208m"); // orange
        player = pO;
    }

    public void start(){
        // pieces per player
        System.out.print("Enter number of pieces per player (default 6, enter -1 for default): ");
        count = sc.nextInt();
        while(count != -1 && !(count >= 3 && count <= 10)) {
            System.out.print("Invalid count, please enter a number between 3 and 10 (or -1): ");
            count = sc.nextInt();
        }
        if(count == -1) count = 6;
        setPiecePool(count);

        // choose mode
        System.out.print("1. Two player mode\n2. Computer mode\nEnter 1 or 2: ");
        int choice = sc.nextInt();
        while(choice != 1 && choice != 2){
            System.out.print("Invalid choice, please enter 1 or 2: ");
            choice = sc.nextInt();
        }
        if(choice == 2) aiForB = new RandomAI();

        // consume newline so nextLine() works below
        sc.nextLine();

        // game loop
        while(true){
            view.printTop(board);
            makeMove();
            if (checkWin(player.id())) { view.printTop(board); System.out.println(player.id() + " wins!"); break; }
            if (!hasAnyLegalMove(pO) && !hasAnyLegalMove(pB)) { view.printTop(board); System.out.println("Draw - no legal moves"); break; }
            switchPlayer();
        }
    }

    private void setPiecePool(int count){
        for(int i=1;i<=count;i++){
            pO.addToPool(new Piece(pO.id(), i, pO.colorCode()));
            pB.addToPool(new Piece(pB.id(), i, pB.colorCode()));
        }
    }

    private void makeMove(){
        List<Move> legal = collectLegalMoves(player);
        if(player == pB && aiForB != null){
            System.out.println("AI thinking...");
            Move m = aiForB.chooseMove(board, pB, pO, legal);
            if(m == null){ System.out.println("AI has no move."); return; }
            applyMove(m);
            return;
        }

        if(legal.isEmpty()){
            System.out.println("No legal move for " + player.id());
            return;
        }

        // Print pool
        System.out.println("Pool: " + player.poolView());

        // Ask user whether to PLACE (from pool) or MOVE (an on-board top piece)
        while(true){
            System.out.print("Enter 'P' to place from pool or 'M' to move a top piece: ");
            String mode = sc.nextLine().trim().toUpperCase();
            if(mode.equals("P")){
                handlePlace();
                break;
            } else if(mode.equals("M")){
                handleMove();
                break;
            } else {
                System.out.println("Invalid option. Enter P or M.");
            }
        }
    }

    private void handlePlace(){
        // choose size from pool
        int size = -1;
        while(true){
            System.out.print("Enter size of piece to place from pool (e.g. 3 for O3): ");
            String s = sc.nextLine().trim();
            try { size = Integer.parseInt(s); }
            catch(Exception e){ System.out.println("Invalid number"); continue; }
            if(size <= 0 || size > count){ System.out.println("Size out of allowed range."); continue; }
            Optional<Piece> opt = player.findInPoolBySize(size);
            if(!opt.isPresent()){ System.out.println("You don't have that piece in your pool."); continue; }
            Piece p = opt.get();

            String pos = getLegalPosition();
            int col = pos.charAt(0) - 'A';
            int row = Integer.parseInt(pos.substring(1)) - 1;
            if(!board.canPlace(p, row, col)){ System.out.println("Cannot place there (size rule). Try again."); continue; }

            applyMove(new Move(p, -1, -1, row, col));
            break;
        }
    }

    private void handleMove(){
        // choose origin position (must be top and belong to player)
        while(true){
            System.out.print("Enter origin position of your top piece (e.g. A1): ");
            String origin = sc.nextLine().trim().toUpperCase();
            if(origin.length() < 2 || origin.length() > 3){ System.out.println("Invalid format"); continue; }
            char colC = origin.charAt(0);
            int col = colC - 'A';
            int row;
            try { row = Integer.parseInt(origin.substring(1)) - 1; }
            catch(Exception e){ System.out.println("Invalid row"); continue; }
            if(row<0 || row>=board.size() || col<0 || col>=board.size()){ System.out.println("Out of range"); continue; }
            Optional<Piece> top = board.topAt(row, col);
            if(!top.isPresent() || top.get().owner() != player.id()){ System.out.println("No your top piece at that position"); continue; }
            Piece p = top.get();

            // choose destination
            String dest = getLegalPosition();
            int dcol = dest.charAt(0) - 'A';
            int drow = Integer.parseInt(dest.substring(1)) - 1;
            if(drow==row && dcol==col){ System.out.println("Destination same as origin"); continue; }
            if(!board.canPlace(p, drow, dcol)){ System.out.println("Cannot move there (size rule)."); continue; }

            applyMove(new Move(p, row, col, drow, dcol));
            break;
        }
    }

    private String getLegalPosition(){
        while(true){
            System.out.print("Enter target position (eg. A1,B3,C2): ");
            String pos = sc.nextLine().trim().toUpperCase();
            if(pos == null || pos.length() < 2 || pos.length() > 3){ System.out.println("Invalid format"); continue; }
            char colChar = pos.charAt(0);
            if(!( (colChar>='A' && colChar<='Z') )){ System.out.println("Invalid column letter"); continue; }
            int rowNum;
            try { rowNum = Integer.parseInt(pos.substring(1)); }
            catch(Exception e){ System.out.println("Invalid row number"); continue; }
            int colIndex = colChar - 'A';
            int rowIndex = rowNum - 1;
            if(colIndex < 0 || colIndex >= board.size() || rowIndex < 0 || rowIndex >= board.size()){
                System.out.println("Position out of board range. Board size: " + board.size()); continue;
            }
            return "" + colChar + rowNum;
        }
    }

    private boolean hasAnyLegalMove(Player p){
        return !collectLegalMoves(p).isEmpty();
    }

    private void switchPlayer(){ player = (player == pO) ? pB : pO; }

    private void applyMove(Move m){
        if(m.fromR >= 0){
            board.popTop(m.fromR, m.fromC); // remove from origin
        } else {
            // remove from owner's pool
            Player ownerPlayer = (m.piece.owner() == pO.id()) ? pO : pB;
            boolean removed = ownerPlayer.removeFromPool(m.piece);
            if(!removed){
                // defensive: should not happen because we always use real object from pool or board top
                System.out.println("Warning: piece not removed from pool (unexpected).");
            }
        }
        board.place(m.piece, m.toR, m.toC);
        System.out.println("Applied " + m);
    }

    private List<Move> collectLegalMoves(Player p){
        List<Move> moves = new ArrayList<>();

        // pool moves (use real pool pieces)
        for(Piece piece : p.poolView()){
            for(int r=0;r<board.size();r++)
                for(int c=0;c<board.size();c++){
                    if(board.canPlace(piece, r, c)) moves.add(new Move(piece, -1, -1, r, c));
                }
        }

        // top pieces on board (use actual top piece objects)
        for(int r=0;r<board.size();r++){
            for(int c=0;c<board.size();c++){
                Optional<Piece> top = board.topAt(r, c);
                if(top.isPresent() && top.get().owner() == p.id()){
                    Piece piece = top.get();
                    for(int r2=0;r2<board.size();r2++){
                        for(int c2=0;c2<board.size();c2++){
                            if(r==r2 && c==c2) continue;
                            if(board.canPlace(piece, r2, c2)) moves.add(new Move(piece, r, c, r2, c2));
                        }
                    }
                }
            }
        }
        return moves;
    }

    /**
     * Check win for playerId by looking at top pieces (correct row/col indexing).
     */
    public boolean checkWin(char playerId){

        int rows=board.size();
        int cols=board.size();

        // rows
        for (int r = 0; r < rows; r++) {
            boolean ok = true;
            for (int c = 0; c < cols; c++) {
                Optional<Piece> t = board.topAt(r, c);
                if (!t.isPresent() || t.get().owner() != playerId) { ok = false; break; }
            }
            if (ok) return true;
        }
        // cols
        for (int c = 0; c < cols; c++) {
            boolean ok = true;
            for (int r = 0; r < rows; r++) {
                Optional<Piece> t = board.topAt(r, c);
                if (!t.isPresent() || t.get().owner() != playerId) { ok = false; break; }
            }
            if (ok) return true;
        }
        // main diag
        boolean ok = true;
        for (int i = 0; i < Math.min(rows, cols); i++) {
            Optional<Piece> t = board.topAt(i, i);
            if (!t.isPresent() || t.get().owner() != playerId) { ok = false; break; }
        }
        if (ok) return true;
        // anti-diag
        ok = true;
        for (int i = 0; i < Math.min(rows, cols); i++) {
            Optional<Piece> t = board.topAt(i, cols - 1 - i);
            if (!t.isPresent() || t.get().owner() != playerId) { ok = false; break; }
        }
        return ok;
    }

    public static void main(String[] args) {
        System.out.println("Welcome to Gobbler Tic-Tac-Toe");
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the size of board (default 3x3, enter -1 for default): ");
        int size = sc.nextInt();
        while(size != -1 && !(size >= 3 && size <= 10)){
            System.out.print("Invalid size please enter between 3 to 10 or -1: ");
            size = sc.nextInt();
        }
        if(size == -1) size = 3;
        Game g = new Game(size);
        g.start();
    }
}
