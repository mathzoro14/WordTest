package voc;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
public class MainMenuPanel extends JPanel {
    public MainFrame frame;

    WordBookPanel word;
    NoteManagerPanel note;
    QuizPanel quiz;

    public MainMenuPanel(MainFrame frame, WordBookPanel voc, NoteManagerPanel note, QuizPanel quiz) {
        this.frame = frame;
        this.word = voc;
        this.note = note;
        this.quiz = quiz;
        setLayout(new GridBagLayout());
        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(Color.white);
        box.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 2),
                new EmptyBorder(20, 80, 20, 80)
        ));
        JLabel title = new JLabel("단어장", SwingConstants.CENTER);
        title.setFont(new Font("bold",Font.BOLD, 30));
        title.setForeground(Color.darkGray);
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(box.getBackground());
        titlePanel.add(title, BorderLayout.CENTER);
        box.add(titlePanel, BorderLayout.NORTH);
        JPanel btnPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        btnPanel.setBackground(box.getBackground());
        btnPanel.setBorder(new EmptyBorder(20, 0, 10, 0));
        JButton vocBtn = mainButton("단어장 실행");
        JButton quizBtn = mainButton("퀴즈 모드");
        JButton noteBtn = mainButton("오답노트");
        JButton exitBtn = exitButton("프로그램 종료");
        vocBtn.addActionListener(e -> frame.showCard("voc3"));
        quizBtn.addActionListener(e -> frame.showCard("quiz"));
        quizBtn.addActionListener(e-> quiz.showCard());
        noteBtn.addActionListener(e -> frame.showCard("note"));
        noteBtn.addActionListener(e-> note.InitializeNote());
        exitBtn.addActionListener(e -> frame.exitProgram());
        btnPanel.add(vocBtn);
        btnPanel.add(quizBtn);
        btnPanel.add(noteBtn);
        btnPanel.add(exitBtn);
        box.add(btnPanel, BorderLayout.CENTER);
        GridBagConstraints pos = new GridBagConstraints();
        add(box, pos);
    }
    private JButton mainButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(90, 140, 230));
        btn.setForeground(Color.white);
        btn.setFont(new Font("bold", Font.BOLD, 20));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(200, 50));
        btn.setMargin(new Insets(10, 20, 10, 20));
        return btn;
    }
    private JButton exitButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.lightGray); // 연한 회색
        btn.setForeground(Color.white);
        btn.setFont(new Font("bold", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setMargin(new Insets(10, 20, 10, 20));
        return btn;
    }
}
