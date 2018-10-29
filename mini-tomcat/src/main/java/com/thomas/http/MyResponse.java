package com.thomas.http;

import java.io.IOException;
import java.io.OutputStream;

public class MyResponse {
    private OutputStream os;

    public MyResponse(OutputStream os) {
        this.os = os;
    }
    public void write(String outstr) throws IOException {
        os.write(outstr.getBytes());
        os.flush();
    }
}
