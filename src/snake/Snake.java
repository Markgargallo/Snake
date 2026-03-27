/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package snake;

import snake.interfaces.DrawSquareInterface;
import java.awt.Graphics;
import java.util.ArrayList;
import javax.xml.xpath.XPathConstants;
import static snake.Direction.DOWN;
import static snake.Direction.RIGHT;

/**
 *
 * @author margargar96
 */
public class Snake {

    private ArrayList<Node> nodes = new ArrayList<>();
    private Direction direction = Direction.RIGHT;

    private DrawSquareInterface drawSquareInterface;
    private int nodesToGrow;

    public Snake(DrawSquareInterface drawSquareInterface) {
        int row = Board.NUM_ROWS / 2;
        int col = Board.NUM_COLS / 2;
        for (int i = 0; i < 4; i++) {
            Node node = new Node(row, col - i);
            nodes.add(node);
        }
        this.drawSquareInterface = drawSquareInterface;
        nodesToGrow = 0;
    }

    public void paint(Graphics g) {
        boolean first = true;
        for (Node node : nodes) {
            drawSquareInterface.drawSquare(g, node.getRow(), node.getCol(), first);
            if (first) {
                first = false;
            }
        }
    }

    public Node getFirst() {
        return nodes.get(0);
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public void move() {
        Node node;
        switch (direction) {
            case UP:
                node = new Node(this.getFirst().getRow() - 1, this.getFirst().getCol());
                break;
            case DOWN:
                node = new Node(this.getFirst().getRow() + 1, this.getFirst().getCol());
                break;
            case RIGHT:
                node = new Node(this.getFirst().getRow(), this.getFirst().getCol() + 1);
                break;
            case LEFT:
                node = new Node(this.getFirst().getRow(), this.getFirst().getCol() - 1);
                break;
            default:
                return;
        }

        nodes.add(0, node);

        if (nodesToGrow > 0) {
            nodesToGrow--;
        } else {
            nodes.remove(nodes.size() - 1);
        }
    }

    public void grow() {
        nodesToGrow++;
    }

    public boolean hitsSelf(int row, int col) {
        for (Node node : nodes) {
            if (node.getRow() == row && node.getCol() == col) {
                return true;
            }
        }
        return false;
    }

}
