package Main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static Main.GamePanel.*;


public class Duck {

    // variables
    ArrayList<Integer> pos = new ArrayList<Integer>();

    long id;
    int size = 1;
    int timeBeforeDead = TIMEDUCKDIE;

    private BufferedImage duckImage;

    String direction;
    int whenChange = 0;


    // constructor
    public Duck(long id,  int[] rocksList, ArrayList<Duck> ducksList) {
        ArrayList<Integer> posArray = new ArrayList<Integer>();
        Collections.addAll(posArray, 1 + (int)(Math.random() * ((WIDTH * SCALE - 1) + 1))
                , 1 + (int)(Math.random() * ((HEIGHT * SCALE - 1) + 1)));

        while (!canGo(posArray, rocksList, ducksList)) {
            posArray.set(0, 1 + (int)(Math.random() * ((WIDTH * SCALE - 1) + 1)));
            posArray.set(1, 1 + (int)(Math.random() * ((HEIGHT * SCALE - 1) + 1)));
        }

        this.pos.add(posArray.get(0));
        this.pos.add(posArray.get(1));
        this.id = id;
    }

    // set functions
    public void setPos(int x, int y) {
        this.pos.set(0, x);
        this.pos.set(1, y);
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setTimeBeforeDead(int timeBeforeDead) {
        this.timeBeforeDead = timeBeforeDead;
    }

    public void setImage() {

        try {
            // if not a big duck
            if (this.size < 5) {
                switch (this.direction) {
                    case "top":
                        this.duckImage = ImageIO.read(new File("Resources\\ducks\\duck-top-1.png"));
                        break;
                    case "bottom":
                        this.duckImage = ImageIO.read(new File("Resources\\ducks\\duck-bottom-1.png"));
                        break;
                    case "left":
                        this.duckImage = ImageIO.read(new File("Resources\\ducks\\duck-left-1.png"));
                        break;
                    case "right":
                        this.duckImage = ImageIO.read(new File("Resources\\ducks\\duck-right-1.png"));
                        break;
                    default:
                        System.out.println("We have a problem sir!");
                        break;
                }
            }
            else {
                switch (this.direction) {
                    case "top":
                        this.duckImage = ImageIO.read(new File("Resources\\ducks\\duck-top-2.png"));
                        break;
                    case "bottom":
                        this.duckImage = ImageIO.read(new File("Resources\\ducks\\duck-bottom-2.png"));
                        break;
                    case "left":
                        this.duckImage = ImageIO.read(new File("Resources\\ducks\\duck-left-2.png"));
                        break;
                    case "right":
                        this.duckImage = ImageIO.read(new File("Resources\\ducks\\duck-right-2.png"));
                        break;
                    default:
                        System.out.println("We have a problem sir!");
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // get functions
    public ArrayList<Integer> getPos() {
        return this.pos;
    }

    public int getSize() {
        int realSize;
        if (this.size == 1) {
            realSize = 15;
        }
        else if (this.size == 2){
            realSize = 20;
        }
        else {
            realSize = 10 * this.size;
        }

        return realSize;
    }

    public int getSmallSize() {
        return this.size;
    }

    public int getTimeBeforeDead() {
        return this.timeBeforeDead;
    }

    public BufferedImage getImage() {
        return this.duckImage;
    }

    public long getID() {
        return this.id;
    }

    // probably essentials functions
    public void move(int[] rocksList, ArrayList<Duck> ducksList) {
        // time to change direction
        if (this.whenChange == 0) {
            notSoRandomDirection();
            this.whenChange = 50 + (int)(Math.random() * ((WIDTH * SCALE - 50) + 50));
        }

        // can the duck go that way ?
        while (!canGo(nextPosition(), rocksList, ducksList)) {
            notSoRandomDirection();
        }

        this.setImage();

        switch (this.direction) {
            case "top":
                this.pos.set(1, this.pos.get(1) - 1);
                break;
            case "bottom":
                this.pos.set(1, this.pos.get(1) + 1);
                break;
            case "left":
                this.pos.set(0, this.pos.get(0) - 1);
                break;
            case "right":
                this.pos.set(0, this.pos.get(0) + 1);
                break;
            default:
                System.out.println("We have a problem sir!");
                break;
        }

        whenChange--;
    }

    private void notSoRandomDirection() {
        if (this.direction == null) {
            // if no direction, set direction
            setDirection();
        }
        else {
            String oldDirection = this.direction;
            // if direction exist, change direction
            while (this.direction.equals(oldDirection)) {
                setDirection();
                // conditions so the duck as less possibilities of going back on his own "steps"
                if (this.direction.equals("left") && oldDirection.equals("right") ||
                        this.direction.equals("right") && oldDirection.equals("left")) {
                    setDirection();
                }
                else if (this.direction.equals("top") && oldDirection.equals("bottom") ||
                        this.direction.equals("bottom") && oldDirection.equals("top")) {
                    setDirection();
                }
            }
        }
    }

    // this create and associate a random number (1 to 4) to a direction
    private void setDirection() {
        int random = 1 + (int)(Math.random() * ((4 - 1) + 1));
        switch (random) {
            case 1:
                this.direction = "top";
                break;
            case 2:
                this.direction = "bottom";
                break;
            case 3:
                this.direction = "left";
                break;
            case 4:
                this.direction = "right";
                break;
            default:
                System.out.println("We have a problem sir!");
                break;
        }
    }

    // gives the position where the duck wants to go next loop
    private ArrayList<Integer> nextPosition() {
        ArrayList<Integer> nextPos = new ArrayList<Integer>();

        switch (this.direction) {
            case "top":
                Collections.addAll(nextPos, this.pos.get(0), this.pos.get(1) - 1);
                break;
            case "bottom":
                Collections.addAll(nextPos, this.pos.get(0), this.pos.get(1) + 1);
                break;
            case "left":
                Collections.addAll(nextPos, this.pos.get(0) - 1, this.pos.get(1));
                break;
            case "right":
                Collections.addAll(nextPos, this.pos.get(0) + 1, this.pos.get(1));
                break;
            default:
                System.out.println("We have a problem sir!");
                break;
        }
        return nextPos;
    }

    // verify if the duck can go where he wants
    private boolean canGo(ArrayList<Integer> pos, int[] rocksList, ArrayList<Duck> ducksList) {
        if (pos.get(0) < 1 || pos.get(1) < 1 || (pos.get(0) + this.getSize()) > WIDTH * SCALE ||
                (pos.get(1) + this.getSize()) > HEIGHT * SCALE) {
            return false;
        }

        // collision between duck (doesn't work that well)
        /*if (thereIsADuck(pos.get(0), pos.get(1), ducksList)) {
            return false;
        }*/

        // collision with rocks
        if (thereIsARock(pos.get(0), pos.get(1), rocksList)) {
            return false;
        }

        return true;
    }

    // rock on the way
    private boolean thereIsARock(int posX, int posY, int[] rocksList) {

        for (int i = 0; i < 20; i+=2) {
            for (int x = rocksList[i] - this.getSize(); x < 50 + rocksList[i]; x++) {
                for (int y = rocksList[i+1] - this.getSize(); y < 50 + rocksList[i+1]; y++) {
                    if (posX == x && posY == y) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    // duck on the way
    private boolean thereIsADuck(int posX, int posY, ArrayList<Duck> ducksList) {

            for (Duck duck1: ducksList) {
                if (duck1.getID() != this.id) {
                    for (int x = duck1.getPos().get(0) - this.getSize();
                         x < this.getSize() + duck1.getPos().get(0); x++) {
                        for (int y = duck1.getPos().get(1) - this.getSize();
                             y < this.getSize() + duck1.getPos().get(1); y++) {
                            // duck at the moment
                            if (posX == x && posY == y) {
                                return true;
                            }
                        }
                    }
                }
            }


        return false;
    }
}
