import java.util.*;

class PuzzleState {
    int[][] board;
    int emptyX, emptyY;
    int g; // Cost to reach this state
    int h; // Heuristic cost to reach the goal
    PuzzleState parent;
    int[][] goalState; // goal state of the puzzle

    PuzzleState(int[][] board, int emptyX, int emptyY, int g, PuzzleState parent, int[][] goalState) {
        this.board = board;
        this.emptyX = emptyX;
        this.emptyY = emptyY;
        this.g = g;
        this.goalState = goalState;
        this.h = calculateHeuristic(board, goalState);
        this.parent = parent;
    }

    private int calculateHeuristic(int[][] board, int[][] goalState) {
        int h = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] != 0) {
                    int targetX = 0, targetY = 0;
                    for (int k = 0; k < goalState.length; k++) {
                        for (int l = 0; l < goalState[0].length; l++) {
                            if (goalState[k][l] == board[i][j]) {
                                targetX = k;
                                targetY = l;
                                break;
                            }
                        }
                    }
                    h += Math.abs(i - targetX) + Math.abs(j - targetY);
                }
            }
        }
        return h;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PuzzleState other) {
            return Arrays.deepEquals(this.board, other.board);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}

@SuppressWarnings("ALL")
public class SlidingPuzzle {
    private static final int[] DX = {1, -1, 0, 0};
    private static final int[] DY = {0, 0, 1, -1};

    public static List<PuzzleState> solvePuzzle(int[][] initialBoard, int[][] goalState) {
        PriorityQueue<PuzzleState> openList = new PriorityQueue<>(Comparator.comparingInt(a -> a.g + a.h));
        Set<PuzzleState> closedList = new HashSet<>();

        int emptyX = 0, emptyY = 0;
        findEmptyTile:
        for (int i = 0; i < initialBoard.length; i++) {
            for (int j = 0; j < initialBoard[0].length; j++) {
                if (initialBoard[i][j] == 0) {
                    emptyX = i;
                    emptyY = j;
                    break findEmptyTile;
                }
            }
        }

        PuzzleState initialState = new PuzzleState(initialBoard, emptyX, emptyY, 0, null, goalState);
        openList.add(initialState);

        while (!openList.isEmpty()) {
            PuzzleState current = openList.poll();

            if (Arrays.deepEquals(current.board, goalState)) {
                return reconstructPath(current);
            }

            closedList.add(current);

            for (int i = 0; i < 4; i++) {
                int newX = current.emptyX + DX[i];
                int newY = current.emptyY + DY[i];

                if (isInBounds(newX, newY, current.board.length, current.board[0].length)) {
                    int[][] newBoard = deepCopy(current.board);
                    newBoard[current.emptyX][current.emptyY] = newBoard[newX][newY];
                    newBoard[newX][newY] = 0;

                    PuzzleState neighbor = new PuzzleState(newBoard, newX, newY, current.g + 1, current, goalState);

                    if (closedList.contains(neighbor)) {
                        continue;
                    }

                    openList.add(neighbor);
                }
            }
        }
        return Collections.emptyList();
    }

    private static boolean isInBounds(int x, int y, int rows, int cols) {
        return x >= 0 && x < rows && y >= 0 && y < cols;
    }

    private static int[][] deepCopy(int[][] board) {
        int[][] copy = new int[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, board[0].length);
        }
        return copy;
    }

    private static List<PuzzleState> reconstructPath(PuzzleState state) {
        List<PuzzleState> path = new ArrayList<>();
        while (state != null) {
            path.add(state);
            state = state.parent;
        }
        Collections.reverse(path);
        return path;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int[][] goalState = new int[3][3];
        System.out.println("Enter the goal board values (3x3) row by row, using 0 for the empty space:");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                goalState[i][j] = scanner.nextInt();
            }
        }

        int[][] initialBoard = new int[3][3];
        System.out.println("Enter the initial board values (3x3) row by row, using 0 for the empty space:");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                initialBoard[i][j] = scanner.nextInt();
            }
        }

        List<PuzzleState> solution = solvePuzzle(initialBoard, goalState);
        if (!solution.isEmpty()) {
            System.out.println("Solution found:");
            for (PuzzleState state : solution) {
                for (int[] row : state.board) {
                    System.out.println(Arrays.toString(row));
                }
                System.out.println();
            }
            System.out.println("Sliding Count: " + (solution.size() - 1));
        } else {
            System.out.println("No solution found.");
        }
    }
}
