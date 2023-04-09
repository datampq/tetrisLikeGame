package drow.tetrisLikeGame;

public class shape {
    public int[][] shape;

    public shape(int[][] shape) {
        this.shape = shape;
    }

    public void rotate() {
        int[][] newShape = new int[shape.length][shape[0].length];
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                newShape[i][j] = shape[shape.length - j - 1][i];
            }
        }
        shape = newShape;
    }
}
