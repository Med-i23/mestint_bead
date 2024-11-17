///,Csete.Medard@stud.u-szeged.hu
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

    /**
     * Meghatározza a kígyó következő lépését az élelem felé vezető
     * legrövidebb út alapján, vagy véletlenszerű irányt választ, ha
     * nincs útvonal.
     *
     * @param remainingTime a megmaradt idő
     * @return a következő irány
     */
    @Override
    public Direction getAction(long remainingTime) {
        // Élelem megkeresése a pályán
        Cell food = null;
        for (int i = 0; i < gameState.board.length; i++) {
            for (int j = 0; j < gameState.board[i].length; j++) {
                if (gameState.board[i][j] == SnakeGame.FOOD) {
                    food = new Cell(i, j);
                }
            }
        }

        // A BFS algoritmal megkeresi a kígyó fejétől az élelemig vezető útvonalat
        Cell head = gameState.snake.peekFirst();
        List<Cell> path = bfs(head, food);

        // Ha talált útvonalat, visszaadja a következő lépés irányát, ha nem akkor random-ot ad
        if (path != null && path.size() > 1) {
            Cell nextCell = path.get(1);
            return head.directionTo(nextCell);
        } else {
            Direction action = SnakeGame.DIRECTIONS[random.nextInt(SnakeGame.DIRECTIONS.length)];
            return action;
        }

    }

    /**
     * Breadth-First Search (BFS) algoritmus, amely megkeresi a legrövidebb
     * útvonalat a kígyó feje és az élelem között.
     *
     * @param start a kezdő cella (a kígyó feje)
     * @param end az elérendő cella (az élelem helye)
     * @return az útvonal a fej és az élelem között, vagy null, ha nincs útvonal
     */
    private List<Cell> bfs(Cell start, Cell end) {
        Queue<Node> queue = new LinkedList<>();
        Set<Cell> visited = new HashSet<>();
        queue.offer(new Node(start, null));
        visited.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            Cell currentCell = current.cell;

            // Ha elértük a célcellát, rekonstruáljuk az útvonalat
            if (currentCell.equals(end)) {
                List<Cell> path = new ArrayList<>();
                while (current != null) {
                    path.add(current.cell);
                    current = current.parent;
                }
                Collections.reverse(path);
                return path;
            }

            // Szomszédvizsgálat
            for (Cell neighbor : currentCell.neighbors()) {
                // Ellenőrizzük, hogy a szomszéd a táblán van-e, nem része-e a kígyónak, és nem látogattuk-e már meg
                if (gameState.isOnBoard(neighbor) && gameState.getValueAt(neighbor) != SnakeGame.SNAKE && !visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(new Node(neighbor, current));
                }
            }
        }

        // Ha nincs útvonal, null
        return null;
    }

    /**
     * Segédosztály a BFS állapotainak reprezentálásához: egy cella és annak szülője.
     */
    private static class Node {
        Cell cell;
        Node parent;

        Node(Cell cell, Node parent) {
            this.cell = cell;
            this.parent = parent;
        }
    }

}