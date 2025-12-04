# Gobbler Tic-Tac-Toe (Java Console Version)

## Overview

Gobbler Tic-Tac-Toe is an enhanced version of the classic Tic-Tac-Toe
game where each player has pieces of different sizes that can "gobble"
(cover) smaller pieces. This implementation follows clean OOP
architecture with MVC separation and supports both Player-vs-Player and
Player-vs-AI modes.

------------------------------------------------------------------------

## Features

-   Object-Oriented Java implementation
-   MVC architecture (Model--View--Controller)
-   ANSI terminal-colored pieces
-   Stack-based board (each cell is a stack of pieces)
-   Player vs Player mode
-   Player vs AI mode (RandomAI)
-   Customizable board size (3--10)
-   Customizable number of pieces per player (3--10)
-   Move or place mechanics
-   Safe input validation and error handling
-   No duplicate piece sizes per player

------------------------------------------------------------------------

## Rules

### Pieces

-   Each player has pieces: `O1, O2, O3...` and `B1, B2, B3...`
-   Larger numbered piece = larger size
-   A piece can cover ("gobble") a smaller top piece
-   Players cannot have duplicate piece sizes

### Valid Moves

A player may: 1. **Place** a piece from their pool\
2. **Move** one of their own **top pieces** on the board

### Placement Rules

-   Can place on an empty cell
-   Can place on a smaller top piece
-   Cannot place on an equal or larger piece

### Winning

A player wins by forming 3-in-a-row (row, column, diagonal) **based on
the top visible pieces**.

### Draw

Occurs when neither player has a legal move available.

------------------------------------------------------------------------

## Technologies Used

-   **Java 17+**
-   **Object-Oriented Programming**
-   **MVC Architecture**
-   **Collections Framework**
    -   `Deque` for stacks
    -   `LinkedHashSet` for piece pools
-   **ANSI Escape Codes** for colored output
-   **RandomAI** using Java `Random`

------------------------------------------------------------------------

## Requirements

-   Java JDK 17 or higher
-   ANSI-color--capable terminal (CMD, PowerShell, Linux terminal,
    IntelliJ terminal, etc.)

------------------------------------------------------------------------

## How to Build and Run

### Compile

    javac -d out $(find src/main/java -name "*.java")

### Run

    java -cp out com.gobblertictactoe.game.Game

------------------------------------------------------------------------

## ASCII UML Diagram

### Class Diagram

    +-----------------+          +-----------------+
    |     Player      | 1 ---- * |      Piece      |
    +-----------------+          +-----------------+
    | - id: char      |          | - owner: char   |
    | - colorCode     |          | - size: int     |
    | - pool:Set      |          +-----------------+
    +-----------------+
    | +addToPool()    |
    | +removeFromPool()|
    | +findInPool()   |
    +-----------------+


    +-----------------+
    |      Board      |
    +-----------------+
    | - grid:Deque[][]|
    +-----------------+
    | +canPlace()     |
    | +place()        |
    | +popTop()       |
    | +topAt()        |
    | +checkWin()     |
    +-----------------+


    +-----------------+
    |     BoardView   |
    +-----------------+
    | +printTop()     |
    | +printFull()    |
    +-----------------+


    +-----------------+
    |      Game       |
    +-----------------+
    | - board         |
    | - players       |
    | - aiForB        |
    +-----------------+
    | +start()        |
    | +makeMove()     |
    | +handlePlace()  |
    | +handleMove()   |
    +-----------------+

------------------------------------------------------------------------

## ASCII Screenshot Example

          A      B      C
     1   __    __    __
     2   O3    B2    __
     3   __    __    O1

------------------------------------------------------------------------

## Project Structure

    src/
     └── main/
         └── java/
             └── com/gobblertictactoe/
                 ├── model/
                 │    ├── Piece.java
                 │    ├── Player.java
                 │    ├── Board.java
                 │    ├── Move.java
                 │    ├── AI.java
                 │    └── RandomAI.java
                 │
                 ├── view/
                 │    └── BoardView.java
                 │
                 └── game/
                      └── Game.java

------------------------------------------------------------------------

## License

This project is free to use, modify, and learn from.
