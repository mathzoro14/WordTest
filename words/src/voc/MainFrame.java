package voc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {

    public CardLayout cardLayout = new CardLayout();
    public  JPanel cardPanel = new JPanel(cardLayout);
    public  NoteManager noteManager = new NoteManager();
    public  VocManager vocManager = new VocManager(noteManager);
    public MainFrame() {
        super("단어장");
        setBackground(Color.gray);
        setSize(550, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        cardPanel.setBackground(Color.CYAN);
        cardPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        vocManager.makeVoc("res/word/word.txt");
        noteManager.setFile();

        WordBookPanel wordBookPanel = new WordBookPanel(this,vocManager);
        NoteManagerPanel noteManagerPanel = new NoteManagerPanel(this,noteManager);
        QuizPanel quizPanel = new QuizPanel(this,noteManager,vocManager);
        MainMenuPanel menuPanel = new MainMenuPanel(this,wordBookPanel,noteManagerPanel,quizPanel);
        cardPanel.add(menuPanel, "menu");
        cardPanel.add(wordBookPanel, "voc3");
        cardPanel.add(noteManagerPanel,"note");
        cardPanel.add(quizPanel,"quiz");
        add(cardPanel);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exitProgram();
            }
        });
    }
    public void showCard(String name) {
        cardLayout.show(cardPanel, name);
    }

    public void exitProgram() {
        vocManager.saveVoc("res/word/word.txt");
        System.exit(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {MainFrame frame = new MainFrame();
            frame.setVisible(true);})
    ;}
}
