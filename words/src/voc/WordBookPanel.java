package voc;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;

public class WordBookPanel extends JPanel {
    CardLayout card = new CardLayout();
    JPanel northPanel = new JPanel(card),searchPanel,editPanel;
    JPanel doPanel;
    JButton btn1,btn2,undo,redo;
    JTextField text,engText,korText;
    JRadioButton asc,desc;
    JLabel notice = new JLabel("");

    JTable table;
    DefaultTableModel model;
    String[] header = {"영단어","뜻"};

    VocManager baseManager;
    VocManager subsetManager;

    String filename;
    LinkedList<Word> redoList = new LinkedList<>();
    LinkedList<Word> undoList = new LinkedList<>();
    int REDOLISTCAP = 10;

    boolean flag=true;
    MainFrame frame;
    public WordBookPanel(MainFrame frame,VocManager vocM){
        this.frame = frame;
        this.baseManager = vocM;
        this.subsetManager = new VocManager(null);
        initLayout();
    }

    private void initLayout() {
        setLayout(new BorderLayout());
        initNorthPanel();
        initSouthPanel();
        initCenterPanel();
        updateTableData("");
    }

    private void initTableData() {
        removeTableData();
        if (flag) {
            subsetManager.voc.sort(new Comparator<Word>() {
                @Override
                public int compare(Word o1, Word o2) {
                    return o1.eng.compareTo(o2.eng);
                }
            });
        }else{
            subsetManager.voc.sort(new Comparator<Word>() {
                @Override
                public int compare(Word o1, Word o2) {
                    return o2.eng.compareTo(o1.eng);
                }
            });
        }
        for (Word w : subsetManager.voc) {
            model.addRow(new String[]{w.eng, w.kor});
        }
    }

    private void initCenterPanel() {
        this.model = new DefaultTableModel(header,0);
        this.table = new JTable(model);
        add(new JScrollPane(table),"Center");
    }

    private void initSouthPanel() {
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BorderLayout());
        JButton btnReturn = new JButton("메뉴");
        btnReturn.addActionListener(e -> frame.showCard("menu"));

        initDoPanel();

        southPanel.add(btnReturn,"West");
        JPanel noticePanel = new JPanel();
        noticePanel.add(notice);
        southPanel.add(noticePanel,"Center");
        southPanel.add(doPanel,"East");

        add(southPanel,"South");
    }

    private void initDoPanel() {
        doPanel = new JPanel();
        redo = new JButton("⮪");
        undo = new JButton("⮫");

        redo.addActionListener(e -> pressedDoBtn(1));
        undo.addActionListener(e -> pressedDoBtn(2));

        redo.setEnabled(false);
        undo.setEnabled(false);

        doPanel.add(redo);
        doPanel.add(undo);
    }

    private void initNorthPanel() {
        initSearchPanel();
        initEditPanel();
        add(northPanel,"North");
    }

    private void initEditPanel() {
        this.editPanel = new JPanel();
        this.btn2 = new JButton("단어 검색");
        this.btn2.addActionListener(e -> card.show(northPanel,"searchMode"));
        JButton editor = new JButton("추가/수정"),del = new JButton("제거");
        editor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String eng = engText.getText();
                String kor = korText.getText();
                if (eng.isEmpty() || kor.isEmpty()){
                    notice.setText("단어 또는 뜻이 없습니다");
                    notice.setForeground(new Color(172, 41, 52));
                }else {
                    if (baseManager.findWordIndex(eng) == -1){
                        updateRedoList(new Word(eng,null));
                        notice.setText(eng+" 이/가 성공적으로 추가되었습니다");
                    }
                    else{
                        updateRedoList(new Word(eng,baseManager.voc.get(baseManager.findWordIndex(eng)).kor));
                        notice.setText(eng+" 이/가 성공적으로 수정되었습니다");
                    }
                    baseManager.editWord(eng,kor); //실제 수정

                    notice.setForeground(new Color(0,158,96));
                    updateTableData(text.getText());
                }
            }
        });
        del.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String eng = engText.getText();
                if (eng.isEmpty()){
                    notice.setText("입력이 없습니다");
                    notice.setForeground(new Color(172, 41, 52));
                }
                else if (-1==baseManager.findWordIndex(eng)){
                    notice.setText("단어가 단어장에 없습니다");
                    notice.setForeground(new Color(172, 41, 52));
                }else {
                    updateRedoList(new Word(eng,baseManager.voc.get(baseManager.findWordIndex(eng)).kor));
                    baseManager.deleteWord(eng);
                    notice.setText(eng+" 이/가 성공적으로 삭제됐습니다");
                    notice.setForeground(new Color(0,158,96));
                    updateTableData(text.getText());
                }
            }
        });
        this.engText = new JTextField(7);
        this.korText = new JTextField(7);
        this.editPanel.add(btn2);
        this.editPanel.add(new JLabel("영단어 :"));
        this.editPanel.add(engText);
        this.editPanel.add(new JLabel("뜻 :"));
        this.editPanel.add(korText);
        this.editPanel.add(editor);
        this.editPanel.add(del);
        this.northPanel.add(editPanel,"editMode");
    }

    private void initSearchPanel() {
        this.searchPanel = new JPanel();
        this.btn1 = new JButton("단어 편집");
        this.btn1.addActionListener(e -> card.show(northPanel,"editMode"));
        this.text = new JTextField(10);
        this.text.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTableData(text.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTableData(text.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });
        this.asc = new JRadioButton("A-Z");
        this.asc.setSelected(true);
        this.asc.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange()==ItemEvent.SELECTED){
                    flag = true;
                    initTableData();
                }
            }
        });
        this.desc = new JRadioButton("Z-A");
        this.desc.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange()==ItemEvent.SELECTED){
                    flag = false;
                    initTableData();
                }
            }
        });
        ButtonGroup group = new ButtonGroup();
        group.add(asc);
        group.add(desc);
        this.searchPanel.add(btn1);
        this.searchPanel.add(new JLabel("검색할 단어 :"));
        this.searchPanel.add(text);
        this.searchPanel.add(new JLabel("   정렬 :"));
        this.searchPanel.add(asc);
        this.searchPanel.add(desc);
        this.northPanel.add(searchPanel,"searchMode");
    }

    private void updateTableData(String str) {
        subsetManager.voc = baseManager.searchVoc2(str);
        initTableData();
    }

    private void updateRedoList(Word w){
        if (redoList.size() >= REDOLISTCAP){
            redoList.removeFirst();
        }
        redoList.add(w);
        undoList.clear();
        updateDoBtn();
    }
    private void pressedDoBtn(int selector){
        LinkedList<Word> list1;
        LinkedList<Word> list2;
        if (selector == 1) {
            list1 = redoList;
            list2 = undoList;
        }else{
            list1 = undoList;
            list2 = redoList;
        }
        Word w = list1.removeLast();
        if (-1==baseManager.findWordIndex(w.eng))
            list2.add(new Word(w.eng,null));
        else
            list2.add(new Word(w.eng,baseManager.voc.get(baseManager.findWordIndex(w.eng)).kor));
        if (w.kor == null)
            baseManager.deleteWord(w.eng);
        else
            baseManager.editWord(w.eng,w.kor);
        updateTableData(text.getText());
        updateDoBtn();
        notice.setText(w.eng+" 은/는 이제 "+(w.kor == null ? "없습니다" : w.kor+"입니다"));
    }
    private void updateDoBtn(){
        undo.setEnabled(!undoList.isEmpty());
        redo.setEnabled(!redoList.isEmpty());
    }

    private void removeTableData() {
        if (model.getRowCount() > 0){
            model = new DefaultTableModel(header,0);
            table.setModel(model);
        }
    }
}