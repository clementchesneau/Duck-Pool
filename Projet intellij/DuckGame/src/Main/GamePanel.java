package Main;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable, KeyListener {

    private static final long serialVersionUID = 1L;
    private boolean showMessage = false;

    // fonts
    Font font = new Font("SansSerif", Font.BOLD, 20);
    Font font1 = new Font("SansSerif", Font.PLAIN, 48);

    // dimensions
    public static final int WIDTH = 533;
    public static final int HEIGHT = 300;
    public static final int SCALE = 2;

    // public variables
    public int[] rocksList =  { 0, 0, 500, 10, 1000, 50, 200, 200, 700, 400,
            10, 540, 300, 500, 800, 100, 754, 432, 842, 481};
    public ArrayList<Duck> ducksList = new ArrayList<Duck>();
    public ArrayList<int[]> waterlilyList = new ArrayList<int[]>();
    public static final int TIMEDUCKDIE = 3000;

    // game thread
    private Thread thread;
    private boolean running;
    private int FPS = 60;
    private long time = 500 / FPS;

    // images
    private BufferedImage rock;
    private BufferedImage waterlily;

    // panel
    public GamePanel() {
        super();
        setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        setFocusable(true);
        requestFocus();
        setBackground(new Color(0,128,255));
    }

    public void addNotify() {
        super.addNotify();
        if (thread == null) {
            thread = new Thread(this);
            addKeyListener(this);
            thread.start();
        }
    }

    // initialisation
    public void init() {

        // run
        running = true;

        // load images
        try {
            rock = ImageIO.read(new File("Resources\\rock.png"));
            waterlily = ImageIO.read(new File("Resources\\waterlily.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // main function
    public void run() {

        long timer = 0;
        long id = 1;
        init();

        // game loop
        while (running) {

            // don't pay attention to this
            if (timer % 200 == 0) {
                showMessage = false;
            }
            if (timer % 3000 == 0  && timer != 0) {
                showMessage = true;
            }

            // create new duck
            if (timer % 500 == 0) {
                Duck duck = new Duck(id, rocksList, ducksList);
                ducksList.add(duck);
                id++;
            }

            // create new water lily
            waterlilySpawn(timer);

            eatWaterLily();

            for(Duck duck1: ducksList){
                // moves ducks
                duck1.move(rocksList, ducksList);
                // big ducks whistle
                if (duck1.getSmallSize() == 5 && timer % 1500 == 0) {
                    audio("Resources\\sounds\\sifflement.wav");
                }
            }

            duckLifeManager();

            repaint();
            timer++;

            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // draw rocks
        for (int i = 0; i < 20; i += 2) {
            g2.drawImage(rock, rocksList[i], rocksList[i+1], 50, 50, null);
        }

        // draw ducks
        for (Duck duck1: ducksList) {
            g2.drawImage(duck1.getImage(), duck1.getPos().get(0), duck1.getPos().get(1),
                    duck1.getSize(), duck1.getSize(), null);
            g.setFont(font);
            g.setColor(Color.WHITE);
            //g2.drawString(String.valueOf(duck1.getID()), duck1.getPos().get(0)+10, duck1.getPos().get(1)+10);
        }

        // draw waterlily
        for (int[] lily: waterlilyList) {
            g2.drawImage(waterlily, lily[0], lily[1], 50, 30, null);
        }

        // HUMOR HUMOR HUMOR HUMOR
        /*if (showMessage) {
            g.setFont(font1);
            g.setColor(Color.WHITE);
            g2.drawString("IF YOU HAVE HUMOR CLICK 'E'", 180, 100);
        }*/

        g2.dispose();
    }

    public void waterlilySpawn(long timer) {
        if (timer % 200 == 0) {
            int randomX = 1 + (int)(Math.random() * ((WIDTH * SCALE - 50) + 1));
            int randomY = 1 + (int)(Math.random() * ((HEIGHT * SCALE - 50) + 1));

            while (!canGo(randomX, randomY)) {
                randomX = 1 + (int)(Math.random() * ((WIDTH * SCALE - 50) + 1));
                randomY = 1 + (int)(Math.random() * ((HEIGHT * SCALE - 50) + 1));
            }

            int[] xAndY = {randomX, randomY};
            waterlilyList.add(xAndY);
        }
    }

    private boolean canGo(int x, int y) {
        // Borders
        if (x < 36 || y < 36 || x + 50 > WIDTH * SCALE - 36 ||
                y + 36 > HEIGHT * SCALE - 36) {
            return false;
        }

        // Rocks in the way
        if (thereIsARock(x, y)) {
            return false;
        }

        return true;
    }

    private boolean thereIsARock(int posX, int posY) {

        for (int i = 0; i < 20; i+=2) {
            for (int x = rocksList[i] - 86; x < 86 + rocksList[i]; x++) {
                for (int y = rocksList[i+1] - 66; y < 86 + rocksList[i+1]; y++) {
                    if (posX == x && posY == y) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void eatWaterLily() {
        int index = 0;
        int stockIndex = -1;

        for (int[] lily: waterlilyList) {
            for (Duck duck1: ducksList) {
                for (int x = lily[0] - duck1.getSize(); x < lily[0] + 50; x++) {
                    for (int y = lily[1] - duck1.getSize(); y < lily[1] + 30; y++) {
                        if (duck1.getPos().get(0) == x && duck1.getPos().get(1) == y) {
                            stockIndex = index;
                            if (duck1.getSmallSize() < 5) {
                                duck1.setSize(duck1.getSmallSize() + 1);
                            }
                            duck1.setTimeBeforeDead(TIMEDUCKDIE);
                            audio("Resources\\sounds\\manger.wav");
                        }
                    }
                }
            }
            index++;
        }

        if (stockIndex != -1) {
            waterlilyList.remove(stockIndex);
        }
    }

    private void duckLifeManager() {
        int index = 0;
        int stockIndex = -1;

        for (Duck duck1: ducksList) {
            switch (duck1.getSmallSize()) {
                case 1:
                    duck1.setTimeBeforeDead(duck1.getTimeBeforeDead() - 1);
                    break;
                case 2:
                    duck1.setTimeBeforeDead(duck1.getTimeBeforeDead() - 100 / 90);
                    break;
                case 3:
                    duck1.setTimeBeforeDead(duck1.getTimeBeforeDead() - 100 / 80);
                    break;
                case 4:
                    duck1.setTimeBeforeDead(duck1.getTimeBeforeDead() - 100 / 70);
                    break;
                case 5:
                    duck1.setTimeBeforeDead(duck1.getTimeBeforeDead() - 2);
                    break;
                default:
                    System.out.println("We have a problem sir!");
                    break;
            }

            if (duck1.getTimeBeforeDead() < 1) {
                stockIndex = index;
            }
            index++;
        }

        if (stockIndex != -1) {
            ducksList.remove(stockIndex);
        }
    }

    // play audio function
    public void audio(String path) {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(
                    path));
            // Get a sound clip resource.
            Clip clip = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // keyListener functions
    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {
        int keycode = e.getKeyCode();
        if (keycode == KeyEvent.VK_E) {
            //audio("Resources\\sounds\\carry_valorant.wav");
        }
    }

    public void keyReleased(KeyEvent e) {

    }
}
