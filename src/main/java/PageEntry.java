public class PageEntry implements Comparable<PageEntry> {
    private final String pdfName;
    private final int page;
    private final int count;


    public PageEntry(String pdfName, int page, int count) {
        this.pdfName = pdfName;
        this.page = page;
        this.count = count;
    }

    public String getPdfName() {
        return pdfName;
    }

    public int getPage() {
        return page;
    }

    public int getCount() {
        return count;
    }

    @Override
    public int compareTo(PageEntry o) {
        if ((this.count < o.count) ||
                ((this.count == o.count) && (this.pdfName.compareTo(o.pdfName) > 0)) ||
                ((this.count == o.count) && (this.pdfName.compareTo(o.pdfName) == 0) && (this.page > o.page))) {
            return 1;
        } else if ((this.count == o.count) && (this.pdfName.compareTo(o.pdfName) == 0) && (this.page == o.page)) {
            return 0;
        } else {
            return -1;
        }
    }

    public String toString() {
        return "\n{ \n pdfName: " + this.pdfName + "," + "\n page: " + +this.page + "," + "\n count: " + this.count + "\n}";
    }

}
