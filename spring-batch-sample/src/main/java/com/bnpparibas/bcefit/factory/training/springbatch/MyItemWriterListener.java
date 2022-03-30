package com.bnpparibas.bcefit.factory.training.springbatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemWriteListener;

import java.util.List;

public class MyItemWriterListener implements ItemWriteListener<UserOut> {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void beforeWrite(List<? extends UserOut> list) {
        list.forEach(user -> log.info("START WRITE "+user));
    }

    @Override
    public void afterWrite(List<? extends UserOut> list) {
        list.forEach(user -> log.info("END WRITE "+user));
    }

    @Override
    public void onWriteError(Exception e, List<? extends UserOut> list) {
        log.error(" ERREUR : "+ e);
        list.forEach(user -> log.info("ERROR on WRITE "+user));
    }
}
