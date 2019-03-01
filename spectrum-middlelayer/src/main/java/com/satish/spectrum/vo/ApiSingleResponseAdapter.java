package com.satish.spectrum.vo;

import java.util.concurrent.ExecutionException;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureAdapter;

public class ApiSingleResponseAdapter<T> extends ListenableFutureAdapter<T, ResponseEntity<ApiResponse<T>>> {
	 
    public ApiSingleResponseAdapter(ListenableFuture<ResponseEntity<ApiResponse<T>>> resp) {
        super(resp);
    }
 
    @Override
    protected T adapt(ResponseEntity<ApiResponse<T>> responseEntity) throws ExecutionException {
    	ApiResponse<T> response = responseEntity.getBody();
        T item = response.results.get(0);
        return item;
    }
}