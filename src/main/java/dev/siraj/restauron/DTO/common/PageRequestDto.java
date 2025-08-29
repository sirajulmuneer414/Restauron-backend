package dev.siraj.restauron.DTO.common;

public class PageRequestDto {

    private int pageNo;

    private int size;

    private String filter;

    private String search;

    private boolean isFiltered = false;

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {

        if(filter == null || filter.equals("none")) setFiltered(false);

        setFiltered(true);
        this.filter = filter;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        if(size <= 0 ) this.size = 10;
        this.size = size;
    }

    public boolean isFiltered() {
        return isFiltered;
    }

    private void setFiltered(boolean filtered) {
        isFiltered = filtered;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
