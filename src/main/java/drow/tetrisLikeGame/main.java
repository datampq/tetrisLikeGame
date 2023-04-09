package drow.tetrisLikeGame;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class main {

  

    boolean up = true;
    Dimension d = new Dimension(800, 1200);
    Dimension squareSize = new Dimension(50, 50);
    int rowLength = 0;
    Color bg = Color.decode("0x0d0628");
    Color bgPanel = Color.decode("0x0d0628");
    Font font = new Font("Arial", Font.BOLD, 20);
    Color text = Color.decode("0xf9dbbd");
    Color gc = Color.decode("0xf9dbbd");
    Color bc = Color.decode("0xbe0aff");
    Color guides = Color.decode("0x2B3467");
    Color disposeColor = Color.decode("0xfca17d");
    int difficulty = 2;
    boolean gameOver = false;
    boolean paused = false;

    int[] diffs = new int[] { 8, 6, 4, 3, 2, 1 };
    String[] diffNames = new String[] { "Easiest", "Easy", "Normal", "Hard", "Harder", "Hardest" };
    Color sColor = Color.decode("0xff0a78");

    Color[] colors = new Color[] { Color.decode("0xda627d"), Color.decode("0xda627d"), Color.decode("0xda627d"),
            Color.decode("0xda627d"), Color.decode("0xda627d"), Color.decode("0xda627d"), Color.decode("0xda627d") };

    int speed = 1;
    int index = 0;
    shape currentBlock = null;
    shape nextBlock = null;
    factory f = new factory();
    boolean hasMoving = false;
    int score = 0;
    boolean automated = false;
    int oldSpeed = 0;
    int removed = 0;
    boolean noMoves = true;
    button diffBtn;
    button pauseBtn;

    public main() {
        rowLength = d.width / squareSize.width;
        JFrame f = new JFrame();
        f.setSize(d.width, d.height);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        FrameDragListener frameDragListener = new FrameDragListener(f);
        f.addMouseListener(frameDragListener);
        f.addMouseMotionListener(frameDragListener);
        f.setUndecorated(true);
        // set always on top
        f.setAlwaysOnTop(true);
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(bg);
        p.add(new topPanel());
        p.add(new scoringPanel());
        p.add(new playground());

        f.add(p);

        f.pack();
        f.setVisible(true);
        f.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (!automated && !paused) {
                    if (evt.getKeyCode() == 65) {
                        if (currentBlock != null) {
                            currentBlock.translateX(-50);
                        }
                    }
                    if (evt.getKeyCode() == 68) {
                        if (currentBlock != null) {
                            currentBlock.translateX(50);
                        }
                    }

                    if (evt.getKeyCode() == 87) {
                        if (currentBlock != null) {
                            currentBlock.rotate();
                        }
                    }
                    if (evt.getKeyCode() == 83) {
                        if (currentBlock != null) {

                        }
                    }
                }

            }
        });
    }

    public String loadScore() {
        ObjectInputStream oi = null;
        scoreContainer s;
        try {
            FileInputStream fi = new FileInputStream(new File("scorebobjane.drow"));
            oi = new ObjectInputStream(fi);
            s = (scoreContainer) oi.readObject();
            System.out.println(s.score);
            oi.close();
            return s.score + "";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }

    }

    public void saveScore() {
        scoreContainer s = new scoreContainer();
        s.score = score;
        try (FileOutputStream f = new FileOutputStream(new File("scorebobjane.drow"));
                ObjectOutputStream o = new ObjectOutputStream(f)) {
            o.writeObject(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int best = 0;

    JLabel bestScore = new JLabel("Best Score: 0");

    class topPanel extends JPanel {
        public topPanel() {
            setPreferredSize(new Dimension(d.width, 35));
            setBackground(bg);
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

            diffBtn = new button(diffNames[difficulty], new java.awt.event.MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    difficulty++;
                    if (difficulty > 5) {
                        difficulty = 0;
                    }
                    Component component = diffBtn.getComponent(0);
                    if (component instanceof JLabel) {
                        JLabel label = (JLabel) component;
                        label.setText(diffNames[difficulty]);
                    }
                }

                public void mouseEntered(MouseEvent e) {
                    e.getComponent().setBackground(bc);
                }

                public void mouseExited(MouseEvent e) {
                    e.getComponent().setBackground(bg);
                }
            });

            bestScore.setForeground(text);
            bestScore.setFont(font);
            String s = loadScore();
            try {
                best = Integer.parseInt(s);
            } catch (Exception e) {
                best = 0;
            }
            bestScore.setText("Best: " + s);

            JPanel p = new JPanel();
            p.setPreferredSize(new Dimension(d.width - 5 * 100, 35));
            JLabel title = new JLabel("bob and jane");
            title.setForeground(text);
            title.setFont(font);
            p.add(title);
            p.setBackground(bg);

            add(p);
            add(bestScore);
            add(diffBtn);
            add(new button("Restart", new java.awt.event.MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if (automated) {
                        automated = false;
                    }
                    if (score > best) {
                        best = score;
                        bestScore.setText("Best: " + best);
                        saveScore();
                    }
                    currentBlock = null;
                    score = 0;
                    speed = 1;
                    noMoves = true;
                    nextBlock = null;
                    squares.clear();
                }

                public void mouseEntered(MouseEvent e) {
                    e.getComponent().setBackground(bc);
                }

                public void mouseExited(MouseEvent e) {
                    e.getComponent().setBackground(bg);
                }
            }));
            pauseBtn = new button("Pause", new java.awt.event.MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    paused = !paused;
                    System.out.println(paused);
                    Component component = pauseBtn.getComponent(0);
                    if (component instanceof JLabel) {
                        JLabel label = (JLabel) component;
                        if (paused) {
                            label.setText("Resume");
                        } else {
                            label.setText("Pause");
                        }
                    }
                }

                public void mouseEntered(MouseEvent e) {
                    e.getComponent().setBackground(bc);
                }

                public void mouseExited(MouseEvent e) {
                    e.getComponent().setBackground(bg);
                }
            });
            add(pauseBtn);
            add(new button("Exit", new java.awt.event.MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    System.exit(0);
                }

                public void mouseEntered(MouseEvent e) {
                    e.getComponent().setBackground(bc);
                }

                public void mouseExited(MouseEvent e) {
                    e.getComponent().setBackground(bg);
                }
            }));
        }
    }

    class button extends JPanel {
        public button(String title, java.awt.event.MouseAdapter adapter) {
            setPreferredSize(new Dimension(100, 35));
            setBackground(bg);
            addMouseListener(adapter);
            JLabel l = new JLabel(title);
            l.setForeground(text);
            l.setFont(font);
            add(l);
        }
    }

    class scoringPanel extends JPanel {
        public scoringPanel() {
            setPreferredSize(new Dimension(d.width, 50));
            setBackground(bgPanel);
            Timer t = new Timer(20, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    repaint();
                }
            });
            t.setRepeats(true);
            t.start();
        }

        @Override
        public void paint(java.awt.Graphics g) {
            super.paint(g);

            g.setColor(text);
            g.setFont(font);
            g.drawString("Score: " + score, 10, 25);
            g.drawString("Speed: " + speed, 200, 25);
            g.drawString("Automated: " + automated, 400, 25);
            if (nextBlock != null) {
                g.drawString("Next: " + nextBlock.name, 650, 25);
            } else {
                g.drawString("Next: " + "none", 650, 25);
            }
        }

    }

    List<square> squares = new ArrayList<square>();

    public int checkPos(int i) {
        return (i / squareSize.width) * squareSize.width;
    }

    public int checkPosY(int i) {
        return (i / squareSize.height) * squareSize.height;
    }

    class playground extends JPanel {
        // square s1 = new square(0, 0, 100, 100, Color.RED);
        square ground = new square(0, d.height - 25, new Dimension(d.width, 25), gc);

        public playground() {

            setPreferredSize(d);
            setBackground(Color.BLACK);
            // mouse click:
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (!paused) {
                        automated = true;
                        oldSpeed = speed;
                        speed = 10;
                    }

                }
            });

            // scrolling:
            addMouseWheelListener(new MouseWheelListener() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    if (!paused) {
                        if (currentBlock != null && !automated) {
                            currentBlock.rotate();
                        }
                    }

                }
            });

            // mouse motion listener:
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    if (!paused) {
                        if (currentBlock != null && !automated) {
                            int translate = e.getX() - currentBlock.getCenter();
                            translate = checkPos(translate);
                            currentBlock.translateX(translate);
                        }
                    }

                }
            });

            Timer t = new Timer(20, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!paused) {
                        repaint();
                        if (noMoves && !paused) {
                            if (currentBlock != null) {
                                if (automated) {
                                    speed = oldSpeed;
                                    automated = false;
                                    score += (currentBlock.score / 5) * oldSpeed;
                                } else {
                                    score += (currentBlock.score / 5) * speed;
                                }
                            }
                            shape s = f.getShape();
                            if (nextBlock == null) {
                                nextBlock = f.getShape();
                            } else {
                                shape p = nextBlock;
                                nextBlock = s;
                                s = p;
                            }

                            List<square> sqs = new ArrayList<>();
                            for (square sq : s.squares) {
                                square s1 = new square(sq.x + d.width / 2 - squareSize.width * 2, sq.y,
                                        squareSize,
                                        sq.color);
                                squares.add(s1);
                                sqs.add(s1);
                            }
                            currentBlock = new shape(sqs, s.score, s.name);
                            if (removed > (rowLength * diffs[difficulty])) {
                                removed = 0;
                                speed++;
                                oldSpeed = speed;
                            }
                        }
                    }
                }
            });
            t.setRepeats(true);
            t.start();
            Thread t2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if (!paused) {

                            if (speed < 1) {
                                speed = 1;
                            }
                            removeAllSameY(squares);
                            int moveScore = 0;
                            for (int i = 0; i < squares.size(); i++) {
                                square s = squares.get(i);
                                if (s.getRemove() == true) {
                                    squares.remove(i);
                                    removed++;
                                    score += speed;
                                    continue;
                                }
                                boolean fr = collision(squares.get(i), ground);
                                if (fr) {
                                    squares.get(i).moving = false;
                                    squares.get(i).color = bc;
                                    hasMoving = false;
                                    squares.get(i).y = ground.getY() - ground.height / 2;

                                } else {
                                    if (collision(squares.get(i), squares)) {
                                        if (!currentBlock.contains(squares.get(i))) {
                                            squares.get(i).moving = false;
                                            squares.get(i).color = bc;
                                            hasMoving = false;
                                        }

                                    } else {
                                        squares.get(i).y += speed;
                                        squares.get(i).moving = true;
                                        hasMoving = true;
                                        moveScore++;
                                    }
                                }
                            }

                            if (moveScore == 0) {
                                noMoves = true;
                            } else {
                                noMoves = false;
                            }

                        }
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            t2.start();
        }

        @Override
        public void paint(java.awt.Graphics g) {
            super.paint(g);
            g.setColor(bg);
            g.fillRect(0, 0, d.width, d.height);
            g.fillRect(ground.x, ground.y, ground.width, ground.height);
            for (square s : squares) {
                g.setColor(s.color);
                g.fillRect(s.x, s.y, s.width, s.height);
                g.setColor(guides);
                g.drawRect(s.x, s.y, s.width, s.height);
                if (s.moving) {
                    g.setColor(guides);
                    g.drawLine(s.x, s.y, s.x, d.height);
                    g.drawLine(s.x + squareSize.width, s.y, s.x + squareSize.width, d.height);
                }
            }
        }
    }

    class factory {
        public List<shape> shapes = new ArrayList<shape>();

        public factory() {
        }

        public shape getShape() {
            Random r = new Random();
            int index = r.nextInt(7);
            if (index == 0) {
                return getSquare();
            } else if (index == 1) {
                return getL();
            } else if (index == 2) {
                return getL2();
            } else if (index == 3) {
                return getT();
            } else if (index == 4) {
                return getZ();
            } else if (index == 5) {
                return getZ2();
            } else {
                return getLine();
            }
        }

        public shape getLine() {
            return new shape(
                    new square[] { new square(0, 0,
                            squareSize,
                            sColor),
                            new square(squareSize.width, 0,
                                    squareSize,
                                    sColor),
                            new square(squareSize.width * 2, 0,
                                    squareSize,
                                    sColor),
                            new square(squareSize.width * 3, 0,
                                    squareSize,
                                    sColor) },
                    25, "line");
        }

        public shape getSquare() {
            return new shape(
                    new square[] { new square(0, 0, squareSize, sColor),
                            new square(squareSize.width, 0, squareSize, sColor),
                            new square(0, squareSize.height, squareSize, sColor),
                            new square(squareSize.width, squareSize.height, squareSize, sColor) },
                    50, "square");
        }

        public shape getL() {
            return new shape(
                    new square[] { new square(0, 0, squareSize, sColor),
                            new square(squareSize.width, 0, squareSize, sColor),
                            new square(squareSize.width * 2, 0, squareSize, sColor), new square(squareSize.width * 2,
                                    squareSize.height, squareSize, sColor) },
                    75, "L");
        }

        public shape getL2() {
            return new shape(
                    new square[] { new square(0, 0, squareSize, sColor),
                            new square(squareSize.width, 0, squareSize, sColor),
                            new square(squareSize.width * 2, 0, squareSize, sColor), new square(0,
                                    squareSize.height, squareSize, sColor) },
                    75, "L2");
        }

        public shape getT() {
            return new shape(
                    new square[] { new square(0, 0, squareSize, sColor),
                            new square(squareSize.width, 0, squareSize, sColor),
                            new square(squareSize.width * 2, 0, squareSize, sColor), new square(squareSize.width,
                                    squareSize.height, squareSize, sColor) },
                    100, "T");
        }

        public shape getZ() {
            return new shape(
                    new square[] { new square(0, 0,
                            squareSize, sColor),
                            new square(
                                    squareSize.width, 0,
                                    squareSize, sColor),
                            new square(squareSize.width, squareSize.height,
                                    squareSize, sColor),
                            new square(squareSize.width * 2, squareSize.height,
                                    squareSize, sColor) },
                    125, "Z");
        }

        public shape getZ2() {
            return new shape(
                    new square[] { new square(0,
                            squareSize.width,
                            squareSize, sColor),
                            new square(
                                    squareSize.width,
                                    squareSize.height,
                                    squareSize, sColor),
                            new square(
                                    squareSize.width, 0,
                                    squareSize, sColor),
                            new square(
                                    squareSize.width * 2, 0,
                                    squareSize, sColor) },
                    125, "Z2");
        }

    }

    class shape {
        public square[] squares;
        public int score;
        public String name;

        public square[][] states;

        public void translateX(int x) {
            boolean canMove = true;
            for (square s : this.squares) {
                if (s.x + x < 0 || s.x + x > d.width - squareSize.width) {
                    canMove = false;
                }
            }
            if (canMove) {
                for (square s : squares) {
                    s.x += x;
                }
            }
        }

        public int getCenter() {
            int center = 0;
            for (square s : this.squares) {
                center += s.x;
            }
            center /= this.squares.length;
            return center;
        }

        public boolean contains(square square) {
            for (square s : this.squares) {
                if (s.x == square.x && s.y == square.y) {
                    return true;
                }
            }
            return false;
        }

        public void rotate() {
            int centerx = squares[0].x;
            int centery = squares[0].y;
            // ---- rotate ----
            for (square s : this.squares) {
                int x = s.x;
                int y = s.y;
                s.x = centerx - centery + y;
                s.y = centery + centerx - x;
                s.x = checkPos(s.x);
                s.y = checkPosY(s.y);

            }
            // move bakck in bounds if needed:
            int move = 0;
            for (square s : this.squares) {
                if (s.x < 0) {
                    move = Math.max(move, -s.x);
                }
                if (s.x > d.width - squareSize.width) {
                    move = Math.min(move, d.width - squareSize.width - s.x);
                }
            }
            for (square s : this.squares) {
                s.x += move;
            }
        }

        public shape(List<square> squares, int score, String name) {
            this.squares = new square[squares.size()];
            for (int i = 0; i < squares.size(); i++) {
                this.squares[i] = squares.get(i);
            }
            this.score = score;
            this.name = name;
        }

        public shape(square[] squares, int score, String name) {
            this.squares = squares;
            this.score = score;
            this.name = name;
        }
    }

    class square {
        int x;
        int y;
        int width;
        int height;
        Color color;
        public boolean moving = false;
        public boolean remove = false;

        public square(int x, int y, Dimension d, Color color) {
            this.x = x;
            this.y = y;
            this.width = d.width;
            this.height = d.height;
            this.color = color;
        }

        public boolean getRemove() {
            return remove;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getSizeX() {
            return width;
        }

        public int getSizeY() {
            return height;
        }
    }

    public boolean collision(square a, square b) {
        if (a.getX() + a.getSizeX() > b.getX() && a.getX() < b.getX() + b.getSizeX()
                && a.getY() + a.getSizeY() > b.getY() && a.getY() < b.getY() + b.getSizeY()) {
            return true;
        }
        return false;
    }

    public boolean collisionBottom(square a, square b) {
        return (a.getY() + a.getSizeY() > b.getY() && a.getY() + a.getSizeY() < b.getY() + b.getSizeY()
                && a.getX() + a.getSizeX() > b.getX() && a.getX() < b.getX() + b.getSizeX());
    }

    public boolean collision(square a, List<square> c) {
        for (square b : c) {
            if (b == a) {
                continue;
            }
            if (collisionBottom(a, b)) {
                return true;
            }
        }
        return false;
    }

    private void removeAllSameY(List<square> squares) {
        int minimum = rowLength;

        // if 14 squares are on the same y, remove them
        for (int i = 0; i < squares.size(); i++) {
            int count = 0;
            for (int j = 0; j < squares.size(); j++) {
                if (squares.get(i).y == squares.get(j).y) {
                    count++;
                }
            }
            if (count >= minimum) {
                for (int j = 0; j < squares.size(); j++) {
                    if (squares.get(i).y == squares.get(j).y) {
                        squares.get(j).remove = true;
                        squares.get(j).color = disposeColor;
                    }
                }
            }
        }

    }

}
