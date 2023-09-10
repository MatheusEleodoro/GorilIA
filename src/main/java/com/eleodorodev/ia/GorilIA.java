package com.eleodorodev.ia;

import com.eleodorodev.ia.chatgpt.ChatIA;
import com.eleodorodev.ia.config.Config.Log;
import com.eleodorodev.ia.voice.Speech;

import javax.swing.*;
import java.awt.*;

public class GorilIA {
    private static Speech speech;

    public static void process(){
        speech.setEndHear(true);
    }

    public static void inicialize() {
        try {
            ChatIA gpt = new ChatIA();
            speech = new Speech();

            while (!speech.isEndHear()) {
                String userText = speech.hear();
                gpt.conversation(userText, speech);
            }
        } catch (Exception e) {
            Log.LOGGER.severe(e.getMessage());
        }
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("GorilIA");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        ImageIcon imageIcon = new ImageIcon("src/main/resources/logo.png");
        Image image = imageIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);

        ImageIcon icon = new ImageIcon(image);

        JLabel imageLabel = new JLabel(icon);
        JLabel label = new JLabel("Estou te ouvindo");
        JButton button = new JButton("Perguntar");
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(150,122,199));
        button.setBorder(null);
        button.setPreferredSize(new Dimension(200,50));


        panel.add(Box.createVerticalGlue());
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(imageLabel);

        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(10));

        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(e-> process());

        panel.add(button);
        panel.add(Box.createVerticalGlue());


        frame.add(panel);

        frame.setPreferredSize(new Dimension(270, 300));
        frame.pack();
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
        inicialize();
    }

}
