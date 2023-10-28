package com.irene.tasks;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.irene.IreneBot;

public class Task implements Job{

    protected IreneBot bot = IreneBot.getInstance();

    public Task() {
        
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        
    }
    
}
