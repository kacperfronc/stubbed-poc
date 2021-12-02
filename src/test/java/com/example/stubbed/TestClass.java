package com.example.stubbed;

public class TestClass {

    private String dd;

    public TestClass() {
    }

    public TestClass(String dd) {
        this.dd = dd;
    }

    public String getDd() {
        return dd;
    }

    public void setDd(String dd) {
        this.dd = dd;
    }

    @Override
    public String toString() {
        return "TestClass{" +
                "dd='" + dd + '\'' +
                '}';
    }
}
