/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package snake;

import snake.interfaces.Incrementer1;
import snake.interfaces.DrawSquareInterface;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.Timer;

/**
 *
 * @author margargar96
 */
public class Board extends javax.swing.JPanel implements DrawSquareInterface {

    public static final int NUM_ROWS = 20;
    public static final int NUM_COLS = 40;
    private int DELTA_TIME = 150;
    private boolean canChangeDirection = true;
    private Food food;
    private SpecialFood specialFood;
    private int specialFoodCounter = 0;
    private boolean isMultijugador = false;
    private Incrementer1 incrementer;

    private Snake snake;
    private Snake snake2;
    private Timer timer;
    private MyKeyAdapter keyAdapter;

    public Board() {
        initComponents();
        snake = new Snake(this);
        snake2 = null;
        initBoard();

    }

    class MyKeyAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            if (!canChangeDirection) {
                return;
            }

            Direction dir1 = snake.getDirection();
            Direction dir2 = (snake2 != null) ? snake2.getDirection() : null;

            switch (e.getKeyCode()) {

                case KeyEvent.VK_LEFT:
                    if (dir1 != Direction.RIGHT) {
                        snake.setDirection(Direction.LEFT);
                        canChangeDirection = false;
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (dir1 != Direction.LEFT) {
                        snake.setDirection(Direction.RIGHT);
                        canChangeDirection = false;
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (dir1 != Direction.DOWN) {
                        snake.setDirection(Direction.UP);
                        canChangeDirection = false;
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (dir1 != Direction.UP) {
                        snake.setDirection(Direction.DOWN);
                        canChangeDirection = false;
                    }
                    break;

                case KeyEvent.VK_A:
                    if (isMultijugador && snake2 != null && dir2 != Direction.RIGHT) {
                        snake2.setDirection(Direction.LEFT);
                        canChangeDirection = false;
                    }
                    break;
                case KeyEvent.VK_D:
                    if (isMultijugador && snake2 != null && dir2 != Direction.LEFT) {
                        snake2.setDirection(Direction.RIGHT);
                        canChangeDirection = false;
                    }
                    break;
                case KeyEvent.VK_W:
                    if (isMultijugador && snake2 != null && dir2 != Direction.DOWN) {
                        snake2.setDirection(Direction.UP);
                        canChangeDirection = false;
                    }
                    break;
                case KeyEvent.VK_S:
                    if (isMultijugador && snake2 != null && dir2 != Direction.UP) {
                        snake2.setDirection(Direction.DOWN);
                        canChangeDirection = false;
                    }
                    break;
            }
            repaint();
        }
    }

    /**
     * Creates new form Board
     */
    private void initBoard() {

        keyAdapter = new MyKeyAdapter();
        addKeyListener(keyAdapter);
        setFocusable(true);

        timer = new Timer(DELTA_TIME, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                doGameLoop();
            }
        });
    }

    public void setIncrementer(Incrementer1 incrementer) {
        this.incrementer = incrementer;
    }

    private void initGame() {
        timer.start();
        generateFood();
        specialFood = null;
        specialFoodCounter = 0;
    }

    public void startGame(int speed, boolean multijugador) {
        this.DELTA_TIME = speed;
        this.isMultijugador = multijugador;

        snake = new Snake(this);
        if (isMultijugador) {
            snake2 = new Snake(this);
        } else {
            snake2 = null;
        }

        if (incrementer != null) {
            incrementer.reset();
        }

        timer.setDelay(DELTA_TIME);
        timer.setInitialDelay(DELTA_TIME);
        initGame();
        this.requestFocusInWindow();
    }

    private void generateFood() {
        int row, col;
        boolean overlap;
        do {
            row = (int) (Math.random() * NUM_ROWS);
            col = (int) (Math.random() * NUM_COLS);
            overlap = snake.hitsSelf(row, col);
            if (isMultijugador && snake2 != null) {
                overlap = overlap || snake2.hitsSelf(row, col);
            }
        } while (overlap);

        food = new Food(row, col);
    }

    private void generateSpecialFood() {
        int row2, col2;
        boolean overlap;

        do {
            row2 = (int) (Math.random() * NUM_ROWS);
            col2 = (int) (Math.random() * NUM_COLS);

            overlap = snake.hitsSelf(row2, col2) || (row2 == food.getRow() && col2 == food.getCol());

            if (isMultijugador && snake2 != null) {
                overlap = overlap || snake2.hitsSelf(row2, col2);
            }

        } while (overlap);

        specialFood = new SpecialFood(row2, col2);
        specialFoodCounter = 0;
    }

    private void doGameLoop() {
        int nextRow1 = snake.getFirst().getRow();
        int nextCol1 = snake.getFirst().getCol();
        switch (snake.getDirection()) {
            case UP:
                nextRow1--;
                break;
            case DOWN:
                nextRow1++;
                break;
            case LEFT:
                nextCol1--;
                break;
            case RIGHT:
                nextCol1++;
                break;
        }

        int nextRow2 = 0;
        int nextCol2 = 0;
        if (isMultijugador && snake2 != null) {
            nextRow2 = snake2.getFirst().getRow();
            nextCol2 = snake2.getFirst().getCol();
            switch (snake2.getDirection()) {
                case UP:
                    nextRow2--;
                    break;
                case DOWN:
                    nextRow2++;
                    break;
                case LEFT:
                    nextCol2--;
                    break;
                case RIGHT:
                    nextCol2++;
                    break;
            }
        }

        boolean canMove1 = canMove(nextRow1, nextCol1);
        boolean canMove2 = isMultijugador && snake2 != null ? canMove(nextRow2, nextCol2) : true;

        if (canMove1 && canMove2) {
            if (food != null && nextRow1 == food.getRow() && nextCol1 == food.getCol()) {
                snake.grow();
                generateFood();
                if (incrementer != null) {
                    incrementer.incrementScore(1, 1);
                }
            }

            if (isMultijugador && snake2 != null && food != null && nextRow2 == food.getRow() && nextCol2 == food.getCol()) {
                snake2.grow();
                generateFood();
                if (incrementer != null) {
                    incrementer.incrementScore(2, 1);
                }
            }

            if (specialFood != null) {
                if (nextRow1 == specialFood.getRow() && nextCol1 == specialFood.getCol()) {
                    snake.grow();
                    snake.grow();
                    snake.grow();
                    specialFood = null;
                    specialFoodCounter = 0;
                    if (incrementer != null) {
                        incrementer.incrementScore(1, 3);
                    }
                } else if (isMultijugador && snake2 != null && nextRow2 == specialFood.getRow() && nextCol2 == specialFood.getCol()) {
                    snake2.grow();
                    snake2.grow();
                    snake2.grow();
                    specialFood = null;
                    specialFoodCounter = 0;
                    if (incrementer != null) {
                        incrementer.incrementScore(2, 3);
                    }
                } else {
                    specialFoodCounter++;
                    if (specialFoodCounter >= 35) {
                        specialFood = null;
                        specialFoodCounter = 0;
                    }
                }
            } else {
                specialFoodCounter++;
                if (specialFoodCounter >= 50) {
                    generateSpecialFood();
                }
            }

            snake.move();
            if (isMultijugador && snake2 != null) {
                snake2.move();
            }

            canChangeDirection = true;
        } else {
            timer.stop();
            gameOver();
        }
        repaint();
    }

    private void gameOver() {
        timer.stop();

        java.awt.Window parentWindow = javax.swing.SwingUtilities.getWindowAncestor(this);
        DeadDialog dialog = new DeadDialog((java.awt.Frame) parentWindow, true);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        if (dialog.getUserSelection() == DeadDialog.RESTART_SAME) {
            startGame(this.DELTA_TIME, this.isMultijugador);
        } else if (dialog.getUserSelection() == DeadDialog.RESTART_NEW) {
            if (parentWindow != null) {
                parentWindow.dispose();
            }
            new Game().setVisible(true);
        }
    }

    private boolean canMove(int row, int col) {
        if (row < 0 || row >= NUM_ROWS || col < 0 || col >= NUM_COLS) {
            return false;
        }
        if (snake.hitsSelf(row, col)) {
            return false;
        }

        if (isMultijugador && snake2 != null && snake2.hitsSelf(row, col)) {
            return false;
        }
        return true;
    }

    private int squareWidth() {
        return getWidth() / NUM_COLS;
    }

    private int squareHeight() {
        return getHeight() / NUM_ROWS;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintBackground(g);
        paintBorderBoard(g);

        if (food != null) {
            drawSquare(g, food.getRow(), food.getCol(), false);
        }
        if (specialFood != null) {
            drawSquare(g, specialFood.getRow(), specialFood.getCol(), true);
        }

        if (snake != null) {
            snake.paint(g);
        }

        if (isMultijugador && snake2 != null) {
            snake2.paint(g);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void paintBorderBoard(Graphics g) {
        g.setColor(Color.black);
        int widht = squareWidth() * NUM_COLS;
        int height = squareHeight() * NUM_ROWS;
        g.drawRect(0, 0, widht, height);
    }

    private void paintBackground(Graphics g) {
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                if ((row + col) % 2 == 0) {
                    g.setColor(new Color(170, 215, 81));
                } else {
                    g.setColor(new Color(162, 209, 73));
                }

                int x = col * squareWidth();
                int y = row * squareHeight();

                g.fillRect(x, y, squareWidth(), squareHeight());
            }
        }
    }

    public void drawSquare(Graphics g, int row, int col,
            boolean isHead) {
        Color colors[] = {new Color(0, 0, 0),
            new Color(204, 102, 102),
            new Color(102, 204, 102), new Color(102, 102, 204),
            new Color(204, 204, 102), new Color(204, 102, 204),
            new Color(102, 204, 204), new Color(218, 170, 0)
        };
        int x = col * squareWidth();
        int y = row * squareHeight();
        Color color = isHead ? new Color(204, 102, 102) : new Color(102, 102, 204);
        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2,
                squareHeight() - 2);
        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);
        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1,
                y + squareHeight() - 1,
                x + squareWidth() - 1, y + 1);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 453, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 443, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
