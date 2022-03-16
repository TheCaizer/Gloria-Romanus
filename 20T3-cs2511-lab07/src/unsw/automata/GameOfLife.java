package unsw.automata;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Conway's Game of Life on a 10x10 grid.
 *
 * @author Robert Clifton-Everest
 *
 */
public class GameOfLife {

    public final int size = 10;
    private BooleanProperty[][] cells;

    public GameOfLife() {
        this.cells = new BooleanProperty[size][size];
        //Dead cells
        for(int i = 0; i < size;i++){
            for(int j = 0; j < size;j++){
                this.cells[i][j] = new SimpleBooleanProperty(false){
                };
            }
        }
    }

    public BooleanProperty cellProperty(int x, int y){
        return this.cells[x][y];
    }

    public void ensureAlive(int x, int y) {
        cellProperty(x, y).set(true);
    }

    public void ensureDead(int x, int y) {
        cellProperty(x, y).set(false);
    }

    public boolean isAlive(int x, int y) {
        return cellProperty(x, y).get();
    }

    public boolean checkState(boolean[][] state, int x, int y){
        return state[x][y];
    }

    public void tick() {
        boolean[][] oldCells = new boolean[size][size];

        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                oldCells[i][j] = isAlive(i,j);
            }
        }
        for(int x = 0; x < size; x++){
            for(int y = 0; y < size; y++){
                List<Position> neighbours =  FindNeighbours(x, y);
                int num_neighbours = CountNeighbours(neighbours, oldCells);
                if(isAlive(x,y)){
                    if(num_neighbours < 2){
                        ensureDead(x, y);
                    }
                    else if(num_neighbours > 3){
                        ensureDead(x, y);
                    }
                    else{
                        ensureAlive(x, y);
                    }
                }
                else{
                    if(num_neighbours == 3){
                        ensureAlive(x, y);
                    }
                    else{
                        ensureDead(x, y);
                    }
                }
            }
        }
    }

    public List<Position> FindNeighbours(int x, int y) {
		ArrayList<Position> neighbours = new ArrayList<Position>();
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (i == 0 && j == 0) {
					continue;
				}
				neighbours.add(new Position(mod(x + i, size), mod(y + j, size)));
			}
        }
		return neighbours;
    }
    
    public int CountNeighbours(List<Position> list, boolean[][] map){
        int count = 0;
        for(Position p:list){
            if(checkState(map, p.getX(), p.getY())){
                count++;
            }
        }
        return count;
    }

	public int mod(int n, int mod) {
		return (n + mod) % mod;
	}

}

class Position {

	public int x;
	public int y;

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
    }
    
    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }
}

