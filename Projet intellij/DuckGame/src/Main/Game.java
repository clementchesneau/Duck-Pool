package Main;

import javax.swing.*;

public class Game {

    public static void main(String[] args) {

        // window
        JFrame window = new JFrame("Duck Pool");
        window.setContentPane(new GamePanel());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.pack();
        window.setVisible(true);
    }
}
