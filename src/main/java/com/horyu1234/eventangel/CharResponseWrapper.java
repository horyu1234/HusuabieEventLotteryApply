package com.horyu1234.eventangel;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.CharArrayWriter;
import java.io.PrintWriter;

/**
 * Created by horyu on 2018-01-15
 */
public class CharResponseWrapper extends HttpServletResponseWrapper {
    private final CharArrayWriter output = new CharArrayWriter();

    public CharResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public String toString() {
        return output.toString();
    }

    @Override
    public PrintWriter getWriter() {
        return new PrintWriter(output);
    }
}