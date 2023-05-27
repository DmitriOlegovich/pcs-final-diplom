import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    public static final String STOP_FILE = "stop-ru.txt";
    private final Map<String, List<PageEntry>> cache;

    //список для стоп слов из txt
    private final List<String> stopWords;

    public BooleanSearchEngine(File pdfsDir) throws IOException {

        cache = new HashMap<>();
        stopWords = saveWordsFromStopFile();

        if (pdfsDir.isDirectory()) {
            for (File pdf : Objects.requireNonNull(pdfsDir.listFiles())) {

                if (pdf.isFile()) {

                    PdfDocument doc = new PdfDocument(new PdfReader(pdf));

                    for (int pageNumber = 1; pageNumber <= doc.getNumberOfPages(); pageNumber++) {

                        PdfPage page = doc.getPage(pageNumber);

                        String textFromPage = PdfTextExtractor.getTextFromPage(page);

                        String[] wordsFromPage = textFromPage.split("\\P{IsAlphabetic}+");

                        Map<String, Integer> frequencyOnPage = new HashMap<>();

                        countingWordsOnOnePage(wordsFromPage, frequencyOnPage);

                        saveIntoCache(pdf, pageNumber, frequencyOnPage);
                    }
                }
            }
        }
    }

    private List<String> saveWordsFromStopFile() {

        List<String> stopWords = new ArrayList<>();

        File stopFile = new File(STOP_FILE);

        if (stopFile.isFile()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(stopFile))) {

                String buff;
                while ((buff = reader.readLine()) != null) {

                    stopWords.add(buff);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return stopWords;
    }

    private void countingWordsOnOnePage(String[] wordsFromPage, Map<String, Integer> frequencyOnPage) {
        for (var word : wordsFromPage) {
            if (word.isEmpty()) {
                continue;
            }
            word = word.toLowerCase();
            frequencyOnPage.put(word, frequencyOnPage.getOrDefault(word, 0) + 1);
        }
    }

    private void saveIntoCache(File pdf, int pageNumber, Map<String, Integer> frequencyOnPage) {
        for (var entryWordFromPage : frequencyOnPage.entrySet()) {

            String word = entryWordFromPage.getKey();
            int count = entryWordFromPage.getValue();

            PageEntry wordInfo = new PageEntry(pdf.getName(), pageNumber, count);

            if (cache.containsKey(word)) {
                cache.get(word).add(wordInfo);

            } else {
                cache.put(word, new ArrayList<>());
                cache.get(word).add(wordInfo);
            }
        }
    }

    @Override
    public List<PageEntry> search(String input) {
        List<PageEntry> resultList = new ArrayList<>();

        String[] words = input.split(" ");

        for (String word : words) {

            if (cache.get(word) != null && !stopWords.contains(word)) {
                List<PageEntry> listFromCache = cache.get(word);

                if (resultList.isEmpty()) {
                    resultList.addAll(listFromCache);

                } else {
                    for (var cache : listFromCache) {
                        if (resultList.contains(cache)) {

                            var pageForChange = resultList.get(resultList.indexOf(cache));
                            pageForChange.setCount(pageForChange.getCount() + cache.getCount());

                            resultList.set(resultList.indexOf(cache), pageForChange);

                        } else {
                            resultList.add(cache);
                        }
                    }
                }
            }
        }
        resultList.sort(Comparator.reverseOrder());
        return resultList;
    }
}
