// Cave Explorer

// Mads Degn
// 20/11-24

import java.util.Random; // To generate random placement for treasures, traps and obstacles.
import java.util.Scanner; // To take user input.

public class CaveExplorer {

    // Variables for games grid size and number of objects per feature on said grid.
    private static final int GRID = 10;
    private static final int OBJECTS = 5;

    // Variables for game/grid symbols.
    private static final char HIDDEN = '?';
    private static final char FREE = '.';
    private static final char TREASURE = 'T';
    private static final char TRAP = 'X';
    private static final char OBSTACLE = 'O';

    // Variables for player part of game.
    private static char playerDirection = '^'; // Player start direction.
    private static int playerRow = 0; // Player start row coordinate.
    private static int playerColumn = 0; // Plater start column coordinates.
    private static int score = 0; // Player start score.

    // 2D arrays for main game board and visible player board.
    private static char[][] board = new char[GRID][GRID];
    private static char[][] visibleBoard = new char[GRID][GRID];

    public static void main(String[] args) {

        // Display welcome message to player.
        System.out.println("");
        System.out.println("Welcome to Cave Explorer!");
        System.out.println("Collect treasures (T) and avoid traps (X) and obstacles (O)!");
        System.out.println("Commands: Turn left (L), Turn right (R), Move forward (F), Exit (E)");

        initializeBoards();
        placeObjects();

        // Scanner for user input.
        Scanner scanner = new Scanner(System.in);

        // Boolean variable to determine game state.
        boolean gameRunning = true;

        // Main game loop.
        while (gameRunning) {
            displayBoard();
            System.out.println("Score: " + score); // Player info.
            System.out.print("Enter your move: "); // Player instruction.
            char input = scanner.next().toUpperCase().charAt(0); // Taking user input, converting to upper case and taking first letter of input as fail safe.
        
            // Creating switch statement to execute player movement.
            switch (input) {
                case 'L':
                    turnLeft();
                    break;
                case 'R':
                    turnRight();
                    break;
                case 'F':
                    gameRunning = move(); // If move returns false, game over. If true, game goes on.
                    break;
                case 'E':
                    gameRunning = false; // Game over.
                    break;
                default:
                    System.out.println("Invalid input. Try again."); // User input fail safe.
            }
        }
        System.out.println("Game over! Your final score is: " + score); // Display final player score.
    }

    private static void initializeBoards() {
        for (int i = 0; i < GRID; i++) { // Loop through grid rows.
            for (int j = 0; j < GRID; j++) { // Loop through grid columns.
                board[i][j] = FREE; // Setting game board to free (no obstacles).
                visibleBoard[i][j] = HIDDEN; // Setting visible player board to hidden.
            }
        }
        visibleBoard[playerRow][playerColumn] = playerDirection; // Setting player position at (0,0) facing upwards.
    }

    private static void placeObjects() {
        Random random = new Random(); // Creating random object.
        createObjects(random, TREASURE); // Creating random treasures.
        createObjects(random, TRAP); // Creating random traps.
        createObjects(random, OBSTACLE); // Creating random obstacles.
    }

    private static void createObjects(Random random, char objectType) {
        int amount = 0; // Variable to keep track of number of objects.
        while (amount < OBJECTS) { // Loop to generate objects until variable OBJECTS is met (5).
            int row = random.nextInt(GRID); // Create random row coordinate for object.
            int column = random.nextInt(GRID); // Create random column coordinate for object.
            if (board[row][column] == FREE && (row != 0 || column != 0)) { // Makes sure coordinate on board is free and not on player start position.
                board[row][column] = objectType; // Places object on random generated coordiante.
                amount++;
            }
        }
    }

    private static void displayBoard() {
        for (int i = 0; i < GRID; i++) { // Loop through grid rows.
            for (int j = 0; j < GRID; j++) { // Loop through grid columns.
                System.out.print(visibleBoard[i][j] + " "); // Displaying visible board + a space to make board a square.
            }
            System.out.println(); // Displaying new line between every row to make board a square.
        }
    }
    
    private static void turnLeft() {
        playerDirection = switch (playerDirection) { // Set player direction to player direction after switch statement.
            case '^' -> '<'; // Is player facing up, turn left.
            case '<' -> 'v'; // Is player facing left, turn down.
            case 'v' -> '>'; // Is player facing down, turn right.
            case '>' -> '^'; // Is player facing right, turn up.
            default -> playerDirection; // Fail safe.
        };
        updatePlayerVisibility();
        visibleBoard[playerRow][playerColumn] = playerDirection; // Update visible board.
    }

    private static void turnRight() {
        playerDirection = switch (playerDirection) { // Set player direction to player direction after switch statement.
            case '^' -> '>'; // Is player facing up, turn right.
            case '>' -> 'v'; // Is player facing right, turn down.
            case 'v' -> '<'; // Is player facing down, turn left.
            case '<' -> '^'; // Is player facing left, turn up.
            default -> playerDirection; // Fail safe.
        };
        updatePlayerVisibility();
        visibleBoard[playerRow][playerColumn] = playerDirection; // Update visible board.
    }

    private static boolean move() {
        int newRow = playerRow; // Variable for new player row position.
        int newColumn = playerColumn; // Variable for new player column position.

        // Switch statement for new player position.
        switch (playerDirection) {
            case '^': // If player facing up, minus 1 in row (move upwards).
                newRow--;
                break;
            case 'v': // If player facing down, plus 1 in row (move downwards).
                newRow++;
                break;
            case '<': // If player facing left, minus 1 in column (move left).
                newColumn--;
                break;
            case '>': // If player facing right, plus 1 in grid (move right).
                newColumn++;
                break;
        }

        if (newRow < 0 || newRow >= GRID || newColumn < 0 || newColumn >= GRID) { // Fail safe for player position vs grid edges.
            System.out.println("You can't move outside the grid."); // Error message to player.
            return true; // Game goes on. Move not counted.
        }

        if (board[newRow][newColumn] == OBSTACLE) { // Fail safe for player position vs obstacles.
            System.out.println("You can't move onto an obstacle."); // Error message to player.
            return true; // Game goes on. Move not counted.
        }

        // If no fail safes are triggered, update player position.
        playerRow = newRow; 
        playerColumn = newColumn;

        // If updated player position hits treasure or trap.
        char currentPosition = board[playerRow][playerColumn]; // Variable for player position.
        if (currentPosition == TREASURE) { // If player postion is same as a treasure.
            score += 10; // Plus 10 points to score.
            System.out.println("You found a treasure! +10 points."); // Display message to player.
        } else if (currentPosition == TRAP) { // If player position is same as a trap.
            score -= 5; // Minus 5 points to score.
            System.out.println("You triggered a trap! -5 points."); // Display message to user.
        }

        board[playerRow][playerColumn] = FREE; // Set new player postion to free so treasure/trap is removed after hit.
        visibleBoard[playerRow][playerColumn] = playerDirection; // Update visible board.

        // To exit cave naturally.
        if (playerRow == 0 && playerColumn == 0) { // If player position is (0,0).
            System.out.println("You have exited the cave."); // Display message to player.
            return false; // End game.
        }

        return true; // Game goes on.
    }

    private static void updatePlayerVisibility() {
        for (int i = 0; i < GRID; i++) { // Loop through grid rows.
            for (int j = 0; j < GRID; j++) { // Loop through grid columns.
                // If a cell is already revealed on the visible board and not the player's current position,
                // synchronize it with the main game board.
                if (visibleBoard[i][j] != HIDDEN && visibleBoard[i][j] != playerDirection) {
                visibleBoard[i][j] = board[i][j]; // Update the cell to match the main game board.
                }
            }
        }

        // Switch statement to update visibility based on the player's current direction.
        switch (playerDirection) {
            case '^': // If player is facing up, update vision upwards.
                for (int i = playerRow; i >= 0; i--) { // Loop upwards from player's current position.
                    visibleBoard[i][playerColumn] = board[i][playerColumn]; // Reveal tiles upwards.
                    if (board[i][playerColumn] == OBSTACLE) { // If an obstacle is encountered, show it.
                        break; // Stop revealing further beyond the obstacle.
                    }
                }
                break;
            case 'v': // If player is facing down, update vision downwards.
                for (int i = playerRow; i < GRID; i++) { // Loop downwards from player's current position.
                    visibleBoard[i][playerColumn] = board[i][playerColumn]; // Reveal tiles downwards.
                    if (board[i][playerColumn] == OBSTACLE) { // If an obstacle is encountered, show it.
                        break; // Stop revealing further beyond the obstacle.
                    }
                }
                break;
            case '<': // If player is facing left, update vision to the left.
                for (int j = playerColumn; j >= 0; j--) { // Loop leftwards from player's current position.
                    visibleBoard[playerRow][j] = board[playerRow][j]; // Reveal tiles to the left.
                    if (board[playerRow][j] == OBSTACLE) { // If an obstacle is encountered, show it.
                        break; // Stop revealing further beyond the obstacle.
                    }
                }
                break;
            case '>': // If player is facing right, update vision to the right.
                for (int j = playerColumn; j < GRID; j++) { // Loop rightwards from player's current position.
                    visibleBoard[playerRow][j] = board[playerRow][j]; // Reveal tiles to the right.
                    if (board[playerRow][j] == OBSTACLE) { // If an obstacle is encountered, show it.
                        break; // Stop revealing further beyond the obstacle.
                    }
                }
                break;
        }
    }
}