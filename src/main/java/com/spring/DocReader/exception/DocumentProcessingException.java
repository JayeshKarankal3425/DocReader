package com.spring.DocReader.exception;


public class DocumentProcessingException extends RuntimeException {
    public DocumentProcessingException(String message){
        super(message);
    }

    public DocumentProcessingException(){
        super("Error is processing document !!");
    }

    public DocumentProcessingException(String message, Throwable ex){
        super(message,ex);
    }

}
