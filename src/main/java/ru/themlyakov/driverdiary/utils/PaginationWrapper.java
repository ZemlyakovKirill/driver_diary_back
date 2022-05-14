package ru.themlyakov.driverdiary.utils;

import com.google.gson.annotations.Expose;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public class PaginationWrapper {
    @Expose
    private final int page;
    @Expose
    private final int totalPages;
    @Expose
    private final List data;

    private static final int PAGE_SIZE=10;

    public <S>PaginationWrapper(Page<S> pagedData) {
        this.page = pagedData.getNumber();
        this.totalPages=pagedData.getTotalPages();
        this.data= pagedData.getContent();
    }

    public <S extends Sortable<S>>PaginationWrapper(List<S> unPagedData, int page, String sortingParameter, Sort.Direction direction){
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
        if (direction == Sort.Direction.ASC) {
            unPagedData.sort((o1, o2) -> o1.parameterComparingTo(o2, sortingParameter));
        } else {
            unPagedData.sort((o1, o2) -> o2.parameterComparingTo(o1, sortingParameter));
        }
        List<S> pagedList = unPagedData.subList(fromIndex, Math.min(fromIndex + PAGE_SIZE, unPagedData.size()));
        this.data= pagedList;
    }

    public <S>PaginationWrapper(List<S> unPagedData, int page){
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
        this.data= pagedList;
    }
}
