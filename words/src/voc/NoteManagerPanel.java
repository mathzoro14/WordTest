package voc;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Comparator;
import java.util.regex.*;

public class NoteManagerPanel extends JPanel {
    static JFileChooser chooser = new JFileChooser();
    MainFrame frame;

    static NoteManager baseManager;
    static NoteManager subsetManager;
    static NoteManager backupManager;

    static JTable table;
    JToolBar toolBar;
    JMenuBar mb;
    JScrollPane jScrollPane;
    JPanel southPanel;
    static JComboBox<String> combo;
    static JTextField WordSearch;
    static DefaultTableModel model;
    static String[] ColumnNames = {"영단어", "뜻", "삭제 여부"};
    static int sortFlag = 0;
    static boolean isCreating = false;
    static boolean flag = true;


    public NoteManagerPanel(MainFrame frame, NoteManager note) {
        this.frame = frame;
        this.setLayout(new BorderLayout());
        baseManager = note;
        subsetManager = new NoteManager();
        backupManager = new NoteManager();
        CreateMenu();
        CreateToolBar();
        CreateJTable();
        initSouthPanel();
        toolBar.setVisible(false);
        mb.setVisible(false);
        jScrollPane.setVisible(false);
        southPanel.setVisible(false);
    }

    void InitializeNote() {
        toolBar.setVisible(true);
        mb.setVisible(true);
        jScrollPane.setVisible(true);
        southPanel.setVisible(true);
        baseManager.deleteAll();
        baseManager.AddNote("res/reminder/" + combo.getSelectedItem());
        updateTableData("");
    }

    void initSouthPanel()
    {
        southPanel = new JPanel();
        add(southPanel,"South");
    }

    void CreateMenu() {
        mb = new JMenuBar();
        JMenu FileMenu = getJMenu();


        JMenu WordMenu = new JMenu("단어(W)");
        JMenuItem DeleteWord = new JMenuItem("단어 삭제");
        JMenuItem DeleteAll = new JMenuItem("전체 삭제");
        DeleteWord.addActionListener(e->{DeleteWord dw = new DeleteWord();});
        DeleteAll.addActionListener(e->{baseManager.deleteAll();
            baseManager.SaveNote("res/reminder/"+combo.getSelectedItem());
            updateTableData("");});
        WordMenu.add(DeleteWord);
        WordMenu.addSeparator();
        WordMenu.add(DeleteAll);

        JMenu ReturnMenu = new JMenu("이동(M)");
        JMenuItem toMain = new JMenuItem("메인 메뉴");
        JMenuItem toWordBook = new JMenuItem("단어장");
        JMenuItem toQuiz = new JMenuItem("퀴즈");

        toMain.addActionListener(e -> frame.showCard("menu"));
        toMain.addActionListener(e->baseManager.SaveNote("res/reminder/"+combo.getSelectedItem()));
        toMain.addActionListener(e -> mb.setVisible(false));
        toMain.addActionListener(e-> toolBar.setVisible(false));
        toMain.addActionListener(e->jScrollPane.setVisible(false));

        toWordBook.addActionListener(e->frame.showCard("voc3"));
        toWordBook.addActionListener(e->baseManager.SaveNote("res/reminder/"+combo.getSelectedItem()));
        toWordBook.addActionListener(e -> mb.setVisible(false));
        toWordBook.addActionListener(e-> toolBar.setVisible(false));
        toWordBook.addActionListener(e->jScrollPane.setVisible(false));

        toQuiz.addActionListener(e->frame.showCard("quiz"));
        toQuiz.addActionListener(e -> QuizPanel.showCard());
        toQuiz.addActionListener(e->baseManager.SaveNote("res/reminder/"+combo.getSelectedItem()));
        toQuiz.addActionListener(e -> mb.setVisible(false));
        toQuiz.addActionListener(e-> toolBar.setVisible(false));
        toQuiz.addActionListener(e->jScrollPane.setVisible(false));

        ReturnMenu.add(toMain);
        ReturnMenu.add(toWordBook);
        ReturnMenu.add(toQuiz);

        mb.add(FileMenu);
        mb.add(WordMenu);
        mb.add(ReturnMenu);
        this.frame.setJMenuBar(mb);
    }

    private JMenu getJMenu() {
        JMenu FileMenu = new JMenu("파일(F)");
        JMenuItem CurrentFileSave = new JMenuItem("현재 상태 저장");
        JMenuItem OtherFileSave = new JMenuItem("다른 이름으로 저장");
        JMenuItem LoadFile = new JMenuItem("외부 파일 불러오기");
        JMenuItem CreateFile = new JMenuItem("파일 생성");
        JMenuItem DeleteFile = new JMenuItem("파일 삭제");

        CurrentFileSave.addActionListener(e -> {baseManager.SaveNote("res/reminder/"+combo.getSelectedItem());});
        OtherFileSave.addActionListener(e->{isCreating=false;CreateFile crFile = new CreateFile();});

        LoadFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt files", "txt");
                chooser.setFileFilter(filter);
                int ret = chooser.showOpenDialog(null);
                if (ret != JFileChooser.APPROVE_OPTION) {
                    JOptionPane.showMessageDialog(null,
                            "파일을 선택하지 않았습니다",
                            "경고", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String filePath = chooser.getSelectedFile().getPath();
                String[] SplitFile = filePath.split(Pattern.quote("\\"));
                System.out.println(SplitFile[SplitFile.length-1].trim());
            }
        });

        CreateFile.addActionListener(e->{isCreating=true;CreateFile cf = new CreateFile();});
        DeleteFile.addActionListener(e->{DeleteFile df = new DeleteFile();});

        FileMenu.add(CurrentFileSave);
        FileMenu.add(OtherFileSave);
        FileMenu.addSeparator();
        //FileMenu.add(LoadFile);
        FileMenu.add(CreateFile);
        FileMenu.add(DeleteFile);
        return FileMenu;
    }

    static class CreateFile extends JFrame{
        public CreateFile() {
            setTitle("신규 파일 생성/저장");
            Container c = getContentPane();
            c.setLayout(new FlowLayout());
            JTextField tf = new JTextField("",20);
            JButton btn = new JButton("완료(C)");

            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String text = tf.getText();
                    if(text.isEmpty())
                    {
                        JOptionPane.showMessageDialog(null,"공백으로 입력하실 수 없습니다",
                                "Error",JOptionPane.ERROR_MESSAGE);
                    }
                    else if(baseManager.FileSearch(text+".txt") != -1)
                    {
                        JOptionPane.showMessageDialog(null,"동일한 파일명이 이미 있습니다.",
                                "Warning",JOptionPane.WARNING_MESSAGE);
                    }
                    else
                    {
                        combo.addItem(text+".txt");
                        if(isCreating)
                        {
                            baseManager.deleteAll();
                            baseManager.SaveNote("res/reminder/"+text+".txt");
                            baseManager.AddNote("res/reminder/"+combo.getSelectedItem());
                            updateTableData("");
                        }
                        else baseManager.SaveNote("res/reminder/"+text+".txt");
                        JOptionPane.showMessageDialog(null,"파일 추가가 완료되었습니다.",
                                "Information",JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    }
                }
            });

            c.add(new JLabel("생성을 하실 파일의 이름을 입력해주세요(.txt)"));
            c.add(tf);
            c.add(btn);
            setSize(300, 120);
            setLocationRelativeTo(null);
            setVisible(true);
        }
    }

    static class DeleteFile extends JFrame
    {
        public DeleteFile()
        {
            setTitle("파일 삭제");
            Container c = getContentPane();
            c.setLayout(new FlowLayout());
            setSize(300, 100);
            setLocationRelativeTo(null);
            setVisible(true);
            JTextField tf = new JTextField(20);
            JButton confirmBtn = new JButton("확인");

            confirmBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String text = tf.getText();
                    File myfile;
                    int x;
                    if(text.isEmpty())
                    {
                        JOptionPane.showMessageDialog(null,"공백으로 입력하실 수 없습니다",
                                "Error",JOptionPane.ERROR_MESSAGE);
                    }
                    else if(baseManager.FileSearch(text+".txt") == -1)
                    {
                        JOptionPane.showMessageDialog(null,"디렉토리에 없는 파일명입니다.",
                                "Warning",JOptionPane.WARNING_MESSAGE);
                    }
                    else {
                        x = baseManager.FileSearch(text + ".txt");
                        combo.removeItem(text + ".txt");
                        if (combo.getSelectedItem() == text) combo.setSelectedItem(baseManager.fileList.get(x - 1));
                        baseManager.deleteAll();
                        baseManager.AddNote("res/reminder/" + combo.getSelectedItem());
                        updateTableData("");
                        myfile = new File("res/reminder/" + text + ".txt");
                        if (myfile.delete())
                        {
                            JOptionPane.showMessageDialog(null,"삭제 완료되었습니다.",
                                    "Information",JOptionPane.INFORMATION_MESSAGE);
                            dispose();
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(null,"삭제처리가 되지 못했습니다.",
                                    "Warning",JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            });

            c.add(new JLabel("삭제할 단어장 파일을 입력하세요.(.txt)"));
            c.add(tf);
            c.add(confirmBtn);
        }
    }

    static class DeleteWord extends JFrame
    {
        public DeleteWord()
        {
            setTitle("단어 삭제");
            setSize(300, 100);
            setLocationRelativeTo(null);
            setVisible(true);
            Container c = getContentPane();
            c.setLayout(new FlowLayout());
            JTextField tf = new JTextField(20);
            JButton confirmBtn = new JButton("확인");

            confirmBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String word = tf.getText();
                    if(word.isEmpty())
                    {
                        JOptionPane.showMessageDialog(null,"공백으로 입력하실 수 없습니다",
                                "Error",JOptionPane.ERROR_MESSAGE);
                    }
                    else if(baseManager.deleteWord(word) == -1)
                    {
                        JOptionPane.showMessageDialog(null,"단어장에 없는 단어입니다",
                                "Warning",JOptionPane.WARNING_MESSAGE);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null,"삭제되었습니다",
                                "Information",JOptionPane.INFORMATION_MESSAGE);
                        updateTableData(WordSearch.getText());
                        dispose();
                    }
                }
            });

            c.add(new JLabel("삭제할 영단어 입력 후 확인 버튼을 누르세요."));
            c.add(tf);
            c.add(confirmBtn);
        }
    }

    void CreateToolBar() {
        toolBar = new JToolBar();
        ImageIcon image = new ImageIcon("res/images/searchicon.jpg");
        Image img = image.getImage();
        Image scaledImage = img.getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(scaledIcon);

        toolBar.setBackground(Color.LIGHT_GRAY);

        JButton sortButton = new JButton("정렬 방식 선택");
        sortButton.addActionListener(e->{SortOption sort = new SortOption();});

        WordSearch = new JTextField(20);
        WordSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {updateTableData(WordSearch.getText());}

            @Override
            public void removeUpdate(DocumentEvent e) {updateTableData(WordSearch.getText());}

            @Override
            public void changedUpdate(DocumentEvent e) {} //사용치 않는 기능
        });

        JComboBox<String> searchOption = new JComboBox<>();
        searchOption.addItem("영단어로 검색");
        searchOption.addItem("뜻으로 검색");
        searchOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox<String> sourceComboBox = (JComboBox<String>) e.getSource();
                String selectedItem = (String) sourceComboBox.getSelectedItem();
                if(selectedItem != "영단어로 검색") flag = false;
                else flag = true;
            }
        });

        initCombo();

        toolBar.add(sortButton);
        toolBar.addSeparator();
        toolBar.add(searchOption);
        toolBar.addSeparator();
        toolBar.add(WordSearch);
        toolBar.add(imageLabel);
        toolBar.addSeparator();
        toolBar.add(combo);
        this.frame.add(toolBar, "North");


    }

    void initCombo()
    {
        combo = new JComboBox<>();
        for(String f: baseManager.fileList)
        {
            combo.addItem(f);
        }
        combo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox<String> sourceComboBox = (JComboBox<String>) e.getSource();
                String selectedItem = (String) sourceComboBox.getSelectedItem();
                if(baseManager.fileList.size() != 0)
                {
                    baseManager.deleteAll();
                    baseManager.AddNote("res/reminder/"+selectedItem);
                    updateTableData("");
                }
            }
        });
    }

    static class SortOption extends JFrame
    {
        public SortOption()
        {
            setTitle("정렬 방식 선택");
            setSize(300, 100);
            setLocationRelativeTo(null);
            setVisible(true);
            Container c = getContentPane();
            c.setLayout(new FlowLayout());

            JRadioButton basic = new JRadioButton("추가 순서(기본)");
            JRadioButton AtoZ = new JRadioButton("A-Z");
            JRadioButton ZtoA = new JRadioButton("Z-A");
            JRadioButton MeaningASC = new JRadioButton("ASC(뜻)");
            JRadioButton MeaningDESC = new JRadioButton("DESC(뜻)");

            ButtonGroup btn =  new ButtonGroup();

            switch(sortFlag)
            {
                case 0 -> basic.setSelected(true);
                case 1 -> AtoZ.setSelected(true);
                case 2-> ZtoA.setSelected(true);
                case 3 -> MeaningASC.setSelected(true);
                case 4 -> MeaningDESC.setSelected(true);
            }

            basic.addActionListener(e->{sortFlag=0;NoteManagerPanel.initTableData();});
            AtoZ.addActionListener(e->{sortFlag=1;NoteManagerPanel.initTableData();});
            ZtoA.addActionListener(e->{sortFlag=2;NoteManagerPanel.initTableData();});
            MeaningASC.addActionListener(e->{sortFlag=3;NoteManagerPanel.initTableData();});
            MeaningDESC.addActionListener(e->{sortFlag=4;NoteManagerPanel.initTableData();});

            btn.add(basic);
            btn.add(AtoZ);
            btn.add(ZtoA);
            btn.add(MeaningASC);
            btn.add(MeaningDESC);

            c.add(basic);
            c.add(AtoZ);
            c.add(ZtoA);
            c.add(MeaningASC);
            c.add(MeaningDESC);
        }
    }

    void CreateJTable() {
        model = new DefaultTableModel(ColumnNames, 0);
        table = new JTable(model);
        jScrollPane = new JScrollPane(table);
        add(jScrollPane, "Center");
        updateTableData("");
    }

    private static void initTableData()
    {
        removeTableData();
        switch(sortFlag)
        {
            case 1 -> subsetManager.voc.sort(new Comparator<Word>() {
                @Override
                public int compare(Word o1, Word o2) {
                    return o1.eng.compareTo(o2.eng);
                }
            }); //a-z
            case 2 -> subsetManager.voc.sort((o1,o2)->o2.eng.compareTo(o1.eng));
            case 3 -> subsetManager.voc.sort(new Comparator<Word>() {
                @Override
                public int compare(Word o1, Word o2) {
                    return o1.kor.compareTo(o2.kor);
                }
            });
            case 4 -> subsetManager.voc.sort(((o1, o2) -> o2.kor.compareTo(o1.kor)));
        }
        if(sortFlag!=0)
        {
            for (Word w : subsetManager.voc) {
                model.addRow(new String[]{w.eng, w.kor});
            }
        }
        else
        {
            for (Word w : backupManager.voc) {
                model.addRow(new String[]{w.eng, w.kor});
            }
        }
    }

    private static void removeTableData() {
        if (model.getRowCount() > 0){
            model = new DefaultTableModel(ColumnNames,0);
            table.setModel(model);
        }
    }

    private static void updateTableData(String str) {
        subsetManager.voc = baseManager.searchVoc2(str,flag);
        backupManager.voc = baseManager.searchVoc2(str,flag);
        initTableData();
    }
}
