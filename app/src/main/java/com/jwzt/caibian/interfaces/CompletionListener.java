package com.jwzt.caibian.interfaces;

import java.util.List;

/**
 * Created by Karthik on 22/01/16.
 */
public interface CompletionListener {

    public void onProcessCompleted(String message, List<String> merger);

}
