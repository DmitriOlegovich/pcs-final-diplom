import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    protected Map<String, List<PageEntry>> indexing = new HashMap<>();
    protected Set<String> unusefulWords = new HashSet<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        // прочтите тут все pdf и сохраните нужные данные,
        // тк во время поиска сервер не должен уже читать файлы


        for (File file : Objects.requireNonNull(pdfsDir.listFiles())) {
            String fileName = file.getName();
            String absFileName = file.getAbsolutePath();
            var doc = new PdfDocument(new PdfReader(absFileName));
            for (int i = 1; i <= doc.getNumberOfPages(); i++) {
                var page = doc.getPage(i);
                String text = PdfTextExtractor.getTextFromPage(page);
                String[] words = text.split("\\P{IsAlphabetic}+");

                Map<String, Integer> freqs = new HashMap<>();
                for (var word : words) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    word = word.toLowerCase();
                    freqs.put(word, freqs.getOrDefault(word, 0) + 1);
                }

                for (String word : freqs.keySet()) {
                    PageEntry pageEntry = new PageEntry(fileName, i, freqs.get(word));
                    List<PageEntry> pageEntryList = indexing.getOrDefault(word, new ArrayList<>());
                    pageEntryList.add(pageEntry);
                    Collections.sort(pageEntryList);
                    indexing.put(word, pageEntryList);
                }
            }

        }

    }


    private List<PageEntry> addLists(List<PageEntry> list1, List<PageEntry> list2) {
        List<PageEntry> res = new ArrayList<>();
        List<PageEntry> tempList2 = new ArrayList<>(list2);

        for (PageEntry pageEntry : list1) {
            boolean bothPage = false;
            int count = -1;
            for (int j = 0; j < tempList2.size(); j++) {
                if (tempList2.get(j).getPdfName().equals(pageEntry.getPdfName()) &&
                        tempList2.get(j).getPage() == pageEntry.getPage()) {
                    res.add(new PageEntry(pageEntry.getPdfName(), pageEntry.getPage(), pageEntry.getCount() + tempList2.get(j).getCount()));
                    bothPage = true;
                    count = j;
                    break;
                }
            }
            if (!bothPage) {
                res.add(pageEntry);
            } else {
                tempList2.remove(count);
            }
        }

        res.addAll(tempList2);

        Collections.sort(res);
        return res;
    }

    @Override
    public List<PageEntry> search(String text) {
        String[] words = text.split("\\P{IsAlphabetic}+");
        List<String> cleanWords = new ArrayList<>();

        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            if (!unusefulWords.contains(word)) {
                cleanWords.add(word);
            }
        }

        List<PageEntry> result = new ArrayList<>();
        for (String word : cleanWords) {
            result = addLists(result, indexing.get(word.toLowerCase()));
        }

        return result;
    }


    public void readUnusefulWords(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String s;
            while ((s = br.readLine()) != null) {
                unusefulWords.add(s);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
