package com.test.multithreadingtesting;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

public class MainActivityJava extends AppCompatActivity {

    private final static int deadLock = 0;
    private final static int raceCondition = 1;
    private final static int priorityInversion = 2;
    private final static Object ProductManager = new Object();
    private final static Object ProjectManager = new Object();
    public static int value = 0;
    static boolean flag = false;
    String x = "0";
    String y = "0";
    private MaterialButton startValitoleCheckingBtn, startDeadlockBtn, startRaceConditionBtn, startPriorityInversionBtn;
    private ImageButton clearBtn;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initClick();

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run()
//            }
//        }, 5000);

    }

    private void initViews() {

        startValitoleCheckingBtn = findViewById(R.id.startValitoleChecking);
        startDeadlockBtn = findViewById(R.id.startDeadlock);
        startRaceConditionBtn = findViewById(R.id.startRaceCondition);
        startPriorityInversionBtn = findViewById(R.id.startPriorityInversion);
        clearBtn = findViewById(R.id.clearBtn);
        tvTitle = findViewById(R.id.tvTitle);
    }

    private void initClick() {

        startValitoleCheckingBtn.setOnClickListener(v -> startValotileChecking());
        startDeadlockBtn.setOnClickListener(v -> startDeadLock());
        startRaceConditionBtn.setOnClickListener(v -> startRaceCondition());
        startPriorityInversionBtn.setOnClickListener(v -> setStartPriorityInversion());
        clearBtn.setOnClickListener(v -> clearLogs());
    }

    private void clearLogs() {
        tvTitle.post(() -> tvTitle.setText(""));
    }

    private void refreshStatus(String status) {

        tvTitle.post(new Runnable() {
            @Override
            public void run() {
                tvTitle.setText(tvTitle.getText() + status + "\n");
            }
        });
    }

    private void doWork() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    try {
                        Thread.sleep(3000);
                        tvTitle.post(new Runnable() {
                            @Override
                            public void run() {
                                tvTitle.setText(String.valueOf(System.currentTimeMillis()));
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void startValotileChecking() {

        Runnable whileFlagFalse = () -> {
            while (!flag) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
                Log.d("ACTIVITY_TAG", "Flag is now FALSE");
            }
            Log.d("ACTIVITY_TAG", "Flag is now TRUE");
        };

        new Thread(whileFlagFalse).start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }

        flag = true;
    }

    private void startDeadLock() {

        ProgrammingTask programmingTask = new ProgrammingTask(deadLock);
        TestingTask testingTask = new TestingTask(deadLock);
        Worker workerGosha = new Worker("Гоша");
        Worker workerVova = new Worker("Вова");

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                workerGosha.doSomeWork(workerVova);
//            }
//        });
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                workerVova.doSomeWork(workerGosha);
//            }
//        });

        programmingTask.start();
        testingTask.start();
    }

    private void startRaceCondition() {

        Counter counter = new Counter();

        Thread t1 = new Thread(counter, "Thread-1");
        Thread t2 = new Thread(counter, "Thread-2");
        Thread t3 = new Thread(counter, "Thread-3");

        t1.start();
        t2.start();
        t3.start();
    }

    private void setStartPriorityInversion() {

    }

    class ProgrammingTask extends Thread {

        private final int operation;

        ProgrammingTask(int operation) {
            this.operation = operation;
        }

        @Override
        public void run() {
            switch (operation) {
                case deadLock:

                    synchronized (ProductManager) {

                        refreshStatus("ProductManager is busy by programming task");

                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ignored) {
                        }

                        refreshStatus("Programming task is waiting for ProjectManager");

                        synchronized (ProjectManager) {
                            refreshStatus("ProjectManager and Product Manager are busy by programming task");
                        }
                    }
                    break;
            }
        }
    }

    class TestingTask extends Thread {

        private final int operation;

        TestingTask(int operation) {
            this.operation = operation;
        }

        @Override
        public void run() {

            switch (operation) {
                case deadLock:

                    synchronized (ProjectManager) {

                        refreshStatus("ProjectManager is busy by testing task");

                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ignored) {
                        }

                        refreshStatus("Testing task is waiting for ProductManager");

                        synchronized (ProductManager) {
                            refreshStatus("ProductManager and Product Manager are busy by testing task");
                        }
                    }
                    break;
            }
        }
    }

    class Counter implements Runnable {
        private int c = 0;

        public void increment() {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            c++;
        }

        public void decrement() {
            c--;
        }

        public int getValue() {
            return c;
        }

        @Override
        public void run() {

//            synchronized(this) {

            //incrementing
            this.increment();
            refreshStatus("Value for Thread After increment " + Thread.currentThread().getName() + " " + this.getValue());
            //decrementing
            this.decrement();
            refreshStatus("Value for Thread at last "
                    + Thread.currentThread().getName() + " " + this.getValue());
//            }
        }
    }

    class Worker {

        private final String name;

        Worker(String name) {
            this.name = name;
        }

        synchronized void doSomeWork(@NotNull Worker worker) {
            refreshStatus(this.name + " воткнул " + worker.name);
        }
    }
}