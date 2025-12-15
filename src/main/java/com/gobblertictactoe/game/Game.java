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
    private final Player orange;
    private final Player blue;
    private Player currentPlayer;
    private final Scanner scanner = new Scanner(System.in);
    private final BoardView view = new BoardView();
    private int pieceCount;
    private AI aiForBlue = null; // if not null,B is AI

    public Game(int size){
        this.board = new Board(size);
        this.orange = new Player('O',"\u001B[38;5;208m" ,"Orange");     // orange  
        this.blue = new Player('B', "\u001B[34m","Blue"); // blue
        currentPlayer = orange;
    }

    public void start(){

        // pieces per player
        while (true) {
            System.out.print(
                "Enter number of pieces per player (default 6, enter -1 for default): "
            );

            // 1️. Check if input is an integer
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // discard invalid token
                continue;
            }

            pieceCount = scanner.nextInt();

            // 2️. Handle default
            if (pieceCount == -1) {
                pieceCount = 6;
                break;
            }

            // 3️. Validate range
            if (pieceCount >= 3 && pieceCount <= 10) {
                break;
            }

            System.out.println(
                "Invalid count. Please enter a number between 3 and 10 (or -1)."
            );
        }

        // 4️. Consume newline so nextLine() works later
        scanner.nextLine();

        // 5️. Initialize pool
        setPiecePool(pieceCount);


        int choice;

        while (true) {
            System.out.print(
                "1. Two player mode\n2. Computer mode\nEnter 1 or 2: "
            );

            // 1️. Validate integer input
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // discard invalid token
                continue;
            }

            choice = scanner.nextInt();

            // 2️. Validate allowed choices
            if (choice == 1 || choice == 2) {
                break;
            }

            System.out.println("Invalid choice. Please enter 1 or 2.");
        }

        // 3️. Assign AI if needed
        if (choice == 2) {
            aiForBlue = new RandomAI();
        }

        // 4️. Consume newline so nextLine() works correctly later
        scanner.nextLine();


        // game loop
        while(true){
            view.printTop(board);
            makeMove();
            if (checkWin(currentPlayer.id())) { view.printTop(board); System.out.println(currentPlayer + " wins!"); break; }
            if (!hasAnyLegalMove(orange) && !hasAnyLegalMove(blue)) { view.printTop(board); System.out.println("Draw - no legal moves"); break; }
            switchPlayer();
        }
    }

    private void setPiecePool(int count){
        for(int i=1;i<=count;i++){
            orange.addToPool(new Piece(orange.id(), i, orange.colorCode()));
            blue.addToPool(new Piece(blue.id(), i, blue.colorCode()));
        }
    }

    private void makeMove(){
        List<Move> legalMoves = collectLegalMoves(currentPlayer);
        if(currentPlayer == blue && aiForBlue != null){
            System.out.println("AI thinking...");
            Move move = aiForBlue.chooseMove(board, blue, orange, legalMoves);
            if(move == null){ System.out.println("AI has no move."); return; }
            applyMove(move);
            return;
        }

        if(legalMoves.isEmpty()){
            System.out.println("No legal move for " + currentPlayer.id());
            return;
        }

        // Print pool
        System.out.println("Pool: " + currentPlayer.poolView());

        // Ask user whether to PLACE (from pool) or MOVE (an on-board top piece)
        while(true){
            System.out.print("Enter 'P' to place from pool or 'M' to move a top piece: ");
            String mode = scanner.nextLine().trim().toUpperCase();
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

    // choose size from pool
    private void handlePlace(){
        
        int size;

        while (true) {
            System.out.print("Enter size of piece to place from pool (e.g. 3 for O3): ");

            // 1️. Validate integer input
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // discard invalid token
                continue;
            }

            size = scanner.nextInt();
            scanner.nextLine(); // consume newline

            // 2️. Validate range
            if (size <= 0 || size > pieceCount) {
                System.out.println("Size out of allowed range.");
                continue;
            }

            // 3️. Validate piece exists in pool
            Optional<Piece> opt = currentPlayer.findInPoolBySize(size);
            if (!opt.isPresent()) {
                System.out.println("You don't have that piece in your pool.");
                continue;
            }

            Piece piece = opt.get();

            // 4️. Get and validate position
            String position = getLegalPosition();
            int col = position.charAt(0) - 'A';
            int row = Integer.parseInt(position.substring(1)) - 1;

            if (!board.canPlace(piece, row, col)) {
                System.out.println("Cannot place there (size rule). Try again.");
                continue;
            }

            // 5️. Apply move
            applyMove(new Move(piece, -1, -1, row, col));
            break;
        }

    }

    private void handleMove() {

        while (true) {
            System.out.print("Enter origin position of your top piece (e.g. A1): ");
            String origin = scanner.nextLine().trim().toUpperCase();

            // 1️. Basic format check
            if (origin.length() < 2 || origin.length() > 3) {
                System.out.println("Invalid format. Use A1, B3, etc.");
                continue;
            }

            char colChar = origin.charAt(0);

            // 2️. Column must be a letter
            if (colChar < 'A' || colChar >= 'A' + board.size()) {
                System.out.println("Invalid column.");
                continue;
            }

            // 3️. Row must be numeric
            String rowPart = origin.substring(1);
            if (!rowPart.matches("\\d+")) {
                System.out.println("Invalid row number.");
                continue;
            }

            int fromCol = colChar - 'A';
            int fromRow = Integer.parseInt(rowPart) - 1;

            // 4️. Range check
            if (fromRow < 0 || fromRow >= board.size()) {
                System.out.println("Position out of board range.");
                continue;
            }

            // 5️. Must be player's top piece
            Optional<Piece> top = board.topAt(fromRow, fromCol);
            if (top.isEmpty() || top.get().owner() != currentPlayer.id()) {
                System.out.println("No your top piece at that position.");
                continue;
            }

            Piece piece = top.get();

            // 6️. Destination selection
            String dest = getLegalPosition();
            int toCol = dest.charAt(0) - 'A';
            int toRow = Integer.parseInt(dest.substring(1)) - 1;

            if (fromRow == toRow && fromCol == toCol) {
                System.out.println("Destination cannot be the same as origin.");
                continue;
            }

            if (!board.canPlace(piece, toRow, toCol)) {
                System.out.println("Cannot move there (size rule).");
                continue;
            }

            // 7️. Apply move
            applyMove(new Move(piece, fromRow, fromCol, toRow, toCol));
            break;
        }
    }


    private String getLegalPosition() {

        while (true) {
            System.out.print("Enter target position (eg. A1, B3, C2): ");
            String position = scanner.nextLine().trim().toUpperCase();

            // 1️. Basic format check
            if (position.length() < 2 || position.length() > 3) {
                System.out.println("Invalid format. Use A1, B3, etc.");
                continue;
            }

            char colChar = position.charAt(0);
            String rowPart = position.substring(1);

            // 2️. Column must be a letter within board range
            if (colChar < 'A' || colChar >= 'A' + board.size()) {
                System.out.println("Invalid column. Allowed: A-" + (char)('A' + board.size() - 1));
                continue;
            }

            // 3️. Row must be digits only
            if (!rowPart.matches("\\d+")) {
                System.out.println("Invalid row number.");
                continue;
            }

            int rowNum = Integer.parseInt(rowPart);
            int rowIndex = rowNum - 1;

            // 4️. Row range validation
            if (rowIndex < 0 || rowIndex >= board.size()) {
                System.out.println("Row out of range. Allowed: 1-" + board.size());
                continue;
            }

            // 5️. Valid position
            return "" + colChar + rowNum;
        }
    }


    private boolean hasAnyLegalMove(Player p){
        return !collectLegalMoves(p).isEmpty();
    }

    private void switchPlayer(){ currentPlayer = (currentPlayer == orange) ? blue : orange; }

    private void applyMove(Move m){
        if(m.fromRow >= 0){
            board.popTop(m.fromRow, m.fromCol); // remove from origin
        } else {
            // remove from owner's pool
            Player ownerPlayer = (m.piece.owner() == orange.id()) ? orange : blue;
            boolean removed = ownerPlayer.removeFromPool(m.piece);
            if(!removed){
                // defensive: should not happen because we always use real object from pool or board top
                System.out.println("Warning: piece not removed from pool (unexpected).");
            }
        }
        board.place(m.piece, m.toRow, m.toCol);
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
        Scanner scanner = new Scanner(System.in);
        int size;

        // handling input for integer
        while (true) {
            System.out.print("Enter the size of board (default 3x3, enter -1 for default): ");

            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // discard invalid token
                continue;
            }

            size = scanner.nextInt();

            if (size == -1) {
                size = 3;   // default
                break;
            }

            if (size >= 3 && size <= 10) {
                break;
            }

            System.out.println("Invalid size. Please enter between 3 and 10 or -1.");
        }

        // consume newline so nextLine() works later
        scanner.nextLine();

        if(size == -1) size = 3;
        Game g = new Game(size);
        g.start();
        scanner.close();
    }
}
