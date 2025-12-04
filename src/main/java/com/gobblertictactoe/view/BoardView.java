package com.gobblertictactoe.view;

import com.gobblertictactoe.model.Board;
import com.gobblertictactoe.model.Piece;
import com.gobblertictactoe.util.Stack;

public class BoardView {

    /**
     * Print top pieces (uses Piece.toString() which includes color codes).
     * If you want to show full stacks, change to iterate stackAt().
     */
    public void printTop(Board board) {
        int n = board.size();
        int cellWidth = 6; // fixed width so all colored pieces fit

        // Print header
        System.out.print("    ");
        for (int c = 0; c < n; c++) {
            System.out.printf("%-" + cellWidth + "s", (char)('A' + c));
        }
        System.out.println();

        // Print rows
        for (int r = 0; r < n; r++) {
            System.out.printf("%3d ", r + 1);
            for (int c = 0; c < n; c++) {
                Piece p = board.topAt(r,c).orElse(null);
                String text = (p == null ? "__" : p.toString());

                // Pad so width stays stable even with invisible ANSI codes
                System.out.print(padANSI(text, cellWidth));
            }
            System.out.println();
        }
    }

    private String padANSI(String text, int width) {
    // Remove ANSI codes to measure visible width
    String plain = text.replaceAll("\u001B\\[[;\\d]*m", "");
    int pad = width - plain.length();
    if (pad < 0) pad = 0;

    return text + " ".repeat(pad);
}



    /**
     * Optional: print full stacks (bottom -> top) in a compact form.
     */
    public void printFullStacks(Board board) {
        int size = board.size();
        System.out.print("    ");
        for (int j = 0; j < size; j++) {
            System.out.printf("%8s", (char) ('A' + j));
        }
        System.out.println();
        for (int i = 0; i < size; i++) {
            System.out.printf("%3d ", i + 1);
            for (int j = 0; j < size; j++) {
                Stack<Piece> stack = board.stackAt(i, j);
                if (stack == null || stack.isEmpty()) {
                    System.out.printf("%8s", "__");
                } else {
                    // show bottom->top
                    StringBuilder sb = new StringBuilder();
                    Object[] arr = stack.toArray();
                    for (int k = arr.length - 1; k >= 0; k--) { // arr[0] is bottom, last is top
                        sb.append(((Piece)arr[k]).toString());
                        if (k != 0) sb.append("/");
                    }
                    System.out.printf("%8s", sb.toString());
                }
            }
            System.out.println();
        }
    }
}
