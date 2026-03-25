package voc;

import javax.swing.*;
import java.util.*;
import java.io.*;

public class NoteManager
{
    Vector<Word> voc = new Vector<>();
    Vector<String> fileList = new Vector<>();

    // 단어 추가
    void addWord(String eng, String kor) {
        Filter(eng);
        this.voc.add(new Word(eng, kor));
    }

    // 중복 방지
    void Filter(String eng)
    {
        int idx = findWordIndex(eng);
        if (idx != -1) {
            voc.remove(idx);
        }
    }

    // 노트 내용 수정을 위한 파트
    void SaveNote(String NoteName)
    {
        try (PrintWriter outfile = new PrintWriter(NoteName)) {
            for (Word w: this.voc){
                outfile.println(w.eng+"\t"+w.kor);
            }
        }catch(FileNotFoundException e){
            JOptionPane.showMessageDialog(null,"파일 조회 불가",
                    "Error",JOptionPane.ERROR_MESSAGE);
        }

    }

    void setFile()
    {
        fileList = new Vector<>();
        String directoryPath = "res/reminder";
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        fileList.add(file.getName());
                    }
                    AddNote("res/reminder/"+files[0].getName());
                }
            }
        }
        else {
            JOptionPane.showMessageDialog(null,"파일 경로가 존재하지 않습니다",
                    "Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    int FileSearch(String file)
    {
        String directoryPath = "res/reminder";
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                int i=0;
                for (File file1 : files) {
                    if(file1.getName().equals(file)) {
                        return i;
                    }
                    i++;
                }
            }
        }
        return -1;
    }

    // 노트 인식용
    void AddNote(String NoteName)
    {
        try (Scanner file = new Scanner(new File(NoteName))) {
            while (file.hasNext()) {
                String str = file.nextLine();
                String[] temp = str.split("\t");
                this.voc.add(new Word(temp[0].trim(), temp[1].trim()));
            }
        }
        catch (FileNotFoundException e) {
            System.out.println();
        }
    }

    //영단어 삭제
    int deleteWord(String eng) {

        int idx = findWordIndex(eng);
        if (idx == -1) {
            return -1;
        }
        voc.remove(idx);
        return 0;
    }

    int deleteAll()
    {
        Vector<Word> words = this.voc;
        voc.removeAll(words);
        return 0;
    }

    //검색
    public Vector<Word> searchVoc2(String sWord, boolean flag) {
        Vector<Word> wordVector = new Vector<>();
        for (Word w : voc) {
            if (w.eng.contains(sWord) && flag) {
                wordVector.add(w);
            }
            if(w.kor.contains(sWord) && !flag)
            {
                wordVector.add(w);
            }
        }
        return wordVector;
    }

    //단어 수정
    public void AlterWord(String eng, String kor)
    {
        int idx = findWordIndex(eng);
        if(idx!=-1) voc.get(idx).kor = kor;
    }

    public int findWordIndex(String eng) { // 못 찾으면 -1
        Vector<Word> words = this.voc;
        for (int i = 0; i < words.size(); i++) {
            if (voc.get(i).eng.equals(eng)) {
                return i;
            }
        }
        return -1;
    }
}
