package com.bnpparibas.bcefit.factory.training.springbatch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class MainStarter {


    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(MainStarter.class, args);

        BatchJobLauncher launcher = context.getBean(BatchJobLauncher.class);

        try {
            launcher.perform();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
