import java.util.*;

import game.snake.Direction;
import game.snake.SnakeGame;
import game.snake.SnakePlayer;
import game.snake.utils.Cell;
import game.snake.utils.SnakeGameState;

public class SamplePlayer extends SnakePlayer {

    public SamplePlayer(SnakeGameState gameState, int color, Random random) {
        super(gameState, color, random);
    }

    @Override
    public Direction getAction(long remainingTime) {
        // find food on the table
        Cell food = null;
        for (int i = 0; i < gameState.board.length; i++) {
            for (int j = 0; j < gameState.board[i].length; j++) {
                if (gameState.board[i][j] == SnakeGame.FOOD) {
                    food = new Cell(i, j);
                }
            }
        }
        // Get the BFS path from the snake head to the food
        Cell head = gameState.snake.peekFirst();
        List<Cell> path = bfs(head, food);

        // If path is found, return the next move direction
        //if (path != null && path.size() > 1) {
            Cell nextCell = path.get(1); // The first cell in the path after the head
            return head.directionTo(nextCell);
        //} else {
            // If no path is found (shouldn't happen), move randomly
           // return Direction.values()[random.nextInt(Direction.values().length)];
       // }

    }

    private List<Cell> bfs(Cell start, Cell goal) {
        // Queue for BFS, storing the current position and the path taken to reach it
        Queue<Node> queue = new LinkedList<>();
        Set<Cell> visited = new HashSet<>();
        queue.offer(new Node(start, null)); // Start with the head and no parent
        visited.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            Cell currentCell = current.cell;

            // If we reached the goal, reconstruct the path
            if (currentCell.equals(goal)) {
                List<Cell> path = new ArrayList<>();
                while (current != null) {
                    path.add(current.cell);
                    current = current.parent;
                }
                Collections.reverse(path);
                return path; // Return the path from head to food
            }

            // Explore neighbors (up, down, left, right)
            for (Cell neighbor : currentCell.neighbors()) {
                // Ensure the neighbor is on the board, not part of the snake, and not visited
                if (gameState.isOnBoard(neighbor) &&
                        gameState.getValueAt(neighbor) != SnakeGame.SNAKE &&
                        !visited.contains(neighbor)) {

                    visited.add(neighbor);
                    queue.offer(new Node(neighbor, current)); // Add neighbor to the queue
                }
            }
        }

        return null; // Return null if no path is found
    }

    // Helper class to represent a state in BFS: a cell and its parent
    private static class Node {
        Cell cell;
        Node parent;

        Node(Cell cell, Node parent) {
            this.cell = cell;
            this.parent = parent;
        }
    }

}