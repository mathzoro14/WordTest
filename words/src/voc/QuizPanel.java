package voc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class QuizPanel extends JPanel
{
    static Random random = new Random();

    int num;
    int[] randomNum;
    boolean[] askEng;
    int number; // curr -> number
    boolean IsMultiple;

    NoteManager note;
    VocManager voc;

    MainFrame frame;
    static JPanel topPanel;
    static JPanel centerPanel;

    JLabel qLabel = new JLabel("", JLabel.CENTER);
    JButton[] choices = new JButton[4]; // optionButtons -> choices
    JTextField shortAnswerField = new JTextField();
    JButton submitButton = new JButton("제출");

    JButton btnMulti = new JButton("객관식 퀴즈");
    JButton btnShort = new JButton("주관식 퀴즈");
    JButton btnReturn = new JButton("돌아가기");

    public QuizPanel(MainFrame frame, NoteManager note, VocManager voc)
    {
        this.frame = frame;
        this.note = note;
        this.voc = voc;
        this.setLayout(new BorderLayout());
        initLayout();
        topPanel.setVisible(false);
        centerPanel.setVisible(false);
    }

    private void initLayout() {
        initNorthPanel();
        initCenterPanel();
        initButtonListeners();
    }

    public static void showCard()
    {
        topPanel.setVisible(true);
        centerPanel.setVisible(true);
    }

    private void initNorthPanel() {
        topPanel = new JPanel();
        topPanel.add(btnMulti);
        topPanel.add(btnShort);
        topPanel.add(btnReturn);
        add(topPanel, BorderLayout.NORTH);
    }

    private void initCenterPanel() {
        centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(qLabel, BorderLayout.NORTH);

        JPanel optionPanel = new JPanel(new GridLayout(4, 1, 10, 10));

        for (int i = 0; i < 4; i++) {
            choices[i] = new JButton();
            choices[i].setVisible(false);
            optionPanel.add(choices[i]);
        }

        JPanel shortAnswerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        shortAnswerField = new JTextField(15);

        shortAnswerPanel.add(shortAnswerField);
        shortAnswerPanel.add(submitButton);

        shortAnswerField.setVisible(false);
        submitButton.setVisible(false);

        centerPanel.add(optionPanel, BorderLayout.CENTER);
        centerPanel.add(shortAnswerPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }

    class CheckQuizTypeActionListener implements ActionListener {
        private boolean isMultipleChoice;

        public CheckQuizTypeActionListener(boolean isMultipleChoice) {
            this.isMultipleChoice = isMultipleChoice;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            toggleQuizTypeButtons(false);
            if (isMultipleChoice) {
                MultipleQuiz();
            } else {
                ShortAnswerQuiz();
            }
        }
    }

    class OptionButtonActionListener implements ActionListener {
        private final int index;

        public OptionButtonActionListener(int index) {
            this.index = index;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            checkMultipleAnswer(index);
        }
    }


    private void initButtonListeners() {
        btnMulti.addActionListener(new QuizPanel.CheckQuizTypeActionListener(true));
        btnShort.addActionListener(new QuizPanel.CheckQuizTypeActionListener(false));
        btnReturn.addActionListener(e-> frame.showCard("menu"));
        btnReturn.addActionListener(e->{topPanel.setVisible(false);});
        btnReturn.addActionListener(e->{centerPanel.setVisible(false);});
        btnReturn.addActionListener(e->{note.SaveNote("res/reminder/"+NoteManagerPanel.combo.getSelectedItem());});

        for (int i = 0; i < 4; i++) {
            choices[i].addActionListener(new QuizPanel.OptionButtonActionListener(i));
        }

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkShortAnswer();
            }
        });
        shortAnswerField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkShortAnswer();
            }
        });
    }

    private void toggleQuizTypeButtons(boolean visible) {
        btnMulti.setVisible(visible);
        btnShort.setVisible(visible);
        btnReturn.setVisible(visible);
    }

    void QuizNum() {
        boolean bool = false;
        while (!bool) {
            try {
                String input = JOptionPane.showInputDialog(this,
                        "퀴즈의 개수를 입력하세요 (현재 단어장 단어수: " + voc.voc.size() + ")");
                if (input == null) return;
                num = Integer.parseInt(input);

                if (num > 0 && num <= voc.voc.size()) {
                    randomNum = new int[num];
                    askEng = new boolean[num];

                    for (int i = 0; i < num; i++) {
                        randomNum[i] = random.nextInt(voc.voc.size());
                        for (int j = 0; j < i; j++) {
                            if (randomNum[j] == randomNum[i]) {
                                i--;
                                break;
                            }
                        }
                        askEng[i] = random.nextBoolean();
                    }
                    bool = true;
                } else {
                    JOptionPane.showMessageDialog(this, "퀴즈 개수를 확인해주세요");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "숫자를 입력해주세요");
            }
        }
        number = 0;
    }

    void MultipleQuiz() {
        if (voc.voc.size() == 0) {
            JOptionPane.showMessageDialog(this, "단어를 먼저 추가해주세요.");
            toggleQuizTypeButtons(true);
            return;
        }
        if (voc.voc.size() < 4) {
            JOptionPane.showMessageDialog(this, "객관식 퀴즈는 단어장에 최소 4개의 단어가 필요합니다.");
            toggleQuizTypeButtons(true);
            return;
        }

        QuizNum();
        if (num == 0) {
            toggleQuizTypeButtons(true);
            return;
        }
        IsMultiple = true;
        showNextQuestion();
    }

    void ShortAnswerQuiz() {
        if (voc.voc.size() == 0) {
            JOptionPane.showMessageDialog(this, "단어를 먼저 추가해주세요.");
            toggleQuizTypeButtons(true);
            return;
        }
        QuizNum();
        if (num == 0) {
            toggleQuizTypeButtons(true);
            return;
        }
        IsMultiple = false;
        showNextQuestion();
    }

    void showNextQuestion() {
        if (number >= num) {
            JOptionPane.showMessageDialog(this, "퀴즈 종료!");
            for (JButton b : choices) b.setVisible(false);
            shortAnswerField.setVisible(false);
            submitButton.setVisible(false);
            qLabel.setText("");
            toggleQuizTypeButtons(true);
            return;
        }

        int idx = randomNum[number];
        String question;

        if (IsMultiple) {
            shortAnswerField.setVisible(false);
            submitButton.setVisible(false);
            for (JButton b : choices) b.setVisible(true);

            if (askEng[number]) {
                question = voc.voc.get(idx).eng + " 의 뜻은?";
            } else {
                question = voc.voc.get(idx).kor + " 의 스펠링은?";
            }
            qLabel.setText((number + 1) + "번 문제: " + question);

            int correctPos = random.nextInt(4);
            String correctText;
            if (askEng[number]) {
                correctText = voc.voc.get(idx).kor;
            } else {
                correctText = voc.voc.get(idx).eng;
            }
            choices[correctPos].setText(correctText);

            for (int i = 0; i < 4; i++) {
                if (i == correctPos) continue;
                int randIdx;
                String wrongText;
                boolean isDuplicate;

                do {
                    isDuplicate = false;
                    randIdx = random.nextInt(voc.voc.size());

                    if (askEng[number]) {
                        wrongText = voc.voc.get(randIdx).kor;
                    } else {
                        wrongText = voc.voc.get(randIdx).eng;
                    }

                    if (randIdx == idx) {
                        isDuplicate = true;
                        continue;
                    }

                    for(int j=0; j<i; j++) {
                        if(j != correctPos && choices[j].getText().equals(wrongText)) {
                            isDuplicate = true;
                            break;
                        }
                    }
                } while (isDuplicate);

                choices[i].setText(wrongText);
            }

        } else {
            for (JButton b : choices) b.setVisible(false);
            shortAnswerField.setVisible(true);
            submitButton.setVisible(true);

            if (askEng[number]) {
                question = voc.voc.get(idx).eng + " 의 뜻을 입력해주세요";
            } else {
                question = voc.voc.get(idx).kor + " 의 스펠링을 입력해주세요";
            }

            qLabel.setText((number + 1) + "번 문제: " + question);
            shortAnswerField.setText("");
            shortAnswerField.requestFocusInWindow();
        }


    }

    void checkMultipleAnswer(int selected) {
        int idx = randomNum[number];
        String correct;
        if (askEng[number]) {
            correct = voc.voc.get(idx).kor;
        } else {
            correct = voc.voc.get(idx).eng;
        }


        if (choices[selected].getText().equals(correct)) {
            JOptionPane.showMessageDialog(this, "정답입니다!");
        } else {
            JOptionPane.showMessageDialog(this, "틀렸습니다. 정답: " + correct);
            note.addWord(voc.voc.get(idx).eng, voc.voc.get(idx).kor);
        }
        number++;
        showNextQuestion();
    }

    void checkShortAnswer() {
        int idx = randomNum[number];
        String user = shortAnswerField.getText().trim();
        String correct;
        if (askEng[number]) {
            correct = voc.voc.get(idx).kor;
        } else {
            correct = voc.voc.get(idx).eng;
        }

        boolean isCorrect = false;

        if (askEng[number]) {
            String[] answers = correct.split("/");
            for (String a : answers) {
                if (a.trim().equals(user)) {
                    isCorrect = true;
                    break;
                }
            }
        } else {
            if (user.equals(correct)) isCorrect = true;
        }

        if (isCorrect) {
            JOptionPane.showMessageDialog(this, "정답입니다!");
        } else {
            JOptionPane.showMessageDialog(this, "틀렸습니다. 정답: " + correct);
            note.addWord(voc.voc.get(idx).eng, voc.voc.get(idx).kor);
        }

        number++;
        showNextQuestion();
    }
}
