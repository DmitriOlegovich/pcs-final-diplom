import java.util.Objects;

public class PageEntry implements Comparable<PageEntry> {
    private final String pdfName;
    private final int page;
    private int count;

    public PageEntry(String pdfName, int page, int count) {
        this.pdfName = pdfName;
        this.page = page;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int compareTo(PageEntry obj) {
        return count - obj.count;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PageEntry)) return false;

        PageEntry pageEntry = (PageEntry) obj;

        return page == pageEntry.page && Objects.equals(pdfName, pageEntry.pdfName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pdfName, page);
    }

    @Override
    public String toString() {
        return "PageEntry{pdf=" + pdfName +
                ", page=" + page +
                ", count=" + count + "}";
    }
}
