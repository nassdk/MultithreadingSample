package com.test.multithreadingtesting;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutorTestingActivity extends AppCompatActivity {

    private MaterialButton startSingleThreadExecutor, startFixedThreadPool, startCachedThreadPool, startScheduledThreadPool;
    private ImageButton clearBtn;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_executor_testing);

        initViews();
        initClick();
    }

    private void initViews() {

        tvTitle = findViewById(R.id.tvTitle);
        clearBtn = findViewById(R.id.clearBtn);
        startSingleThreadExecutor = findViewById(R.id.startSingleThreadExecutor);
        startFixedThreadPool = findViewById(R.id.startFixedThreadPool);
        startCachedThreadPool = findViewById(R.id.startCachedThreadPool);
        startScheduledThreadPool = findViewById(R.id.startScheduledThreadPool);
    }

    private void initClick() {

        startSingleThreadExecutor.setOnClickListener(v -> testSingleThreadExecutor());
        startFixedThreadPool.setOnClickListener(v -> testFixedThreadPool());
        startCachedThreadPool.setOnClickListener(v -> testCachedThreadPool());
        startScheduledThreadPool.setOnClickListener(v -> testScheduledThreadPool());
        clearBtn.setOnClickListener(v -> clearLogs());
    }

    private void clearLogs() {
        tvTitle.post(() -> tvTitle.setText(""));
    }

    private void refreshStatus(String status) {
        tvTitle.post(() -> tvTitle.setText(tvTitle.getText() + status + "End Time for" + new Date() + "\n"));
    }

    private void testSingleThreadExecutor() {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        for (int i = 1; i <= 5; i++) {
            Runnable worker = new MyRunnable("Task" + i);
            executor.execute(worker);
        }
        executor.shutdown();
    }

    private void testFixedThreadPool() {

        ExecutorService executor = Executors.newFixedThreadPool(3);
        for (int i = 1; i <= 5; i++) {
            Runnable worker = new MyRunnable("Task" + i);
            executor.execute(worker);
        }
        executor.shutdown();
    }

    private void testCachedThreadPool() {

        ExecutorService executor = Executors.newCachedThreadPool();
        for (int i = 1; i <= 5; i++) {
            Runnable worker = new MyRunnable("Task" + i);
            executor.execute(worker);
        }
        executor.shutdown();
    }

    private void testScheduledThreadPool() {

        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(3);

        for (int i = 1; i <= 3; i++) {
            /*try {
                Thread.sleep(2000);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }*/
            MyRunnable task = new MyRunnable("Task " + i);
            scheduledThreadPool.schedule(task, 3, TimeUnit.SECONDS); // планируется задержка выполнения задачи на три секунды
//            scheduledThreadPool.scheduleAtFixedRate(task, 3, 10, TimeUnit.SECONDS); // планируется задержка выполнения задачи на три секунды
//            scheduledThreadPool.scheduleWithFixedDelay(task, 3, 15, TimeUnit.SECONDS); // планируется задержка выполнения задачи на три секунды
        }

        /*try {
            Thread.sleep(30000); // добавляем некоторую задержку
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }*/

        scheduledThreadPool.shutdown();
        while (!scheduledThreadPool.isTerminated()) {
            //wait for all tasks to finish
        }
        refreshStatus("Completed all threads");
    }

    class MyRunnable implements Runnable {
        private final String task;

        MyRunnable(String task) {
            this.task = task;
        }

        @Override
        public void run() {
            for (int i = 0; i < 3; i++) {
                refreshStatus("Executing " + task + " with " + Thread.currentThread().getName());
            }
        }
    }
}