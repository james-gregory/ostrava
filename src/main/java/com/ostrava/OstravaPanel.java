package com.ostrava;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Slf4j
@Singleton
public class OstravaPanel extends PluginPanel {
    private final OstravaPlugin plugin;

    private final JButton recordButton;
    private final JButton pauseButton;
    public OstravaPanel(OstravaPlugin plugin) {
        this.plugin = plugin;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBorder(new EmptyBorder(1, 0, 10, 0));

        JLabel title = new JLabel();
        title.setText("Ostrava");
        title.setForeground(Color.WHITE);

        northPanel.add(title, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        infoPanel.setLayout(new GridLayout(0, 1));

        JLabel info = new JLabel(htmlLabel("Press record to start tracking your activity.", "#FFFFFF"));
        infoPanel.add(info);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setLayout(new GridLayout(2, 1));
        buttonPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

        this.recordButton = new JButton("Record");
        this.pauseButton = new JButton("Pause");

        buttonPanel.add(recordButton);
        buttonPanel.add(pauseButton);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;

        recordButton.setToolTipText("Start recording activity");
        recordButton.addActionListener(l -> plugin.record());
        pauseButton.setToolTipText("Pause recording");
        pauseButton.addActionListener(l -> plugin.pause());

        add(northPanel, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }


    public void update(boolean recording, boolean paused) {
        if (recording) {
            recordButton.setText("Stop");
        } else {
            recordButton.setText("Record");
        }
        if (paused) {
            pauseButton.setText("Resume");
        } else {
            pauseButton.setText("Pause");
        }
    }



    private static String htmlLabel(String key, String color)
    {
        return "<html><body style = 'color:" + color + "'>" + key + "</body></html>";
    }
}