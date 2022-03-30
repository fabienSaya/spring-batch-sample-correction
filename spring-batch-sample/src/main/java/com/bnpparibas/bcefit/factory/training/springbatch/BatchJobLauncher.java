package com.bnpparibas.bcefit.factory.training.springbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BatchJobLauncher {


    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    public void perform() throws Exception{
        JobParameters jobParameter = new JobParametersBuilder().addString("JobID", String.valueOf(System.currentTimeMillis())).toJobParameters();
        jobLauncher.run(job, jobParameter);
    }
}
