package org.openremote.rest;

public class GenericResourceResultWithErrorMessage {

  private String errorMessage;
  private Object result;

  public GenericResourceResultWithErrorMessage() {
    super();
  }

  public GenericResourceResultWithErrorMessage(String errorMessage, Object result) {
    super();
    this.errorMessage = errorMessage;
    this.result = result;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public Object getResult() {
    return result;
  }

  public void setResult(Object result) {
    this.result = result;
  }

}