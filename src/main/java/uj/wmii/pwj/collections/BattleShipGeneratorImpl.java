package uj.wmii.pwj.collections;


import java.util.ArrayList;
import java.util.Random;

public class BattleShipGeneratorImpl implements  BattleshipGenerator{
    final int ROWS = 10;
    final int COLS = 10;
    final int[] SIZES = {4,3,2,1};
    final int[] AMOUNTS = {1,2,3,4};
    final int TRIES = 5;
    final int ATTEMPTS = 1000;
    StringBuilder map;


    private int convert(int row ,int column){return row*COLS+column;}
    private char getField(int x){ return map.charAt(x);}
    private void setField(int x, char val){map.setCharAt(x, val);}


    private int[] availableNeighbours(int p) {
        int r = p / COLS, c = p % COLS;
        int[] tmp = new int[4];
        int n = 0;

        if (r > 0 && getField(convert(r - 1, c)) == '*') tmp[n++] = convert(r - 1, c);
        if (r < ROWS - 1 && getField(convert(r + 1, c)) == '*') tmp[n++] = convert(r + 1, c);
        if (c > 0 && getField(convert(r, c - 1)) == '*') tmp[n++] = convert(r, c - 1);
        if (c < COLS - 1 && getField(convert(r, c + 1)) == '*') tmp[n++] = convert(r, c + 1);

        return n == 4 ? tmp : java.util.Arrays.copyOf(tmp, n);
    }

    private boolean placeShips(int size) {
        Random random = new Random();
        int attempts = ATTEMPTS;
        while (attempts-- > 0) {
            int pos = random.nextInt(ROWS * COLS);
            if (getField(pos) == '*') {
                ArrayList<Integer> selection = new ArrayList<>();
                selection.add(pos);
                ArrayList<Integer> neighbours = new ArrayList<>();
                for (int n : availableNeighbours(pos)) {
                    neighbours.add(n);
                }
                int tries = TRIES;
                while (selection.size() < size && tries > 0 && !neighbours.isEmpty()) {
                    tries--;
                    int idx = random.nextInt(neighbours.size());
                    int chosen = neighbours.get(idx);
                    selection.add(chosen);
                    neighbours.remove(idx);

                    for (int n : availableNeighbours(chosen)) {
                        if (!selection.contains(n) && !neighbours.contains(n)) {
                            neighbours.add(n);
                        }
                    }
                }

                if (selection.size() == size) {
                    select(selection, neighbours);
                    break;
                }
            }
        }
        if(attempts == 0)return false;
        else return true;
    }

    private void select(ArrayList<Integer> selection, ArrayList<Integer> neighbours){
        for(int x : selection){
            setField(x, '#');
        }
        for(int x: neighbours){
            setField(x, '.');
        }
        for(int x: selection){
            checkCorners(x);
        }
    }

    private void checkCorners(int x){
        int r = x / COLS;
        int c = x % COLS;
        if(r-1 >= 0 && c-1 >= 0 && getField(convert(r-1,c-1)) != '#')
            setField(convert(r-1,c-1),'.');
        if(r+1 < ROWS  && c-1 >= 0 && getField(convert(r+1,c-1)) != '#')
            setField(convert(r+1,c-1),'.');
        if(r-1 >= 0 && c+1 < COLS && getField(convert(r-1,c+1)) != '#')
            setField(convert(r-1,c+1),'.');
        if(r+1 < ROWS && c+1 < COLS && getField(convert(r+1,c+1)) != '#')
            setField(convert(r+1,c+1),'.');
    }



    public String generateMap(){
        while (true) {
            map = new StringBuilder("*".repeat(ROWS * COLS));
            boolean success = true;

            for (int i = 0; i < SIZES.length && success; i++) {
                int size = SIZES[i];
                int count = AMOUNTS[i];
                for (int j = 0; j < count && success; j++) {
                    if (!placeShips(size)) {
                        success = false;
                    }
                }
            }

            if (success) break;
        }

        for(int i = 0; i < COLS*ROWS; i++){
            if(getField(i) == '*')setField(i,'.');
        }

        return map.toString();
    }
}
