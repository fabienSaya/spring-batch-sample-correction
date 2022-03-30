package com.bnpparibas.bcefit.factory.training.springbatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;

public class MyItemProcessorListener implements ItemProcessListener<UserCSV,UserOut> {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void beforeProcess(UserCSV userCSV) {
        log.info("START PROCESS "+userCSV);
    }

    @Override
    public void afterProcess(UserCSV userCSV, UserOut userOut) {
        log.info("AFTER PROCESS "+userCSV + " Result="+userOut);
    }

    @Override
    public void onProcessError(UserCSV userCSV, Exception e) {
        log.error(" ERREUR on: "+ userCSV+" detail error="+e);
        e.printStackTrace();
    }
}
