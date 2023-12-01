package com.irene.tasks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.irene.IreneBot;

public class Tasks {

    private static boolean didDailyReportTaskStarted = false;
    private static boolean didVibeCheckTaskStarted = false;
    private static final Logger logger = LogManager.getLogger(Tasks.class);

    public static void createTask(Job job, String cron){
        JobDetail jobDetail = JobBuilder.newJob(job.getClass()).build();
        Trigger trigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
        try {
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.start();
        } catch (SchedulerException e) {
            //add log.
        }
    }

    public static void initTasks(){

        if(!IreneBot.getInstance().getSubscriptionDao().getSpecificSubscriptionType("daily").isEmpty())
            createDailyReportTask();
        if(!IreneBot.getInstance().getSubscriptionDao().getSpecificSubscriptionType("vibe").isEmpty())
            createCheckVibeTask();
    }

    

    public static void createDailyReportTask(){
        if(didDailyReportTaskStarted){
        }
        else{
            logger.info("creating new daily report task!");
            Tasks.createTask(new DailyReportTask(), "0 45 7 * * ?");
            didDailyReportTaskStarted = true;
        }
    }

    public static void createCheckVibeTask(){
        if(didVibeCheckTaskStarted){
        }
        else{
            logger.info("creating new vibecheck task!");
            //merged this two task for now. Maybe split it later.
            Tasks.createTask(new VibeCheckTask(), "*/30 * * * * ?");
            logger.info("creating new uponly task!");
            Tasks.createTask(new UpOnlyTask(), "*/30 * * * * ?");
            didVibeCheckTaskStarted = true;
        }
    }

    
    
}
