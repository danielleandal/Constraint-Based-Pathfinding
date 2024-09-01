// Danielle Andal - 07/06/24

// Solving a maze with implementation of BFS(Breadth First Search). The maze has different kinds of restriction and teleportation is posible if conditions are met.

package Assignments.PA4;

import java.util.*;

public class Main {

    public static int row; // Number of rows
    public static int col; // Number of columns
    public static int n; // Number of indexes/nodes in the matrix
    public static char maze[][]; // Maze
    public static boolean maze_teleport[][]; // Keeps track of which Letter's to teleport
    public static Map<Character, List<Integer>> uppercasePositions = new HashMap<>(); // keeps tracks of all the upper letter's indexes


    // checks up, down, left, and right
    final public static int[] DX = {1, -1, 0, 0};
    final public static int[] DY = {0, 0, 1, -1};

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        row = in.nextInt(); // Number of rows in the maze
        col = in.nextInt(); // Number of columns in the maze
    
        maze = new char[row][col]; // Initialize the maze grid
        maze_teleport = new boolean[row][col]; // Initialize teleportation grid
        
        // Set all cells in the teleportation grid to true initially
        for (int i = 0; i < row; i++) {
            Arrays.fill(maze_teleport[i], true);
        }
        
        n = row * col; 
    
        // Scan in the maze from input
        for (int i = 0; i < row; i++) {
             // Read each row of the maze
            String rowline = in.next();
            for (int j = 0; j < col; j++) {
                // Store each character in the maze grid
                maze[i][j] = rowline.charAt(j); 
                
                // Makes adjacency list for the letters and it's indexes
                // Check if the character is an uppercase letter,indicating a teleportation point
                if (Character.isUpperCase(maze[i][j])) {

                    // Calculate the position index in the maze
                    int position = i * col + j; 
                    
                    // If the uppercase letter hasn't been encountered before, create a new list for its positions
                    if (!uppercasePositions.containsKey(maze[i][j])) {
                        uppercasePositions.put(maze[i][j], new ArrayList<>());
                    }
                    
                    // Add the current position index to the list of positions for this uppercase letter
                    uppercasePositions.get(maze[i][j]).add(position);
                }
            }
        }
      
        // Find Gustavo's location and his destination
        int location = find('*');
        int destination = find('$');
    
        // Perform BFS from the starting position to the destination
        int result = bfs(location, destination);

        // Destination is blocked
        if (result == -1) {
                System.out.println("Stuck, we need help!");
        } 
        else {
            // Destination is found!
            System.out.println(result);
        }
    }


    public static int bfs(int location, int destination) {
        LinkedList<Integer> q = new LinkedList<>();
        q.offer(location);
    
        int[] distance = new int[row * col];
        Arrays.fill(distance, -1);
        distance[location] = 0;
    
        while (!q.isEmpty()) {
            int cur = q.poll(); // dequeue
    
            // Check if reached destination
            if (cur == destination) {
                return distance[destination];
            }
    
            // Explore each direction
            for (int i = 0; i < DX.length; i++) {
                int nX = cur / col + DX[i];
                int nY = cur % col + DY[i];
    
                // Check if out of bounds or obstacle
                if (!inbounds(nX, nY) || maze[nX][nY] == '!' || distance[nX * col + nY] != -1) {
                    continue;
                }
    
                // Update distance and enqueue
                distance[nX * col + nY] = distance[cur] + 1;
                q.offer(nX * col + nY); 


                // Handles teleportation, if we encounter a specific letter connect the duplicates to implement teleportation
                if (Character.isUpperCase(maze[nX][nY]) && maze_teleport[nX][nY]) {
                    // Get the letter
                    char letter = maze[nX][nY];
                    // Locate in HashMap
                    if (uppercasePositions.containsKey(letter)) {
                        // Goes through all the possible teleportation position 
                        List<Integer> positions = uppercasePositions.get(letter);

                        // Get the index
                        for (int pos : positions) {
                            int nXx = pos / col;
                            int nYy = pos % col;
                            
                            // Teleport, connects the duplicates appropriately
                            if (maze_teleport[nXx][nYy] && distance[nXx * col + nYy] == -1) {
                                maze_teleport[nXx][nYy] = false; // marked a duplicate visited
                                distance[nXx *col + nYy] = distance[nX*col+nY]+1; // Teleported, so distance increments
                                q.offer(nXx *col + nYy); // enqueue the index in the q as it is connected now
                            }
                        }
                    }
                }
    
            
            }
        }
    
        // Destination not found!
        return -1;
    }
    
    // Checks if we're still inside the maze
    public static boolean inbounds(int x, int y) {
        return x >= 0 && x < row && y >= 0 && y < col;
    }

    public static int find(char c) {
        // Goes through each index in the maze
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (maze[i][j] == c) {
                    return i * col + j; // Return the correct index
                }
            }
        }
        // Character does not exist
        return -1;
    }
}