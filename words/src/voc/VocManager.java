package voc;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class VocManager {

    Vector<Word> voc = new Vector<>();
    NoteManager note;
    static Scanner scan = new Scanner(System.in);

    public VocManager(NoteManager note) {this.note = note;}

    void addWord(String eng, String kor) {voc.add(new Word(eng, kor));}

    void editWord(String eng,String kor){
        int idx = findWordIndex(eng);
        if (idx==-1) {
            //추가
            addWord(eng, kor);
        }else{
            //변경
            voc.get(idx).kor = kor;
            note.AlterWord(voc.get(idx).eng, voc.get(idx).kor);
        }
    }
    @Deprecated
    void addWord() {
        try
        {
            System.out.println("추가할 영단어를 입력해 주세요.");
            String eng = scan.nextLine();
            int idx = findWordIndex(eng);
            if (idx!=-1) {
                System.out.println("이미 존재하는 영단어입니다.");
                return;
            }
            System.out.println("영단어의 뜻을 입력해 주세요.");
            String kor = scan.nextLine();

            addWord(eng, kor);
            System.out.println("단어 추가가 완료되었습니다.");
        }
        catch(InputMismatchException e)
        {
            System.out.println("입력 형식을 지켜주세요");
        }
    }
    @Deprecated
    void changeWord() {
        System.out.println("변경할 영단어를 입력해 주세요.");
        try
        {
            int idx = findWordIndex(scan.nextLine());
            if (-1 == idx) {
                System.out.println("존재하지 않는 영단어입니다.");
                return;
            }
            System.out.println("영단어의 새 뜻을 입력해 주세요.");
            String kor = scan.nextLine();
            if (kor.isEmpty()) {
                System.out.println("변경이 취소되었습니다.");
                return;
            }
            voc.get(idx).kor = kor;
            note.AlterWord(voc.get(idx).eng, voc.get(idx).kor);
            System.out.println("변경이 완료되었습니다.");
        }
        catch(InputMismatchException e)
        {
            System.out.println("입력 형식을 지켜주세요");
        }
    }

    void deleteWord(String eng) {
        int idx = findWordIndex(eng);
        voc.remove(idx);
    }

    void makeVoc(String fileName) {
        try (Scanner file = new Scanner(new File(fileName))) {
            while (file.hasNext()) {
                String str = file.nextLine();
                String[] temp = str.split("\t");
                this.addWord(temp[0].trim(), temp[1].trim());
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null,"파일 경로를 찾을 수 없습니다.",
                    "Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    void saveVoc(String fileName) {
        try (PrintWriter outfile = new PrintWriter(fileName)) {
            for (Word w: voc){
                outfile.println(w.eng+"\t"+w.kor);
            }
        }catch(FileNotFoundException e){
            JOptionPane.showMessageDialog(null,"파일 조회 불가",
                    "Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    public Vector<Word> searchVoc2(String sWord) {
        Vector<Word> wordVector = new Vector<>();
        for (Word w : voc) {
            if (w.eng.contains(sWord)) {
                wordVector.add(w);
            }
        }
        return wordVector;
    }

    public int findWordIndex(String eng) { // 못 찾으면 -1
        Vector<Word> words = voc;
        for (int i = 0; i < words.size(); i++) {
            if (voc.get(i).eng.equals(eng)) {
                return i;
            }
        }
        return -1;
    }
}