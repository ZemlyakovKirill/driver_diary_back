package ru.themlyakov.driverdiary.utils;

import com.google.gson.annotations.Expose;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PaginationWrapper<S> {
    @Expose
    private final int page;
    @Expose
    private final int totalPages;
    @Expose
    private final List<S> data;

    private static final int PAGE_SIZE=10;

    public PaginationWrapper(Page<S> pagedData) {
        this.page = pagedData.getNumber();
        this.totalPages=pagedData.getTotalPages();
        this.data=pagedData.getContent();
    }

    public PaginationWrapper(List<S> unPagedData, int page, Comparator<S>... comparators){
        if(page<1){
            throw new IllegalArgumentException("Page must be more or equal to 1");
        }
        int fromIndex = (page - 1) * PAGE_SIZE;
        this.page=page;
        this.totalPages=(int) Math.ceil((double)unPagedData.size()/PAGE_SIZE);
        if(fromIndex> unPagedData.size()){
            this.data=new ArrayList<>();
            return;
        }
        List<S> pagedList = unPagedData.subList(fromIndex, Math.min(fromIndex + PAGE_SIZE, unPagedData.size()));
        for (Comparator<S> comparator : comparators) {
            pagedList.sort(comparator);
        }
        this.data= pagedList;
    }
}
