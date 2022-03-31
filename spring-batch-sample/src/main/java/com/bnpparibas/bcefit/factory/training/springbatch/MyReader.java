package com.bnpparibas.bcefit.factory.training.springbatch;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.io.File;

/**
 * on peut dans certains cas avoir a développer nous meme le reader si on doit lire à partir de flux particulier
 * genre goal gateway
 *
 * et on peut aussi devoir le faire pour le writer en implementant un ItemWriter.
 */
public class MyReader implements ItemReader<UserCSV> {
    File file = new File("mon path c:/.../.csv");


    @Override
    public UserCSV read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        return null;
    }
}
