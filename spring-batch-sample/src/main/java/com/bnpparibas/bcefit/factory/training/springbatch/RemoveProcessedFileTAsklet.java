package com.bnpparibas.bcefit.factory.training.springbatch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import java.io.File;

public class RemoveProcessedFileTAsklet implements Tasklet, InitializingBean {

    private final String NEW_PATH = "C:/temp/file_treated_for_spring_batch_sample/";

    public Resource[] resources;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        for (Resource res : resources){
            File file = res.getFile();
            String fileName = file.getName();
            String newFilePath = NEW_PATH.concat(fileName);
            File newFile = new File(newFilePath);
            boolean renamed = file.renameTo(newFile);
            if (!renamed){
                throw new UnexpectedJobExecutionException("Coult not rename file to " + newFilePath);
            }

        }

        return RepeatStatus.FINISHED;
    }

    public void setResources(Resource[] resources) {
        this.resources = resources;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
